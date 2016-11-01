http_archive(
  name = "io_bazlets",
  url = "https://github.com/davido/bazlets/archive/v2.13.2.tar.gz",
  sha256 = "efa4502e962c5404ac79b60d831e10743ad434ad2959135ff8a58af4ff658192",
  strip_prefix = "bazlets-2.13.2",
)

load("@io_bazlets//:gerrit_api.bzl", "gerrit_api")
gerrit_api()
