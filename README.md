Changes View prototype
=

The goal is to create a prototype/analogue of the "Local Changes" panel in IntelliJ-based products, i. e. a panel which always displays actual VCS statuses of all tracked files without a need to explicitly request the status.

Git is the preferable VCS but any other VCS will be fine as well.
It is preferable to communicate with the command line Git, but you may use any other method if you wish (JGit, libgit2).

The task should be implemented in Java 8 (or Kotlin). If you'd like to use a 3-party library (not included into JDK), it is not prohibited and sometimes even a good thing, but please get an approval in beforehand.

When the task is ready, push it to GitHub or other VCS hosting service.

A UI is a tabbed simplest possible editor (no highlighting, formatting, etc. is needed, just a text area) with a dedicated panel at the bottom to display VCS statuses, and with another panel at the left or at the right to display the file tree (analogue of the Project View in IntelliJ IDEA).

The Project View should display a simple file tree for the Git working tree root selected by user (e.g. via the "Open File" action).

The Changes View should display all files opened in the editor with the indication of their VCS statuses (modified, deleted, unversioned, etc.;

While a file is open in one of the editors, its VCS status is being tracked and shown in the Changes View. I. e. the UI allows to know the VCS status of the open files all the time, without need to call `git status` every time.

* When the user starts typing, the status of the file should be refreshed. Of course it shouldn't block the UI, it should be done asynchronously and not for every key stroke.

* When the user saves the file, the status should be refreshed as well.

* When the user starts typing in a versioned file, the user-visible status of the file should be changed immediately.

* There should be a "Refresh" button in the Changes View that would refresh states of all open files.

* Files can be saved explicitly. Unsaved files should be displayed in Changes View in some special way.

* There should be no slowdowns when starting to type in a file or saving it, all checks should be done asynchronously.

* Revert & Delete actions should be available. Files deleted from the VCS should be displayed in Changes View with an ability to revert deletion.

* You may assume that all files are under a single VCS root.

* Tracking external file system changes is not required.

* Optionally "Sync on frame activation" can be implemented.

* Optionally commit & update actions can be implemented.