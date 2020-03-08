package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for deleting tasks. */
@WebServlet("/deleteComment")
public class DeleteCommentServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL("/index.html");
      response.sendRedirect(loginUrl);
      return;
    }
    long id = Long.parseLong(request.getParameter("idInput"), 10);
    Key commentEntityKey = KeyFactory.createKey("Comment", id);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity ent;
    try{
        ent = datastore.get(commentEntityKey);
    }catch(EntityNotFoundException e){
        return;
    }
    if(ent == null){
        return;
    }
    if(((String) ent.getProperty("UserID")).compareTo((String) userService.getCurrentUser().getUserId()) == 0){
        datastore.delete(commentEntityKey);
    }
    else if(((String)userService.getCurrentUser().getEmail()).compareTo("syevenyo@sps-program.com") == 0){
        datastore.delete(commentEntityKey);
    }
    response.sendRedirect("/index.html");
  }
}