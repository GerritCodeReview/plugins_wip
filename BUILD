load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
)

gerrit_plugin(
    name = "wip",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
     manifest_entries = [
        "Gerrit-PluginName: wip",
        "Gerrit-Module: com.googlesource.gerrit.plugins.wip.Module",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.wip.HttpModule",
    ],
)

java_library(
    name = 'classpath',
    deps = [':wip__plugin'],
)
