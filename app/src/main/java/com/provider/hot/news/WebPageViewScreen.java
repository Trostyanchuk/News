package com.provider.hot.news;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebPageViewScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        Uri url = getIntent().getData();
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new Callback());
        webView.loadUrl(url.toString());

        setTitle(getTitle() + ": " + getIntent().getExtras().getString("Title"));

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading
                (WebView view, String url) {
            return (false);
        }
    }
}
