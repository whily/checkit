/**
 * Dialog fragment containing EditText. 
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public final class SelectionDialogFragment extends CustomDialogFragment {
  private RadioGroup group;
  private String[] selections;

  public static SelectionDialogFragment newInstance(int titleId, 
                                                    String[] selections) {
    SelectionDialogFragment sdf = new SelectionDialogFragment();
    sdf.titleId = titleId;
    sdf.selections = selections;
    return sdf;
  }

  @Override
  /**
   * Return string of corresponding id.
   */
  public CharSequence getMessage() {
    return "" + group.getCheckedRadioButtonId();
  }

  @Override
  public View onInflateDialog(Bundle icicle) {
    LayoutInflater li = LayoutInflater.from(getActivity());
    View v = li.inflate(R.layout.selection, null);
    group = (RadioGroup)v.findViewById(R.id.selection_list);

    RadioGroup.LayoutParams rg 
      = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                                    RadioGroup.LayoutParams.WRAP_CONTENT);
    for (int i = 0; i < selections.length; ++i) {
      RadioButton rb = new RadioButton(getActivity());
      rb.setId(i);
      rb.setText(selections[i]);
      //android:textAppearance="?android:attr/textAppearanceMedium" 
      group.addView(rb, rg);
    }

    if (icicle != null) {
      group.check(icicle.getInt("selection"));
    } else {
      group.check(0);
    }

    return v;
  }

  @Override
  public void onSaveInstanceState(Bundle icicle) {
    icicle.putInt("selection", group.getCheckedRadioButtonId());
    super.onPause();
  }
}
