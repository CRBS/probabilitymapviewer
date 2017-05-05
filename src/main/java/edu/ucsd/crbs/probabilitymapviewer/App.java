package edu.ucsd.crbs.probabilitymapviewer;

import edu.ucsd.crbs.probabilitymapviewer.handler.ImageProcessorHandler;
import edu.ucsd.crbs.probabilitymapviewer.html.HtmlPageGenerator;
import edu.ucsd.crbs.probabilitymapviewer.html.SingleImageIndexHtmlPageGenerator;
import edu.ucsd.crbs.probabilitymapviewer.slice.Dm4SliceConverterDaemon;
import edu.ucsd.crbs.probabilitymapviewer.slice.Dm4SliceMonitorImpl;
import edu.ucsd.crbs.probabilitymapviewer.slice.Dm4ToSliceConverter;
import edu.ucsd.crbs.probabilitymapviewer.slice.SimulatedSliceMonitor;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceMonitor;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceMonitorImpl;
import edu.ucsd.crbs.probabilitymapviewer.io.WorkingDirCreator;
import edu.ucsd.crbs.probabilitymapviewer.io.WorkingDirCreatorImpl;
import edu.ucsd.crbs.probabilitymapviewer.job.JobResult;
import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;
import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayerFromPropertiesFactory;
import edu.ucsd.crbs.probabilitymapviewer.server.SegmenterWebServer;
import edu.ucsd.crbs.probabilitymapviewer.server.SegmenterWebServerFactory;
import edu.ucsd.crbs.probabilitymapviewer.slice.ClipStatsSliceIntensityDistributionFactory;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceDir;
import edu.ucsd.crbs.probabilitymapviewer.util.CubeProgressBar;
import edu.ucsd.crbs.probabilitymapviewer.util.CubeProgressBarImpl;
import edu.ucsd.crbs.probabilitymapviewer.util.RunCommandLineProcessImpl;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.io.FileUtils;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Main entry point for Probability Map Viewer application
 *
 */
public class App {

    private static final Logger _log = Logger.getLogger(App.class.getName());

    public static final String INPUT_IMAGE_ARG = "inputimage";
    public static final String ADJUSTED_INPUT_IMAGE_ARG = "adjustedinputimage";    
    public static final String PORT_ARG = "port";
    public static final String DIR_ARG = "dir";
    public static final String NUM_CORES_ARG = "cores";
    public static final String CUSTOM_ARG = "custom";
    public static final String IMAGE_WIDTH_ARG = "imagewidth";
    public static final String IMAGE_HEIGHT_ARG = "imageheight";
    public static final String TITLE_ARG = "title";
    public static final String IMAGE_NAME_ARG = "inputimagename";
    public static final String OVERLAY_OPACITY_ARG = "overlayopacity";
    public static final String TILE_SIZE_ARG = "tilesize";
    public static final String TEMP_DIR_CREATED_FLAG = "TEMP_DIR_CREATED";
    public static final String USE_SGE_ARG = "usesge";
    public static final String SGE_CHM_QUEUE_ARG = "sgechmqueue";
    public static final String SGE_ILASTIK_QUEUE_ARG = "sgeilastikqueue";
    public static final String CONVERT_ARG = "convertbinary";
    public static final String CCDB_ARG = "ccdb";
    public static final String SIMULATE_COLLECTION_ARG = "simulatecollection";
    public static final String COLLECTION_MODE_ARG = "collectionmode";
    public static final String DM4_COLLECTION_MODE_ARG = "dm4collectionmode";
    public static final String COLLECTION_DELAY_ARG = "collectiondelay";
    public static final String EXPECTED_SLICES_ARG = "expectedslices";
    public static final String REFRESH_OVERLAY_DELAY_ARG
            = "refreshoverlaydelay";
    public static final String DISABLE_ANALYZING_TILE_ARG
            = "disableanalyzingtile";

    public static final String DM2MRC_ARG = "dm2mrcbinary";
    public static final String MRC2TIF_ARG = "mrc2tifbinary";
    public static final String CLIP_ARG = "clipbinary";

    public static final String DOWNSAMPLEFACTOR_ARG = "downsamplefactor";
    
    public static final String DM4_CONVERTED_DIR_NAME = "dm4converted";
    public static final String DM4_CONVERTED_DIR_ARG = "dm4converteddir";
    public static final String CONVERT_EQUALIZE_ARG = "convertequalize";
    

    public static ConcurrentLinkedDeque<Callable> tilesToProcess
            = new ConcurrentLinkedDeque<Callable>();
    public static boolean SIGNAL_RECEIVED = false;

    public static List<Future> futureTaskList = Collections
            .synchronizedList(new LinkedList<Future>());

    public static int totalProcessedCount = 0;

    public static long totalJobsRun = 0;
    public static long totalRunTimeOfJobs = 0;

    public static double overloadFactor = 0.5;

    public static String latestSlice = "";
    public static String collectionName = "";
    public static String slicesCollected = "";
    public static String expectedSlices = "";
    public static String cubeImage = "cubes/cube.png";
    public static String LAYER_HANDLER_BASE_DIR = "layerhandlers";
    public static String LAYER_MODEL_BASE_DIR = "layermodels";

    public static String IMAGES_CONTEXT_PATH = "images";

    public static void main(String[] args) {

        Signal.handle(new Signal("INT"), new SignalHandler() {
            // Signal handler method
            public void handle(Signal signal) {
                _log.log(Level.INFO, "Got signal{0} exiting", signal);
                SIGNAL_RECEIVED = true;
            }
        });

        //log everything to all handlers
        _log.getParent().setLevel(Level.ALL);
        for (Handler h : _log.getParent().getHandlers()){
            h.setLevel(Level.ALL);
        }
        
        final String tempDirectory = System.getProperty("java.io.tmpdir")
                + File.separator + UUID.randomUUID().toString();

        SegmenterWebServer sws = null;
        boolean tempDirUsed = false;

        final List<String> helpArgs = Arrays.asList("h", "help", "?");
        try {
            OptionParser parser = new OptionParser() {

                {
                    accepts(INPUT_IMAGE_ARG, "Tiled input image directory.  "
                            + "Tiles must have name in following format: "
                            + "0-r#_c#.png"
                            + " where r# is the 0 offset row number and c# is "
                            + "the 0 offset column number.  Ex: 0-r0_c0.png  "
                            + "Tiles must also be size 128x128")
                            .withRequiredArg().ofType(File.class).required();

                    accepts(TITLE_ARG, "Title for app").withRequiredArg()
                            .ofType(String.class).defaultsTo("Probability Map "
                                    + "Viewer");
                    accepts(IMAGE_NAME_ARG, "Name of input image")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("Base image");
                    accepts(IMAGE_WIDTH_ARG, "Width of image in pixels")
                            .withRequiredArg().ofType(Integer.class)
                            .defaultsTo(50000);
                    accepts(IMAGE_HEIGHT_ARG, "Height of image in pixels")
                            .withRequiredArg().ofType(Integer.class)
                            .defaultsTo(50000);
                    accepts(OVERLAY_OPACITY_ARG, "Opacity of segmentation "
                            + "layers 0-1").withRequiredArg()
                            .ofType(Double.class).defaultsTo(0.3);
                    accepts(PORT_ARG, "Port to run service").withRequiredArg()
                            .ofType(Integer.class).defaultsTo(8080);

                    accepts(DIR_ARG, "Working/Temp directory for server")
                            .withRequiredArg().ofType(File.class)
                            .defaultsTo(new File(tempDirectory));

                    accepts(TILE_SIZE_ARG, "Size of tiles in pixels ie 128 "
                            + "means 128x128").withRequiredArg()
                            .ofType(Integer.class).defaultsTo(128);

                    accepts(CCDB_ARG, "URL for Cell Centered Database (CCDB) "
                            + "web services").withRequiredArg()
                            .ofType(String.class)
                            .defaultsTo("http://surus.crbs.ucsd.edu:8080/");

                    accepts(NUM_CORES_ARG, "Number of concurrent probability map"
                            + "generator scripts to run. ")
                            .withRequiredArg().ofType(Integer.class)
                            .defaultsTo(1);

                    accepts(USE_SGE_ARG, "Use Sun/Oracle Grid Engine (SGE) "
                            + "to run probability map generator script.  "
                            + "If used then --"
                            + DIR_ARG + " must be set to a path on a shared "
                            + "filesystem accessible to all compute nodes");

                    accepts(SGE_CHM_QUEUE_ARG, "Sets the SGE chm queue to use."
                            + "  Only relevant with --" + USE_SGE_ARG)
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("all.q");
                    
                    accepts(CONVERT_ARG, "Sets path to convert command (only "
                            + "works with --" + USE_SGE_ARG + " and --"
                            + DM4_COLLECTION_MODE_ARG + ")")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("convert");
                    
                    accepts(CLIP_ARG, "Sets path to clip command (only "
                            + "works with --" + USE_SGE_ARG + " and --"
                            + DM4_COLLECTION_MODE_ARG + ")")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("clip");

                    accepts(DM2MRC_ARG, "Sets path to dm2mrc command (only "
                            + "works with --" + DM4_COLLECTION_MODE_ARG + ")")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("dm2mrc");

                    accepts(MRC2TIF_ARG, "Sets path to mrc2tif command (only "
                            + "works with --"
                            + DM4_COLLECTION_MODE_ARG + ")")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("mrc2tif");

                    accepts(CUSTOM_ARG, "Custom probability map layer\n(comma "
                            + "delimited)\n"
                            + " *name - Name to display in overlay menu\n"
                            + " *color - can be one of the following: red,green"
                            + ",blue,yellow,magenta,cyan\n"
                            + " *script - path to probability map generator.\n"
                            + "           This script needs to accept 2 "
                            + "           arguments\n"
                            + "           first is input png file and\n"
                            + "           second is path to png file\n"
                            + "           where script should write\n"
                            + "           output.  Output image\n"
                            + "           should be 8-bit grayscale\n"
                            + "           image where 0 is no segmentation\n"
                            + "           and 255 will be shown on UI\n"
                            + " *optargs - optional arguments to pass to "
                            + "            script after the first two"
                            + "            arguments\n")
                            .withRequiredArg().ofType(String.class)
                            .describedAs("name,color,script,optargs");
                    accepts(COLLECTION_DELAY_ARG,
                            "Delay in seconds before loading next image for "
                            + "simluated collection and delay between "
                            + "checks for real collection.  Used with"
                            + " --" + SIMULATE_COLLECTION_ARG + " and"
                            + " --"
                            + COLLECTION_MODE_ARG).withRequiredArg()
                            .ofType(Integer.class).defaultsTo(240);

                    accepts(SIMULATE_COLLECTION_ARG,
                            "Simulates collection with new image every "
                            + "(value of --" + COLLECTION_DELAY_ARG
                            + " seconds using slice_### folders that "
                            + "exist in --" + INPUT_IMAGE_ARG
                            + " directory.");

                    accepts(DM4_COLLECTION_MODE_ARG,
                            "Collection mode that tells probabilitymapviewer to "
                            + "search for new *.dm4 files in --"
                            + INPUT_IMAGE_ARG + " directory.");

                    accepts(DOWNSAMPLEFACTOR_ARG,
                            "Sets downsampling factor ie value of 2 means to"
                            + "reduce image size by half.  Used with"
                            + "--" + DM4_COLLECTION_MODE_ARG).withRequiredArg()
                            .ofType(Integer.class).defaultsTo(4);

                    accepts(COLLECTION_MODE_ARG, "Runs Segmenter in Collection "
                            + "mode which looks for new slice_### folders in --"
                            + INPUT_IMAGE_ARG + " directory.");

                    accepts(EXPECTED_SLICES_ARG, "Expected number of slices "
                            + "in collection used with --"
                            + SIMULATE_COLLECTION_ARG + " and --"
                            + COLLECTION_MODE_ARG)
                            .withRequiredArg().ofType(Integer.class)
                            .defaultsTo(1000);

                    accepts(REFRESH_OVERLAY_DELAY_ARG, "Delay in seconds "
                            + "between overlay refreshes on webbrowser")
                            .withRequiredArg().ofType(Integer.class)
                            .defaultsTo(10);
                    accepts(DISABLE_ANALYZING_TILE_ARG, "If set app no longer "
                            + "denotes with less opaque tile which tiles are "
                            + "being processed");
                    
                    accepts(CONVERT_EQUALIZE_ARG, "If set, adds -equalize "
                            + "parameter to convert command to perform "
                            + "histogram equalization.  Used with --"
                            + DM4_COLLECTION_MODE_ARG);

                    acceptsAll(helpArgs, "Show Help").forHelp();
                }
            };

            OptionSet optionSet = null;
            try {
                optionSet = parser.parse(args);
            } catch (OptionException oe) {
                System.err.println("\nThere was an error parsing arguments: "
                        + oe.getMessage() + "\n\n");
                parser.printHelpOn(System.err);
                System.exit(1);
            }

            //help check
            for (String helpArgName : helpArgs) {
                if (optionSet.has(helpArgName)) {
                    System.out.println("\n\nHelp\n\n");
                    parser.printHelpOn(System.out);
                    System.exit(2);
                }
            }

            Properties props = getPropertiesFromCommandLine(optionSet);

            WorkingDirCreator wDirCreator = new WorkingDirCreatorImpl();
            wDirCreator.createWorkingDir(props);

            CustomLayerFromPropertiesFactory layerFac
                    = new CustomLayerFromPropertiesFactory();
            List<CustomLayer> layers = layerFac.getCustomLayers(props);

            if (layers != null && !layers.isEmpty()) {
                for (CustomLayer cl : layers) {
                    File layerDir
                            = new File(props.getProperty(LAYER_HANDLER_BASE_DIR)
                                    + File.separator + cl.getVarName());
                    if (!layerDir.exists()) {
                        layerDir.mkdirs();
                    }
                }
            }

            if (props.getProperty(TEMP_DIR_CREATED_FLAG, "false")
                    .equals("true")) {
                tempDirUsed = true;
            }

            copyOverLeafletLibrary(props);

            copyOverJqueryLibrary(props);

            //if collection mode is enabled then set the latest slice path
            SliceMonitor sliceMonitor = null;
            CubeProgressBar cubeProgressBar = new CubeProgressBarImpl(props);
            
            if (props.getProperty(App.SIMULATE_COLLECTION_ARG, "false")
                    .equals("true")) {
                sliceMonitor = new SimulatedSliceMonitor(props);
            } else if (props.getProperty(App.COLLECTION_MODE_ARG, "false")
                    .equals("true")) {
                _log.log(Level.INFO, "Creating standard slice monitor for "
                        + "collection mode");
                sliceMonitor = new SliceMonitorImpl(props, null);
            } else if (props.getProperty(App.DM4_COLLECTION_MODE_ARG, "false")
                    .equals("true")) {
                _log.log(Level.INFO, "Creating Dm4 slice monitor for "
                        + "collection mode");
                sliceMonitor = new Dm4SliceMonitorImpl(
                        new Dm4SliceConverterDaemon(props,
                                new Dm4ToSliceConverter(props,
                                        new ClipStatsSliceIntensityDistributionFactory("clip",new RunCommandLineProcessImpl()))));
            }

            if (sliceMonitor != null) {
                App.expectedSlices = props.getProperty(App.EXPECTED_SLICES_ARG,
                        "");
            }

            generateIndexHtmlPage(props, layers);
            int numCores = Integer.parseInt(props.getProperty(NUM_CORES_ARG));
            ExecutorService es = getExecutorService(numCores);

            sws = getWebServer(es, props, layers);
            if (sws == null){
              _log.log(Level.SEVERE," unable to get web server");   
            }
            _log.log(Level.INFO," Starting up webserver");
            sws.getServer().start();
            int desiredLoad = numCores + (int) ((double) numCores * overloadFactor);
            int prevTotalProcessedCount = -1;
            long iterationCounter = 0;
            int collectionDelay = Integer.parseInt(props.getProperty(COLLECTION_DELAY_ARG));
            _log.log(Level.INFO,"Entering while loop");
            while (SIGNAL_RECEIVED == false && (sws.getServer().isStarting() || 
                    sws.getServer().isRunning())) {
                //one idea is to have all the image processors dump to a single list
                //and to have this loop track the completed job list and running job list
                //we can then just grab the newest items from the list and pass them to the
                //executor service
                totalProcessedCount += removeCompletedTasks(futureTaskList);

                //if this is a collection check for new # directory in input image
                //if found update status.latestslice to this slice
                if (iterationCounter % collectionDelay == 0) {
                    //_log.log(Level.INFO,"Checking for new slices");
                    updateSlices(sws, sliceMonitor, cubeProgressBar);
                }

                if (totalProcessedCount != prevTotalProcessedCount) {
                    _log.log(Level.INFO, "Total Processed Count is {0}, Future "
                            + "Task List Size: {1}, and desired load is {2} "
                            + "and tiles to process is {3}",
                            new Object[]{totalProcessedCount,
                                futureTaskList.size(), desiredLoad,
                                tilesToProcess.size()});
                }
                prevTotalProcessedCount = totalProcessedCount;
                int size = futureTaskList.size();

                if (size < desiredLoad) {

                    while (!tilesToProcess.isEmpty() && size < desiredLoad) {
                        _log.log(Level.INFO, "Submitting task");
                        futureTaskList.add(es.submit(tilesToProcess.remove()));
                        size++;
                    }
                }
                threadSleep(1000);
                iterationCounter++;
            }

            sws.getServer().stop();
            es.shutdownNow();

            _log.log(Level.INFO, "Sleeping for 5 seconds to let things cool "
                    + "down");
            threadSleep(5000);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            if (sws != null){
              if (sws.getServer() != null) {
                  _log.log(Level.INFO, "Shutting down webserver");
                  sws.getServer().destroy();
              }
            }

            if (tempDirUsed) {
                File tempDir = new File(tempDirectory);
                if (tempDir.exists()) {
                    _log.log(Level.INFO, "\n\nDeleting temp directory: "
                            + tempDirectory + "\n");
                    try {
                        FileUtils.deleteDirectory(tempDir);
                    } catch (IOException ioex) {
                        _log.log(Level.WARNING, "Unable to delete{0} : {1}",
                                new Object[]{tempDir.getAbsolutePath(),
                                    ioex.getMessage()});
                    }
                }
            }
        }
    }

    public static void updateSlices(SegmenterWebServer sws, 
            SliceMonitor sliceMonitor,
            CubeProgressBar cubeProgressBar) throws Exception {
        if (sliceMonitor == null) {
            return;
        }

        List<SliceDir> slices = sliceMonitor.getSlices();
        if (slices == null || slices.isEmpty()) {
            App.latestSlice = "";
            return;
        }

        Properties props = sliceMonitor.getCollectionInformation();
        if (props != null) {
            App.collectionName = props.getProperty("name", "");
        }
        
        SliceDir latestSlice = slices.get(slices.size() - 1);
        //see if the slice changed if so update the App.### values
        //so all the handlers know about the change.
        if (!latestSlice.getSliceName().equals(App.latestSlice)) {
            _log.log(Level.FINE, "New image " + latestSlice.getSliceName() +
                    " replacing " + App.latestSlice);
            App.latestSlice = latestSlice.getSliceName();
            App.tilesToProcess.clear();
            App.slicesCollected = Integer.toString(slices.size());
            List<ImageProcessorHandler> iphlist = sws.getImageProcHandlers();
            if (iphlist != null){
                for (ImageProcessorHandler iph : iphlist){
                    iph.clearProcessedImages();
                }
            }
            if (cubeProgressBar != null) {
                App.cubeImage = cubeProgressBar.getCubeImage(slices.size());
            }
        }
    }

    /**
     * Converts <b>optionSet</b> to a {@link Properties} object
     *
     * @param optionSet
     * @return {@link Properties} set to values from command line and or
     * configuration file
     * @throws Exception
     */
    public static Properties getPropertiesFromCommandLine(OptionSet optionSet)
            throws Exception {

        Properties props = new Properties();

        //@TODO need to fail if multiple custom layers have the same name cause
        //this screws everything up. 
        List<String> customList = (List<String>) optionSet.valuesOf(CUSTOM_ARG);
        int counter = 1;
        for (String s : customList) {
            props.setProperty(CUSTOM_ARG + "." + counter, s);
            counter++;
        }

        if (optionSet.has(USE_SGE_ARG)) {
            props.setProperty(USE_SGE_ARG, "true");
        } else {
            props.setProperty(USE_SGE_ARG, "false");
        }

        if (optionSet.has(DISABLE_ANALYZING_TILE_ARG)) {
            props.setProperty(DISABLE_ANALYZING_TILE_ARG, "true");
        } else {
            props.setProperty(DISABLE_ANALYZING_TILE_ARG, "false");
        }

        int collectionModeCounter = 0;
        
        if (optionSet.has(SIMULATE_COLLECTION_ARG)){
            collectionModeCounter++;
        }
        if (optionSet.has(COLLECTION_MODE_ARG)) {
            collectionModeCounter++;
        }
        if (optionSet.has(DM4_COLLECTION_MODE_ARG)){
            collectionModeCounter++;
        }
        if (collectionModeCounter>1){
            throw new Exception("Only one of the following can be set: --" 
                    + SIMULATE_COLLECTION_ARG + ", --"
                    + COLLECTION_MODE_ARG + ", --" + DM4_COLLECTION_MODE_ARG);
        }

        if (optionSet.has(SIMULATE_COLLECTION_ARG)) {
            props.setProperty(SIMULATE_COLLECTION_ARG, "true");
        } else {
            props.setProperty(SIMULATE_COLLECTION_ARG, "false");
        }

        if (optionSet.has(COLLECTION_MODE_ARG)) {
            props.setProperty(COLLECTION_MODE_ARG, "true");
        } else {
            props.setProperty(COLLECTION_MODE_ARG, "false");
        }

        if (optionSet.has(DM4_COLLECTION_MODE_ARG)) {
            props.setProperty(DM4_COLLECTION_MODE_ARG, "true");
        } else {
            props.setProperty(DM4_COLLECTION_MODE_ARG, "false");
        }
        
        if (optionSet.has(CONVERT_EQUALIZE_ARG)){
            _log.log(Level.CONFIG, "Setting "+ CONVERT_EQUALIZE_ARG 
                    + " to true");
            props.setProperty(CONVERT_EQUALIZE_ARG, "true");
        } else {
            props.setProperty(CONVERT_EQUALIZE_ARG, "false");
        }
        
        props.setProperty(DM2MRC_ARG, (String) optionSet.valueOf(DM2MRC_ARG));
        props.setProperty(MRC2TIF_ARG, (String) optionSet.valueOf(MRC2TIF_ARG));
        props.setProperty(CLIP_ARG, (String) optionSet.valueOf(CLIP_ARG));
        props.setProperty(DOWNSAMPLEFACTOR_ARG,
                ((Integer) optionSet.valueOf(DOWNSAMPLEFACTOR_ARG)).toString());
        props.setProperty(REFRESH_OVERLAY_DELAY_ARG,
                ((Integer) optionSet.valueOf(REFRESH_OVERLAY_DELAY_ARG))
                .toString());
        props.setProperty(EXPECTED_SLICES_ARG,
                ((Integer) optionSet.valueOf(EXPECTED_SLICES_ARG)).toString());
        props.setProperty(COLLECTION_DELAY_ARG,
                ((Integer) optionSet.valueOf(COLLECTION_DELAY_ARG)).toString());
        props.setProperty(CONVERT_ARG, (String) optionSet.valueOf(CONVERT_ARG));
        props.setProperty(SGE_CHM_QUEUE_ARG, (String) optionSet.valueOf(SGE_CHM_QUEUE_ARG));
        props.setProperty(IMAGE_NAME_ARG, (String) optionSet.valueOf(IMAGE_NAME_ARG));
        props.setProperty(TITLE_ARG, (String) optionSet.valueOf(TITLE_ARG));
        props.setProperty(TILE_SIZE_ARG, ((Integer) optionSet.valueOf(TILE_SIZE_ARG)).toString());
        props.setProperty(OVERLAY_OPACITY_ARG, ((Double) optionSet.valueOf(OVERLAY_OPACITY_ARG)).toString());
        props.setProperty(IMAGE_WIDTH_ARG, ((Integer) optionSet.valueOf(IMAGE_WIDTH_ARG)).toString());
        props.setProperty(IMAGE_HEIGHT_ARG, ((Integer) optionSet.valueOf(IMAGE_HEIGHT_ARG)).toString());
        props.setProperty(NUM_CORES_ARG, ((Integer) optionSet.valueOf(NUM_CORES_ARG)).toString());
        props.setProperty(INPUT_IMAGE_ARG, ((File) optionSet.valueOf(INPUT_IMAGE_ARG)).getAbsolutePath());
        props.setProperty(PORT_ARG, ((Integer) optionSet.valueOf(PORT_ARG)).toString());
        props.setProperty(DIR_ARG, ((File) optionSet.valueOf(DIR_ARG)).getAbsolutePath());
        
        if (props.getProperty(App.DM4_COLLECTION_MODE_ARG,"false").equals("true")){
            props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG,props.getProperty(DIR_ARG) + File.separator +
                  App.DM4_CONVERTED_DIR_NAME);
        }
        else {
            props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG,
                    props.getProperty(INPUT_IMAGE_ARG));
        }
        
        props.setProperty(LAYER_HANDLER_BASE_DIR, props.getProperty(DIR_ARG)
                + File.separator + LAYER_HANDLER_BASE_DIR);

        props.setProperty(LAYER_MODEL_BASE_DIR, props.getProperty(DIR_ARG)
                + File.separator + LAYER_MODEL_BASE_DIR);

        props.setProperty(CCDB_ARG, (String) optionSet.valueOf(CCDB_ARG));

        _log.log(Level.CONFIG, props.toString());
        return props;
    }

    public static void copyOverLeafletLibrary(Properties props) throws Exception {
        String leaflet = "leaflet-0.7.3";
        File leafletDir = new File(props.getProperty(DIR_ARG) + File.separator + leaflet);

        File leafletImagesDir = new File(leafletDir.getAbsolutePath() + File.separator + "images");
        leafletImagesDir.mkdirs();

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + leaflet + "/leaflet.js"), new File(leafletDir.getAbsolutePath() + File.separator + "leaflet.js"));
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + leaflet + "/leaflet.css"), new File(leafletDir.getAbsolutePath() + File.separator + "leaflet.css"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + leaflet + "/images/layers-2x.png"), new File(leafletImagesDir.getAbsolutePath() + File.separator + "layers-2x.png"));
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + leaflet + "/images/layers.png"), new File(leafletImagesDir.getAbsolutePath() + File.separator + "layers.png"));
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + leaflet + "/images/marker-icon-2x.png"), new File(leafletImagesDir.getAbsolutePath() + File.separator + "marker-icon-2x.png"));
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + leaflet + "/images/marker-icon.png"), new File(leafletImagesDir.getAbsolutePath() + File.separator + "marker-icon.png"));
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + leaflet + "/images/marker-shadow.png"), new File(leafletImagesDir.getAbsolutePath() + File.separator + "marker-shadow.png"));

    }

    public static void copyOverJqueryLibrary(Properties props) throws Exception {
        String jquery = "jquery-1.11.2";
        String jqueryFileName = jquery + ".min.js";
        File jqueryDir = new File(props.getProperty(DIR_ARG) + File.separator + jquery);
        jqueryDir.mkdirs();
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jquery + "/" + jqueryFileName), new File(jqueryDir.getAbsolutePath()
                        + File.separator + jqueryFileName));

        String jqueryUI = "jquery-ui-1.11.2";
        String jqueryUIPrefix = "jquery-ui";

        File jqueryUIDir = new File(props.getProperty(DIR_ARG) + File.separator + jqueryUI);

        jqueryUIDir.mkdirs();
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUI + "/" + jqueryUIPrefix + ".min.css"), new File(jqueryUIDir.getAbsolutePath()
                        + File.separator + jqueryUIPrefix + ".min.css"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUI + "/" + jqueryUIPrefix + ".min.js"), new File(jqueryUIDir.getAbsolutePath()
                        + File.separator + jqueryUIPrefix + ".min.js"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUI + "/" + jqueryUIPrefix + ".structure.min.css"), new File(jqueryUIDir.getAbsolutePath()
                        + File.separator + jqueryUIPrefix + ".structure.min.css"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUI + "/" + jqueryUIPrefix + ".theme.min.css"), new File(jqueryUIDir.getAbsolutePath()
                        + File.separator + jqueryUIPrefix + ".theme.min.css"));

        File imagesDir = new File(jqueryUIDir.getAbsolutePath() + File.separator + "images");
        imagesDir.mkdirs();
        String jqueryUIImages = jqueryUI + "/images";

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_diagonals-thick_18_b81900_40x40.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_diagonals-thick_18_b81900_40x40.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_diagonals-thick_20_666666_40x40.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_diagonals-thick_20_666666_40x40.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_flat_10_000000_40x100.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_flat_10_000000_40x100.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_glass_100_f6f6f6_1x400.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_glass_100_f6f6f6_1x400.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_glass_100_fdf5ce_1x400.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_glass_100_fdf5ce_1x400.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_glass_65_ffffff_1x400.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_glass_65_ffffff_1x400.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_gloss-wave_35_f6a828_500x100.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_gloss-wave_35_f6a828_500x100.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_highlight-soft_100_eeeeee_1x100.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_highlight-soft_100_eeeeee_1x100.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-bg_highlight-soft_75_ffe45c_1x100.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-bg_highlight-soft_75_ffe45c_1x100.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-icons_222222_256x240.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-icons_222222_256x240.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-icons_228ef1_256x240.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-icons_228ef1_256x240.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-icons_ef8c08_256x240.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-icons_ef8c08_256x240.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-icons_ffd27a_256x240.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-icons_ffd27a_256x240.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"
                + jqueryUIImages + "/ui-icons_ffffff_256x240.png"), new File(imagesDir.getAbsolutePath()
                        + File.separator + "ui-icons_ffffff_256x240.png"));

    }

    public static SegmenterWebServer getWebServer(ExecutorService es, 
            Properties props, List<CustomLayer> layers) throws Exception {

        SegmenterWebServerFactory serverFac = new SegmenterWebServerFactory();

        SegmenterWebServer sws = serverFac.getSegmenterWebServer(es, props, 
                layers);

        System.out.println("\n\n\tOpen a browser to this URL: http://localhost:"
                + props.getProperty(PORT_ARG) + "\n\n");

        return sws;
    }

    public static void generateIndexHtmlPage(Properties props, 
            List<CustomLayer> layers) throws Exception {

        HtmlPageGenerator pageGenerator = new SingleImageIndexHtmlPageGenerator(props, layers);
        pageGenerator.generateHtmlPage(props.getProperty(DIR_ARG));
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_red.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_red.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_blue.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_blue.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_green.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_green.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_cyan.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_cyan.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_yellow.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_yellow.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_magenta.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_magenta.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_red_50opac.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_red_50opac.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_blue_50opac.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_blue_50opac.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_green_50opac.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_green_50opac.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_cyan_50opac.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_cyan_50opac.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_yellow_50opac.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_yellow_50opac.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing_magenta_50opac.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "analyzing_magenta_50opac.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/powericon.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "powericon.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/powericonmouseover.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "powericonmouseover.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/layersadd.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "layersadd.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/layersaddmouseover.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "layersaddmouseover.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/refresh.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "refresh.png"));

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/refreshmouseover.png"),
                new File(props.getProperty(DIR_ARG) + File.separator + "refreshmouseover.png"));

        copyOverCubes(props);
    }

    public static void copyOverCubes(Properties props) throws Exception {

        String cubes = "cubes";
        File cubesDir = new File(props.getProperty(DIR_ARG) + File.separator + cubes);
        cubesDir.mkdirs();

        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + cubes
                + "/cube.png"),
                new File(cubesDir.getAbsolutePath() + File.separator + "cube.png"));

        for (int i = 10; i <= 100; i += 10) {
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + cubes
                    + "/cube" + i + ".png"),
                    new File(cubesDir.getAbsolutePath() + File.separator + "cube" + i + ".png"));
        }
    }

    static void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
        }
    }

    /**
     * This method creates an {@link java.util.concurrent.ExecutorService} with
     * a thread pool size set to percentOfCoresToUse passed in X
     * {@link java.lang.Runtime.getRuntime()#availableProcessors() Runtime.getRuntime().availableProcessors()}
     * or 1 if value is less then 1.
     *
     * @param percentOfCoresToUse The percentage of cores to use and should be a
     * value between 0 and 1. For example 0.9 means 90%
     * @return ExecutorService
     */
    static ExecutorService getExecutorService(int numCoresToUse) {
        _log.log(Level.INFO, "Using {0} cores", numCoresToUse);
        return Executors.newFixedThreadPool(numCoresToUse);
    }

    static int removeCompletedTasks(List<Future> taskList) {
        if (taskList == null) {
            return 0;
        }
        Future f;
        int removeCount = 0;
        JobResult jobResult = null;
        Iterator<Future> itr = taskList.iterator();
        while (itr.hasNext()) {
            f = itr.next();
            if (f.isDone() || f.isCancelled()) {
                if (f.isDone()) {
                    try {
                        jobResult = (JobResult) f.get();
                        if (jobResult != null) {
                            App.totalJobsRun++;
                            App.totalRunTimeOfJobs += jobResult.getRunTimeInMilliseconds();
                        }
                    } catch (Exception ex) {

                    }

                }
                removeCount++;
                itr.remove();

            }
        }
        return removeCount;
    }

}
