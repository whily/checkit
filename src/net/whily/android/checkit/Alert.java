/**
 * Show simple alert dialogs which only accept OK/Cancel like input.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011 Yujian Zhang
 */

package net.whily.android.checkit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public final class Alert {
  /**
   * Show alert dialog without title.
   */
  public static void show(Context context, int messageId, int positiveTextId, 
                          int negativeTextId,
                          DialogInterface.OnClickListener positiveListener) {
    showTitle(context, -1, messageId, positiveTextId, negativeTextId, 
              positiveListener);
  }

  /**
   * Show alert dialog with title but without icon. If titleId < 0,
   * title itself is not shown.
   */
  public static void showTitle(Context context, int titleId, int messageId, 
                               int positiveTextId, int negativeTextId,
                               DialogInterface.OnClickListener positiveListener) {
    showTitleIcon(context, -1, titleId, messageId, positiveTextId, 
                  negativeTextId, positiveListener);
  }

  /**
   * Show alert dialog with title and icon. If iconId < 0, icon is not
   * show. If titleId < 0, title is not shown.
   */
  public static void showTitleIcon(Context context, int iconId, int titleId, 
                                   int messageId, int positiveTextId, 
                                   int negativeTextId,
                                   DialogInterface.OnClickListener positiveListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(messageId)
      .setCancelable(true)
      .setPositiveButton(positiveTextId, positiveListener)
      .setNegativeButton(negativeTextId, null);
    if (titleId >= 0) {
      builder.setTitle(titleId);
      if (iconId >= 0) {
        builder.setIcon(iconId);
      }
    }
    builder.create().show();
  }
}
