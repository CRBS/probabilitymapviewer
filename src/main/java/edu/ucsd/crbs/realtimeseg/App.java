package edu.ucsd.crbs.realtimeseg;

import edu.ucsd.crbs.realtimeseg.handler.CHMHandler;
import edu.ucsd.crbs.realtimeseg.io.ResourceToExecutableScriptWriter;
import edu.ucsd.crbs.realtimeseg.io.ResourceToExecutableScriptWriterImpl;
import edu.ucsd.crbs.realtimeseg.util.ImageProcessor;
import edu.ucsd.crbs.realtimeseg.util.SimpleCHMImageProcessor;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Handler;
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
    public static final String TRAINED_MODEL_ARG = "trainedmodel";
    public static final String DIR_ARG = "dir";
    public static final String MATLAB_ARG = "matlab";
    public static final String HELP_ARG = "h";
    public static final String NUM_CORES_ARG = "cores";
    
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
        
        try {
            OptionParser parser = new OptionParser() {
                {
                    accepts(INPUT_IMAGE_ARG, "Tiled input image directory").withRequiredArg().ofType(File.class);
                    accepts(CHM_BIN_ARG, "Path to CHM bin directory").withRequiredArg().ofType(File.class);
                    accepts(MATLAB_ARG,"Path to Matlab base directory ie /../matlab2013a/v81").withRequiredArg().ofType(File.class);
                    accepts(PORT_ARG, "Port to run service").withRequiredArg().ofType(Integer.class).defaultsTo(8080);
                    accepts(TRAINED_MODEL_ARG, "Path to CHM trained model").withRequiredArg().ofType(File.class);
                    accepts(DIR_ARG, "Working/Temp directory for server").withRequiredArg().ofType(File.class).defaultsTo(new File(tempDirectory));
                    accepts(NUM_CORES_ARG,"Number of concurrent CHM jobs to run.  Each job requires 1gb ram.").withRequiredArg().ofType(Integer.class).defaultsTo(1);
                    accepts(HELP_ARG).forHelp();
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

            if (optionSet.has(HELP_ARG)) {
                System.out.println("Help\n");
                parser.printHelpOn(System.out);
                System.exit(2);
            }

            if (!optionSet.has(INPUT_IMAGE_ARG)) {
                System.err.println("\n\nERROR --" + INPUT_IMAGE_ARG + " is required to run this program. Invoke with -h for more information\n\n");
                System.exit(1);
            }

            File workingDir = null;
            if (optionSet.has(DIR_ARG)) {
                workingDir = (File) optionSet.valueOf(DIR_ARG);
            } else {
                workingDir = new File(tempDirectory);
                System.out.println("\n--" + DIR_ARG + " not set using " + tempDirectory);
                tempDirUsed = true;
            }

            if (workingDir.exists() == false) {
                System.out.println("--dir "+workingDir.getAbsolutePath()+" does not exist.  Creating directory");
                if (workingDir.mkdirs() == false) {
                    System.err.println("Unable to create " + workingDir.getAbsolutePath());
                    System.exit(1);
                }
            }
            
            File probmapsSubDir = new File(workingDir.getAbsolutePath()+File.separator+"mito");
            probmapsSubDir.mkdirs();
            

            generateIndexHtmlPage(workingDir.getAbsolutePath());

            Integer numCores = new Integer(1);
            if (optionSet.has(NUM_CORES_ARG)){
                numCores = (Integer)optionSet.valueOf(NUM_CORES_ARG);
            }
            
            ExecutorService es = getExecutorService(numCores);

            ImageProcessor ip = getImageProcessor(es,workingDir,optionSet);
            
            server = getWebServer(((File) optionSet.valueOf(INPUT_IMAGE_ARG)).getAbsolutePath(),
                    workingDir.getAbsolutePath(),
                    (Integer) optionSet.valueOf(PORT_ARG),
                    ((File) optionSet.valueOf(TRAINED_MODEL_ARG)).getAbsolutePath(),ip);

            
            server.start();
            
            while (SIGNAL_RECEIVED == false && (server.isStarting() || server.isRunning())){
                threadSleep(1000);
            }
            
            server.stop();
            es.shutdown();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            if (server != null){
                server.destroy();
            }

            if (tempDirUsed == true) {
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

    public static ImageProcessor getImageProcessor(ExecutorService es,
            File workingDir,OptionSet optionSet) throws Exception {
        
        return new SimpleCHMImageProcessor(es,((File) optionSet.valueOf(INPUT_IMAGE_ARG)).getAbsolutePath(),
                    workingDir.getAbsolutePath()+File.separator+"mito",
                    ((File) optionSet.valueOf(TRAINED_MODEL_ARG)).getAbsolutePath(),
                    ((File)optionSet.valueOf(App.CHM_BIN_ARG)).getAbsolutePath()+File.separator+"CHM_test.sh",
                    ((File) optionSet.valueOf(MATLAB_ARG)).getAbsolutePath());
    }
    
    public static Server getWebServer(final String inputImagePath, final String workingDirArg, Integer port,
            final String trainedModel,
            ImageProcessor ip) throws Exception {

        // Create a basic Jetty server object that will listen on port 8080.  Note that if you set this to port 0
        // then a randomly available port will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(port);

        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        ResourceHandler inputImageHandler = new ResourceHandler();
        inputImageHandler.setDirectoriesListed(true);
        inputImageHandler.setResourceBase(inputImagePath);
        ContextHandler imageContext = new ContextHandler("/images");
        imageContext.setHandler(inputImageHandler);

        ResourceHandler workingDirHandler = new ResourceHandler();
        workingDirHandler.setDirectoriesListed(true);
        workingDirHandler.setResourceBase(workingDirArg);
        workingDirHandler.setWelcomeFiles(new String[]{"index.html"});
        ContextHandler workingDirContext = new ContextHandler("/");
        workingDirContext.setHandler(workingDirHandler);

        CHMHandler chmHandler = new CHMHandler(ip);
        ContextHandler chmContext = new ContextHandler("/process");
        chmContext.setHandler(chmHandler);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{imageContext, workingDirContext, chmContext});

        server.setHandler(contexts);

        // Start things up! By using the server.join() the server thread will join with the current thread.
        // See "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()" for more details.
        System.out.println("\n\n\tOpen a browser to this URL: http://localhost:" + port + "\n\n");
        server.start();

        return server;
    }

    public static void generateIndexHtmlPage(final String workingDir) throws Exception {
        ResourceToExecutableScriptWriter scriptWriter = new ResourceToExecutableScriptWriterImpl();
        scriptWriter.writeResourceToScript("/index.html", workingDir + File.separator + "index.html", null);
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/analyzing.png"), new File(workingDir+File.separator+"analyzing.png"));
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

        //Create a threadpool that is 90% size of number of processors on system
        //with a minimum size of 1.
        //int threadPoolSize = (int) Math.round((double) Runtime.getRuntime().availableProcessors()
        //        * percentOfCoresToUse);
        //if (threadPoolSize < 1) {
        //    threadPoolSize = 1;
        //}
        //System.out.println
        return Executors.newFixedThreadPool(numCoresToUse);
    }

    static int removeCompletedTasks(List<Future> taskList) {
        Future f;
        int removeCount = 0;
        Iterator<Future> itr = taskList.iterator();
        while (itr.hasNext()) {
            f = itr.next();
            if (f.isDone() || f.isCancelled()) {
                removeCount++;
                itr.remove();
            }
        }
        return removeCount;
    }
}
