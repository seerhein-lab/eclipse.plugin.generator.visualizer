eclipse.plugin.generator.visualizer
===================================

Eclipse Plugin to mark / visualize code affected by a code generator.

Mark code which was
* generated
* not modified manually
* added manually
* modified manually
* removed

Add shortcuts in order
* to jump directly to the generated counterpart
* to compare the counterparts

Give actions in order
* to disable the plugin
* to ignore certain line of codes in the compare view (tree diff)
* to set colour / way of visualization

Installation
------------

The eclipse plug-in could be installed from the following eclipse update site:

http://seerhein-lab.github.io/eclipse.plugin.generator.visualizer/0.1.0/

Use the plugin
--------------

As you can see in the following screenshot your editor view will be enhanced by markers, annotations and actions:
![enhanced editor](https://raw.github.com/Seitenbau/eclipse.plugin.generator.visualizer/docu_resources/screenshots/editor_all.png)

Configure the plugin
--------------------

You can configure the plugin with the following two screens:

1. Eclipse Preferences: General > Editors > Text Editors > Annotations
    Here you can setup the four Annotations added by this plugin (added, deleted, generated, modified).

    ![annotation preferences](https://raw.github.com/Seitenbau/eclipse.plugin.generator.visualizer/docu_resources/screenshots/prefs_annotations.png)
2. Eclipse Preferences: Generator Visualizer
    In that screen to can configure the general behaviour of the plugin.

    ![general preferences](https://raw.github.com/Seitenbau/eclipse.plugin.generator.visualizer/docu_resources/screenshots/prefs_main.png)


Developing
==========

You need Maven 3.x.

TODO
====

* Full description.
* Add more screenshots.
