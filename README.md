CheckIt
=======

CheckIt is a simple checklist Android app. It is currently under
development and is expected to have following features:

* Multiple checklists
* Extensive templates for commonly used checklists
* Backup to and restore from SD card
* Import from and export to CSV file
* Synchronization with Google Tasks

For more information about CheckIt, please go to
  <https://github.com/whily/checkit>

Wiki pages can be found at
  <https://wiki.github.com/whily/checkit>

Installation
------------

Development
-----------

The following tools are needed to build CheckIt from source:

* JDK version 6/7 from <http://www.java.com> if Java is not available. 
  Note that JDK is preinstalled on Mac OS X and available via package manager
  on many Linux systems. 
* Android SDK r16.
* [Inkscape](http://inkscape.org) to generate icons.

CheckIt is built using [Ant](http://en.wikipedia.org/wiki/Apache_Ant)
instead of IDEs like Eclipse. Type the following commands at the
project directory checked out (assuming debug version):

1. android update project -p .
2. ./genart
3. ant debug

License
-------

CheckIt is released under GNU General Public License v2, whose information
is available at:
  <http://www.gnu.org/licenses/gpl-2.0.html>


