/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this segmenter for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this segmenter into commercial products
 * or use for commercial purposes should contact the Technology Transfer Office, 
 * University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, 
 * La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, 
 * E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS segmenter, EVEN IF THE UNIVERSITY 
 * OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE segmenter PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE segmenter WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */
package edu.ucsd.crbs.segmenter.handler;

import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.job.JobResult;
import java.io.IOException;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class StatusHandler extends AbstractHandler {

    private int _numCores;

    public StatusHandler(int numCores) {
        _numCores = numCores;
        if (_numCores <= 0) {
            _numCores = 1;
        }
    }

    @Override
    public void handle(String string, Request request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) throws IOException,
            ServletException {

        int tilesToProcess = App.tilesToProcess.size();
        String avgTimePerTile = "?";
        double rawAvgTimePerTile = 0.0;
        String totalTimeHours = "?";
        String estTimeMinutes = "?";
        String estCpuHours = "?";
        String numCoresInUse = "?";
        int numCoresInUseCount = 0;

        for (Future f : App.futureTaskList) {

            if (!f.isCancelled() && !f.isDone()) {
                numCoresInUseCount++;
            }

        }
        // @TODO this is stupid, but it will be less confusing to the user
        if (numCoresInUseCount > _numCores) {
            numCoresInUseCount = _numCores;
        }
        numCoresInUse = Integer.toString(numCoresInUseCount);

        if (App.totalJobsRun > 0) {
            long totalTime = App.totalRunTimeOfJobs;
            long numJobs = App.totalJobsRun;

            if (numJobs > 0) {
                rawAvgTimePerTile = (double) totalTime / (double) numJobs;
                avgTimePerTile = Long.toString(Math.round(rawAvgTimePerTile / (double) 1000.0));
                totalTimeHours = Long.toString(Math.round((double) totalTime / (double) 1000.0 / (double) 3600.0));
                if (tilesToProcess > 0) {
                        //take # of tiles to process and divide by num cores 
                    //and multiply by average time per tile then convert to
                    //minutes
                    estTimeMinutes = Long.toString(Math.round(tilesToProcess / (double) _numCores * rawAvgTimePerTile / (double) 1000.0 / (double) 60.0));

                        //take # of tiles to process and multiply by avg time per tile
                    //and convert to hours
                    estCpuHours = Long.toString(Math.round(tilesToProcess * rawAvgTimePerTile / (double) 1000.0 / (double) 3600.0));
                }
            }

        }
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"tilestoprocess\": ");
        sb.append(tilesToProcess);
        sb.append(",\"futuretasklistsize\": ");
        sb.append(App.futureTaskList.size());
        sb.append(",\"esttimehours\": \"");
        sb.append(estTimeMinutes);
        sb.append("\"");
        sb.append(",\"estcpuhours\": \"");
        sb.append(estCpuHours);
        sb.append("\"");
        sb.append(",\"tilesprocessed\": ");
        sb.append(App.totalProcessedCount);

        sb.append(",\"cpuconsumedhours\": \"");
        sb.append(totalTimeHours);
        sb.append("\"");

        sb.append(",\"numcoresinuse\": \"");
        sb.append(numCoresInUse);
        sb.append("\"");

        sb.append(",\"avgtimepertile\": \"");
        sb.append(avgTimePerTile);
        sb.append("\"");

        sb.append(",\"latestslice\": \"");
        sb.append(App.latestSlice);
        sb.append("\"");

        sb.append(",\"collectionname\": \"");
        sb.append(App.collectionName);
        sb.append("\"");

        sb.append(",\"sliceaquiretime\": \"");
        sb.append(App.sliceAquireTime);
        sb.append("\"");

        
        sb.append("}");

        servletResponse.setContentType("application/json");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.getWriter().write(sb.toString());
        servletResponse.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
    }

}
