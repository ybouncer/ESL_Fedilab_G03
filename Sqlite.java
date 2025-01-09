/* Copyright 2017 Thomas Schneider
 *
 * This file is a part of Fedilab
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Fedilab is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Fedilab; if not,
 * see <http://www.gnu.org/licenses>. */

package app.fedilab.android.sqlite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.fedilab.android.R;
import app.fedilab.android.client.Entities.Account;
import app.fedilab.android.helper.Helper;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Thomas on 23/04/2017.
 * Manage the  DataBase
 */

public class Sqlite extends SQLiteOpenHelper {

    public static final int DB_VERSION = 38;
    public static final String DB_NAME = "mastodon_etalab_db";
    //Table for custom emoji
    public static final String TABLE_CUSTOM_EMOJI = "CUSTOM_EMOJI";
    //Table for cached statuses
    public static final String TABLE_STATUSES_CACHE = "STATUSES_CACHE";
    //Table for timeline cache
    public static final String TABLE_TIMELINE_CACHE = "TIMELINE_CACHE";
    //Table for scheduling boosts
    public static final String TABLE_BOOST_SCHEDULE = "BOOST_SCHEDULE";
    //Table for blocking tracking domains
    public static final String TABLE_TRACKING_BLOCK = "TRACKING_BLOCK";
    //Table for timelines
    public static final String TABLE_TIMELINES = "TIMELINES";
    //Table for timelines
    public static final String TABLE_REMOTE_INSTANCE_TAGS = "REMOTE_INSTANCE_TAGS";
    //Table for notifications
    public static final String TABLE_NOTIFICATION_CACHE = "NOTIFICATION_CACHE";
    //Table for main menu items
    public static final String TABLE_MAIN_MENU_ITEMS = "MAIN_MENU_ITEMS";
    //Table for taking notes about accounts
    public static final String TABLE_USER_NOTES = "USER_NOTES";
    /***
     * List of tables to manage users and data
     */
    //Table of owned accounts
    static final String TABLE_USER_ACCOUNT = "USER_ACCOUNT";
    //Table of stored status
    static final String TABLE_STATUSES_STORED = "STATUSES_STORED";
    //Table for search
    static final String TABLE_SEARCH = "SEARCH";
    //Table for temp muting
    static final String TABLE_TEMP_MUTE = "TEMP_MUTE";
    //Table for instance names
    static final String TABLE_INSTANCES = "INSTANCES";
    //Table for peertube favorites
    static final String TABLE_PEERTUBE_FAVOURITES = "PEERTUBE_FAVOURITES";
    //Table for tags cache
    static final String TABLE_CACHE_TAGS = "CACHE_TAGS";
    static final String COL_USER_ID = "USER_ID";
    static final String COL_USERNAME = "USERNAME";
    static final String COL_ACCT = "ACCT";
    static final String COL_DISPLAYED_NAME = "DISPLAYED_NAME";
    static final String COL_LOCKED = "LOCKED";
    static final String COL_CREATED_AT = "CREATED_AT";
    static final String COL_FOLLOWERS_COUNT = "FOLLOWERS_COUNT";
    static final String COL_FOLLOWING_COUNT = "FOLLOWING_COUNT";
    static final String COL_STATUSES_COUNT = "STATUSES_COUNT";
    static final String COL_NOTE = "NOTE";
    static final String COL_URL = "URL";
    static final String COL_AVATAR = "AVATAR";
    static final String COL_AVATAR_STATIC = "AVATAR_STATIC";
    static final String COL_HEADER = "HEADER";
    static final String COL_HEADER_STATIC = "HEADER_STATIC";
    static final String COL_INSTANCE = "INSTANCE";
    static final String COL_OAUTHTOKEN = "OAUTH_TOKEN";
    static final String COL_EMOJIS = "EMOJIS";
    static final String COL_SOCIAL = "SOCIAL";
    static final String COL_CLIENT_ID = "CLIENT_ID";
    static final String COL_CLIENT_SECRET = "CLIENT_SECRET";
    static final String COL_REFRESH_TOKEN = "REFRESH_TOKEN";
    static final String COL_IS_MODERATOR = "IS_MODERATOR";
    static final String COL_IS_ADMIN = "IS_ADMIN";
    static final String COL_UPDATED_AT = "UPDATED_AT";
    static final String COL_PRIVACY = "PRIVACY";
    static final String COL_SENSITIVE = "SENSITIVE";
    static final String COL_ID = "ID";
    static final String COL_STATUS_SERIALIZED = "STATUS_SERIALIZED";
    static final String COL_STATUS_REPLY_SERIALIZED = "STATUS_REPLY_SERIALIZED";
    static final String COL_DATE_CREATION = "DATE_CREATION";
    static final String COL_IS_SCHEDULED = "IS_SCHEDULED";
    static final String COL_DATE_SCHEDULED = "DATE_SCHEDULED";
    static final String COL_SENT = "SENT";
    static final String COL_DATE_SENT = "DATE_SENT";
    static final String COL_SHORTCODE = "SHORTCODE";
    static final String COL_URL_STATIC = "URL_STATIC";
    static final String COL_KEYWORDS = "KEYWORDS";
    static final String COL_IS_ART = "IS_ART";
    static final String COL_IS_NSFW = "IS_NSFW";
    static final String COL_ANY = "ANY_TAG";
    static final String COL_ALL = "ALL_TAG";
    static final String COL_NONE = "NONE_TAG";
    static final String COL_NAME = "NAME";
    static final String COL_TARGETED_USER_ID = "TARGETED_USER_ID";
    static final String COL_DATE_END = "DATE_END";
    static final String COL_CACHED_ACTION = "CACHED_ACTION";
    static final String COL_STATUS_ID = "STATUS_ID";
    static final String COL_URI = "URI";
    static final String COL_ACCOUNT = "ACCOUNT";
    static final String COL_IN_REPLY_TO_ID = "IN_REPLY_TO_ID";
    static final String COL_IN_REPLY_TO_ACCOUNT_ID = "IN_REPLY_TO_ACCOUNT_ID";
    static final String COL_REBLOG = "REBLOG";
    static final String COL_CONTENT = "CONTENT";
    static final String COL_REBLOGS_COUNT = "REBLOGS_COUNT";
    static final String COL_FAVOURITES_COUNT = "FAVOURITES_COUNT";
    static final String COL_REBLOGGED = "REBLOGGED";
    static final String COL_FAVOURITED = "FAVOURITED";
    static final String COL_MUTED = "MUTED";
    static final String COL_SPOILER_TEXT = "SPOILER_TEXT";
    static final String COL_VISIBILITY = "VISIBILITY";
    static final String COL_MEDIA_ATTACHMENTS = "MEDIA_ATTACHMENTS";
    static final String COL_MENTIONS = "MENTIONS";
    static final String COL_TAGS = "TAGS";
    static final String COL_APPLICATION = "APPLICATION";
    static final String COL_LANGUAGE = "LANGUAGE";
    static final String COL_PINNED = "PINNED";
    static final String COL_DATE_BACKUP = "DATE_BACKUP";
    static final String COL_CARD = "CARD";
    static final String COL_INSTANCE_TYPE = "INSTANCE_TYPE";
    static final String COL_FILTERED_WITH = "FILTERED_WITH";
    static final String COL_UUID = "UUID";
    static final String COL_CACHE = "CACHE";
    static final String COL_DATE = "DATE";
    static final String COL_DOMAIN = "DOMAIN";
    static final String COL_TYPE = "TYPE";
    static final String COL_LIST_TIMELINE = "LIST_TIMELINE";
    static final String COL_DISPLAYED = "DISPLAYED";
    static final String COL_POSITION = "POSITION";
    static final String COL_REMOTE_INSTANCE = "REMOTE_INSTANCE";
    static final String COL_TAG_TIMELINE = "TAG_TIMELINE";
    static final String COL_NOTIFICATION_ID = "NOTIFICATION_ID";
    static final String COL_STATUS_ID_CACHE = "STATUS_ID_CACHE";
    static final String COL_NAV_NEWS = "NAV_NEWS";
    static final String COL_NAV_LIST = "NAV_LIST";
    static final String COL_NAV_SCHEDULED = "NAV_SCHEDULED";
    static final String COL_NAV_ARCHIVE = "NAV_ARCHIVE";
    static final String COL_NAV_ARCHIVE_NOTIFICATIONS = "NAV_ARCHIVE_NOTIFICATIONS";
    static final String COL_NAV_PEERTUBE = "NAV_PEERTUBE";
    static final String COL_NAV_FILTERS = "NAV_FILTERS";
    static final String COL_NAV_HOW_TO_FOLLOW = "NAV_HOW_TO_FOLLOW";
    static final String COL_NAV_ADMINISTRATION = "NAV_ADMINISTRATION";
    static final String COL_NAV_BLOCKED = "NAV_BLOCKED";
    static final String COL_NAV_MUTED = "NAV_MUTED";
    static final String COL_NAV_BLOCKED_DOMAINS = "NAV_BLOCKED_DOMAINS";
    static final String COL_NAV_HOWTO = "NAV_HOWTO";
    static final String COL_NAV_TRENDS = "NAV_TRENDS";
    static final String COL_POLL = "POLL";

    private static final String TABLE_USER_ACCOUNT_TEMP = "USER_ACCOUNT_TEMP";
    private static final String CREATE_TABLE_USER_ACCOUNT = "CREATE TABLE " + TABLE_USER_ACCOUNT + " ("
            + COL_USER_ID + " TEXT, " + COL_USERNAME + " TEXT NOT NULL, " + COL_ACCT + " TEXT NOT NULL, "
            + COL_DISPLAYED_NAME + " TEXT NOT NULL, " + COL_LOCKED + " INTEGER NOT NULL, "
            + COL_FOLLOWERS_COUNT + " INTEGER NOT NULL, " + COL_FOLLOWING_COUNT + " INTEGER NOT NULL, " + COL_STATUSES_COUNT + " INTEGER NOT NULL, "
            + COL_NOTE + " TEXT NOT NULL, " + COL_URL + " TEXT NOT NULL, "
            + COL_AVATAR + " TEXT NOT NULL, " + COL_AVATAR_STATIC + " TEXT NOT NULL, "
            + COL_HEADER + " TEXT NOT NULL, " + COL_HEADER_STATIC + " TEXT NOT NULL, "
            + COL_EMOJIS + " TEXT, "
            + COL_SOCIAL + " TEXT, "
            + COL_IS_MODERATOR + " INTEGER  DEFAULT 0, "
            + COL_IS_ADMIN + " INTEGER  DEFAULT 0, "
            + COL_CLIENT_ID + " TEXT, " + COL_CLIENT_SECRET + " TEXT, " + COL_REFRESH_TOKEN + " TEXT,"
            + COL_UPDATED_AT + " TEXT, "
            + COL_PRIVACY + " TEXT, "
            + COL_SENSITIVE + " INTEGER DEFAULT 0, "
            + COL_INSTANCE + " TEXT NOT NULL, " + COL_OAUTHTOKEN + " TEXT NOT NULL, " + COL_CREATED_AT + " TEXT NOT NULL)";
    private static final String CREATE_TABLE_STATUSES_STORED = "CREATE TABLE " + TABLE_STATUSES_STORED + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_USER_ID + " TEXT NOT NULL, " + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_STATUS_SERIALIZED + " TEXT NOT NULL, " + COL_STATUS_REPLY_SERIALIZED + " TEXT, " + COL_DATE_CREATION + " TEXT NOT NULL, "
            + COL_IS_SCHEDULED + " INTEGER NOT NULL, " + COL_DATE_SCHEDULED + " TEXT, "
            + COL_SENT + " INTEGER NOT NULL, " + COL_DATE_SENT + " TEXT)";
    private static final String CREATE_TABLE_BOOST_SCHEDULE = "CREATE TABLE " + TABLE_BOOST_SCHEDULE + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_USER_ID + " TEXT NOT NULL, " + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_STATUS_SERIALIZED + " TEXT NOT NULL, " + COL_DATE_SCHEDULED + " TEXT, "
            + COL_IS_SCHEDULED + " INTEGER NOT NULL, " + COL_SENT + " INTEGER NOT NULL, " + COL_DATE_SENT + " TEXT)";
    private static final String CREATE_TABLE_TRACKING_BLOCK = "CREATE TABLE " + TABLE_TRACKING_BLOCK + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_DOMAIN + " TEXT NOT NULL)";
    private static final String CREATE_TABLE_TIMELINES = "CREATE TABLE IF NOT EXISTS " + TABLE_TIMELINES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_POSITION + " INTEGER NOT NULL, "
            + COL_USER_ID + " TEXT NOT NULL, " + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_TYPE + " TEXT NOT NULL, "
            + COL_REMOTE_INSTANCE + " TEXT, "
            + COL_TAG_TIMELINE + " TEXT, "
            + COL_DISPLAYED + " INTEGER NOT NULL, "
            + COL_LIST_TIMELINE + " TEXT)";
    private static final String CREATE_TABLE_TIMELINE_CACHE = "CREATE TABLE "
            + TABLE_TIMELINE_CACHE + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_STATUS_ID + " TEXT NOT NULL, "
            + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_USER_ID + " TEXT NOT NULL, "
            + COL_CACHE + " TEXT NOT NULL, "
            + COL_DATE + " TEXT NOT NULL)";
    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE "
            + TABLE_NOTIFICATION_CACHE + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOTIFICATION_ID + " TEXT NOT NULL, "
            + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_USER_ID + " TEXT NOT NULL, "
            + COL_ACCOUNT + " TEXT NOT NULL, "
            + COL_TYPE + " TEXT NOT NULL, "
            + COL_STATUS_ID + " TEXT, "
            + COL_IN_REPLY_TO_ID + " TEXT, "
            + COL_STATUS_ID_CACHE + " INTEGER, "
            + COL_CREATED_AT + " TEXT NOT NULL)";
    private static final String CREATE_TABLE_MAIN_MENU_ITEMS = "CREATE TABLE "
            + TABLE_MAIN_MENU_ITEMS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_USER_ID + " TEXT NOT NULL, "
            + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_NAV_NEWS + " INTEGER  DEFAULT 1, "
            + COL_NAV_LIST + " INTEGER  DEFAULT 1, "
            + COL_NAV_SCHEDULED + " INTEGER  DEFAULT 1, "
            + COL_NAV_ARCHIVE + " INTEGER  DEFAULT 1, "
            + COL_NAV_ARCHIVE_NOTIFICATIONS + " INTEGER  DEFAULT 1, "
            + COL_NAV_PEERTUBE + " INTEGER  DEFAULT 1, "
            + COL_NAV_FILTERS + " INTEGER  DEFAULT 1, "
            + COL_NAV_HOW_TO_FOLLOW + " INTEGER  DEFAULT 1, "
            + COL_NAV_ADMINISTRATION + " INTEGER  DEFAULT 1, "
            + COL_NAV_BLOCKED + " INTEGER  DEFAULT 1, "
            + COL_NAV_MUTED + " INTEGER  DEFAULT 1, "
            + COL_NAV_BLOCKED_DOMAINS + " INTEGER  DEFAULT 1, "
            + COL_NAV_TRENDS + " INTEGER  DEFAULT 1, "
            + COL_NAV_HOWTO + " INTEGER  DEFAULT 1)";
    private static final String CREATE_TABLE_USER_NOTES = "CREATE TABLE "
            + TABLE_USER_NOTES + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ACCT + " TEXT NOT NULL, "
            + COL_NOTE + " TEXT, "
            + COL_DATE_CREATION + " TEXT NOT NULL)";
    public static SQLiteDatabase db;
    private static Sqlite sInstance;
    private final String CREATE_TABLE_CUSTOM_EMOJI = "CREATE TABLE " + TABLE_CUSTOM_EMOJI + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_SHORTCODE + " TEXT NOT NULL, " + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_URL + " TEXT NOT NULL, " + COL_URL_STATIC + " TEXT NOT NULL, " + COL_DATE_CREATION + " TEXT NOT NULL)";
    private final String CREATE_TABLE_SEARCH = "CREATE TABLE " + TABLE_SEARCH + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_KEYWORDS + " TEXT NOT NULL, " + COL_USER_ID + " TEXT NOT NULL, "
            + COL_ANY + " TEXT, " + COL_ALL + " TEXT, " + COL_NONE + " TEXT, " + COL_NAME + " TEXT, "
            + COL_IS_ART + " INTEGER  DEFAULT 0, " + COL_IS_NSFW + " INTEGER  DEFAULT 0, "
            + COL_DATE_CREATION + " TEXT NOT NULL)";
    private final String CREATE_TABLE_TEMP_MUTE = "CREATE TABLE " + TABLE_TEMP_MUTE + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ACCT + " TEXT NOT NULL, " + COL_INSTANCE + " TEXT NOT NULL, " + COL_TARGETED_USER_ID + " TEXT NOT NULL, " + COL_DATE_CREATION + " TEXT NOT NULL, " + COL_DATE_END + " TEXT NOT NULL)";
    private final String CREATE_TABLE_STATUSES_CACHE = "CREATE TABLE " + TABLE_STATUSES_CACHE + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CACHED_ACTION + " INTEGER NOT NULL, " + COL_INSTANCE + " TEXT NOT NULL, " + COL_USER_ID + " NOT NULL, " + COL_DATE_BACKUP + " TEXT NOT NULL, "
            + COL_STATUS_ID + " TEXT NOT NULL, " + COL_URI + " TEXT NOT NULL, " + COL_URL + " TEXT NOT NULL, "
            + COL_ACCOUNT + " TEXT NOT NULL, " + COL_IN_REPLY_TO_ID + " TEXT, " + COL_IN_REPLY_TO_ACCOUNT_ID + " TEXT,"
            + COL_REBLOG + " TEXT, " + COL_CONTENT + " TEXT NOT NULL, " + COL_CREATED_AT + " TEXT NOT NULL, "
            + COL_EMOJIS + " TEXT, " + COL_REBLOGS_COUNT + " INTEGER NOT NULL, " + COL_FAVOURITES_COUNT + " INTEGER NOT NULL, "
            + COL_REBLOGGED + " INTEGER, " + COL_FAVOURITED + " INTEGER, " + COL_MUTED + " INTEGER, " + COL_SENSITIVE + " INTEGER, "
            + COL_SPOILER_TEXT + " TEXT, " + COL_VISIBILITY + " TEXT NOT NULL, " + COL_MEDIA_ATTACHMENTS + " TEXT," + COL_CARD + " TEXT,"
            + COL_MENTIONS + " TEXT, " + COL_POLL + " TEXT, " + COL_TAGS + " TEXT, " + COL_APPLICATION + " TEXT,"
            + COL_LANGUAGE + " TEXT," + COL_PINNED + " INTEGER)";
    private final String CREATE_UNIQUE_CACHE_INDEX = "CREATE UNIQUE INDEX instance_statusid on "
            + TABLE_STATUSES_CACHE + "(" + COL_INSTANCE + "," + COL_STATUS_ID + "," + COL_CACHED_ACTION + ")";
    private final String CREATE_TABLE_INSTANCES = "CREATE TABLE " + TABLE_INSTANCES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_INSTANCE + " TEXT NOT NULL, " + COL_USER_ID + " TEXT NOT NULL, " + COL_INSTANCE_TYPE + " TEXT, " + COL_TAGS + " TEXT, " + COL_FILTERED_WITH + " TEXT, " + COL_DATE_CREATION + " TEXT NOT NULL)";
    private final String CREATE_TABLE_PEERTUBE_FAVOURITES = "CREATE TABLE "
            + TABLE_PEERTUBE_FAVOURITES + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_UUID + " TEXT NOT NULL, "
            + COL_INSTANCE + " TEXT NOT NULL, "
            + COL_CACHE + " TEXT NOT NULL, "
            + COL_DATE + " TEXT NOT NULL)";
    private final String CREATE_TABLE_CACHE_TAGS = "CREATE TABLE "
            + TABLE_CACHE_TAGS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TAGS + " TEXT NOT NULL)";

    public Sqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public static synchronized Sqlite getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        if (sInstance == null) {
            sInstance = new Sqlite(context, name, factory, version);
        }
        return sInstance;
    }

    public static void importDB(Activity activity, String backupDBPath) {
        try {
            db.close();
            File dbDest = activity.getDatabasePath(DB_NAME);
            File dbSource = new File(backupDBPath);
            FileChannel src = new FileInputStream(dbSource).getChannel();
            FileChannel dst = new FileOutputStream(dbDest).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Helper.logoutCurrentUser(activity);
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(activity, activity.getString(R.string.data_import_error_simple), Toast.LENGTH_LONG).show();
        }
    }

    public static void exportDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
                String backupDBPath = "Fedilab_export_" + timeStamp + ".fedilab";
                File dbSource = context.getDatabasePath(DB_NAME);
                File dbDest = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(dbSource).getChannel();
                FileChannel dst = new FileOutputStream(dbDest).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                final Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(dbDest);
                intent.setDataAndType(uri, "*/*");
                SQLiteDatabase db = Sqlite.getInstance(context.getApplicationContext(), Sqlite.DB_NAME, null, Sqlite.DB_VERSION).open();
                SharedPreferences sharedpreferences = context.getSharedPreferences(Helper.APP_PREFS, MODE_PRIVATE);
                String userId = sharedpreferences.getString(Helper.PREF_KEY_ID, null);
                String instance = sharedpreferences.getString(Helper.PREF_INSTANCE, Helper.getLiveInstance(context));
                Account account = new AccountDAO(context, db).getUniqAccount(userId, instance);
                Helper.notify_user(context, account, intent, BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher_bubbles), Helper.NotifType.STORE, context.getString(R.string.save_over), context.getString(R.string.download_from, backupDBPath));
                Toasty.success(context, context.getString(R.string.data_base_exported), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(context, context.getString(R.string.data_export_error_simple), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER_ACCOUNT);
        db.execSQL(CREATE_TABLE_STATUSES_STORED);
        db.execSQL(CREATE_TABLE_CUSTOM_EMOJI);
        db.execSQL(CREATE_TABLE_SEARCH);
        db.execSQL(CREATE_TABLE_TEMP_MUTE);
        db.execSQL(CREATE_TABLE_STATUSES_CACHE);
        db.execSQL(CREATE_UNIQUE_CACHE_INDEX);
        db.execSQL(CREATE_TABLE_INSTANCES);
        db.execSQL(CREATE_TABLE_PEERTUBE_FAVOURITES);
        db.execSQL(CREATE_TABLE_CACHE_TAGS);
        db.execSQL(CREATE_TABLE_BOOST_SCHEDULE);
        db.execSQL(CREATE_TABLE_TRACKING_BLOCK);
        db.execSQL(CREATE_TABLE_TIMELINES);
        db.execSQL(CREATE_TABLE_TIMELINE_CACHE);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS);
        db.execSQL(CREATE_TABLE_MAIN_MENU_ITEMS);
        db.execSQL(CREATE_TABLE_USER_NOTES);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(CREATE_TABLE_STATUSES_STORED);
            case 2:
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUSES_STORED);
                db.execSQL(CREATE_TABLE_STATUSES_STORED);
            case 3:
                db.execSQL(CREATE_TABLE_CUSTOM_EMOJI);
            case 4:
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOM_EMOJI + " ("
                        + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_SHORTCODE + " TEXT NOT NULL, " + COL_INSTANCE + " TEXT NOT NULL, "
                        + COL_URL + " TEXT NOT NULL, " + COL_URL_STATIC + " TEXT NOT NULL, " + COL_DATE_CREATION + " TEXT NOT NULL)");
            case 5:
                db.execSQL("delete from " + TABLE_CUSTOM_EMOJI); //Reset table due to bugs
            case 6:
                db.execSQL(CREATE_TABLE_SEARCH);
            case 7:
                db.execSQL(CREATE_TABLE_TEMP_MUTE);
            case 8:
                db.execSQL(CREATE_TABLE_STATUSES_CACHE);
            case 9:
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUSES_CACHE);
                db.execSQL(CREATE_TABLE_STATUSES_CACHE);
            case 10:
                //Table cache is deleted to avoid error during migration
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUSES_CACHE);
                db.execSQL(CREATE_TABLE_STATUSES_CACHE);
                db.execSQL(CREATE_UNIQUE_CACHE_INDEX);
            case 11:
                db.execSQL(CREATE_TABLE_INSTANCES);
            case 12:
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_EMOJIS + " TEXT");
            case 13:
                if (oldVersion > 11)
                    db.execSQL("ALTER TABLE " + TABLE_INSTANCES + " ADD COLUMN " + COL_INSTANCE_TYPE + " TEXT");
            case 14:
                db.execSQL(CREATE_TABLE_PEERTUBE_FAVOURITES);
            case 15:
                if (oldVersion > 8)
                    db.execSQL("ALTER TABLE " + TABLE_STATUSES_CACHE + " ADD COLUMN " + COL_CARD + " TEXT");
            case 16:
            case 17:
                db.execSQL("DROP TABLE IF EXISTS '" + TABLE_TIMELINE_CACHE + "'");
                db.execSQL(CREATE_TABLE_CACHE_TAGS);
            case 18:
                db.execSQL(CREATE_TABLE_BOOST_SCHEDULE);
            case 19:
                if (oldVersion > 6) {
                    db.execSQL("ALTER TABLE " + TABLE_SEARCH + " ADD COLUMN " + COL_IS_ART + " INTEGER  DEFAULT 0");
                    db.execSQL("ALTER TABLE " + TABLE_SEARCH + " ADD COLUMN " + COL_IS_NSFW + " INTEGER  DEFAULT 0");
                }
            case 20:
                if (oldVersion > 6) {
                    db.execSQL("ALTER TABLE " + TABLE_SEARCH + " ADD COLUMN " + COL_ANY + " TEXT");
                    db.execSQL("ALTER TABLE " + TABLE_SEARCH + " ADD COLUMN " + COL_ALL + " TEXT");
                    db.execSQL("ALTER TABLE " + TABLE_SEARCH + " ADD COLUMN " + COL_NONE + " TEXT");
                }
            case 21:
                if (oldVersion > 6) {
                    db.execSQL("ALTER TABLE " + TABLE_SEARCH + " ADD COLUMN " + COL_NAME + " TEXT");
                }
            case 22:
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_SOCIAL + " TEXT");
            case 23:
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_CLIENT_ID + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_CLIENT_SECRET + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_REFRESH_TOKEN + " TEXT");
            case 24:
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_IS_MODERATOR + " INTEGER  DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_IS_ADMIN + " INTEGER  DEFAULT 0");
            case 25:
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_UPDATED_AT + " TEXT");
            case 26:
                db.execSQL(CREATE_TABLE_TRACKING_BLOCK);
            case 27:
                db.execSQL(CREATE_TABLE_TIMELINES);
            case 28:
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_PRIVACY + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT + " ADD COLUMN " + COL_SENSITIVE + " INTEGER DEFAULT 0");
            case 29:
                db.execSQL(CREATE_TABLE_TIMELINE_CACHE);
            case 30:
                if (oldVersion > 11) {
                    db.execSQL("ALTER TABLE " + TABLE_INSTANCES + " ADD COLUMN " + COL_TAGS + " TEXT");
                    db.execSQL("ALTER TABLE " + TABLE_INSTANCES + " ADD COLUMN " + COL_FILTERED_WITH + " TEXT");
                }
            case 31:
                String CREATE_TABLE_USER_ACCOUNT_TEMP = "CREATE TABLE " + TABLE_USER_ACCOUNT_TEMP + " ("
                        + COL_USER_ID + " TEXT, " + COL_USERNAME + " TEXT NOT NULL, " + COL_ACCT + " TEXT NOT NULL, "
                        + COL_DISPLAYED_NAME + " TEXT NOT NULL, " + COL_LOCKED + " INTEGER NOT NULL, "
                        + COL_FOLLOWERS_COUNT + " INTEGER NOT NULL, " + COL_FOLLOWING_COUNT + " INTEGER NOT NULL, " + COL_STATUSES_COUNT + " INTEGER NOT NULL, "
                        + COL_NOTE + " TEXT NOT NULL, " + COL_URL + " TEXT NOT NULL, "
                        + COL_AVATAR + " TEXT NOT NULL, " + COL_AVATAR_STATIC + " TEXT NOT NULL, "
                        + COL_HEADER + " TEXT NOT NULL, " + COL_HEADER_STATIC + " TEXT NOT NULL, "
                        + COL_EMOJIS + " TEXT, "
                        + COL_SOCIAL + " TEXT, "
                        + COL_IS_MODERATOR + " INTEGER  DEFAULT 0, "
                        + COL_IS_ADMIN + " INTEGER  DEFAULT 0, "
                        + COL_CLIENT_ID + " TEXT, " + COL_CLIENT_SECRET + " TEXT, " + COL_REFRESH_TOKEN + " TEXT,"
                        + COL_UPDATED_AT + " TEXT, "
                        + COL_PRIVACY + " TEXT, "
                        + COL_SENSITIVE + " INTEGER DEFAULT 0, "
                        + COL_INSTANCE + " TEXT NOT NULL, " + COL_OAUTHTOKEN + " TEXT NOT NULL, " + COL_CREATED_AT + " TEXT NOT NULL)";
                db.execSQL(CREATE_TABLE_USER_ACCOUNT_TEMP);

                String insert = "INSERT INTO " + TABLE_USER_ACCOUNT_TEMP + " ("
                        + COL_USER_ID + ", " + COL_USERNAME + ", " + COL_ACCT + ", " + COL_DISPLAYED_NAME + ", " + COL_LOCKED + ", " + COL_FOLLOWERS_COUNT + ", "
                        + COL_FOLLOWING_COUNT + ", " + COL_STATUSES_COUNT + ", " + COL_NOTE + ", " + COL_URL + ", " + COL_AVATAR + ", " + COL_AVATAR_STATIC + ", "
                        + COL_HEADER + ", " + COL_HEADER_STATIC + ", " + COL_EMOJIS + ", " + COL_SOCIAL + ", " + COL_IS_MODERATOR + ", " + COL_IS_ADMIN + ", "
                        + COL_CLIENT_ID + ", " + COL_CLIENT_SECRET + ", " + COL_REFRESH_TOKEN + ", " + COL_UPDATED_AT + ", " + COL_PRIVACY + ", " + COL_SENSITIVE + ", "
                        + COL_INSTANCE + ", " + COL_OAUTHTOKEN + ", " + COL_CREATED_AT + ") "
                        + " SELECT " + COL_USER_ID + ", " + COL_USERNAME + ", " + COL_ACCT + ", " + COL_DISPLAYED_NAME + ", " + COL_LOCKED + ", " + COL_FOLLOWERS_COUNT + ", "
                        + COL_FOLLOWING_COUNT + ", " + COL_STATUSES_COUNT + ", " + COL_NOTE + ", " + COL_URL + ", " + COL_AVATAR + ", " + COL_AVATAR_STATIC + ", "
                        + COL_HEADER + ", " + COL_HEADER_STATIC + ", " + COL_EMOJIS + ", " + COL_SOCIAL + ", " + COL_IS_MODERATOR + ", " + COL_IS_ADMIN + ", "
                        + COL_CLIENT_ID + ", " + COL_CLIENT_SECRET + ", " + COL_REFRESH_TOKEN + ", " + COL_UPDATED_AT + ", " + COL_PRIVACY + ", " + COL_SENSITIVE + ", "
                        + COL_INSTANCE + ", " + COL_OAUTHTOKEN + ", " + COL_CREATED_AT + " FROM " + TABLE_USER_ACCOUNT;
                db.execSQL(insert);
                db.execSQL("DROP TABLE " + TABLE_USER_ACCOUNT);
                db.execSQL("ALTER TABLE " + TABLE_USER_ACCOUNT_TEMP + " RENAME TO " + TABLE_USER_ACCOUNT);

            case 32:
                db.execSQL(CREATE_TABLE_NOTIFICATIONS);
            case 33:
                db.execSQL(CREATE_TABLE_MAIN_MENU_ITEMS);
            case 34:
                db.execSQL("DROP INDEX IF EXISTS instance_statusid");
                db.execSQL("DELETE FROM " + TABLE_STATUSES_CACHE);
                db.execSQL("DELETE FROM " + TABLE_NOTIFICATION_CACHE);
                db.execSQL(CREATE_UNIQUE_CACHE_INDEX);
            case 35:
                db.execSQL(CREATE_TABLE_USER_NOTES);
            case 36:
                if (oldVersion > 33) {
                    db.execSQL("ALTER TABLE " + TABLE_MAIN_MENU_ITEMS + " ADD COLUMN " + COL_NAV_TRENDS + " INTEGER  DEFAULT 1");
                }
            case 37:
                if (oldVersion > 8)
                    db.execSQL("ALTER TABLE " + TABLE_STATUSES_CACHE + " ADD COLUMN " + COL_POLL + " TEXT");
            default:
                break;
        }
    }

    public SQLiteDatabase open() {
        //opened with write access
        db = getWritableDatabase();
        return db;
    }

    public void close() {
        //Close the db
        if (db != null && db.isOpen()) {
            db.close();
        }
    }


}
