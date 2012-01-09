/**
 * Interface OnDialogDoneListener. Based on Pro Android 3.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011 Yujian Zhang
 */

package net.whily.android.checkit;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class PromptDialogFragment extends DialogFragment
  implements View.OnClickListener {
  private EditText et;

  public static PromptDialogFragment newInstance(String text) {
    PromptDialogFragment pdf = new PromptDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putString("text", text);
    pdf.setArguments(bundle);
    return pdf;
  }

  @Override
  public void onAttach(Activity act) {
    // If attached activity has not implemented OnDialogDoneListener,
    // the following line will throw ClassCastException.
    OnDialogDoneListener test = (OnDialogDoneListener)act;
    super.onAttach(act);
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.setCancelable(true);
    int style = DialogFragment.STYLE_NORMAL; 
    int theme = 0;
    setStyle(style, theme);
  }

  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle icicle) {
    getDialog().setTitle(getResources().getString(R.string.edit));

    View v = inflater.inflate(R.layout.prompt, container, false);
    Button cancelButton = (Button)v.findViewById(R.id.button_cancel);
    cancelButton.setOnClickListener(this);
    Button OKButton = (Button)v.findViewById(R.id.button_ok);
    OKButton.setOnClickListener(this);
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

  public void onClick(View v) {
    OnDialogDoneListener act = (OnDialogDoneListener)getActivity();
    if (v.getId() == R.id.button_ok) {
      act.onDialogDone(this.getTag(), false, et.getText());
      dismiss();
      return;
    }
    if (v.getId() == R.id.button_cancel) {
      act.onDialogDone(this.getTag(), true, null);
      dismiss();
      return;
    }
  }
}
