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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private ArrayList<String> comments;
    private Gson gson = new Gson();

    @Override
    public void init(){
        comments = new ArrayList<>();
        comments.add("Example Comment");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Comment");//.addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            comments.add(entity.getProperty("comment").toString());
        }
    }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    // response.getWriter().print("Hello Gus!");
    response.getWriter().print(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment");
    comments.add(comment);
    storeComment(comment);
    response.sendRedirect("/comment.html");
  }

  private void storeComment(String comment){
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment", comment);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }
}
