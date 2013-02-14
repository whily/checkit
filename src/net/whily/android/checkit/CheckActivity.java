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
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public final class CheckActivity extends ListActivity 
  implements OnDialogDoneListener {
  private static final String TAG = "CheckActivity";

  public static final String EDIT_DIALOG_TAG = "EDIT_DIALOG_TAG";

  private boolean resumeFromSaved;

  // Indicate whether the list content is modified since last onPause().
  private boolean modified = false;

  private Uri uri;
  private Cursor cursor;
  private static final String[] PROJECTION =
    new String[] {
    ChecklistMetadata.Checklists._ID,
    ChecklistMetadata.Checklists.COLUMN_TITLE,
    ChecklistMetadata.Checklists.COLUMN_CONTENT
  };

  private ArrayList<CheckedItem> items;
  private Button addButton;
  private EditText entry;
  private ListView list;
  private RelativeLayout rl;
  private int editPosition; // Save the position of the item to be editted.

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Intent intent = getIntent();
    final String action = intent.getAction();

    if (Intent.ACTION_EDIT.equals(action)) {
      uri = intent.getData();
    } else {
      Log.e(TAG, "Unknown action, exiting.");
      finish();
      return;  // Return RESULT_CANCELED to originating activity.
    }

    cursor = managedQuery(uri, PROJECTION, null, null, null);

    setContentView(R.layout.check);
    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState != null) {
      items = savedInstanceState.getParcelableArrayList("items");
      resumeFromSaved = true;
    } else {
      cursor.moveToFirst();
      items = refreshItems();
      resumeFromSaved = false;
    }
    setListAdapter(new CheckAdapter());
    list = (ListView)getListView();
    list.setMultiChoiceModeListener(new ModeCallback());

    rl = (RelativeLayout)findViewById(R.id.add_entry_button);
    addButton = (Button)findViewById(R.id.add);

    entry = (EditText)findViewById(R.id.entry);
    addButton.setEnabled(entry.getText().length() > 0);    
    entry.addTextChangedListener(new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, 
                                  int count) {
          addButton.setEnabled(entry.getText().length() > 0);    
        }        

        public void beforeTextChanged(CharSequence s, int start, int count, 
                                      int after) {
        }
        
        public void afterTextChanged(Editable s) {
        }
      });
    entry.setOnEditorActionListener(new OnEditorActionListener() {
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
  protected void onResume() {
    super.onResume();

    if (cursor != null) {
      cursor.requery();
      cursor.moveToFirst();

      int columnTitleIndex = cursor.getColumnIndex(ChecklistMetadata.Checklists.COLUMN_TITLE);
      String title = cursor.getString(columnTitleIndex);
      setTitle(title);

      if (resumeFromSaved) {
        resumeFromSaved = false;
      } else {
        items = refreshItems();
        // Update adapter since items are changed.
        setListAdapter(new CheckAdapter()); 
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (cursor != null) {
      if (modified) {
        updateChecklist(CheckedItem.serialize(items));
        modified = false;
      }
    }
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
  public boolean onPrepareOptionsMenu(Menu menu) {
    // Only enabled "paste" menu item if there is text in Clipboard.
    ClipboardManager clipboard = (ClipboardManager)
      getSystemService(Context.CLIPBOARD_SERVICE);    
    MenuItem pasteItem = menu.findItem(R.id.paste);
    pasteItem.setEnabled(clipboard.hasPrimaryClip()
      && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN));

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
  int id = item.getItemId();
    switch(id) {
      case android.R.id.home:
        // App icon in action bar clicked; go home.
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;
        
      case R.id.copy:
      case R.id.paste:
        ClipboardManager clipboard = (ClipboardManager)
          getSystemService(Context.CLIPBOARD_SERVICE);
        if (id == R.id.copy) {
          ClipData clip = ClipData.newPlainText("CheckIt", CheckedItem.toText(items));
          clipboard.setPrimaryClip(clip);
          Util.toast(this, "Text copied to clipboard.");
        } else {
          // Only handle one item at a time.
          ClipData.Item clipItem = clipboard.getPrimaryClip().getItemAt(0);
          String pasteData = clipItem.getText().toString();

          if (pasteData != null) {
            CheckedItem.addItems(items, pasteData);
            refreshListView();
            list.smoothScrollToPosition(items.size() - 1);
            modified = true;
            Util.toast(this, "Text pasted from clipboard.");
          }
        }
        return true;
      
      case R.id.uncheck_all:
        Alert.show(this, R.string.uncheck_message, R.string.uncheck, 
                   R.string.cancel,
                   new DialogInterface.OnClickListener () {
                     public void onClick(DialogInterface dialog, int id) {
                       for (CheckedItem checkedItem : items) {
                         checkedItem.setChecked(false);
                       }
                       refreshListView();
                       modified = true;
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
    CheckedItem item = items.get(position);
    item.toggle();
    CheckedTextView textView = (CheckedTextView)v.findViewById(R.id.checked_text);
    textView.setChecked(item.isChecked());
    modified = true;
    
    super.onListItemClick(l, v, position, id);
  }

  private void editItem(int position) {
    editPosition = position;
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    PromptDialogFragment pdf = 
      PromptDialogFragment.newInstance(R.string.edit, 
                                       items.get(position).getText());
    pdf.show(ft, EDIT_DIALOG_TAG);
  }

  public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
    if (!cancelled) {
      items.get(editPosition).setText(message.toString().trim());
      refreshListView();
      modified = true;
    }
  }

  private void deleteItems(final List<Integer> positions) {
    Alert.show(this, R.string.delete_message, R.string.delete, 
               R.string.cancel,
               new DialogInterface.OnClickListener () {
                 public void onClick(DialogInterface dialog, int id) {
                       Collections.sort(positions);
                       Collections.reverse(positions);
                       for (int position : positions) {
                         items.remove(position);
                       }
                       refreshListView();
                       modified = true;
                 }
               });
  }

  private ArrayList<CheckedItem> refreshItems() {
    int columnContentIndex = cursor.getColumnIndex(ChecklistMetadata.Checklists.COLUMN_CONTENT);
    String content = cursor.getString(columnContentIndex);
    return CheckedItem.deserialize(content);
  }

  public void onAddButtonClick(View v) {
    // Check button status since this function is also called e.g. due
    // to IME action.
    if (addButton.isEnabled()) {
      items.add(new CheckedItem(entry.getText().toString().trim()));
      entry.setText("");
      refreshListView();
      list.smoothScrollToPosition(items.size() - 1);
      modified = true;
    }
  }

  /**
   * Highlight the row if it is selected.
   */
  private void highlightRow(View row, int position) {
    if (items.get(position).isSelected()) {
      row.setBackgroundResource(R.drawable.light_blue);
    } else {
      row.setBackgroundResource(0);
    }
  }

  /**
   * Update the checklist.
   */
  private final void updateChecklist(String content) {
    ContentValues values = new ContentValues();
    values.put(ChecklistMetadata.Checklists.COLUMN_CONTENT, content);
    getContentResolver().update(uri, values, null, null);
  }

  private void refreshListView() {
    CheckAdapter adapter = (CheckAdapter)list.getAdapter();
    adapter.notifyDataSetChanged();
  }

  private final class CheckAdapter extends ArrayAdapter<CheckedItem> {
    CheckAdapter() {
      super(CheckActivity.this, R.layout.checked_item, items); 
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {
      View row = convertView;
      if (row == null) {
        row = getLayoutInflater().inflate(R.layout.checked_item, 
                                          parent, false);
      }
      CheckedTextView textView = (CheckedTextView)row.findViewById(R.id.checked_text);
      textView.setText(items.get(position).getText());
      textView.setChecked(items.get(position).isChecked());
      highlightRow(row, position);

      return row;
    }
  }

  private final class ModeCallback implements ListView.MultiChoiceModeListener {
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      getMenuInflater().inflate(R.menu.check_context, menu);
      setTitle(mode);
      setMenuItemVisibility(menu);
      return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return true;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      switch (item.getItemId()) {
        case R.id.edit:
          editItem(CheckedItem.getFirstSelectedPosition(items));
          mode.finish();
          break;
        case R.id.delete:
          deleteItems(CheckedItem.getSelectedPositions(items));
          mode.finish();
          break;
        default:
          break;
      }
      return true;
    }

    public void onDestroyActionMode(ActionMode mode) {
      CheckedItem.clearSelectedAll(items);
    }

    public void onItemCheckedStateChanged(ActionMode mode,
                                          int position, long id, boolean checked) {
      items.get(position).setSelected(checked);
      setMenuItemVisibility(mode.getMenu());
      setTitle(mode);

      refreshListView();
    }

    private final void setTitle(ActionMode mode) {
      final int selectedCount = CheckedItem.getSelectedCount(items);
      String title = 
        (selectedCount == 0) 
        ? "" 
        : getString(R.string.item_selected_prefix) + selectedCount + getString(R.string.item_selected_postfix);
      mode.setTitle(title);
    }

    private final void setMenuItemVisibility(Menu menu) {
      final int selectedCount = CheckedItem.getSelectedCount(items);
      MenuItem editMenuItem = (MenuItem)menu.findItem(R.id.edit);
      editMenuItem.setVisible(selectedCount == 1);
    }
  }
}
