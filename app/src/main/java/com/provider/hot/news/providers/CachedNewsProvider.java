package com.provider.hot.news.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.provider.hot.news.utils.DBHelper;


public class CachedNewsProvider extends ContentProvider {

    public static final String DB_NAME = "News";
    public static final String TABLE_NAME = "latest_news";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_URL = "image_url";
    public static final String IMAGE = "image";

    public static final String CREATE_DB_TABLE = "CREATE TABLE latest_news ("
            + ID + " INTEGER PRIMARY KEY, " +
            "      title TEXT NOT NULL, " +
            "      link TEXT NOT NULL, " +
            "      date TEXT NOT NULL, " +
            "      description TEXT NOT NULL, " +
            "      image_url TEXT NOT NULL, " +
            "      image BLOB)";

    public static final String PROVIDER_NAME = "com.provider.hot.news";
    public static final String URL = "content://" + PROVIDER_NAME + "/" + TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private SQLiteDatabase db;
    private static final int NEWS = 1;
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME, NEWS);
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        DBHelper dbHelper = new DBHelper(context, DB_NAME, TABLE_NAME, CREATE_DB_TABLE);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case NEWS:

                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case NEWS:
                return "vnd.android.cursor.dir/com.provider.hot.news";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = db.insert(TABLE_NAME, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case NEWS:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
