/**
 * About activity for CheckIt.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011 Yujian Zhang
 */

package net.whily.android.checkit;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {
  WebView browser;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.about);

    browser = (WebView)findViewById(R.id.webkit);
    String about = "<html>"
      + "<body>" 
      + "<center><h3>CheckIt v0.0.1</h3></center>"
      + "<center><h4>Copyright (C) 2011 Yujian Zhang</h4></center>" 
      + "<center><h4>License: <a href=\"http://www.gnu.org/licenses/gpl-2.0.html\">GNU General Public License v2</a></h4></center>" 
      + "</body>" 
      + "</html>";
    browser.loadData(about, "text/html", "UTF-8");
  }
}

