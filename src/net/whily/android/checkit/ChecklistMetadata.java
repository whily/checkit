/**
 * Metadata for database.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2012 Yujian Zhang
 */

package net.whily.android.checkit;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ChecklistMetadata {
  public static final String AUTHORITY = "net.whily.provider.Checklist";
  public static final String DATABASE_NAME = "checklists.db";
  public static final int DATABASE_VERSION = 1;

  // This class cannot be instantiated
  private ChecklistMetadata() {}

  public static final class Checklists implements BaseColumns {

    // This class cannot be instantiated
    private Checklists() {}

    public static final String TABLE_NAME = "checklists";

    private static final String SCHEME = "content://";
    private static final String PATH_CHECKLISTS = "/checklists";
    private static final String PATH_CHECKLIST_ID = "/checklists/";
    public static final int CHECKLIST_ID_PATH_POSITION = 1;
    public static final Uri CONTENT_URI 
      =  Uri.parse(SCHEME + AUTHORITY + PATH_CHECKLISTS);
    public static final Uri CONTENT_ID_URI_BASE
      = Uri.parse(SCHEME + AUTHORITY + PATH_CHECKLIST_ID);
    public static final Uri CONTENT_ID_URI_PATTERN
      = Uri.parse(SCHEME + AUTHORITY + PATH_CHECKLIST_ID + "/#");
    public static final String CONTENT_TYPE 
      = "vnd.android.cursor.dir/vnd.net.whily.checklist";
    public static final String CONTENT_ITEM_TYPE 
      = "vnd.android.cursor.item/vnd.net.whily.checklist";

    public static final String DEFAULT_SORT_ORDER = "modified DESC";

    // String type.
    public static final String COLUMN_TITLE = "title";
    // String type.
    public static final String COLUMN_CONTENT = "content";
    // Integer from System.curentTimeMillis()
    public static final String COLUMN_MODIFIED = "modified";
  }
}
