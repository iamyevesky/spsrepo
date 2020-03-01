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
import java.util.*;
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private List<String> comments;
  
  @Override
  public void init(){
      comments = new ArrayList<>();
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String name = request.getParameter("userName");
      if(name.length() == 0){
          name = "User at "+(String) new Date().toLocaleString();
      }
      String comment = name + ": " + request.getParameter("comment");
      comments.add(comment);
      response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
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
}


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
/**
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private List<String> thanosQuotes;
  
  @Override
  public void init(){
      thanosQuotes = new ArrayList<>();
      thanosQuotes.add("The hardest choices require the strongest wills.");
      thanosQuotes.add("I ask you to what end? Dread it. Run from it. "+
                         "Destiny arrives all "+
                         "the same.");
      thanosQuotes.add("I know what it's like to lose. "
                       +"To feel so desperately that you're right, "+
                       "yet to fail nonetheless. It's frightening, "+
                       "turns the legs to jelly.");
      thanosQuotes.add("Perfectly balanced, as all things should be.");
      thanosQuotes.add("I do. You are not the only one cursed with knowledge.");
      thanosQuotes.add("I finally rest and watch the sun rise on a grateful universe.");
      thanosQuotes.add("Fine! I will do it myself.");
      thanosQuotes.add("I ignored by destiny once. I cannot do that again, "+
                        "even for you.");
      thanosQuotes.add("With all the six stones I could simply snap and they "+
                       "would cease to exist. I call that mercy");
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    response.getWriter().println(convertToJSON(thanosQuotes));
  }

   private String convertToJSON(Object object){
       Gson gson = new Gson();
       return gson.toJson(object);
   }
}
*/
