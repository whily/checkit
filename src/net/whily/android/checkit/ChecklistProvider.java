/**
 * Database helper for CheckIt.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2012 Yujian Zhang
 */

package net.whily.android.checkit;

import java.util.*;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ChecklistProvider extends ContentProvider {
  private static final String TAG = "ChecklistProvider";

  private static HashMap<String, String> checklistProjectionMap;
  static {
    checklistProjectionMap = new HashMap<String, String>();
    checklistProjectionMap.put(ChecklistMetadata.Checklists._ID, 
                               ChecklistMetadata.Checklists._ID);
    checklistProjectionMap.put(ChecklistMetadata.Checklists.COLUMN_TITLE, 
                               ChecklistMetadata.Checklists.COLUMN_TITLE);
    checklistProjectionMap.put(ChecklistMetadata.Checklists.COLUMN_CONTENT,
                               ChecklistMetadata.Checklists.COLUMN_CONTENT);
    checklistProjectionMap.put(ChecklistMetadata.Checklists.COLUMN_MODIFIED,
                               ChecklistMetadata.Checklists.COLUMN_MODIFIED);
  }

  private static final UriMatcher uriMatcher;
  private static final int CHECKLIST_DIR_INDICATOR = 1;
  private static final int CHECKLIST_ITEM_INDICATOR = 2;
  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(ChecklistMetadata.AUTHORITY, "checklists", 
                      CHECKLIST_DIR_INDICATOR);
    uriMatcher.addURI(ChecklistMetadata.AUTHORITY, "checklists/#", 
                      CHECKLIST_ITEM_INDICATOR);
  }

  static class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_CHECKLISTS =
      "CREATE TABLE " + ChecklistMetadata.Checklists.TABLE_NAME
      + " (" + ChecklistMetadata.Checklists._ID + " INTEGER PRIMARY KEY, "
      + ChecklistMetadata.Checklists.COLUMN_TITLE + " TEXT NOT NULL, "
      + ChecklistMetadata.Checklists.COLUMN_CONTENT + " TEXT, "
      + ChecklistMetadata.Checklists.COLUMN_MODIFIED + " INTEGER "
      + ");";
    private static final String DATABASE_SCHEMA = CREATE_TABLE_CHECKLISTS;
  
    public DatabaseHelper(Context context) {
      super(context, ChecklistMetadata.DATABASE_NAME, null, 
            ChecklistMetadata.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_SCHEMA);
    }

  
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database. Existing content will be lost. ["
            + oldVersion + "]->[" + newVersion + "]");
      db.execSQL("DROP TABLE IF EXISTS " + ChecklistMetadata.Checklists.TABLE_NAME);
      onCreate(db);
    }
  }
  
  private DatabaseHelper openHelper;

  @Override
  public boolean onCreate() {
    openHelper = new DatabaseHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
    switch (uriMatcher.match(uri)) {
      case CHECKLIST_DIR_INDICATOR:
        qb.setTables(ChecklistMetadata.Checklists.TABLE_NAME);
        qb.setProjectionMap(checklistProjectionMap);
        break;

      case CHECKLIST_ITEM_INDICATOR:
        qb.setTables(ChecklistMetadata.Checklists.TABLE_NAME);
        qb.setProjectionMap(checklistProjectionMap);
        qb.appendWhere(ChecklistMetadata.Checklists._ID + "="
                       + uri.getPathSegments().get(ChecklistMetadata.Checklists.CHECKLIST_ID_PATH_POSITION));
        break;

      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    String orderBy = TextUtils.isEmpty(sortOrder)
      ? ChecklistMetadata.Checklists.DEFAULT_SORT_ORDER
      : sortOrder;

    SQLiteDatabase db = openHelper.getReadableDatabase();
    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, 
                        orderBy);
    c.setNotificationUri(getContext().getContentResolver(), uri);
    return c;
  }

  @Override
  public String getType(Uri uri) {
    switch (uriMatcher.match(uri)) {
      case CHECKLIST_DIR_INDICATOR:
        return ChecklistMetadata.Checklists.CONTENT_TYPE;
        
      case CHECKLIST_ITEM_INDICATOR:
        return ChecklistMetadata.Checklists.CONTENT_ITEM_TYPE;

      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues initialValues) {
    // Validates the incoming URI.
    if (uriMatcher.match(uri) != CHECKLIST_DIR_INDICATOR) {
      throw new IllegalArgumentException("Unknown URI " + uri);
    }

    ContentValues values = (initialValues == null)
      ? new ContentValues()
      : new ContentValues(initialValues);

    Long now = Long.valueOf(System.currentTimeMillis());

    if (values.containsKey(ChecklistMetadata.Checklists.COLUMN_MODIFIED) == false) {
      values.put(ChecklistMetadata.Checklists.COLUMN_MODIFIED, now);
    }

    if (values.containsKey(ChecklistMetadata.Checklists.COLUMN_TITLE) == false) {
      throw new SQLException("Failed to insert row since title is missing.");
    }

    if (values.containsKey(ChecklistMetadata.Checklists.COLUMN_CONTENT) == false) {
      throw new SQLException("Failed to insert row since content is missing.");
    }

    SQLiteDatabase db = openHelper.getWritableDatabase();

    long rowId = db.insert(ChecklistMetadata.Checklists.TABLE_NAME,
                           ChecklistMetadata.Checklists.COLUMN_CONTENT,
                           values);

    if (rowId > 0) {
      Uri insertedChecklistUri
        = ContentUris.withAppendedId(ChecklistMetadata.Checklists.CONTENT_ID_URI_BASE, 
                                     rowId);
      // Notifies observers registered against this provider that the data changed.
      getContext().getContentResolver().notifyChange(insertedChecklistUri, null);
      return insertedChecklistUri;
    }

    throw new SQLException("Failed to insert row into " + uri);
  }

  @Override
  public int delete(Uri uri, String where, String[] whereArgs) {
    SQLiteDatabase db = openHelper.getWritableDatabase();
    String finalWhere;
    int count;

    switch (uriMatcher.match(uri)) {
      case CHECKLIST_DIR_INDICATOR:
        count = db.delete(ChecklistMetadata.Checklists.TABLE_NAME,
                          where, whereArgs);
        break;

      case CHECKLIST_ITEM_INDICATOR:
        finalWhere = ChecklistMetadata.Checklists._ID + " = " 
          + uri.getPathSegments().get(ChecklistMetadata.Checklists.CHECKLIST_ID_PATH_POSITION)
          + ((where != null) ? " AND " + where : "");

        count = db.delete(ChecklistMetadata.Checklists.TABLE_NAME,
                          finalWhere, whereArgs);
        break;

      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
    SQLiteDatabase db = openHelper.getWritableDatabase();
    int count;
    String finalWhere;

    switch (uriMatcher.match(uri)) {
      case CHECKLIST_DIR_INDICATOR:
        count = db.update(ChecklistMetadata.Checklists.TABLE_NAME,
                          values, where, whereArgs);
        break;

      case CHECKLIST_ITEM_INDICATOR:
        finalWhere = ChecklistMetadata.Checklists._ID + " = " 
          + uri.getPathSegments().get(ChecklistMetadata.Checklists.CHECKLIST_ID_PATH_POSITION)
          + ((where != null) ? " AND " + where : "");
        count = db.update(ChecklistMetadata.Checklists.TABLE_NAME,
                          values, finalWhere, whereArgs);
        break;
        // If the incoming pattern is invalid, throws an exception.
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }
}
