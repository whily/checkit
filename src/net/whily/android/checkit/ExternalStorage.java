/**
 * Access SD card.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.checkit;

import java.io.*;
import java.nio.channels.FileChannel;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class ExternalStorage {
  private static final String TAG = "ExternalStorage";

  enum SDState {
    UNAVAILABLE, READ_ONLY, READ_WRITE
      }

  private String externalPrefix;
  private Activity activity;       // For toast only.

  public ExternalStorage(String externalPrefix, Activity activity) {
    this.externalPrefix = externalPrefix;
    this.activity = activity;
  }

  /**
   * Return directory hosting files on SD card.
   */
  public File getSDDir() {
    // We don't use recommended directories since we don't want
    // backups to be erased if app is uninstalled.
    File path = Environment.getExternalStorageDirectory();
    return new File(path, externalPrefix);
  }

  /**
   * Return file object for files on SD card.
   */
  public File getSDFile(String filename) {
    File prefixPath = getSDDir();
    prefixPath.mkdirs();
    return new File(prefixPath, filename);
  }
   
  /**
   * Copy file to SD.
   *
   * @param source file name (without path) of source file
   * @param dest   file name (without path) of destination file in SD.
   */
  public void copyToSD(String source, String dest) {
    if (getSDState() != SDState.READ_WRITE) {
      Util.toast(activity, activity.getString(R.string.sd_not_writable));
      throw new RuntimeException();
    }

    try {
      copyFile(activity.getDatabasePath(source), getSDFile(dest));
    } catch (IOException e) {
      Log.w(TAG, "Error copying file to SD: ", e);
      throw new RuntimeException();
    }
  }

  /**
   * Copy file from SD.
   *
   * @param source file name (without path) of source file in SD
   * @param dest   file name (without path) of destination file
   */
  public void copyFromSD(String source, String dest) {
    if (getSDState() == SDState.UNAVAILABLE) {
      Util.toast(activity, activity.getString(R.string.sd_not_mounted));
      throw new RuntimeException();
    } 

    try {
      copyFile(getSDFile(source), activity.getDatabasePath(dest));
    } catch (IOException e) {
      Log.w(TAG, "Error copying file from SD: ", e);
      throw new RuntimeException();
    }
  }
    
  /** 
   * Simple file copy operation in Java. JDK 7 has this feature implemented.
   */
  public static void copyFile(File sourceFile, File destFile) throws IOException {
    if(!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if(source != null) {
        source.close();
      }
      if(destination != null) {
        destination.close();
      }
    }
  }

  /**
   * Get SD card state.
   */
  public static SDState getSDState() {
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return SDState.READ_WRITE;
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      return SDState.READ_ONLY;
    } else {
      return SDState.UNAVAILABLE;
    }    
  }
}
