eclipse.plugin.generator.visualizer
===================================

Eclipse Plugin to mark / visualize code affected by a code generator.

Mark code which was
* generated
* not modifed manually
* added manually
* modified manually
* removed

Add shortcuts in order
* to jump directly to the generated counterpart
* to compare the counterparts

Give actions in order
* to disable the plugin
* to ignore certain line of codes in the compare view (tree diff)
* to set color / way of visualization

Use the plugin
--------------

As you can see in the following screenshot your editor view will be enhanced by marks, annotations and actions:
![enhanced editor](https://raw.github.com/Seitenbau/eclipse.plugin.generator.visualizer/docu_resources/screenshots/editor_all.png)

Configure the plugin 
--------------------

You can configure the plugin with two screens:
1. Eclipse Preferences: General > Editors > Text Editors > Annotations
Here you can setup the four Annotations added by this plugin (added, deleted, generated, modified).
![annotation preferences](https://raw.github.com/Seitenbau/eclipse.plugin.generator.visualizer/docu_resources/screenshots/prefs_annotations.png)

2. Eclipse Preferences: Generator Visualizer:
In that screen to can configure the general behavior of the plugin.
![general preferences](https://raw.github.com/Seitenbau/eclipse.plugin.generator.visualizer/docu_resources/screenshots/prefs_main.png)

TODO
====

* Full description.
* Add some screenshots.
