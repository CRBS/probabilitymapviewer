package edu.ucsd.crbs.segmenter;

import edu.ucsd.crbs.segmenter.html.HtmlPageGenerator;
import edu.ucsd.crbs.segmenter.html.SingleImageIndexHtmlPageGenerator;
import edu.ucsd.crbs.segmenter.slice.Dm4SliceConverterDaemon;
import edu.ucsd.crbs.segmenter.slice.Dm4SliceMonitorImpl;
import edu.ucsd.crbs.segmenter.slice.Dm4ToSliceConverter;
import edu.ucsd.crbs.segmenter.slice.SimulatedSliceMonitor;
import edu.ucsd.crbs.segmenter.slice.SliceMonitor;
import edu.ucsd.crbs.segmenter.slice.SliceMonitorImpl;
import edu.ucsd.crbs.segmenter.io.WorkingDirCreator;
import edu.ucsd.crbs.segmenter.io.WorkingDirCreatorImpl;
import edu.ucsd.crbs.segmenter.job.JobResult;
import edu.ucsd.crbs.segmenter.layer.CustomLayer;
import edu.ucsd.crbs.segmenter.layer.CustomLayerFromPropertiesFactory;
import edu.ucsd.crbs.segmenter.server.SegmenterWebServer;
import edu.ucsd.crbs.segmenter.server.SegmenterWebServerFactory;
import edu.ucsd.crbs.segmenter.slice.SliceDir;
import edu.ucsd.crbs.segmenter.util.CubeProgressBar;
import edu.ucsd.crbs.segmenter.util.CubeProgressBarImpl;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.io.FileUtils;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger _log = Logger.getLogger(App.class.getName());

    public static final String INPUT_IMAGE_ARG = "inputimage";
    public static final String CHM_BIN_ARG = "chmbin";
    public static final String ILASTIK_ARG = "ilastik";
    public static final String PORT_ARG = "port";
    public static final String DIR_ARG = "dir";
    public static final String MATLAB_ARG = "matlab";
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
    public static final String INCLUDE_DM4_ARG = "includedm4";
    
    public static final String COLLECTION_DELAY_ARG = "collectiondelay";
    public static final String EXPECTED_SLICES_ARG = "expectedslices";
    public static final String REFRESH_OVERLAY_DELAY_ARG = 
            "refreshoverlaydelay";
    public static final String DISABLE_ANALYZING_TILE_ARG = 
            "disableanalyzingtile";
    
    public static final String DM2MRC_ARG = "dm2mrcbinary";
    public static final String MRC2TIF_ARG = "mrc2tifbinary";
    
    public static final String DOWNSAMPLEFACTOR_ARG = "downsamplefactor";
    

    public static ConcurrentLinkedDeque<Callable> tilesToProcess = 
            new ConcurrentLinkedDeque<Callable>();
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
                            .ofType(String.class).defaultsTo("Segmenter");
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
                    accepts(CHM_BIN_ARG, "Path to CHM bin directory")
                            .withRequiredArg().ofType(File.class);
                    accepts(MATLAB_ARG, "Path to Matlab base directory ie "
                            + "/../matlab2013a/v81").withRequiredArg()
                            .ofType(File.class).required();
                    
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
                    
                    accepts(NUM_CORES_ARG, "Number of concurrent CHM jobs to "
                            + "run.  Each job requires 1gb ram.")
                            .withRequiredArg().ofType(Integer.class)
                            .defaultsTo(1);
                    
                    accepts(USE_SGE_ARG, "Use Sun/Oracle Grid Engine (SGE) "
                            + "to run CHM.  If used then --"
                            + DIR_ARG + " must be set to a path on a shared "
                            + "filesystem accessible to all compute nodes");
                    
                    accepts(SGE_CHM_QUEUE_ARG, "Sets the SGE chm queue to use."
                            + "  Only relevant with --" + USE_SGE_ARG)
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("all.q");
                    
                    accepts(SGE_ILASTIK_QUEUE_ARG, "Sets the SGE Ilastik queue "
                            + "to use.  Only relevant with --" + USE_SGE_ARG)
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("all.q");
                    
                    accepts(CONVERT_ARG, "Sets path to convert command (only "
                            + "works with --" + USE_SGE_ARG + " and --"
                            + COLLECTION_MODE_ARG + ")")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("convert");
                    
                    accepts(DM2MRC_ARG, "Sets path to dm2mrc command (only "
                            + "works with --" + COLLECTION_MODE_ARG + " and --"
                            + INCLUDE_DM4_ARG + ")")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("dm2mrc");
                    
                    accepts(MRC2TIF_ARG, "Sets path to mrc2tif command (only "
                            + "works with --" + COLLECTION_MODE_ARG + " and --"
                            + INCLUDE_DM4_ARG + ")")
                            .withRequiredArg().ofType(String.class)
                            .defaultsTo("mrc2tif");
                    
                    accepts(CUSTOM_ARG, "Custom Segmentation layer (comma "
                            + "delimited)\n"
                            + " *trained model - path to chm trained model\n"
                            + " *name - Name to display in overlay menu\n"
                            + " *color - can be one of the following: red,green"
                            + ",blue,yellow,magenta,cyan\n"
                            + " *binary - Set to 'chm' for now\n")
                            .withRequiredArg().ofType(String.class)
                            .describedAs("trained model,name,color,binary");
                    accepts(ILASTIK_ARG, "Sets path to Ilastik directory ie "
                            + "ilastik-1.1.2-Linux").withRequiredArg()
                            .ofType(File.class)
                            .defaultsTo(new File("/var/tmp/ilastik-1.1.2"
                                    + "-Linux"));
                    
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
                    
                    accepts(INCLUDE_DM4_ARG, 
                            "When set in collection mode tells segmenter to "
                                    + "search for new *.dm4 files in --"
                                    + INPUT_IMAGE_ARG + " directory."
                                    + "Can only be used with --"
                                    + COLLECTION_MODE_ARG);
                    
                    accepts(DOWNSAMPLEFACTOR_ARG,
                            "Sets downsampling factor ie value of 2 means to"
                                    + "reduce image size by half.  Used with"
                                    + "--" + INCLUDE_DM4_ARG + " and --"
                                    + COLLECTION_MODE_ARG).withRequiredArg()
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
                    accepts(DISABLE_ANALYZING_TILE_ARG,"If set app no longer "
                            + "denotes with less opaque tile which tiles are "
                            + "being processed");
                    
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

            CustomLayerFromPropertiesFactory layerFac = 
                    new CustomLayerFromPropertiesFactory();
            List<CustomLayer> layers = layerFac.getCustomLayers(props);

            if (layers != null && !layers.isEmpty()) {
                for (CustomLayer cl : layers) {
                    File layerDir = 
                            new File(props.getProperty(LAYER_HANDLER_BASE_DIR) 
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

            setCHMBinDir(props);

            //if collection mode is enabled then set the latest slice path
            SliceMonitor sliceMonitor = null;
            CubeProgressBar cubeProgressBar = new CubeProgressBarImpl(props);
            if (props.getProperty(App.SIMULATE_COLLECTION_ARG, "false")
                    .equals("true")) {
                sliceMonitor = new SimulatedSliceMonitor(props);
                cubeProgressBar = new CubeProgressBarImpl(props);
            } else if (props.getProperty(App.COLLECTION_MODE_ARG, "false")
                    .equals("true")) {
                if (props.getProperty(App.INCLUDE_DM4_ARG, "false")
                        .equals("true")){
                    sliceMonitor = new Dm4SliceMonitorImpl(props, 
                            new Dm4SliceConverterDaemon(props,
                            new Dm4ToSliceConverter(props)));
                    
                } else{
                    sliceMonitor = new SliceMonitorImpl(props, null);
                }
                cubeProgressBar = new CubeProgressBarImpl(props);
                
            }

            if (sliceMonitor != null) {
                App.expectedSlices = props.getProperty(App.EXPECTED_SLICES_ARG,
                        "");
            }

            generateIndexHtmlPage(props, layers);
            int numCores = Integer.parseInt(props.getProperty(NUM_CORES_ARG));
            ExecutorService es = getExecutorService(numCores);
            
            sws = getWebServer(es, props, layers);

            sws.getServer().start();
            int desiredLoad = numCores + (int) ((double) numCores * overloadFactor);
            int prevTotalProcessedCount = -1;
            long iterationCounter = 0;
            int collectionDelay = Integer.parseInt(props.getProperty(COLLECTION_DELAY_ARG));

            while (SIGNAL_RECEIVED == false && (sws.getServer().isStarting() || sws.getServer().isRunning())) {
                //one idea is to have all the image processors dump to a single list
                //and to have this loop track the completed job list and running job list
                //we can then just grab the newest items from the list and pass them to the
                //executor service
                totalProcessedCount += removeCompletedTasks(futureTaskList);

                //if this is a collection check for new # directory in input image
                //if found update status.latestslice to this slice
                if (iterationCounter % collectionDelay == 0) {
                    //_log.log(Level.INFO,"Checking for new slices");
                    updateSlices(sliceMonitor, cubeProgressBar);
                }

                if (totalProcessedCount != prevTotalProcessedCount) {
                    _log.log(Level.INFO, "Total Processed Count is {0}, Future Task List Size: {1}, and desired load is {2} and tiles to process is {3}",
                            new Object[]{totalProcessedCount, futureTaskList.size(), desiredLoad,
                                tilesToProcess.size()});
                }
                prevTotalProcessedCount = totalProcessedCount;
                int size = futureTaskList.size();

                if (size < desiredLoad) {

                    while (!tilesToProcess.isEmpty() && size < desiredLoad) {
                        _log.log(Level.INFO, "Submitting task");
                        futureTaskList.add(es.submit(tilesToProcess.removeFirst()));
                        size++;
                    }
                }
                threadSleep(1000);
                iterationCounter++;
            }

            sws.getServer().stop();
            es.shutdownNow();

            _log.log(Level.INFO, "Sleeping for 5 seconds to let things cool down");
            threadSleep(5000);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            if (sws.getServer() != null) {
                _log.log(Level.INFO, "Shutting down webserver");
                sws.getServer().destroy();
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

    public static void updateSlices(SliceMonitor sliceMonitor,
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
        if (!latestSlice.getSliceName().equals(App.latestSlice)) {
            App.latestSlice = latestSlice.getSliceName();
            App.tilesToProcess.clear();
            App.slicesCollected = Integer.toString(slices.size());
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
        
        if (optionSet.has(DISABLE_ANALYZING_TILE_ARG)){
            props.setProperty(DISABLE_ANALYZING_TILE_ARG, "true");
        }
        else {
            props.setProperty(DISABLE_ANALYZING_TILE_ARG, "false");
        }

        if (optionSet.has(SIMULATE_COLLECTION_ARG)
                && optionSet.has(COLLECTION_MODE_ARG)) {
            throw new Exception("--" + SIMULATE_COLLECTION_ARG + " and --" +
                    COLLECTION_MODE_ARG + " cannot both be set.  Pick one.");
        }

        if (optionSet.has(SIMULATE_COLLECTION_ARG)) {
            props.setProperty(SIMULATE_COLLECTION_ARG, "true");
            props.setProperty(COLLECTION_MODE_ARG, "false");
        } else {
            props.setProperty(SIMULATE_COLLECTION_ARG, "false");
        }

        if (optionSet.has(COLLECTION_MODE_ARG)) {
            props.setProperty(COLLECTION_MODE_ARG, "true");
        } else {
            props.setProperty(COLLECTION_MODE_ARG, "false");
        }
        props.setProperty(REFRESH_OVERLAY_DELAY_ARG, ((Integer) optionSet.valueOf(REFRESH_OVERLAY_DELAY_ARG)).toString());
        props.setProperty(EXPECTED_SLICES_ARG, ((Integer) optionSet.valueOf(EXPECTED_SLICES_ARG)).toString());
        props.setProperty(COLLECTION_DELAY_ARG, ((Integer) optionSet.valueOf(COLLECTION_DELAY_ARG)).toString());
        props.setProperty(CONVERT_ARG, (String) optionSet.valueOf(CONVERT_ARG));
        props.setProperty(SGE_CHM_QUEUE_ARG, (String) optionSet.valueOf(SGE_CHM_QUEUE_ARG));
        props.setProperty(SGE_ILASTIK_QUEUE_ARG, (String) optionSet.valueOf(SGE_ILASTIK_QUEUE_ARG));
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

        if (optionSet.has(CHM_BIN_ARG)) {
            props.setProperty(CHM_BIN_ARG,
                    ((File) optionSet.valueOf(CHM_BIN_ARG)).getAbsolutePath());
        }

        props.setProperty(MATLAB_ARG,
                ((File) optionSet.valueOf(MATLAB_ARG)).getAbsolutePath());

        props.setProperty(LAYER_HANDLER_BASE_DIR, props.getProperty(DIR_ARG) +
                File.separator + LAYER_HANDLER_BASE_DIR);

        props.setProperty(LAYER_MODEL_BASE_DIR, props.getProperty(DIR_ARG) +
                File.separator + LAYER_MODEL_BASE_DIR);

        props.setProperty(CCDB_ARG, (String) optionSet.valueOf(CCDB_ARG));

        props.setProperty(ILASTIK_ARG,
                ((File) optionSet.valueOf(ILASTIK_ARG)).getAbsolutePath());

        System.out.println(props.toString());
        return props;
    }

    public static void setCHMBinDir(Properties props) throws Exception {
        String chm = "chm-compiled-r2013a-2.1.362";

        if (!props.containsKey(CHM_BIN_ARG)) {
            _log.log(Level.INFO, "--{0} not set using internal copy of CHM",
                    CHM_BIN_ARG);

            File workingDir = new File(props.getProperty(DIR_ARG));
            //chm bin was not set.  Lets copy chm bin out of the resource path and into
            //the working dir under bin
            File workingDirBin = new File(workingDir.getAbsolutePath() + File.separator + "bin");
            workingDirBin.mkdirs();

            File chmTest = new File(workingDirBin + File.separator + "CHM_test");
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + chm + "/CHM_test"), chmTest);
            chmTest.setExecutable(true);

            File chmTestSh = new File(workingDirBin + File.separator + "CHM_test.sh");
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + chm + "/CHM_test.sh"), chmTestSh);
            chmTestSh.setExecutable(true);

            File chmTrain = new File(workingDirBin + File.separator + "CHM_train");
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + chm + "/CHM_train"), chmTrain);
            chmTrain.setExecutable(true);

            File chmTrainSh = new File(workingDirBin + File.separator + "CHM_train.sh");
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + chm + "/CHM_train.sh"), chmTrainSh);
            chmTrainSh.setExecutable(true);

            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + chm + "/LICENSE.txt"), new File(workingDirBin + File.separator + "LICENSE.txt"));
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/" + chm + "/README.txt"), new File(workingDirBin + File.separator + "README.txt"));
            props.setProperty(CHM_BIN_ARG, workingDirBin.getAbsolutePath());
        }
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

    public static SegmenterWebServer getWebServer(ExecutorService es, Properties props, List<CustomLayer> layers) throws Exception {

        SegmenterWebServerFactory serverFac = new SegmenterWebServerFactory();

        SegmenterWebServer sws = serverFac.getSegmenterWebServer(es, props, layers);

        System.out.println("\n\n\tOpen a browser to this URL: http://localhost:"
                + props.getProperty(PORT_ARG) + "\n\n");

        return sws;
    }

    public static void generateIndexHtmlPage(Properties props, List<CustomLayer> layers) throws Exception {

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
