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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public Task(long id, String title, long timestamp) {
      this.id = id;
      this.title = title;
      this.timestamp = timestamp;
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
  }

  public ArrayList<String> messages = new ArrayList<String>(List.of("Hello", "Goodbye", "Thanks"));
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    List<Task> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      // long id = entity.getKey().getId();
      // String title = (String) entity.getProperty("title");
      // long timestamp = (long) entity.getProperty("timestamp");

      // Task task = new Task(id, title, timestamp);
      long id = entity.getKey().getId();
      String title = (String) entity.getProperty("title");
      long timestamp = (long) entity.getProperty("timestamp");

      Task task = new Task(id, title, timestamp);
      comments.add(task);
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

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String title = request.getParameter("title");
    long timestamp = System.currentTimeMillis();

    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("title", title);
    taskEntity.setProperty("timestamp", timestamp);
    datastore.put(taskEntity);

    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println(title);
  }
}
