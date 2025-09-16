<%@ page session="true" %>
<%@ page import="java.util.List" %>
<%@ page import="servlets.ViewBooksServlet.Book" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard - The Book Shelf</title>
  <style>
    body { font-family: Arial, sans-serif; background-color:#e6e7d5; margin:0; padding:20px; }
    h1 { font-family:"Georgia", serif; font-weight:bold; }
    .search-box { margin:20px 0; }
    input[type=text] { padding:10px; width:250px; border-radius:4px; border:none; background:#d9d8d8; }
    button { padding:10px; border:none; background:black; color:white; border-radius:4px; cursor:pointer; }
    button:hover { background:#333; }
    .book { background:#f5f3f3; padding:15px; border-radius:8px; margin:10px 0; box-shadow:0 2px 5px rgba(0,0,0,0.2); }
    form { display:inline; }
  </style>
</head>
<body>
  <h1>Welcome, <%= session.getAttribute("username") %></h1>

  <div class="search-box">
    <form action="searchBook" method="get">
      <input type="text" name="query" placeholder="Search books" required>
      <button type="submit">Search</button>
    </form>
    <a href="index.jsp"><button>Logout</button></a>
  </div>

  <h2>My Diary</h2>
  <%
      List<Book> books = (List<Book>) request.getAttribute("books");
      if(books != null){
          for(Book b : books){
  %>
      <div class="book">
          <%= b.title %>
          <form action="removeBook" method="post">
              <input type="hidden" name="bookId" value="<%= b.id %>">
              <button type="submit">Remove</button>
          </form>
      </div>
  <%
          }
      }
  %>
</body>
</html>


