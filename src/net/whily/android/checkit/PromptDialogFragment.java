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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PromptDialogFragment extends DialogFragment
  implements DialogInterface.OnClickListener {
  private EditText et;

  public static PromptDialogFragment newInstance(String text) {
    PromptDialogFragment pdf = new PromptDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putString("text", text);
    pdf.setArguments(bundle);
    return pdf;
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.setCancelable(true);
    int style = DialogFragment.STYLE_NORMAL; 
    int theme = 0;
    setStyle(style, theme);
  }

  public Dialog onCreateDialog(Bundle icicle) {
    LayoutInflater li = LayoutInflater.from(getActivity());
    View v = li.inflate(R.layout.prompt, null);
    et = (EditText)v.findViewById(R.id.prompt_entry);
    if (icicle != null) {
      et.setText(icicle.getCharSequence("input"));
    } else {
      et.setText(getArguments().getString("text"));
    }
    et.setSelectAllOnFocus(true);

    // Use AlertDialog instead of building a normal dialog since I
    // like the style of the former especially the TextView like
    // OK/Cancel button.
    return new AlertDialog.Builder(getActivity())
      .setTitle(getString(R.string.edit))
      .setView(v)
      .setPositiveButton(getString(R.string.ok), this)
      .setNegativeButton(getString(R.string.cancel), this)
      .create();
  }

  @Override
  public void onSaveInstanceState(Bundle icicle) {
    icicle.putCharSequence("input", et.getText());
    super.onPause();
  }

  public void onClick(DialogInterface dialog, int which) {
    OnDialogDoneListener act = (OnDialogDoneListener)getActivity();
    boolean cancelled = (which == AlertDialog.BUTTON_NEGATIVE);
    act.onDialogDone(this.getTag(), cancelled, et.getText());
  }
}
