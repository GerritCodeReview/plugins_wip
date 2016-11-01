http_archive(
  name = "io_bazel_rules_gerrit",
  url = "https://github.com/davido/bazel_rules_gerrit/archive/0.1.tar.gz",
  sha256 = "d81066299b778f5b2af93bc197e9cb6489409aec68fb3aa7bba1cd63b61d77b7",
  strip_prefix = "bazel_rules_gerrit-0.1",
)

load("@io_bazel_rules_gerrit//gerrit:gerrit.bzl", "gerrit_repositories")
gerrit_repositories()
