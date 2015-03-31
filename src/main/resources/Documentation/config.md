Configuration
=============

The configuration of the @PLUGIN@ plugin is done in the `gerrit.config`
file.

Enable WIP Workflow
-------------------

This plugin overloads Gerrit own DRAFT workflow.  The WIP workflow
can only be enabled, when DRAFT workflow in Gerrit was disabled.

*Warning*: When DRAFT workflow was used in Gerrit, i. e. changes in
status DRAFT still exist, they must be published or deleted before WIP
workflow can be used.

To disable DRAFT workflow the following must be set in `gerrit.config`:

```
  [change]
    allowDrafts = false
```
WIP workflow can be used now.

Customize Status Label
----------------------

The WIP plugin overrides the draft workflow but it does not override the
Gerrit draft status label.  This means that Gerrit will continue to report
draft changes as "Status Draft".  It is advisable to customize the draft
status label to something more appropriate when using the WIP workflow.

To make the status "Work In Progress" appear on the Gerrit UI you can configure
the [draftLabel](../../../Documentation/config-gerrit.html#change) setting:

```
  [change]
    draftLabel = Work In Progress
```

Configure Access
----------------

The WIP plugin workflow binds to the existing Gerrit [access control]
(../../../Documentation/access-control.html#access_categories) feature
that manages draft changes.  By default drafts are only accessible to
the owner and reviewers of the change and also to the administrators group.
It is advisable to configure the drafts access control to match your WIP
workflow.

For example the following setting will allow any user to view WIP changes
from the Gerrit UI and to retrieve WIP changes using the REST API:

```
  [access "refs/*"]
    viewDrafts = group Anonymous Users
```
