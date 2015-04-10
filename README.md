[chm]: http://www.sci.utah.edu/software/chm.html
[imagemagick]: http://www.imagemagick.org/
[matlabruntime]: http://www.mathworks.com/products/compiler/mcr/
[jetty]: http://eclipse.org/jetty/
[maven]: http://maven.apache.org/

segmenter
=====================

Segmenter is an application that lets a caller perform image
segmentation in real time on a previously tiled image.  Segmentation is
performed using [Cascaded Hierarchical Model (CHM)][chm].  

This application spins up a [Jetty][jetty] web server on the local host and 
directs the user to open a browser to view the image with segmentation overlays.


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

    java -jar target/segmenter-REPLACEWITHVERSION-jar-with-dependencies.jar

**REPLACEWITHVERSION** must be set to the version

Example output with -h (for help) flag:

Help


Option (* = required)                Description                            
---------------------                -----------                            
-?, -h, --help                       Show Help                              
--ccdb                               URL for Cell Centered Database (CCDB)  
                                       web services (default: http://surus. 
                                       crbs.ucsd.edu:8080/)                 
--chmbin <File>                      Path to CHM bin directory              
--collectiondelay <Integer>          Delay in seconds before loading next   
                                       image for simluated collection and   
                                       delay between checks for real        
                                       collection.  Used with --            
                                       simulatecollection and --            
                                       collectionmode (default: 240)        
--collectionmode                     Runs Segmenter in Collection mode      
                                       which looks for new slice_###        
                                       folders in --inputimage directory.   
--convertbinary                      Sets path to convert command (only     
                                       works with --usesge) (default:       
                                       convert)                             
--cores <Integer>                    Number of concurrent CHM jobs to run.  
                                       Each job requires 1gb ram. (default: 
                                       1)                                   
--custom <trained model,name,color,  Custom Segmentation layer (comma       
  binary>                              delimited)                           
                                      *trained model - path to chm trained  
                                       model                                
                                      *name - Name to display in overlay    
                                       menu                                 
                                      *color - can be one of the following: 
                                       red,green,blue,yellow,magenta,cyan   
                                      *binary - Set to 'chm' for now        
--dir <File>                         Working/Temp directory for server      
                                       (default: /tmp/d2f2f2c9-d738-464c-   
                                       bc34-b3a68aa5fb57)                   
--ilastik <File>                     Sets path to Ilastik directory ie      
                                       ilastik-1.1.2-Linux (default:        
                                       /var/tmp/ilastik-1.1.2-Linux)        
--imageheight <Integer>              Height of image in pixels (default:    
                                       50000)                               
--imagewidth <Integer>               Width of image in pixels (default:     
                                       50000)                               
* --inputimage <File>                Tiled input image directory.  Tiles    
                                       must have name in following format:  
                                       0-r#_c#.png where r# is the 0 offset 
                                       row number and c# is the 0 offset    
                                       column number.  Ex: 0-r0_c0.png      
                                       Tiles must also be size 128x128      
--inputimagename                     Name of input image (default: Base     
                                       image)                               
* --matlab <File>                    Path to Matlab base directory ie /..   
                                       /matlab2013a/v81                     
--overlayopacity <Double>            Opacity of segmentation layers 0-1     
                                       (default: 0.3)                       
--port <Integer>                     Port to run service (default: 8080)    
--sgechmqueue                        Sets the SGE chm queue to use.  Only   
                                       relevant with --usesge (default: all.
                                       q)                                   
--sgeilastikqueue                    Sets the SGE Ilastik queue to use.     
                                       Only relevant with --usesge          
                                       (default: all.q)                     
--simulatecollection                 Simulates collection with new image    
                                       every (value of --collectiondelay    
                                       seconds using slice_### folders that 
                                       exist in --inputimage directory.     
--tilesize <Integer>                 Size of tiles in pixels ie 128 means   
                                       128x128 (default: 128)               
--title                              Title for app (default: Segmenter)     
--usesge                             Use Sun/Oracle Grid Engine (SGE) to    
                                       run CHM.  If used then --dir must be 
                                       set to a path on a shared filesystem 
                                       accessible to all compute nodes      

    
