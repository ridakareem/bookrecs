<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Book Logger</title>
</head>
<body>
    <h1>📚 Book Logger</h1>

    <h2>Add Book</h2>
    <form action="AddBookServlet" method="post">
        <input type="text" name="query" placeholder="Enter book title/author" required />
        <button type="submit">Add</button>
    </form>

    <h2>View Books</h2>
    <form action="ViewBooksServlet" method="get">
        <button type="submit">View All Books</button>
    </form>

    <h2>Remove Book</h2>
    <form action="RemoveBookServlet" method="post">
        <input type="text" name="title" placeholder="Enter book title to remove" required />
        <button type="submit">Remove</button>
    </form>
</body>
</html>
