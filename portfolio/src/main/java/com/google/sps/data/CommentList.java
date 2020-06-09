
package com.google.sps.data;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

//list of comments sorted by date
public class CommentList{

    private ArrayList<Comment> comments;

    public CommentList(){
        comments = new ArrayList<Comment>();
    }

    public void add(Comment comment){
        int i = 0;
        for(; i < comments.size() && 
            comment.getDate().compareTo(comments.get(i).getDate()) < 0; i++);
        comments.add(i, comment);
    }

    public void clear(){
        comments.clear();
    }

    public List<Comment> getSubList(int start, int end){
        if(end > comments.size()) end = comments.size();
        return comments.subList(start, end);
    }

    public int getTotalComments(){
        return comments.size();
    }
}