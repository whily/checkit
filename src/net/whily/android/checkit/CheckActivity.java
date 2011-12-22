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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.CheckedTextView;

public class CheckActivity extends ListActivity {
  public static final int UNCHECK_ALL_ID = Menu.FIRST + 1;  
  public static final int SETTINGS_ID = Menu.FIRST + 2;
  public static final int ABOUT_ID = Menu.FIRST + 3;
  
  private ArrayAdapter<String> arrayAdapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String[] str = getResources().getStringArray(R.array.travel_list);
    arrayAdapter = new 
      ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, str);
    setListAdapter(arrayAdapter);
    setContentView(R.layout.main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, UNCHECK_ALL_ID, 0, R.string.uncheck_all)
      .setIcon(android.R.drawable.ic_menu_close_clear_cancel);      
    menu.add(0, SETTINGS_ID, 0, R.string.settings)
      .setIcon(android.R.drawable.ic_menu_preferences);
    menu.add(0, ABOUT_ID, 1, R.string.about)
      .setIcon(android.R.drawable.ic_menu_info_details);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case UNCHECK_ALL_ID:
        arrayAdapter.clear();
        return true;
        
      case SETTINGS_ID:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;

      case ABOUT_ID:
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
}
