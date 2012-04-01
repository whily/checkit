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

import android.content.Context;
import android.widget.Toast;

public class Util {
  public static void toast (Context context, String text) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
  }
}
