package com.provider.hot.news;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.provider.hot.news.adapters.NewsListAdapter;
import com.provider.hot.news.entity.Item;
import com.provider.hot.news.providers.CachedNewsProvider;
import com.provider.hot.news.utils.XmlParseUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MainScreen extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String URL_STR = "http://podrobnosti.ua/rss/hot.rss/";
    private static final String METHOD = "GET";
    private static final int TIMEOUT = 10 * 1000;

    private ContentResolver contentResolver;

    private List<Item> items = new ArrayList<>();

    private NewsListAdapter adapter;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.refresh);
        listView = (ListView) this.findViewById(R.id.listView);

        contentResolver = getContentResolver();

        swipeRefreshLayout.setOnRefreshListener(this);

        loadListNewsFromCache(items);
        if (items.size() == 0) {
            loadListNewsFromNetwork();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) parent.getItemAtPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item.getLink()));
                intent.putExtra("Title", item.getTitle());
                startActivity(intent);
            }
        });
    }

    private void loadListNewsFromCache(List<Item> items) {
        Cursor c = contentResolver.query(CachedNewsProvider.CONTENT_URI, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                items.add(new Item(
                        c.getString(c.getColumnIndex(CachedNewsProvider.TITLE)),
                        c.getString(c.getColumnIndex(CachedNewsProvider.LINK)),
                        c.getString(c.getColumnIndex(CachedNewsProvider.DATE)),
                        c.getString(c.getColumnIndex(CachedNewsProvider.DESCRIPTION)),
                        c.getString(c.getColumnIndex(CachedNewsProvider.IMAGE_URL)),
                        c.getBlob(c.getColumnIndex(CachedNewsProvider.IMAGE))));
            } while (c.moveToNext());
        }
        if(items.size() > 0) {
            adapter = new NewsListAdapter(getBaseContext(), items);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    }

    private void loadListNewsFromNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            new RssReader().execute(URL_STR);
        } else {
            Toast.makeText(this, "Network is currently unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCache(List<Item> items) {
        if (items.size() > 0) {
            contentResolver.delete(CachedNewsProvider.CONTENT_URI, null, null);
            for (Item item : items) {
                ContentValues values = new ContentValues();
                values.put(CachedNewsProvider.TITLE, item.getTitle());
                values.put(CachedNewsProvider.LINK, item.getLink());
                values.put(CachedNewsProvider.DATE, item.getDate());
                values.put(CachedNewsProvider.DESCRIPTION, item.getDescription());
                values.put(CachedNewsProvider.IMAGE_URL, item.getImageUrl());
                values.put(CachedNewsProvider.IMAGE, item.getImage());
                contentResolver.insert(CachedNewsProvider.CONTENT_URI, values);
            }
        }
    }

    private boolean shouldBeUpdated(List<Item> items) {
        Cursor c = contentResolver.query(CachedNewsProvider.CONTENT_URI, null, null, null, null);

        if (c.moveToFirst() && items != null && items.size() > 0) {
            if (c.getString(c.getColumnIndex(CachedNewsProvider.TITLE)).equals(items.get(0).getTitle())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRefresh() {
        loadListNewsFromNetwork();
        swipeRefreshLayout.setRefreshing(false);
    }

    private class RssReader extends AsyncTask<String, Integer, List<Item>> {

        @Override
        protected List<Item> doInBackground(String... params) {
            List<Item> items = new ArrayList<>();
            String urlStr = params[0];
            InputStream is;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);
                connection.setRequestMethod(METHOD);
                connection.setDoInput(true);
                connection.connect();

                is = connection.getInputStream();
                XmlParseUtil.parseXmlAndStoreToList(is, items);

                for (Item item : items) {
                    URL imageURL = new URL(item.getImageUrl());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BitmapFactory.decodeStream(imageURL.openStream())
                            .compress(Bitmap.CompressFormat.PNG, 0, baos);
                    item.setImage(baos.toByteArray());
                }

            } catch (IOException | XmlPullParserException | ParseException e) {
                e.printStackTrace();
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            if (items.size() > 0 && shouldBeUpdated(items)) {
                updateCache(items);
                adapter = new NewsListAdapter(getBaseContext(), items);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
