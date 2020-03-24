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

import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.*;
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<ArrayList<String>> comments;
  UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      if (!userService.isUserLoggedIn()) {
        response.sendRedirect("/index.html");
        return;
      }
      String name = getUserName(userService.getCurrentUser().getUserId());
      if(name.length() == 0){
          name = "User at "+(String) new Date().toLocaleString();
      }
      String comment = name + ": " + request.getParameter("comment");
      
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("comment", comment);
      commentEntity.setProperty("timestamp", System.currentTimeMillis());
      commentEntity.setProperty("UserID", userService.getCurrentUser().getUserId());
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
      response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    comments = new ArrayList<ArrayList<String>>();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      ArrayList<String> comment = new ArrayList<>();
      String id = String.valueOf(entity.getKey().getId());
      String commentString = String.valueOf(entity.getProperty("comment"));
      comment.add(id);
      comment.add(commentString);
      comments.add(comment);
    }
    response.setContentType("application/json; charset=utf-8");
    response.getWriter().println(convertToJSON(comments));
  }

  /** Returns any Java object in JSON format
   * 
   * @param object: Object to be converted
   * @return String JSON format of the object
   */
   private String convertToJSON(Object object){
       Gson gson = new Gson();
       return gson.toJson(object);
   }

   /** Returns the name of the user with id, or null if the user has not set a name. */
  private String getUserName(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery result = datastore.prepare(query);
    Entity entity = result.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String name = (String) entity.getProperty("name");
    return name;
  }
}