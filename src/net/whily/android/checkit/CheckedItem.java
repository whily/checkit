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

import android.os.Parcel;
import android.os.Parcelable;

public class CheckedItem implements Parcelable {
  private String text;
  private boolean checked;

  CheckedItem() {
    this("");
  }

  CheckedItem(String text) {
    this(text, false);
  }

  CheckedItem(String text, boolean checked) {
    this.text = text;
    this.checked = checked;
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

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeString(this.toString());
  }

  public String toString() {
    String prefix = checked ? "1" : "0";
    return prefix + text;
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
    String prefix = string.substring(0, 1);
    String text = string.substring(1);
    boolean checked = prefix.equals("1");
    return new CheckedItem(text, checked);
  }

  void toggle() {
    checked = !checked;
  }
}
