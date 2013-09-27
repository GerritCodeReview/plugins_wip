Gerrit Work in Progress plugin.

It adds a new button that allows an authorized user to set a
change to Work In Progress, and a button to change from WIP back
to a "Ready For Review" state.

Any change in the WIP state will not show up in anyone's Review
Requests. Pushing a new patchset will reset the change to Review
In Progress.

In addition this plugin exposes this functionality as REST endpoints
and SSH command.

It supposed to be used in combination with new "Change Owners" group. The
plugin owned capability "Work In Progress" can be granted to that group,
so that only change owners can toggle the WIP state.

It uses the following features that were introduced in Gerrit 2.8:

* Plugin owned capabilities
* Plugin can contribute buttons (UiActions) with JavaScript API
* UiActions are dropped if the user doesn't have the ACL
* Plugin can provide its own name in MANIFEST file

To see it in action:

Turn normal change to WIP and provide description why:
http://imgur.com/tMNkyjM

New comment message appears:
http://i.imgur.com/h2hQQT4.png

The patch set is reloaded and Ready for Review button appears:
http://imgur.com/JGCSyLO

The change appears with status "Work In Progress" on change list:
http://i.imgur.com/P0HCEcn.png

Reviewers Dashboard filters that change:
http://imgur.com/fr4nKv7

Mark it as ready for review:
http://imgur.com/tnMGg16

Comments are updated correspondingly:
http://imgur.com/l4E8evp

Reviewers Dashboard shows that change again:
http://i.imgur.com/VdAtuIO.png

Known limitations:

Old change screen doesn't support JS API. So that the popup dialog is not shown
if "Work In Progress" and "Ready In Review" buttons are used and no comments
can be provided. New change screen should be used for best experience with
WIP Workflow plugin.

This plugin is based on previous work of David Shrewsbury:
https://gerrit-review.googlesource.com/36091

