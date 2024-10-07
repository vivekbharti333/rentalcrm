<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <style>
        /* Style for the message */
        .error-message {
            font-family: 'Arial', sans-serif;
            font-size: 24px;
            color: #FF5733; /* Red color */
            text-align: center;
        }

        /* Style for the container (optional) */
        .container {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
    </style>
</head>
<body>
    <div class="container">
        <p class="error-message">${message}</p>
    </div>
</body>
</html>

