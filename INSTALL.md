Karaf with Hummimgbird pre-deployed
-----------------------------------

Download the latest snapshot of a current Karaf (from which Hbird will automatically be deployed on first startup) from here:
https://oss.sonatype.org/content/repositories/snapshots/org/hbird/karaf/hbird-platform/0.0.1-SNAPSHOT/

If you'd rather do the installation yourself, follow the instructions below.


Manual Karaf setup
---------------------------
Download and extract Karaf version 3 from:
http://karaf.apache.org/

Install Karaf Webconsole if required (features:install webconsole)
You can reach it at http://localhost:8181/system/console/

Hbird
--------
To install snapshot versions of Hbird you must add the following repo to the org.ops4j.pax.url.mvn.cfg config file in etc
 
 http://oss.sonatype.org/content/repositories/snapshots/@snapshots@noreleases

 
Tell Karaf where to find the Hbird software:
repo-add mvn:org.hbird.karaf/hbird-features/0.0.1-SNAPSHOT/xml/features

Install all of Hbird:
feature:install -v hbird-complete




Outdated, kept for reference:
-----------------------------

Optional: Add following config file to $karaf/etc/
	org.hbird.osgi.xtceosgi.xtceSpaceSystemModelFactoryUpdater.cfg
and add the following config option to it:
	spaceSystemModelFilename = /path/to/an/xtce/file.xml
A sample XTCE file can be found in our Stock6 project: https://github.com/markjohndoyle/Stock6


