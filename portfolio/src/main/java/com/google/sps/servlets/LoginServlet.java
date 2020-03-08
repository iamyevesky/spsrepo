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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();

    // If user is not logged in, show a login form (could also redirect to a login page)
    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL("/login");
      out.println("<p>You are not logged in. Log in to post comments</p>");
      out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
      return;
    }

    // If user has not set a name, redirect to setName page
    String name = getUserName(userService.getCurrentUser().getUserId());
    if (name == null) {
      response.sendRedirect("/name");
      return;
    }

    // User is logged in and has a name, so the request can proceed
    String logoutUrl = userService.createLogoutURL("/index.html");
    out.println("<p>Hello " + name + "!</p>");
    out.println("<p>Please leave below what you think about the site in the comments section:</p>");
    out.println("<form action = \"/data\" method = \"POST\">"+ 
//        "<label for=\"name\">Name:</label>"+
//        "<input type = \"text\" id = \"userName\" name = \"userName\" value = \"Your name here\">"+
//        "<br/>"+
        "<label for = \"comment\">Comment:</label>"+
        "<textarea type = \"text\" id = \"comment\" name = \"comment\">Your comment</textarea>"+
        "<input type = \"submit\">"+
        "<br/><br/>"+
    "</form>");
    out.println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
    out.println("<p>Don't like your name? Change your name <a href=\"/name\">here</a>.</p>");
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
