
package com.google.sps.data;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.lang.System;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class WaterSystem {

    private static final String EPA_API_LINK = "https://enviro.epa.gov/enviro/efservice/WATER_SYSTEM/PWSID/";
    private static final String CSV_FORMAT = "/CSV/";
    private static final String ENTITY_NAME = "WaterSystemEntity";
    private static final String PWSID_PROPERTY = "PWSID";
    private static final String LAT_PROPERTY = "LAT";
    private static final String LNG_PROPERTY = "LNG";
    private static final String NAME_PROPERTY = "NAME";

    // private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private String pwsid;
    private String name;
    private int population;
    private double lat;
    private double lng;
    private String address;
    private HashSet<String> violations;

    public WaterSystem(String PWSID){
        this.pwsid = PWSID;
        try {
        URL url = new URL(EPA_API_LINK + PWSID + CSV_FORMAT);
        Scanner scanner = new Scanner(url.openStream());
        while(!scanner.hasNextLine()){
            System.out.println("Awaiting "+PWSID+" from EPA API");
        }
        scanner.nextLine();
        if(!scanner.hasNextLine()){
            System.out.println("No data for "+PWSID+" from EPA API");
            return;
        }
        String line = scanner.nextLine();
        String[] cells = line.split("\",\"");
        name = cells[1];
        try {
            population = Integer.parseInt(cells[15].replace("\"", ""));
        } catch (Exception e){
            System.out.println("Bad Population formatting for "+PWSID);
            population = -1;
        }
        setLatLng(PWSID, cells);

        if(scanner.hasNextLine()){
            System.out.println(PWSID+" has multiple water systems");
        }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //turns the address from the line read in into lat lng 42.4836° N, 71.4418° W
    private void setLatLng(String PWSID, String[] cells){
        address = cells[35]+" "+cells[36]+" "+cells[37]+" "+cells[40]+" "+cells[38];
        address = address.replace("\"", "");

        lat = 0;
        lng = 0;

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(ENTITY_NAME)
            .setFilter(new FilterPredicate(PWSID_PROPERTY, FilterOperator.EQUAL, PWSID));
        PreparedQuery pq = datastore.prepare(q);
        if(pq.countEntities()>0){        
            Entity result = pq.asSingleEntity();
            lat = (Double)(result.getProperty(LAT_PROPERTY));
            lng = (Double)(result.getProperty(LNG_PROPERTY));
        } else {
            System.out.println("Need to Geocode for "+PWSID);            
            LatLng coords = new LatLng(0,0);
            
            try {
                GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(Keys.MAPS_KEY)
                    .build();
                GeocodingResult[] results =  GeocodingApi.geocode(context,
                    address).await();
                if(results.length < 1){
                    address = cells[1]+"+"+cells[40]+"+"+cells[38];
                    results =  GeocodingApi.geocode(context, address).await();
                }

                if(results.length > 0){
                    coords = (LatLng)(results[0].geometry.location);
                }


            lat = coords.lat;
            lng = coords.lng;

            Entity entity = new Entity(ENTITY_NAME);
            entity.setProperty(PWSID_PROPERTY, PWSID);
            entity.setProperty(NAME_PROPERTY, name);
            entity.setProperty(LAT_PROPERTY, lat);
            entity.setProperty(LNG_PROPERTY, lng);

            datastore.put(entity);
            } catch (Exception e){
                e.printStackTrace();
                coords = new LatLng(0,0);
            }
        }
    }

    public void setViolations(HashSet<String> violations){
        this.violations = violations;
    }

    public int getPopulation(){
        if(population < 1) return 0;
        return population;
    }

    public boolean areInvalidCoords(){
        return lat == 0 || lng == 0;
    }

    public String getPWSID(){
        return pwsid;
    }

}