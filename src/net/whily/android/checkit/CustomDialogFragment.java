/**
 * Dialog Fragment interacting with OnDialogDoneListener.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.checkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

public abstract class CustomDialogFragment extends DialogFragment
  implements DialogInterface.OnClickListener {
  protected int titleId;

  /** Return the message from user input. */
  public abstract CharSequence getMessage();

  /** Inflate the dialog. */
  public abstract View onInflateDialog(Bundle icicle);

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.setCancelable(true);
    int style = DialogFragment.STYLE_NORMAL; 
    int theme = 0;
    setStyle(style, theme);
  }

  public Dialog onCreateDialog(Bundle icicle) {
    View v = onInflateDialog(icicle);

    // Use AlertDialog instead of building a normal dialog since I
    // like the style of the former especially the TextView like
    // OK/Cancel button.
    return new AlertDialog.Builder(getActivity())
      .setTitle(titleId)
      .setView(v)
      .setPositiveButton(getString(R.string.ok), this)
      .setNegativeButton(getString(R.string.cancel), this)
      .create();
  }

  public void onClick(DialogInterface dialog, int which) {
    OnDialogDoneListener act = (OnDialogDoneListener)getActivity();
    boolean cancelled = (which == AlertDialog.BUTTON_NEGATIVE);
    act.onDialogDone(this.getTag(), cancelled, getMessage());
  }
}
