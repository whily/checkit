
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
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
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

public final class HomeActivity extends ListActivity 
  implements OnDialogDoneListener {
  private static final String TAG = "HomeActivity";
  private static final String sdPrefix = "checkit";

  private ArrayList<String> lists = new ArrayList<String>(Arrays.asList("first"));
  private static final String[] PROJECTION = new String[] {
    ChecklistMetadata.Checklists._ID,
    ChecklistMetadata.Checklists.COLUMN_TITLE
  };

  private ExternalStorage sd;
  private ListView list;

  private Uri selectedUri;

  private HashSet<Long> selectedIds;

  public static final String EDIT_TITLE_DIALOG_TAG = "EDIT_TITLE_DIALOG";
  public static final String NEW_TITLE_DIALOG_TAG = "NEW_TITLE_DIALG";
  public static final String FROM_TEMPLATE_DIALOG_TAG = "FROM_TEMPLATE_DIALOG";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    if (intent.getData() == null) { // Launched by user directly.
      intent.setData(ChecklistMetadata.Checklists.CONTENT_URI);
    }

    sd = new ExternalStorage(sdPrefix, this);
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

      case R.id.backup:
        backup();
        return true;

      case R.id.new_list:
        newChecklist();
        return true;

      case R.id.restore:
        restore();
        return true;

      case R.id.settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;

      case R.id.template_list:
        createFromTemplate();
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

  private void editChecklistTitle() {
    String title = getSelectedChecklistTitle();
    // Save the URI since selectedIds will be cleard when the dialog is shown.
    selectedUri = getSelectedUri(); 

    FragmentTransaction ft = getFragmentManager().beginTransaction();
    PromptDialogFragment pdf 
      = PromptDialogFragment.newInstance(R.string.edit_title, title);
    pdf.show(ft, EDIT_TITLE_DIALOG_TAG);
  }

  private void newChecklist() {
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    PromptDialogFragment pdf 
      = PromptDialogFragment.newInstance(R.string.edit_title, 
                                         getString(R.string.title));
    pdf.show(ft, NEW_TITLE_DIALOG_TAG);    
  }

  private void backup() {
    String backupName = "checklist_" + Util.timeStamp() + ".db";
    try {
      sd.copyToSD(ChecklistMetadata.DATABASE_NAME, backupName);
      Util.toast(this, 
                 getString(R.string.backup_successful) + " " + backupName + ".");
    } catch (Exception e) {
      // Do nothing since exception is already handled. The main intention here
      // is to avoid showing the "successful" message.
    }
  }

  private void restore() {
  }

  private void createFromTemplate() {
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    SelectionDialogFragment sdf 
      = SelectionDialogFragment.newInstance(R.string.select_template);
    sdf.show(ft, FROM_TEMPLATE_DIALOG_TAG);
  }

  private String getSelectedChecklistTitle() {
    Uri uri = getSelectedUri();
    Cursor c = managedQuery(uri, null, null, null, null);
    int titleIndex = c.getColumnIndex(ChecklistMetadata.Checklists.COLUMN_TITLE);
    c.moveToFirst();
    return c.getString(titleIndex);
  }

  // Return the id of the first selected checklist.
  private long getSelectedId() {
    return ((Long)(selectedIds.toArray()[0])).longValue();
  }

  // Return the uri of the first selected checklist.
  private Uri getSelectedUri() {
    return ContentUris.withAppendedId(getIntent().getData(), getSelectedId());
  }

  private void insertEdit(ContentValues cv) {
    Uri uri = getContentResolver().insert(getIntent().getData(), cv);
    if (uri == null) {
      Log.e(TAG, "Failed to insert new checklist into " + getIntent().getData());
      finish();     // Close activity.
      return;
    }
    startActivity(new Intent(Intent.ACTION_EDIT, uri));
  }

  public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
    if (!cancelled) {
      if (tag.equals(EDIT_TITLE_DIALOG_TAG) || tag.equals(NEW_TITLE_DIALOG_TAG)) {
        String title = message.toString().trim();
        ContentValues cv = new ContentValues();
        cv.put(ChecklistMetadata.Checklists.COLUMN_TITLE, title);
        if (tag.equals(EDIT_TITLE_DIALOG_TAG)) {
          getContentResolver().update(selectedUri, cv, null, null);
        } else if (tag.equals(NEW_TITLE_DIALOG_TAG)) {
          insertEdit(cv);
        }
      } else if (tag.equals(FROM_TEMPLATE_DIALOG_TAG)) {
        String title = message.toString();
        ContentValues cv = new ContentValues();

        String[] itemStrings = getResources().getStringArray(R.array.travel_list);
        ArrayList<CheckedItem> items = new ArrayList<CheckedItem>();
        for (String itemString : itemStrings) {
          items.add(new CheckedItem(itemString));
        }
        String content = CheckedItem.serialize(items);

        cv.put(ChecklistMetadata.Checklists.COLUMN_TITLE, title);
        cv.put(ChecklistMetadata.Checklists.COLUMN_CONTENT, content);
        insertEdit(cv);
      }
    }
  }

  private void deleteChecklists() {
    final String selectedIdString = selectedIds.toString();
    final String where = ChecklistMetadata.Checklists._ID
      + " IN (" + selectedIdString.substring(1, selectedIdString.length() - 1) 
      + ")";
    Alert.showTitleIcon(this, android.R.drawable.ic_dialog_alert,
                        R.string.delete, 
                        R.string.delete_checklists, 
                        R.string.delete, R.string.cancel,
                        new DialogInterface.OnClickListener () {
                          public void onClick(DialogInterface dialog, int id) {
                            getContentResolver().delete(getIntent().getData(), where, null);
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
          editChecklistTitle();
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
