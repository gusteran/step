

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.ThreadManager;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Scanner;
import java.lang.Thread;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.*;
import java.io.*;

import com.google.sps.data.WaterSystem;

/** Servlet that returns a list of all Superfund Sites */
@WebServlet("/water")
public class WaterServlet extends HttpServlet {

    private ArrayList<WaterSystem> waterSystems;
    private HashMap<String, Integer> totalPopExposed = new HashMap<>();
    
    private static final String STATISTICS = "statistics";

    //this is the url to the tab seperated sheet of all the superfund sites
    private static final String WATER_INFRACTIONS = "/WEB-INF/UCMR4";

    @Override
    public void init() {
        waterSystems = new ArrayList<WaterSystem>();

        SequenceInputStream combinedFileStream = null;
        for(char endChar = 'b'; endChar <= 'f'; endChar++){
            if(endChar == 'b'){
                combinedFileStream = new SequenceInputStream(getServletContext().getResourceAsStream(WATER_INFRACTIONS+(char)(endChar-1)), 
                    getServletContext().getResourceAsStream(WATER_INFRACTIONS+endChar));
            } else {
                combinedFileStream = new SequenceInputStream(combinedFileStream, 
                    getServletContext().getResourceAsStream(WATER_INFRACTIONS+endChar));
            }
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(combinedFileStream))) {

            // Scanner reader = new Scanner(getServletContext().getResourceAsStream(WATER_INFRACTIONS));
            reader.readLine();

            String id = "000000000";
            String line = reader.readLine();
            Thread lastThread = new Thread();

            //limits num of water systems so testing is quicker
            int maxAllowed = 500;
            while(true){
                HashSet<String> violations = new HashSet<String>();
                while(line != null && line.startsWith(id)){
                    violations.add(line.split("\t")[11]);
                    line = reader.readLine();
                    // System.out.println(line);
                }
                if(line == null) break;

                //experiment to stop APIs and Database from timing out
                Thread.sleep(100);
                // lastThread.join();

                final String pwsid = id =  line.substring(0,9);

                //multithreading would be nice but must be handled properly
                //and not just throw threads at it like I tried
                // lastThread = ThreadManager.createBackgroundThread( new Thread(){
                //     public void run(){
                //  System.out.println(pwsid);
                   WaterSystem system = new WaterSystem(pwsid);
                    system.setViolations(violations);
                    waterSystems.add(system);
                    for(String violation: violations){
                        if(!totalPopExposed.containsKey(violation)) {
                            totalPopExposed.put(violation, system.getPopulation());
                        } else {
                            totalPopExposed.put(violation, system.getPopulation()+
                                totalPopExposed.get(violation));
                        }
                    }
                //     }
                // });
                // lastThread.start();
            }
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getParameter(STATISTICS) != null){
            response.setContentType("application/json");
            Gson gson = new Gson();
            response.getWriter().println(gson.toJson(totalPopExposed));
        } else {
            response.setContentType("application/json");
            Gson gson = new Gson();
            response.getWriter().println(gson.toJson(waterSystems));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        for(int i = waterSystems.size()-1; i >=0; i--){
            if(waterSystems.get(i).areInvalidCoords()){
                WaterSystem replacement = new WaterSystem(waterSystems.get(i).getPWSID());
                waterSystems.set(i, replacement);
            }
        }
        response.sendRedirect("/environment.html");
    }

}