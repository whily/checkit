
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
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public final class HomeActivity extends ListActivity {
  private static final String TAG = "HomeActivity";

  private ArrayList<String> lists = new ArrayList<String>(Arrays.asList("first"));
  private static final String[] PROJECTION = new String[] {
    ChecklistMetadata.Checklists._ID,
    ChecklistMetadata.Checklists.COLUMN_TITLE
  };

  private ListView list;

  private HashSet<Long> selectedIds;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    if (intent.getData() == null) { // Launched by user directly.
      intent.setData(ChecklistMetadata.Checklists.CONTENT_URI);
    }

    selectedIds = new HashSet<Long>();
    if (savedInstanceState != null) {
      long[] array = savedInstanceState.getLongArray("selectedIds");
      for (long selectedId : array) {
        selectedIds.add(selectedId);
      }
    } else {
      selectedIds = new HashSet<Long>();
    }

    setContentView(R.layout.home);
    ActionBar actionBar = getActionBar();
    actionBar.setHomeButtonEnabled(true);

    //getListView().setOnCreateContextMenuListener(this);

    Cursor cursor = managedQuery(getIntent().getData(),
                                 PROJECTION, null, null, 
                                 ChecklistMetadata.Checklists.DEFAULT_SORT_ORDER);
    String[] dataColumns = { ChecklistMetadata.Checklists.COLUMN_TITLE };
    int[] viewIDs = { android.R.id.text1 };
    HighlightCursorAdapter adapter
      = new HighlightCursorAdapter(this,
                                   android.R.layout.simple_list_item_1,
                                   cursor,
                                   dataColumns,
                                   viewIDs
                                   );
    setListAdapter(adapter);
    list = (ListView)getListView();
    list.setMultiChoiceModeListener(new ModeCallback());
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    long[] array = new long[selectedIds.size()];
    int i = 0;
    for (long selectedId : selectedIds) {
      array[i++] = selectedId;
    }
    outState.putLongArray("selectedIds", array);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.home_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case android.R.id.home:
      case R.id.about:
        startActivity(new Intent(this, AboutActivity.class));
        return true;

      case R.id.new_list:
        startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
        return true;

      case R.id.template_list:
        return true;

      case R.id.settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }
    
  //@Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
    startActivity(new Intent(Intent.ACTION_EDIT, uri));
  }

  private void deleteChecklists() {
    Alert.showTitleIcon(this, android.R.drawable.ic_dialog_alert,
                        R.string.delete, 
                        R.string.delete_checklists, 
                        R.string.delete, R.string.cancel,
                        new DialogInterface.OnClickListener () {
                          public void onClick(DialogInterface dialog, int id) {
                            selectedIds.clear();
                          }
                        });
  }

  private final class ModeCallback implements ListView.MultiChoiceModeListener {
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      getMenuInflater().inflate(R.menu.home_context, menu);
      setTitle(mode);
      setMenuItemVisibility(menu);
      return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return true;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      switch (item.getItemId()) {
        case R.id.edit_title:
          // editItem(CheckedItem.getFirstSelectedPosition(items));
          mode.finish();
          break;
        case R.id.delete:
          deleteChecklists();
          mode.finish();
          break;
        default:
          break;
      }
      return true;
    }

    public void onDestroyActionMode(ActionMode mode) {
      selectedIds.clear();
    }

    public void onItemCheckedStateChanged(ActionMode mode,
                                          int position, long id, boolean checked) {
      if (checked) {
        selectedIds.add(id);
      } else {
        selectedIds.remove(id);
      }
      setMenuItemVisibility(mode.getMenu());
      setTitle(mode);
      getListView().invalidateViews();
    }

    private final void setTitle(ActionMode mode) {
      final int selectedCount = selectedIds.size();
      String title = 
        (selectedCount == 0) 
        ? "" 
        : getString(R.string.item_selected_prefix) + selectedCount + getString(R.string.item_selected_postfix);
      mode.setTitle(title);
    }

    private final void setMenuItemVisibility(Menu menu) {
      final int selectedCount = selectedIds.size();
      MenuItem editMenuItem = (MenuItem)menu.findItem(R.id.edit_title);
      editMenuItem.setVisible(selectedCount == 1);
    }
  }

  class HighlightCursorAdapter extends SimpleCursorAdapter {
    HighlightCursorAdapter(Context context, int layout, Cursor c, 
                           String[] from, int[] to) {
      super(context, layout, c, from, to);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      View view = super.getView(position, convertView, parent);
      long id = getItemId(position);
      if (selectedIds.contains(id)) {
        view.setBackgroundResource(R.drawable.light_blue);
      } else {
        view.setBackgroundResource(0);
      }
      return view;
    }
  }
}
