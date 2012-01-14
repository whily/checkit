/**
 * Interface OnDialogDoneListener. Based on Pro Android 3.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.checkit;

/** 
 * Interface impelemneted by an activity for dialog reports what happended.
 */
public interface OnDialogDoneListener {
  public void onDialogDone(String tag, boolean cancelled, CharSequence message);
}
