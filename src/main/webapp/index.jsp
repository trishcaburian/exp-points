<%-- 
    Document   : index
    Created on : Mar 6, 2016, 8:06:25 PM
    Author     : yla
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>It's something</title>
    </head>
    <body>
        <form method="POST" action="TwilioServlet">
			<p>
				<input name="to_translate" id="to_translate" type="text" value="" placeholder="Enter your Twilio phone number here">
            </p>
            <input type="submit" value="Translate!">
        </form>
    </body>
</html>
