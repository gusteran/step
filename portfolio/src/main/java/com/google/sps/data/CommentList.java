
package com.google.sps.data;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

//list of comments sorted by date
public class CommentList{

    private ArrayList<Comment> comments;
    private int numComments; //used for gson purposes

    public CommentList(){
        comments = new ArrayList<Comment>();
        numComments = 0;
    }

    private CommentList(List<Comment> comments, int numComments){
        this.comments = new ArrayList<Comment>();
        this.comments.addAll(comments);
        this.numComments = numComments;
    }

    public void add(Comment comment){
        int i = 0;
        for(; i < comments.size() && 
            comment.getDate().compareTo(comments.get(i).getDate()) < 0; i++);
        comments.add(i, comment);
        numComments = comments.size();
    }

    public void clear(){
        comments.clear();
    }

    public CommentList getSubList(int start, int end){
        if(end > numComments) end = numComments;
        return new CommentList(comments.subList(start, end), numComments);
    }
}