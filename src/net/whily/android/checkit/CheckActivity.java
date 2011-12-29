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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.CheckedTextView;

public class CheckActivity extends ListActivity {
  private ArrayAdapter<String> arrayAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.check);

    String[] str = getResources().getStringArray(R.array.travel_list);
    arrayAdapter = new 
      ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, str);
    ListView list = (ListView)getListView();
    setListAdapter(arrayAdapter);

    registerForContextMenu(list);
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
        arrayAdapter.clear();
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
    // FIXME: currently items are randomly selected/deselcted.
    CheckedTextView textView = (CheckedTextView)v;
    textView.setChecked(!textView.isChecked());
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
}
