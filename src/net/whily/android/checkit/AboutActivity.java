/**
 * About activity for CheckIt.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.checkit;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public final class AboutActivity extends Activity {
  private WebView browser;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.about);

    browser = (WebView)findViewById(R.id.webkit);
    browser.loadData(getString(R.string.about_html), "text/html", "UTF-8");
  }
}

