

package com.google.sps.data;

public class SuperfundSite {

    private String name;
    private double score;
    private String state;
    private String city;
    private String county;
    private String status;
    private double lattitude;
    private double longitude;

    public SuperfundSite(String name,
        double score,
        String state,
        String city,
        String county,
        String status,
        double lattitude,
        double longitude){
        this.name = name;
        this.score = score;
        this.state = state;
        this.city = city;
        this.county = county;
        this.status = status;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }
    
}