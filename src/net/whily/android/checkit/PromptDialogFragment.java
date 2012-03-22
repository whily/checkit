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
import android.widget.EditText;

public final class PromptDialogFragment extends CustomDialogFragment {
  private EditText et;

  public static PromptDialogFragment newInstance(int titleId, String text) {
    PromptDialogFragment pdf = new PromptDialogFragment();
    pdf.titleId = titleId;
    Bundle bundle = new Bundle();
    bundle.putString("text", text);
    pdf.setArguments(bundle);
    return pdf;
  }

  @Override
  public CharSequence getMessage() {
    return et.getText();
  }

  @Override
  public View onInflateDialog(Bundle icicle) {
    LayoutInflater li = LayoutInflater.from(getActivity());
    View v = li.inflate(R.layout.prompt, null);
    et = (EditText)v.findViewById(R.id.prompt_entry);
    if (icicle != null) {
      et.setText(icicle.getCharSequence("input"));
    } else {
      et.setText(getArguments().getString("text"));
    }
    et.setSelectAllOnFocus(true);

    return v;
  }

  @Override
  public void onSaveInstanceState(Bundle icicle) {
    icicle.putCharSequence("input", et.getText());
    super.onPause();
  }
}
