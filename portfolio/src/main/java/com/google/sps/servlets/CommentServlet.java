// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.Comment;
import com.google.sps.data.CommentList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet({"/comment", "/delete-data"}
)
public class CommentServlet extends HttpServlet {

    private CommentList comments;
    private Gson gson = new Gson();

    private static final String entityName = "Comment";

    private static final String nameProperty = "commentName";
    private static final String textProperty = "commentText";
    private static final String dateProperty = "commentDate";

    private static final String numCommentsParamter = "numComments";
    private static final String pageNumParameter = "pageNum";

    @Override
    public void init(){
        comments = new CommentList();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query(entityName);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            Comment comment = new Comment(
                entity.getProperty(nameProperty).toString(),
                entity.getProperty(textProperty).toString(),
                (Date)entity.getProperty(dateProperty));
            comments.add(comment);
        }
    }

    //responds with a comment list from the start of the comments of that page 
    //to the end of that sublist of comments
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    int numComments = Integer.parseInt(request.getParameter(numCommentsParamter));
    int pageNum = Integer.parseInt(request.getParameter(pageNumParameter));
    int start = numComments * (pageNum-1);
    int end = numComments * pageNum;
    // response.getWriter().println(gson.toJson(20));
    // response.getWriter().print(gson.toJson(comments.getSubList(start,end)));
    response.getWriter().print(formSubListJson(comments, start, end));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if(request.getServletPath().equalsIgnoreCase("/delete-data")){
        deleteData();
    } else {
        addComment(request, response);
    }
  }

  private void deleteData(){
      comments.clear();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query(entityName);//.addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        ArrayList<Key> keys = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            keys.add(entity.getKey());
        }
        datastore.delete(keys);
}

  private void addComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Comment comment;
    if(request.getParameter("name").length()<1){
        comment = new Comment(request.getParameter("comment"));
    } else {
        comment = new Comment(request.getParameter("name"),request.getParameter("comment"));
    }
    comments.add(comment);
    storeComment(comment);
    response.sendRedirect("/comment.html");
}

  private void storeComment(Comment comment){
    Entity commentEntity = new Entity(entityName);
    commentEntity.setProperty(nameProperty, comment.getName());
    commentEntity.setProperty(textProperty, comment.getText());
    commentEntity.setProperty(dateProperty, comment.getDate());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  private String formSubListJson(CommentList commentList, int start, int end){
      String json = "{";
      json+= " \"numComments\": " + commentList.getTotalComments() + ",";
      json+= " \"comments\":"+gson.toJson(commentList.getSubList(start, end));
      json+="}";
      return json;
  }
}
