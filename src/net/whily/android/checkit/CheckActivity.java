/**
 * Check activity for CheckIt.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011 Yujian Zhang
 */

package net.whily.android.checkit;

import java.util.*;
import android.app.ListActivity;
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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CheckActivity extends ListActivity {
  private List<CheckedItem> items;
  private Button addButton;
  private EditText entry;
  private ListView list;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.check);

    String[] itemStrings = getResources().getStringArray(R.array.travel_list);
    items = new ArrayList<CheckedItem>();
    for (String itemString : itemStrings) {
      items.add(new CheckedItem(itemString));
    }
    setListAdapter(new CheckAdapter());
    list = (ListView)getListView();
    registerForContextMenu(list);

    addButton = (Button)findViewById(R.id.add);
    addButton.setEnabled(false);

    entry = (EditText)findViewById(R.id.entry);
    entry.addTextChangedListener(new TextWatcher() {
         @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          addButton.setEnabled(entry.getText().length() > 0);    
        }        

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, R.id.uncheck_all, 0, R.string.uncheck_all)
      .setIcon(android.R.drawable.ic_menu_close_clear_cancel);      
    menu.add(0, R.id.settings, 0, R.string.settings)
      .setIcon(android.R.drawable.ic_menu_preferences);
    menu.add(0, R.id.about, 1, R.string.about)
      .setIcon(android.R.drawable.ic_menu_info_details);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.uncheck_all:
        for (int i = 0; i < items.size(); ++i) {
          items.set(i, new CheckedItem(items.get(i).getText()));
        }
        list.invalidateViews();
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
  protected void onListItemClick(ListView l, View v, int position, long id)
  {
    CheckedItem item = items.get(position);
    item.toggle();
    items.set(position, item);
    CheckedTextView textView = (CheckedTextView)v;
    textView.toggle();

    super.onListItemClick(l, v, position, id);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
                                  ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(Menu.NONE, R.id.edit, Menu.NONE, R.string.edit);
    menu.add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
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
        row = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
      }
      CheckedTextView textView = (CheckedTextView)row;
      textView.setText(items.get(position).getText());
      textView.setChecked(items.get(position).isChecked());

      return textView;
    }
  }

  class CheckedItem {
    private String text;
    private boolean checked = false;

    CheckedItem(String text) {
      this.text = text;
    }

    String getText() {
      return text;
    }

    boolean isChecked() {
      return checked;
    }

    void setChecked(boolean checked) {
      this.checked = checked;
    }

    void toggle() {
      checked = !checked;
    }
  }
}
