
package com.google.sps.data;
import java.util.Date;
import java.util.ArrayList;

public class Comment {
    private String name;
    private String text;
    private Date date;

    public Comment(String name, String text, Date date){
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public Comment(String name, String text){
        this.name = name;
        this.text = text;
        this.date = new Date();
    }

    public Comment(String text){
        this("Anonymous", text);
    }

    public String getName(){
        return name;
    }

    public String getText(){
        return text;
    }

    public Date getDate(){
        return date;
    }

    public String toString(){
        return "Comment from "+name+" is: "+text;
    }

}
