[chm]: http://www.sci.utah.edu/software/chm.html
[imagemagick]: http://www.imagemagick.org/
[matlabruntime]: http://www.mathworks.com/products/compiler/mcr/
[jetty]: http://eclipse.org/jetty/
[maven]: http://maven.apache.org/

realtime-segmentation
=====================

realtime-segmentation is an application that lets a caller perform image
segmentation in real time on a previously tiled image.  Segmentation is
performed using [Cascaded Hierarchical Model (CHM)][chm].  

This application spins up a [Jetty][jetty] web server on the local host and 
directs the user to open a browser to view the image and real time segmentation.


Requirements
============

* Centos 6+ or Ubuntu 12+ 
* Java 1.6+ **(jdk to build)**
* [Image Magick][imagemagick] command line programs (namely **convert**)
* [Matlab Runtime 2013a or better][matlabruntime] (needed to run [CHM][chm])
* [Maven][maven] 3.0 or higher **(to build)**


Building
========

    mvn install

Running
=======

    java -jar target/realtime-segmentation-REPLACEWITHVERSION-jar-with-dependencies.jar

**REPLACEWITHVERSION** must be set to the version

Example output with -h (for help) flag:

    Help

    Option               Description                           
    ------               -----------                           
    --chmbin <File>      Path to CHM bin directory             
    --cores <Integer>    Number of concurrent CHM jobs to run. 
                         Each job requires 1gb ram. (default: 1)                                  
    --dir <File>         Working/Temp directory for server     
                         (default: /tmp/3cc8edf9-81ba-4f65-8d1f-a44cb076a49a)
    -h                                                         
    --inputimage <File>  Tiled input image directory           
    --matlab <File>      Path to Matlab base directory ie /../matlab2013a/v81 
    --port <Integer>     Port to run service (default: 8080)   
    --custom <File: trained model>,<String:name>,<String:color>,<String:custom binary|chm|custom tile path>
    