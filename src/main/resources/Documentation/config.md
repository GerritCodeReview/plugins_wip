Configuration
=============

The configuration of the @PLUGIN@ plugin is done in the `gerrit.config`
file. This plugin overloads Gerrit own DRAFT workflow.  The WIP workflow
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
