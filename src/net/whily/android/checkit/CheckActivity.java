/**
 * Check activity for CheckIt.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.checkit;

import java.util.*;
import android.app.ActionBar;
import android.app.ListActivity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CheckActivity extends ListActivity 
  implements OnDialogDoneListener {
  public static final String EDIT_DIALOG_TAG = "EDIT_DIALOG_TAG";

  private ArrayList<CheckedItem> items;
  private Button addButton;
  private EditText entry;
  private ListView list;
  private RelativeLayout rl;
  private int editPosition; // Save the position of the item to be editted.

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.check);
    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState != null) {
      items = savedInstanceState.getParcelableArrayList("items");
    } else {
      String[] itemStrings = getResources().getStringArray(R.array.travel_list);
      items = new ArrayList<CheckedItem>();
      for (String itemString : itemStrings) {
        items.add(new CheckedItem(itemString));
      }
    }
    setListAdapter(new CheckAdapter());
    list = (ListView)getListView();
    registerForContextMenu(list);

    rl = (RelativeLayout)findViewById(R.id.add_entry_button);
    addButton = (Button)findViewById(R.id.add);

    entry = (EditText)findViewById(R.id.entry);
    addButton.setEnabled(entry.getText().length() > 0);    
    entry.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, 
                                  int count) {
          addButton.setEnabled(entry.getText().length() > 0);    
        }        

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, 
                                      int after) {
        }
        
        @Override
        public void afterTextChanged(Editable s) {
        }
      });
    entry.setOnEditorActionListener(new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (actionId == EditorInfo.IME_ACTION_GO) {
            onAddButtonClick(addButton);
          }
          return true;
        }
      });
    
    rl.setVisibility(View.GONE);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList("items", items);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.check_options, menu);
    CheckBox cb = (CheckBox)menu.findItem(R.id.add_switch).getActionView();
    cb.setText(getString(R.string.add));
    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener () {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, 
                                     boolean isChecked) {
          if (isChecked) {
            rl.setVisibility(View.VISIBLE);
            entry.requestFocus();
          }
          else {
            rl.setVisibility(View.GONE);
          }
        }
      });

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case android.R.id.home:
        // App icon in action bar clicked; go home.
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;

      case R.id.uncheck_all:
        Alert.show(this, R.string.uncheck_message, R.string.uncheck, 
                   R.string.cancel,
                   new DialogInterface.OnClickListener () {
                     public void onClick(DialogInterface dialog, int id) {
                       for (CheckedItem checkedItem : items) {
                         checkedItem.setChecked(false);
                       }
                       list.invalidateViews();
                     }
                   });
        return true;
        
      case R.id.settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;

      case R.id.about:
        startActivity(new Intent(this, AboutActivity.class));
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }
    
  //@Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    items.get(position).toggle();
    CheckedTextView textView = (CheckedTextView)v;
    textView.toggle();

    super.onListItemClick(l, v, position, id);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
                                  ContextMenuInfo menuInfo) {
    getMenuInflater().inflate(R.menu.check_list_context, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
    switch (item.getItemId()) {
      case R.id.edit:
        editItem(info.position);
        return true;
      case R.id.delete:
        deleteItem(info.position);
        return true;
      default:
        return super.onContextItemSelected(item);
    }
  }

  private void editItem(int position) {
    editPosition = position;
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    PromptDialogFragment pdf = 
      PromptDialogFragment.newInstance(items.get(position).getText());
    pdf.show(ft, EDIT_DIALOG_TAG);
  }

  public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
    if (!cancelled) {
      items.get(editPosition).setText(message.toString().trim());
      list.invalidateViews();
    }
  }

  private void deleteItem(int position) {
    items.remove(position);
    list.invalidateViews();
  }

  public void onAddButtonClick(View v) {
    // Check button status since this function is also called e.g. due
    // to IME action.
    if (addButton.isEnabled()) {
      items.add(new CheckedItem(entry.getText().toString().trim()));
      entry.setText("");
      list.invalidateViews();
      list.smoothScrollToPosition(items.size() - 1);
    }
  }

  class CheckAdapter extends ArrayAdapter<CheckedItem> {
    CheckAdapter() {
      super(CheckActivity.this, android.R.layout.simple_list_item_checked, items); 
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {
      View row = convertView;
      if (row == null) {
        LayoutInflater inflater = getLayoutInflater();
        row = inflater.inflate(android.R.layout.simple_list_item_checked, 
                               parent, false);
      }
      CheckedTextView textView = (CheckedTextView)row;
      textView.setText(items.get(position).getText());
      textView.setChecked(items.get(position).isChecked());

      return textView;
    }
  }
}
