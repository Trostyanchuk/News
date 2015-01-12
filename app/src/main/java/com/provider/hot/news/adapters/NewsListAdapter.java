package com.provider.hot.news.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.provider.hot.news.R;
import com.provider.hot.news.entity.Item;

import java.util.List;


public class NewsListAdapter extends BaseAdapter {

    private List<Item> newsList;
    private LayoutInflater inflater;

    public NewsListAdapter(Context context, List<Item> newsList) {
        this.newsList = newsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView description = (TextView) view.findViewById(R.id.description);

        if (newsList.get(position) != null) {
            if(newsList.get(position).getImage() != null) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(newsList.get(position).getImage(),
                        0, newsList.get(position).getImage().length));
            }
            title.setText(newsList.get(position).getTitle());
            date.setText(newsList.get(position).getDate());
            description.setText(newsList.get(position).getDescription());
        } else {
            imageView.setImageResource(R.drawable.ic_launcher);
        }

        return view;
    }
}
