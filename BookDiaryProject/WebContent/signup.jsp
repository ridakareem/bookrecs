<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sign Up - The Book Shelf</title>
  <style>
    body { font-family: Arial, sans-serif; background-color: #e6e7d5; display:flex; flex-direction:column; align-items:center; justify-content:center; min-height:100vh; margin:0;}
    h1 { font-family:"Georgia", serif; font-weight:bold; margin-bottom:0.2em; }
    h2 { font-weight:normal; margin-bottom:1.5em; }
    .form-box { background:#f5f3f3; padding:30px; border-radius:8px; box-shadow:0 4px 10px rgba(0,0,0,0.2); width:350px; }
    .form-box label { display:block; margin:10px 0 5px; font-weight:bold; }
    .form-box input { width:100%; padding:10px; margin-bottom:15px; border:none; background:#d9d8d8; border-radius:4px; }
    .form-box button { width:100%; padding:12px; background:black; color:white; border:none; border-radius:4px; font-size:16px; cursor:pointer; }
    .form-box button:hover { background:#333; }
    .form-box p { text-align:center; margin-top:10px; }
    .form-box a { color:black; text-decoration:none; font-weight:bold; }
  </style>
</head>
<body>
  <h1>THE BOOK SHELF</h1>
  <h2>CREATE ACCOUNT</h2>
  
  <div class="form-box">
    <form action="signup" method="post">
      <label>Username</label>
      <input type="text" name="username" placeholder="Enter your username" required>
      
      <label>Password</label>
      <input type="password" name="password" placeholder="Enter your password" required>
      
      <button type="submit">Sign Up</button>
      <p>Already have an account? <a href="index.jsp">Sign In</a></p>
    </form>
  </div>
</body>
</html>
