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
        <form method="POST" action="Servlet">
			<p>
				<input name="img_url" id="img_url" type="text" value="" placeholder="Image URL">
            </p>
            <input type="submit" value="Extract Information">
        </form>
    </body>
</html>
