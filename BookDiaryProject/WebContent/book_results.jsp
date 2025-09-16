<%@ page import="org.json.*" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Search Results - The Book Shelf</title>
  <style>
    body { font-family: Arial, sans-serif; background-color:#e6e7d5; margin:0; padding:20px; }
    h1 { font-family:"Georgia", serif; font-weight:bold; }
    .book { background:#f5f3f3; padding:15px; border-radius:8px; margin:10px 0; box-shadow:0 2px 5px rgba(0,0,0,0.2); }
    button { padding:8px 12px; border:none; background:black; color:white; border-radius:4px; cursor:pointer; }
    button:hover { background:#333; }
    form { display:inline; }
  </style>
</head>
<body>
  <h1>Search Results</h1>
  <a href="dashboard.jsp"><button>Back to Dashboard</button></a>

  <%
      String json = (String) request.getAttribute("json");
      if(json != null){
          JSONObject obj = new JSONObject(json);
          JSONArray items = obj.optJSONArray("items");
          if(items != null){
              for(int i=0;i<items.length();i++){
                  JSONObject book = items.getJSONObject(i).getJSONObject("volumeInfo");
                  String title = book.optString("title");
  %>
      <div class="book">
          <b><%= title %></b>
          <form action="addBook" method="post">
              <input type="hidden" name="title" value="<%= title %>">
              <button type="submit">Add to Diary</button>
          </form>
      </div>
  <%
              }
          } else {
  %>
      <p>No results found.</p>
  <%
          }
      }
  %>
</body>
</html>
