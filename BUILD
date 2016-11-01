load("@io_bazel_rules_gerrit//gerrit:gerrit.bzl", "gerrit_plugin")
load("//:version.bzl", "PLUGIN_VERSION")

gerrit_plugin(
  name = 'wip',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: wip',
    'Gerrit-Module: com.googlesource.gerrit.plugins.wip.Module',
    'Gerrit-HttpModule: com.googlesource.gerrit.plugins.wip.HttpModule',
  ],
)
