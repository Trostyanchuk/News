package com.provider.hot.news.utils;


import com.provider.hot.news.entity.Item;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParseUtil {

    private static final String ITEM_TAG = "item";
    private static final String TITLE_TAG = "title";
    private static final String LINK_TAG = "link";
    private static final String PUB_DATE_TAG = "pubDate";
    private static final String DESCRIPTION_TAG = "description";
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("<img src=\"(.*)\" alt.*/> (.*)");

    public static void parseXmlAndStoreToList(InputStream is, List<Item> items) throws XmlPullParserException, IOException, ParseException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, null);

        int event = xpp.getEventType();
        Item newsItem = null;
        String tag = "";
        String content = "";
        while (event != XmlPullParser.END_DOCUMENT) {
            String name = xpp.getName();
            switch (event) {
                case XmlPullParser.START_TAG: {
                    if (name.equals(ITEM_TAG)) {
                        newsItem = new Item();
                    } else if (name.equals(TITLE_TAG)) {
                        tag = TITLE_TAG;
                    } else if (name.equals(LINK_TAG)) {
                        tag = LINK_TAG;
                    } else if (name.equals(PUB_DATE_TAG)) {
                        tag = PUB_DATE_TAG;
                    } else if (name.equals(DESCRIPTION_TAG)) {
                        tag = DESCRIPTION_TAG;
                    }
                    break;
                }
                case XmlPullParser.END_TAG: {
                    if (xpp.getName().equals(ITEM_TAG)) {
                        items.add(newsItem);
                        tag = "";
                    }
                    break;
                }
                case XmlPullParser.TEXT: {
                    content = xpp.getText() != null ? xpp.getText() : "";
                    content = content.trim();
                    break;
                }
            }
            //update list item with related field and value
            if (newsItem != null && !content.isEmpty()) {
                switch (tag) {
                    case TITLE_TAG: {
                        newsItem.setTitle(content);
                        break;
                    }
                    case LINK_TAG: {
                        newsItem.setLink(content);
                        break;
                    }
                    case PUB_DATE_TAG: {
                        newsItem.setDate(content);
                        break;
                    }
                    case DESCRIPTION_TAG: {
                        newsItem.setDescription(content);
                        break;
                    }
                }
                tag = "";
            }
            event = xpp.next();
        }
        parseDescriptionAndGetImageSrc(items);
    }

    private static void parseDescriptionAndGetImageSrc(List<Item> items) {

        for (Item item : items) {
            Matcher matcher = DESCRIPTION_PATTERN.matcher(item.getDescription());
            if (matcher.find()) {
                item.setImageUrl(matcher.group(1));
                item.setDescription(matcher.group(2));
            }
        }
    }
}
