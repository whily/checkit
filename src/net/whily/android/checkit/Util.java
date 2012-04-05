/**
 * Utilities.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.checkit;

import java.util.Calendar;
import android.content.Context;
import android.widget.Toast;

public class Util {
  public static void toast (Context context, String text) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
  }

  /**
   * Return current UTC time in HHHHMMDDMMSS.
   */
  public static String timeStamp() {
    Calendar now = Calendar.getInstance();
    return String.format("%04d%02d%02d%02d%02d",
                         now.get(Calendar.YEAR),
                         now.get(Calendar.MONTH) + 1,
                         now.get(Calendar.DAY_OF_MONTH),
                         now.get(Calendar.HOUR_OF_DAY),
                         now.get(Calendar.MINUTE));
  }
}
