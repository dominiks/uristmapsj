Uristmaps v0.3
==============

Uristmaps is a world map renderer that can create google-maps like maps for your browser.
See www.uristmaps.org for more info.

Configuration
-------------

If not already done, copy the conf.cfg.sample file to conf.cfg and adjust the settings
to your liking. At the very least you have to set the directory in which the exported
files can be found.

Starting
-------

 * To create a map, start uristmaps.bat (or uristmaps.sh when on Linux / Mac).
 * To start the web server to view your map, start host.bat (or host.sh for non-windows platforms).


Advanced
--------

You can specify the path to the config file via a parameter "-c". To generate a map for a different config
file you can execute

    java -jar uristmaps.jar -c ../path_to/config.cfg

Contact
-------

For contact send a mail to contact@uristmaps.org

