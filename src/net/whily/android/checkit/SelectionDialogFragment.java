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
import android.widget.RadioGroup;

public final class SelectionDialogFragment extends CustomDialogFragment {
  private RadioGroup group;

  public static SelectionDialogFragment newInstance(int titleId) {
    SelectionDialogFragment sdf = new SelectionDialogFragment();
    sdf.titleId = titleId;
    return sdf;
  }

  @Override
  public CharSequence getMessage() {
    String result ="";
    switch (group.getCheckedRadioButtonId()) {
      case R.id.travel: 
        result = "travel";
    }
    return result;
  }

  @Override
  public View onInflateDialog(Bundle icicle) {
    LayoutInflater li = LayoutInflater.from(getActivity());
    View v = li.inflate(R.layout.selection, null);
    group = (RadioGroup)v.findViewById(R.id.selection_list);
    if (icicle != null) {
      group.check(icicle.getInt("selection"));
    } else {
      group.check(R.id.travel);
    }

    return v;
  }

  @Override
  public void onSaveInstanceState(Bundle icicle) {
    icicle.putInt("selection", group.getCheckedRadioButtonId());
    super.onPause();
  }
}
