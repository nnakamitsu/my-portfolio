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
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
// import com.google.sps.data.Task;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.*;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  public class Task {
    public long id;
    public String title;
    public long timestamp;
    public String name;
    public int likes;
    public String email;
    public String displayemail;

    public Task(long id, String title, long timestamp, String name, String email, String displayemail) {
      this.id = id;
      this.title = title;
      this.timestamp = timestamp;
      this.name = name;
      this.likes = 0;
      this.email = email;
      this.displayemail = displayemail;
    }

    public long getId() {
      return id;
    }
    
    public String getTitle() {
      return title;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public String getName() {
      return name;
    }

    public String getEmail() {
      return email;
    }

  }
  public int maxcount = 3;

  // all options: "newest (descending), oldest (ascending), alphabetical, reverse-alphabetical"
  public String sort = "newest";
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  

  /** Responds with a JSON array containing comments data. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!(request.getParameter("sort") == null)) {
      sort = request.getParameter("sort");
    }
    Query query;
    if (sort.equals("newest")) {
      query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);
    } else if (sort.equals("oldest")) {
      query = new Query("Task").addSort("timestamp", SortDirection.ASCENDING);
    } else if (sort.equals("alphabetical")) {
      query = new Query("Task").addSort("name", SortDirection.ASCENDING);
    } else {
      query = new Query("Task").addSort("name", SortDirection.DESCENDING);
    }
    PreparedQuery results = datastore.prepare(query);
    int count = 0;

    if (!(request.getParameter("maxcomments") == null)) {
      maxcount = Integer.parseInt(request.getParameter("maxcomments"));
    }

    List<Task> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String title = (String) entity.getProperty("title");
      long timestamp = (long) entity.getProperty("timestamp");
      String name = (String) entity.getProperty("name");
      String email = (String) entity.getProperty("email");
      String displayemail = (String) entity.getProperty("displayemail");
      Task task = new Task(id, title, timestamp, name, email, displayemail);
      comments.add(task);

      count++;
      if (count >= maxcount) {
        break;
      }
    }

    response.setContentType("application/json;");
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    response.getWriter().println(json);
  }

  private String convertToJsonUsingGson(ArrayList<String> lst) {
    Gson gson = new Gson();
    String json = gson.toJson(lst);
    return json;
  }

  // A simple HTTP handler to extract text input from submitted web form and respond that context back to the user.
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();

    // Must be logged in to post comments
    String title = request.getParameter("title");
    String name = request.getParameter("name");
    long timestamp = System.currentTimeMillis();
    String email = userService.getCurrentUser().getEmail();
    String displayemail;
    
    if (request.getParameter("displayemail") == null) {
      displayemail = "off";
    } else {
      displayemail = "on";
    }

    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("title", title);
    taskEntity.setProperty("name", name);
    taskEntity.setProperty("timestamp", timestamp);
    taskEntity.setProperty("email", email);
    taskEntity.setProperty("displayemail", displayemail);
    datastore.put(taskEntity);

    response.sendRedirect("/index.html");
    
  }

  public void updateCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!(request.getParameter("maxcomments") == null)) {
      maxcount = Integer.parseInt(request.getParameter("maxcomments"));
    }
  
  }
}
