/**
 * List activity for CheckIt.
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
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends ListActivity {
  private ArrayList<String> lists = new ArrayList<String>(Arrays.asList("first"));

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home);

    setListAdapter(new ListAdapter());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.home_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.new_list:
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
    startActivity(new Intent(this, CheckActivity.class));    
    super.onListItemClick(l, v, position, id);
  }

  class ListAdapter extends ArrayAdapter<String> {
    ListAdapter() {
      super(HomeActivity.this, android.R.layout.simple_list_item_2, lists);
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {
      View row = convertView;
      if (row == null) {
        LayoutInflater inflater = getLayoutInflater();
        row = inflater.inflate(android.R.layout.simple_list_item_2, 
                               parent, false);
      }
      TextView textView1 = (TextView)row.findViewById(android.R.id.text1);
      TextView textView2 = (TextView)row.findViewById(android.R.id.text2);
      textView1.setText("Name");
      textView2.setText("Contents");

      return row;
    }
  }
}
