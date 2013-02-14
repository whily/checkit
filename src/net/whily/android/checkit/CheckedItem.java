/**
 * CheckedItem for checklist.
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
import android.os.Parcel;
import android.os.Parcelable;

public final class CheckedItem implements Parcelable {
  private String text;
  private boolean checked;
  private boolean selected;

  CheckedItem() {
    this("");
  }

  CheckedItem(String text) {
    this(text, false);
  }
  
  CheckedItem(String text, boolean checked) {
    this(text, checked, false);
  }

  CheckedItem(String text, boolean checked, boolean selected) {
    this.text = text;
    this.checked = checked;
    this.selected = selected;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeString(this.toString());
  }

  public String toString() {
    String checkedPrefix = checked ? "1" : "0";
    String selectedPrefix = selected? "1" : "0";
    return checkedPrefix + selectedPrefix + text;
  }

  void toggle() {
    checked = !checked;
  }

  // For serialize and deserialize, we consider field "selected" to
  // reuse existing code.

  public static String serialize(List<CheckedItem> items) {
    StringBuilder result = new StringBuilder();
    String separator = " , "; // This ensures that we can split in a simple way.
    for (CheckedItem item : items) {
      String s = item.toString();
      result.append(s.replace(",", ",,"));
      result.append(separator);
    }
    result.delete(result.length() - separator.length(), result.length());
    return result.toString();
  }

  public static ArrayList<CheckedItem> deserialize(String str) {
    ArrayList<CheckedItem> items = new ArrayList<CheckedItem>();
    if (str.length() > 0) {
      for (String s : str.split("\\s+,\\s+")) {
        CheckedItem item = parse(s.replace(",,", ","));
        item.setSelected(false);
        items.add(item);
      }
    }
    
    return items;
  }
  
  /**
   * Serialize the items to a text with LF separating each item. 
   * Use for copy operation.
   * 
   * @param items
   * @return text
   */
  public static String toText(List<CheckedItem> items) {
    StringBuilder result = new StringBuilder();
    String separator = "\n";
    for (CheckedItem item : items) {
      result.append(item.getText());
      result.append(separator);
    }
    result.delete(result.length() - separator.length(), result.length());
    return result.toString();
  }
  
  /**
   * Add more items from a string. Use for paste operation.
   * 
   * @param items  
   * @param more     A string containing more items to append. Each item is separated by '\n'.
   */
  public static void addItems(List<CheckedItem> items, String more) {
    String[] moreItems = more.split("\n");
    for (String moreItem : moreItems) {
      items.add(new CheckedItem(moreItem));
    }
  }
  
  public static void clearSelectedAll(List<CheckedItem> items) {
    for (CheckedItem item : items) {
      item.setSelected(false);
    }
  }

  public static int getSelectedCount(List<CheckedItem> items) {
    int result = 0;
    for (CheckedItem item : items) {
      if (item.isSelected()) {
        result++;
      }
    }
    return result;
  }

  public static int getFirstSelectedPosition(List<CheckedItem> items) {
    for (int i = 0; i < items.size(); ++i) {
      if (items.get(i).isSelected()) {
        return i;
      }
    }
    return -1;
  }

  public static List<Integer> getSelectedPositions(List<CheckedItem> items) {
    List<Integer> positions = new LinkedList<Integer>();
    for (int i = 0; i < items.size(); ++i) {
      if (items.get(i).isSelected()) {
        positions.add(i);
      }
    }
    return positions;
  }

  public static final Parcelable.Creator<CheckedItem> CREATOR
    = new Parcelable.Creator<CheckedItem>() {
    public CheckedItem createFromParcel(Parcel in) {
      return parse(in.readString());
    }

    public CheckedItem[] newArray(int size) {
      return new CheckedItem[size];
    }
  };

  public static CheckedItem parse(String string) {
    String checkedPrefix = string.substring(0, 1);
    String selectedPrefix = string.substring(1, 2);
    String text = string.substring(2);
    boolean checked = checkedPrefix.equals("1");
    boolean selected = selectedPrefix.equals("1");
    return new CheckedItem(text, checked, selected);
  }
}
