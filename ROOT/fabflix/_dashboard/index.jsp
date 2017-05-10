<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>

<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>

<!-- jstl include -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="en">
    <head>
        <meta charset="UTF-8" />
		<TITLE>Employee Dashboard</TITLE>
    </head>

    <body>
        <script src="/jsScripts/jquery.js"></script>
        <script>
                $.get("/dashboard_command",{
                    command : "meta"
                }).done(function (data) {
                    xmlDoc = $.parseXML(data);
                    $xml = $( xmlDoc );
                    tables = $xml.find('meta_table');
                    tableList = "<ul>";
                    for (i=0;i<tables.length;i++) {
                        tableList+="<li>"+tables.eq(i).text()+"</li>";
                    }
                    tableList += "</ul>";
                    $('#data_container').append(tableList);
                }).fail(function(data, status) {
                    alert("failed with "+status);
                });
        </script>
        <div id="data_container">
        </div>
    </body>
</html>
