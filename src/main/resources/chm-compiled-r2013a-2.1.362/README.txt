Cascaded Hierarchical Model Automatic Segmentation Algorithm
============================================================

(version: 2.1.362 built 05 May 2014)

This is an algorithm designed for automatic segmention of images, including
natural scene processing and cellular structures in electron microscopy data.

If you use this program please cite the following paper:
M. Seyedhosseini, M. Sajjadi, and T. Tasdizen. Image segmentation with cascaded hierarchical models and logistic disjunctive normal networks. In ICCV 2013.

This is the compiled version meaning you don't need a MATLAB license to run it.


Basic Usage
===========
The two main entry points are `CHM_train.sh` and `CHM_test.sh`. All optional
arguments must be at the end.


Prerequisites
-------------
You will need to install the MATLAB Compiler Runtime R2013a (v8.1) from
Mathworks (http://www.mathworks.com/products/compiler/mcr/) on a Linux
64-bit machine. You can also use MATLAB R2013a with the Image Processing
Toolbox installed (without needing a license). Then make sure the 'matlab'
command is on your PATH (basically `which matlab` works on the command-line).


Training
--------
The first thing you need to run in `CHM_train.sh`. In the most basic usage it
takes a set of training data images (grayscale) and a set of training label
images (0=no label, 1=labeled). These file sets are specified as a comma
seperated list of the following:
 * path to a folder            - all PNGs and TIFFs in that folder
 * path to a file              - only that file
 * path with numerical pattern - get all files matching the pattern
   pattern must have #s in it and end with a semicolon and number range
   the #s are replaced by the values at the end with leading zeros
   example: `in/####.png;5-15` would do in/0005.png through in/0015.png  
   Note: the semicolon needs to be escaped or in quotes in some shells
 * path with wildcard pattern  - get all files matching the pattern
   pattern has * in it which means any number of any characters
   example: `in/lbl_*.tif` does all TIFF images starting with lbl_ in "in"
   Note: the asterisk needs to be escaped or in quotes in some shells


All training images must be the same size.

Training will take on the order of a day to complete and require lots of
memory (50-150 GB) depending on the size of the dataset. Recommended that
you use between 500x500 and 1000x1000 size training images with a total of
20-40 slices.

If training fails for some reason (such as running out of memory) you can
restart from the last completed stage/level by using the `-r` flag. The `-r`
flag has no effect if the model directory does not exist/is empty.


Testing
-------
`CHM_test.sh` then takes the model generated with CHM_train and creates
probability maps for how likely a pixel is the same thing as what was labelled
during training. The basic usage is to give a set of data images to process and
the output directory. The set of data images uses the same format as training
image inputs. The output must be a directory though. This will take about 5-15
min per training-image-sized region and 5-15 GB of RAM depending on data image
and training data size.

Make sure the test images are comparable to the training images: same pixel
size, same acquitision parameters, same source (e.g. brain region). Testing
images do not need to be all the same size.


Model Directory
---------------
The output model is by default stored in ./temp. The only files required to
save are the .mat files in the root directory (the subdirectories contain
actual temporary files).

To change the model directory use by either CHM_train or CHM_test you can use
the `-m` argument.


Quality of Results
------------------
Many factors influence the quality of results, including some that are still be
investigated.

The quality of the training data and labels is by far the most important.
Within the training set, every example of your feature must be labelled.
Additionally, a large portion of the data must be that feature. For some
datasets/features applying histogram equalization to a uniform histogram can
help significantly. See the HistogramEqualize tool in wrappers/image-proc.

During training you can also change the number of stages and levels of training
that are performed using the `-S` and `-L` arguments respectively (they default
to 2 and 4). It currently seems unlikely that values larger than this will be
necessary, but in some cases smaller values provide better results than
larger values (smaller values also run faster).

During testing there is less adjustments you can make for quality and the
defaults provided should be good in most cases. First, if you are seeing edge
effects (either along the edge or in the middle of the image) you have to
increase the amount of overlap between tiles using the `-o` argument. The
default is 50 pixels. Additionally, the input images have their histograms
equalized to the training data histogram. To prevent this from happening use
the `-h` flag (this is probably only needed if you perform some equalization on
the images youself).


Speeding It Up
--------------
Training can be sped up by reducing the size of the training data and/or
reducing the number of stages and levels trained on. This has to be done
carefully as to not reduce quality. Typically, large structures can have the
data binned (e.g. we bin by 8 for neucli of cells) which greatly reduces the
training data size. Additionally, we have been experimenting with lower the
number of levels, and in many cases it barely effect results and even some
cases produces better results.

If training goes faster, then testing with that model will go faster as well.

Testing has a lot more room for speed-ups since it can be heavily parallelized.
First, in the basic usage, testing will attempt to use all physical cores (up
to 12) for the bulk of each image (note that for the first 3 "tiles" of each
image it will not be done in parallel).

If you are trying to run CHM_test on a single very large image or have access
to a cluster, you can divide single images down even further by using -t to
specify a tile to process. You can chain multiple `-t` to process multiple
tiles. Be careful to use seperate output directories for each tile-set in this
case. To combine the images after they have been run you can use ImageMagick:

    convert -compose plus tiles1.png tiles2.png -composite tiles3.png -composite ... output.png

Another way to get some speed up is to reduce the overlap between tiles with
`-o`. The default is 50 pixels and in many cases 25 is probably sufficient.
However if this causes edges effects to appear (either at the edges or middle
of the image) then you went too small.

The time required for testing is directly poportional to the following formula:

    CEIL(test_image_width / (training_image_width - 2*overlap_width)) * CEIL(test_image_height / (training_image_height - 2*overlap_height))

Due to the CEIL, you may as well make the overlap width and height as large as
possible before hitting the next integer.


Special Options
---------------
You can specify the MATLAB/MCR location using the -M argument. It also looks
for two environmental variables: MCR_DIR (same as -M argument) and
MCR_CACHE_ROOT which sets where the program should unpack itself to, and
should be some local file path (not on the network). The cache dir defaults to
/tmp/mcr_cache_root_$USER. Setting it to a non-existent directory disables
caching.
