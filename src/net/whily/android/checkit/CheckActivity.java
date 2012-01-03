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

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.CheckedTextView;

public class CheckActivity extends ListActivity {
  // Each string is prepended one character: 1/0 denote the item is 
  // checked/unchecked.
  private String[] items;
  private Button addButton;
  private EditText entry;
  private ListView list;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.check);

    items = getResources().getStringArray(R.array.travel_list);
    for (int i = 0; i < items.length; ++i) {
      items[i] = "0" + items[i];
    }
    setListAdapter(new CheckAdapter());
    list = (ListView)getListView();
    registerForContextMenu(list);

    addButton = (Button)findViewById(R.id.add);
    addButton.setEnabled(false);

    entry = (EditText)findViewById(R.id.entry);
    entry.setOnKeyListener(new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
          addButton.setEnabled(entry.getText().length() > 0);
          return false;
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
        for (int i = 0; i < items.length; ++i) {
          items[i] = "0" + items[i].substring(1);
        }
        // TODO: currently the checked items are not unchecked until scrolling.
        list.invalidate();
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
    // Toggle 0/1.
    if (items[position].startsWith("0")) {
      items[position] = "1" + items[position].substring(1);
    } else {
      items[position] = "0" + items[position].substring(1);
    }
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
        editItem(info.id);
        return true;
      case R.id.delete:
        deleteItem(info.id);
        return true;
      default:
        return super.onContextItemSelected(item);
    }
  }

  private void editItem(long id) {
  }

  private void deleteItem(long id) {
  }

  private void onAddButtonClick(View v) {
    // TODO
  }

  class CheckAdapter extends ArrayAdapter<String> {
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
      textView.setText(items[position].substring(1));
      textView.setChecked(items[position].startsWith("1"));

      return textView;
    }
  }
}
