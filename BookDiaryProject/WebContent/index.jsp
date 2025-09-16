<%@ page session="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sign In - The Book Shelf</title>
  <style>
    body { 
      font-family: Arial, sans-serif; 
      background-color: #e6e7d5; 
      display:flex; 
      flex-direction:column; 
      align-items:center; 
      justify-content:flex-start; 
      min-height:100vh; 
      margin:0;
      padding-top:50px;
    }

    /* Logo styling */
    .logo {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 20px;
    }
    .logo svg {
      width: 80px;
      height: auto;
      stroke: #000;
      stroke-width: 2;
      fill: none;
    }
    .logo h1 {
      margin: 10px 0 5px;
      font-size: 24px;
      letter-spacing: 2px;
      font-weight: bold;
    }

    h2 { font-weight:normal; margin-bottom:1.5em; }

    .form-box { 
      background:#f5f3f3; 
      padding:30px; 
      border-radius:8px; 
      box-shadow:0 4px 10px rgba(0,0,0,0.2); 
      width:350px; 
    }
    .form-box label { display:block; margin:10px 0 5px; font-weight:bold; }
    .form-box input { width:100%; padding:10px; margin-bottom:15px; border:none; background:#d9d8d8; border-radius:4px; }
    .form-box button { width:100%; padding:12px; background:black; color:white; border:none; border-radius:4px; font-size:16px; cursor:pointer; }
    .form-box button:hover { background:#333; }
    .form-box p { text-align:center; margin-top:10px; }
    .form-box a { color:black; text-decoration:none; font-weight:bold; }
  </style>
</head>
<body>

  <!-- Logo Section -->
  <div class="logo">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 100">
      <rect x="10" y="20" width="15" height="60" />
      <rect x="30" y="20" width="15" height="60" />
      <rect x="50" y="20" width="15" height="60" />
      <rect x="70" y="25" width="15" height="55" transform="rotate(5, 77, 52)" />
      <rect x="90" y="15" width="15" height="65" transform="rotate(-5, 97, 48)" />
    </svg>
    <h1>THE BOOK SHELF</h1>
  </div>

  <h2>WELCOME BACK</h2>

  <!-- Form Section -->
  <div class="form-box">
    <form action="login" method="post">
      <label>Username</label>
      <input type="text" name="username" placeholder="Enter your username" required>
      
      <label>Password</label>
      <input type="password" name="password" placeholder="Enter your password" required>
      
      <button type="submit">Sign In</button>
      <p>Don’t have an account? <a href="signup.jsp">Sign Up</a></p>
    </form>
  </div>

</body>
</html>
