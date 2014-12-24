package edu.ucsd.crbs.realtimeseg;

import edu.ucsd.crbs.realtimeseg.handler.CHMHandler;
import edu.ucsd.crbs.realtimeseg.html.HtmlPageGenerator;
import edu.ucsd.crbs.realtimeseg.html.SingleImageIndexHtmlPageGenerator;
import edu.ucsd.crbs.realtimeseg.io.WorkingDirCreator;
import edu.ucsd.crbs.realtimeseg.io.WorkingDirCreatorImpl;
import edu.ucsd.crbs.realtimeseg.layer.CustomLayer;
import edu.ucsd.crbs.realtimeseg.layer.CustomLayerFromPropertiesFactory;
import edu.ucsd.crbs.realtimeseg.util.ImageProcessor;
import edu.ucsd.crbs.realtimeseg.util.SimpleCHMImageProcessor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Hello world!
 *
 */
public class App {

    public static final String INPUT_IMAGE_ARG = "inputimage";
    public static final String CHM_BIN_ARG = "chmbin";
    public static final String PORT_ARG = "port";
    public static final String DIR_ARG = "dir";
    public static final String MATLAB_ARG = "matlab";
    public static final String NUM_CORES_ARG = "cores";
    public static final String CUSTOM_ARG = "custom";
    public static final String IMAGE_WIDTH_ARG = "imagewidth";
    public static final String IMAGE_HEIGHT_ARG = "imageheight";
    public static final String OVERLAY_OPACITY_ARG = "overlayopacity";
    public static final String TILE_SIZE_ARG = "tilesize";
    public static final String TEMP_DIR_CREATED_FLAG="TEMP_DIR_CREATED";
    
    public static ConcurrentLinkedDeque<String> TILES_TO_PROCESS = new ConcurrentLinkedDeque<String>();
    public static boolean SIGNAL_RECEIVED = false;
    
   
    
    
    public static void main(String[] args) {
        
        
        Signal.handle(new Signal("INT"), new SignalHandler() {
            // Signal handler method
            public void handle(Signal signal) {
                System.out.println("Got signal" + signal + " exiting");
                SIGNAL_RECEIVED = true;
            }
        });
       
        
        final String tempDirectory = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();

        Server server = null;
        boolean tempDirUsed = false;
        
        final List<String> helpArgs = Arrays.asList("h","help","?");
        try {
            OptionParser parser = new OptionParser() {
               
                {
                    accepts(INPUT_IMAGE_ARG, "Tiled input image directory.  "
                            + "Tiles must have name in following format: 0-r#_c#.png"
                            + " where r# is the 0 offset row number and c# is "
                            + "the 0 offset column number.  Ex: 0-r0_c0.png  "
                            + "Tiles must also be size 128x128").withRequiredArg().ofType(File.class).required();
                    accepts(IMAGE_WIDTH_ARG,"Width of image in pixels").withRequiredArg().ofType(Integer.class).defaultsTo(50000);
                    accepts(IMAGE_HEIGHT_ARG,"Height of image in pixels").withRequiredArg().ofType(Integer.class).defaultsTo(50000);
                    accepts(OVERLAY_OPACITY_ARG,"Opacity of segmentation layers 0-1").withRequiredArg().ofType(Double.class).defaultsTo(0.3);
                    accepts(CHM_BIN_ARG, "Path to CHM bin directory").withRequiredArg().ofType(File.class);
                    accepts(MATLAB_ARG,"Path to Matlab base directory ie /../matlab2013a/v81").withRequiredArg().ofType(File.class).required();
                    accepts(PORT_ARG, "Port to run service").withRequiredArg().ofType(Integer.class).defaultsTo(8080);
                    accepts(DIR_ARG, "Working/Temp directory for server").withRequiredArg().ofType(File.class).defaultsTo(new File(tempDirectory));
                    accepts(TILE_SIZE_ARG,"Size of tiles in pixels ie 128 means 128x128").withRequiredArg().ofType(Integer.class).defaultsTo(128);
                    accepts(NUM_CORES_ARG,"Number of concurrent CHM jobs to run.  Each job requires 1gb ram.").withRequiredArg().ofType(Integer.class).defaultsTo(1);
                    accepts(CUSTOM_ARG,"Custom Segmentation layer (comma delimited)\n"
                            + " *trained model - path to chm trained model\n"
                            + " *name - Name to display in overlay menu\n"
                            + " *color - can be one of the following: red,green,"
                            + "blue,yellow,magenta,cyan\n"
                            + " *binary - Set to 'chm' for now\n").withRequiredArg().ofType(String.class).describedAs("trained model,name,color,binary");
                    acceptsAll(helpArgs,"Show Help").forHelp();
                }
            };

            OptionSet optionSet = null;
            try {
                optionSet = parser.parse(args);
            } catch (OptionException oe) {
                System.err.println("\nThere was an error parsing arguments: " + oe.getMessage() + "\n\n");
                parser.printHelpOn(System.err);
                System.exit(1);
            }

            //help check
            for (String helpArgName : helpArgs){
                if (optionSet.has(helpArgName)) {
                    System.out.println("\n\nHelp\n\n");
                    parser.printHelpOn(System.out);
                    System.exit(2);
                }
            }
            
            Properties props = getPropertiesFromCommandLine(optionSet);
            
            CustomLayerFromPropertiesFactory layerFac = new CustomLayerFromPropertiesFactory();
            List<CustomLayer> layers = layerFac.getCustomLayers(props);
            
            WorkingDirCreator wDirCreator = new WorkingDirCreatorImpl();
            wDirCreator.createWorkingDir(props,layers);

            if (props.getProperty(TEMP_DIR_CREATED_FLAG,"false").equals("true")){
                tempDirUsed = true;   
            }
            
            setCHMBinDir(props);

            generateIndexHtmlPage(props,layers);
            
            ExecutorService es = getExecutorService(Integer.parseInt(props.getProperty(NUM_CORES_ARG)));
            
            server = getWebServer(es,props,layers);
            
            server.start();
            
            while (SIGNAL_RECEIVED == false && (server.isStarting() || server.isRunning())){
                threadSleep(1000);
            }
            
            server.stop();
            es.shutdownNow();
            
            System.out.println("Sleeping for 5 seconds to let things cool down");
            threadSleep(5000);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            if (server != null){
                server.destroy();
            }

            if (tempDirUsed) {
                File tempDir = new File(tempDirectory);
                if (tempDir.exists()) {
                    System.out.println("\n\nDeleting temp directory: "+tempDirectory+"\n");
                    try {
                        FileUtils.deleteDirectory(tempDir);
                    } catch (IOException ioex) {
                        System.err.println("Unable to delete" + tempDir.getAbsolutePath() + " : " + ioex.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Converts <b>optionSet</b> to a {@link Properties} object
     * @param optionSet
     * @return {@link Properties} set to values from command line and or configuration file
     * @throws Exception 
     */
    public static Properties getPropertiesFromCommandLine(OptionSet optionSet) throws Exception {

        Properties props = new Properties();

        List<String> customList = (List<String>) optionSet.valuesOf(CUSTOM_ARG);
        int counter = 1;
        for (String s : customList) {
            props.setProperty(CUSTOM_ARG+"." + counter, s);
            counter++;
        }
        props.setProperty(TILE_SIZE_ARG, ((Integer)optionSet.valueOf(TILE_SIZE_ARG)).toString());
        props.setProperty(OVERLAY_OPACITY_ARG, ((Double)optionSet.valueOf(OVERLAY_OPACITY_ARG)).toString());
        props.setProperty(IMAGE_WIDTH_ARG, ((Integer)optionSet.valueOf(IMAGE_WIDTH_ARG)).toString());
        props.setProperty(IMAGE_HEIGHT_ARG, ((Integer)optionSet.valueOf(IMAGE_HEIGHT_ARG)).toString());
        props.setProperty(NUM_CORES_ARG,((Integer)optionSet.valueOf(NUM_CORES_ARG)).toString());
        props.setProperty(INPUT_IMAGE_ARG,((File)optionSet.valueOf(INPUT_IMAGE_ARG)).getAbsolutePath());
        props.setProperty(PORT_ARG,((Integer) optionSet.valueOf(PORT_ARG)).toString());
        props.setProperty(DIR_ARG, ((File)optionSet.valueOf(DIR_ARG)).getAbsolutePath());
        
        if (optionSet.has(CHM_BIN_ARG)){
            props.setProperty(CHM_BIN_ARG,((File)optionSet.valueOf(CHM_BIN_ARG)).getAbsolutePath());
        }
        
        props.setProperty(MATLAB_ARG,((File)optionSet.valueOf(MATLAB_ARG)).getAbsolutePath());
        
        System.out.println(props.toString());
        return props;
    }
    
    
    public static File getMitoTrainedModelDir(final String workingDir) throws Exception {
        
        File mitoModelDir = new File(workingDir+File.separator+"mitomodel");
        mitoModelDir.mkdirs();
        String mito = "mito";
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/MODEL_level0_stage1.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"MODEL_level0_stage1.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/MODEL_level0_stage2.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"MODEL_level0_stage2.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/MODEL_level1_stage1.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"MODEL_level1_stage1.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/param.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"param.mat"));
        
        return mitoModelDir;
    }
    
    public static void setCHMBinDir(Properties props) throws Exception {
        String chm = "chm-compiled-r2013a-2.1.362";
       
      
        if (!props.containsKey(CHM_BIN_ARG)){
            System.out.println("--"+CHM_BIN_ARG+" not set using internal copy of CHM");
            File workingDir = new File(props.getProperty(DIR_ARG));
            //chm bin was not set.  Lets copy chm bin out of the resource path and into
            //the working dir under bin
            File workingDirBin = new File(workingDir.getAbsolutePath()+File.separator+"bin");
            workingDirBin.mkdirs();
            
            File chmTest = new File(workingDirBin + File.separator + "CHM_test");
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+chm+"/CHM_test"),chmTest);
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
            
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+chm+"/LICENSE.txt"), new File(workingDirBin+File.separator+"LICENSE.txt"));
            FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+chm+"/README.txt"), new File(workingDirBin+File.separator+"README.txt"));
            props.setProperty(CHM_BIN_ARG,workingDirBin.getAbsolutePath());
        }
    }
    
    
    public static ImageProcessor getMitoImageProcessor(ExecutorService es,
            Properties props) throws Exception {
        
        return new SimpleCHMImageProcessor(es,props.getProperty(INPUT_IMAGE_ARG),
                    props.getProperty(DIR_ARG)+File.separator+WorkingDirCreator.MITO_DIR,
                    getMitoTrainedModelDir(props.getProperty(DIR_ARG)).getAbsolutePath(),
                    props.getProperty(CHM_BIN_ARG)+File.separator+"CHM_test.sh",
                    props.getProperty(MATLAB_ARG),"Red,Blue");
    }
    
    public static ContextHandler getMitoHandler(ExecutorService es,
            Properties props) throws Exception {
        
        ImageProcessor ip = getMitoImageProcessor(es,props);
        CHMHandler chmHandler = new CHMHandler(ip);
        ContextHandler chmContext = new ContextHandler("/"+WorkingDirCreator.MITO_DIR);
        chmContext.setHandler(chmHandler);
        return chmContext;
    }
    
    public static List<ContextHandler> getCustomHandlers(ExecutorService es,Properties props, List<CustomLayer> layers) throws Exception {
        if (layers == null || layers.isEmpty()){
            return null;
        }
        ArrayList<ContextHandler> cHandlers = new ArrayList<ContextHandler>();
        for (CustomLayer cl : layers){
            ImageProcessor imageProc = new SimpleCHMImageProcessor(es,props.getProperty(INPUT_IMAGE_ARG),
                    props.getProperty(DIR_ARG)+File.separator+cl.getVarName(),
                    cl.getTrainedModelDir(),
                    props.getProperty(CHM_BIN_ARG)+File.separator+"CHM_test.sh",
                    props.getProperty(MATLAB_ARG),cl.getConvertColor());
            CHMHandler chmHandler = new CHMHandler(imageProc);
            ContextHandler chmContext = new ContextHandler("/"+cl.getVarName());
            chmContext.setHandler(chmHandler);
            cHandlers.add(chmContext);
        }
        return cHandlers;
    }
    
    public static Server getWebServer(ExecutorService es,Properties props,List<CustomLayer> layers) throws Exception {

        // Create a basic Jetty server object that will listen on port 8080.  Note that if you set this to port 0
        // then a randomly available port will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(Integer.parseInt(props.getProperty(PORT_ARG)));

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        
        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        ResourceHandler inputImageHandler = new ResourceHandler();
        inputImageHandler.setDirectoriesListed(true);
        inputImageHandler.setResourceBase(props.getProperty(INPUT_IMAGE_ARG));
        ContextHandler imageContext = new ContextHandler("/images");
        imageContext.setHandler(inputImageHandler);
        contexts.addHandler(imageContext);
        
        ResourceHandler workingDirHandler = new ResourceHandler();
        workingDirHandler.setDirectoriesListed(true);
        workingDirHandler.setResourceBase(props.getProperty(DIR_ARG));
        workingDirHandler.setWelcomeFiles(new String[]{"index.html"});
        ContextHandler workingDirContext = new ContextHandler("/");
        workingDirContext.setHandler(workingDirHandler);
        contexts.addHandler(workingDirContext);
        
        ContextHandler mitoContext = getMitoHandler(es,props);

        contexts.addHandler(mitoContext);
        
        List<ContextHandler> handlers = getCustomHandlers(es,props,layers);
        if (handlers != null && !handlers.isEmpty()){
            for (ContextHandler ch : handlers){
                contexts.addHandler(ch);
            }
        }
        server.setHandler(contexts);

        System.out.println("\n\n\tOpen a browser to this URL: http://localhost:" 
                + props.getProperty(PORT_ARG) + "\n\n");
        server.start();

        return server;
    }

    public static void generateIndexHtmlPage(Properties props,List<CustomLayer> layers) throws Exception {
      
        HtmlPageGenerator pageGenerator = new SingleImageIndexHtmlPageGenerator(props,layers);
        pageGenerator.generateHtmlPage(props.getProperty(DIR_ARG));
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing.png"), 
                new File(props.getProperty(DIR_ARG)+File.separator+"analyzing.png"));
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
        System.out.println("Using "+numCoresToUse+" cores");
        return Executors.newFixedThreadPool(numCoresToUse);
    }
}
