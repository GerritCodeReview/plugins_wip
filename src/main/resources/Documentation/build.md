Build
=====

This plugin can be built with Bazel, Buck or Maven.

Bazel
-----

### Build standalone

To build the plugin, issue the following command:


```
  bazel build wip
```

The output is created in


```
  bazel-bin/wip/wip.jar
```

### Build in Gerrit tree

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  bazel build plugins/@PLUGIN@
```

The output is created in

```
  bazel-genfiles/plugins/@PLUGIN@/@PLUGIN@.jar0
```

This project can be imported into the Eclipse IDE.
Add the plugin name to the `CUSTOM_PLUGINS` set in
Gerrit core in `tools/bzl/plugins.bzl`, and execute:

```
  ./tools/eclipse/project.py
```

Buck
----

Two build modes are supported: Standalone and in Gerrit tree.
The standalone build mode is recommended, as this mode doesn't require
the Gerrit tree to exist locally.


### Build standalone

Clone bucklets library:

```
  git clone https://gerrit.googlesource.com/bucklets

```
and link it to wip plugin directory:

```
  cd wip && ln -s ../bucklets .
```

Add link to the .buckversion file:

```
  cd wip && ln -s bucklets/buckversion .buckversion
```

To build the plugin, issue the following command:


```
  buck build plugin
```

The output is created in

```
  buck-out/gen/wip.jar
```

### Build in Gerrit tree

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  buck build plugins/wip
```

The output is created in

```
  buck-out/gen/plugins/wip/wip.jar
```

This project can be imported into the Eclipse IDE:

```
  ./tools/eclipse/project.py
```

Maven
-----

Note that the Maven build is provided for compatibility reasons, but
it is considered to be deprecated and will be removed in a future
version of this plugin.

To build with Maven, run

```
mvn clean package
```
