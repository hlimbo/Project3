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
        <input id="addGame" type="button" value="Add Game" /> 
        <input id="insertPublisher" type="button" value="Insert Publisher" />
        <input id="meta" type="button" value="Get Database Meta Data" />
        <input id="menuReturn" type="button" value="Back to Main Menu" />
        <script src="/jsScripts/jquery.js"></script>
        <script src="/jsScripts/utils.js"></script>
        <script>
            function hideMenu () {
                $('#addGame').hide();
                $('#insertPublisher').hide();
                $('#meta').hide();
                $('#menuReturn').show();
                $('#data_container').empty();
                $('#data_container').show();
                $('#data_container').append('Performing Request...');
            }
            showMenu();
            function showMenu () {
                $('#addGame').show();
                $('#insertPublisher').show();
                $('#meta').show();
                $('#menuReturn').hide();
                $('#data_container').hide();
            }
            $('#addGame').click( function () {
                hideMenu();
                $('#data_container').empty();
                $('#data_container').append('Performing Request...');
            });
            $('#insertPublisher').click( function () {
                hideMenu();
            });
            $('#meta').click( function () {
                hideMenu();
                $.get("/dashboard_command",{
                    command : "meta"
                }).done(function (data) {
                    xmlDoc = $.parseXML(data);
                    $xml = $( xmlDoc );
                    error = $xml.find("exception");
                    if (!printXmlException($xml,'#data_container')) {
                        tables = $xml.find('meta_info');
                        tableList = "";
                        for (i=0;i<tables.length;i++) {
                            tableList += "<ul>";
                            table = tables.eq(i);
                            tableList+="<li>"+table.find('meta_table').text()+"</li>\n";
                            columns = table.find('meta_column');
                            for (j=0;j<columns.length;j++) {
                                tableList+="<li>"+columns.eq(j).find('key').text()
                                    +" "+columns.eq(j).find('value').text()+"</li>\n";
                            }
                            tableList += "</ul>\n";
                        }
                        $('#data_container').empty();
                        $('#data_container').append(tableList);
                    }
                }).fail(function(data, status) {
                    alert("failed with "+status);
                })
            });
            $('#menuReturn').click( function () {
                showMenu();
            });
        </script>
        <div id="data_container">
        </div>
    </body>
</html>
