include_defs('//bucklets/gerrit_plugin.bucklet')

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

java_library(
  name = 'classpath',
  deps = [':wip__plugin'],
)
