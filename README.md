imagestats
==========

"ImageStats" is a web-based tool to collect facial and skin annotations from 
images. The collected data is used as established ground truth 
information to develop and improve face-detection and face-matching 
algorithms. It is developed at National Library of Medicine (NLM) to 
help R&D in the field of image processing.

During Dec 2012-Jan 2013, a public-facing instance of this tool was used by
students in the "Google code In" (GCI) contest to annotate face photos.
The description of these tasks, at
  http://wiki.sahanafoundation.org/doku.php/agasti:vesuvius:gci2012:annotation
demonstrates much of the functionality of the tool.  GCI annotators were
organized through Sahana Software Foundation, with mentors at NLM.

ImageStats was rapid-prototyped from a number of open source components, 
among them Vesuvius, GWT, GXT, GWT-Graphics, Tomcat, Mysql, and SOLR. The 
instance is hosted and managed using NLM Vesuvius/PL infrastructure. 
Current thinking is that ImageStats is overly complicated in structures
and dependencies, and might be fruitfully reimplemented in a simpler 
manner, a possible Google Summer of Code 2013 project.

The ImageStats project, including source code and this README file,
is available through github.

The tool is a Google Web Toolkit application using the following open 
source technologies:

Download Sites for Dependencies:
--------------------------------
- Sahana Software Foundation's Vesuvius 0.9x:
  https://launchpad.net/vesuvius
- Google Web Toolkit 2.4.0:
  https://developers.google.com/web-toolkit/
- Sencha GXT 3.0:
  http://www.sencha.com/products/gxt/
- Gwt-graphics 1.0.0:
  http://vaadin.com/directory#addon/7
- JavaCV 0.1:
  http://code.google.com/p/javacv/
- OpenCV 2.4.1:
  http://opencv.org
- SOLR 4.0:
  http://lucene.apache.org/solr/
- MySQL 5.5:
  http://www.mysql.com
- Apache Tomcat 7.0.32:
  http://tomcat.apache.org/
- gson 2.2.1:
  http://code.google.com/p/google-gson/

External Packages:
-----------------------
- Running instance of Vesuvius.
  - Download Vesuvius from Sahana Software Foundation. The package 
    Includes PL webserver and a backend mysql server.
    Use provided table schemas to create 'imagestats' and 
    'image_search' tables in mysql server.
- Running instance of PL SOLR.
  - Download and install SOLR 4.0. Use included schema.xml and 
    solrConfig.xml to create imagestats core in SOLR. A data import
    file db-data-config.xml is also included in the package to 
    import PL data into SOLR.
- Apache Tomcat 7.0.33
  - ImageStats is a servlet (.war) tested with tomcat. 
  - User should Include mysql5.5 jdbc connector in TOMCAT_HOME/lib 
    directory.
  - Use optional Realm defined in servlet's context.xml for any 
    user authentication. A default tomcat_users database schema 
    is included in the package.

Library Packages:
-----------------
- GWT 2.4.0: If using Eclipse IDE, install the eclipse-gwt plugin.
- Sencha GXT (Extended GWT) 3.0: An extended library to GWT. Most 
  of the widgets in the application use GXT.
- GWT-Graphics 1.0.0: Another GWT extension library to support 
  drawing based on SVG.
- OpenCV/JavaCV: An image processing library. OpenCV should be 
  installed on the host system. JavaCV is a wrapper over OpenCV for 
  java applications. Used by ImageStats to export IplImage data 
  structure.
- Gson 1.0.0: Library to manipulate JSON output from PL SOLR.

