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
        <form>
        Game Name: <input hidden="hidden" id="gameName" type="text" value="" /> 
        Publisher Name: <input hidden="hidden" id="publisherName" type="text" value="" /> 
        <input id="addGame" type="button" value="Add Game" /> 
        <input id="insertPublisher" type="button" value="Insert Publisher" />
        <input id="meta" type="button" value="Get Database Meta Data" />
        <input hidden="hidden" id="menuReturn" type="button" value="Back to Main Menu" />
        </form>
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
                $('#gameName').show();
                $('#publisherName').show();
                $('#data_container').hide();
            }
            function handleFailure (data, status) {
                alert("failed with "+status);
            }
            $('#addGame').click( function () {
                hideMenu();
            });
            $('#insertPublisher').click( function () {
                hideMenu();
                $('#publisherName').show();
                $.get("/dashboard_command",{
                    command : "insert_publisher",
                    publisher : $('#publisherName').val()
                }).done(function (data) {
                    xmlDoc = $.parseXML(data);
                    $xml = $( xmlDoc );
                    error = $xml.find("exception");
                    if (!printXmlException($xml,'#data_container')) {
                        insertCode = $xml.find("status_code").text();
                        if (insertCode == 1) {
                            $('#data_container').empty();
                            $('#data_container').append("Publisher inserted into table.");
                        } else {
                            failureMessage = $xml.find("message").text();
                            $('#data_container').empty();
                            $('#data_container').append(failureMessage);
                        }
                    }
                }).fail(handleFailure);
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
                }).fail(handleFailure);
            });
            $('#menuReturn').click( function () {
                showMenu();
            });
        </script>
        <div id="data_container">
        </div>
    </body>
</html>
