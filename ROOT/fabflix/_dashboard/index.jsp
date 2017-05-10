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
        <input id="meta" type="button" value="Get Meta Data" />
        <script src="/jsScripts/jquery.js"></script>
        <script>
                $('#meta').click( function () {$.get("/dashboard_command",{
                    command : "meta"
                }).done(function (data) {
                    xmlDoc = $.parseXML(data);
                    $xml = $( xmlDoc );
                    error = $xml.find("exception");
                    if (error.length > 0) {
                        errors="<ul>";
                        for (i=0;i<error.length;i++) {
                            stack = error.find("stack");
                            for (j=0;j<stack.length;j++) {
                                errors+="<li>"+stack.eq(j).text()+"</li>";
                            }
                        }
                        errors+="</ul>";
                        $('#data_container').empty();
                        $('#data_container').append(errors);
                    } else {
                        tables = $xml.find('meta_table');
                        tableList = "<ul>";
                        for (i=0;i<tables.length;i++) {
                            tableList+="<li>"+tables.eq(i).text()+"</li>";
                        }
                        tableList += "</ul>";
                        $('#data_container').empty();
                        $('#data_container').append(tableList);
                    }
                }).fail(function(data, status) {
                    alert("failed with "+status);
                })});
        </script>
        <div id="data_container">
        </div>
    </body>
</html>
