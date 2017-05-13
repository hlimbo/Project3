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
            <div hidden="hidden" id="addGameFields">
                <div class="label"> Game Name: <input  id="gameName" type="text" value="" /></div>
                <input id="addGame" type="button" value="Add" /> 
            </div>
            <div hidden="hidden" id="insertPublisherFields">
                <div class="label"> Publisher Name: <input id="publisherName" type="text" value="" /></div>
                <input id="insertPublisher" type="button" value="Add" />
            </div>
        <input id="menuAddGame" type="button" value="Add Game" /> 
        <input id="menuInsertPublisher" type="button" value="Add Publisher" />
        <input id="meta" type="button" value="Get Database Meta Data" />
        <div hidden="hidden" id="data_container"></div>
        <input hidden="hidden" id="menuReturn" type="button" value="Back to Main Menu" />
        </form>
        <script src="/jsScripts/jquery.js"></script>
        <script src="/jsScripts/utils.js"></script>
        <script>
            function hideMenu () {
                $('#menuAddGame').hide();
                $('#menuInsertPublisher').hide();
                $('#meta').hide();
                $('#menuReturn').show();
                $('#data_container').empty();
                $('#data_container').show();
            }
            function showMenu () {
                $('#addGameFields').hide();
                $('#insertPublisherFields').hide();
                $('#menuReturn').hide();
                $('#data_container').hide();
                $('#menuAddGame').show();
                $('#menuInsertPublisher').show();
                $('#meta').show();
            }
            showMenu();
            function handleFailure (data, status) {
                alert("failed with "+status);
            }
            $('#menuAddGame').click( function () {
                hideMenu();
                $('#addGameFields').show();
            });
            $('#addGame').click( function () {
                hideMenu();
                $('#addGameFields').show();
                $('#data_container').append('Performing Request...');
            });
            $('#menuInsertPublisher').click( function () {
                hideMenu();
                $('#insertPublisherFields').show();
            });
            $('#insertPublisher').click( function () {
                hideMenu();
                $('#insertPublisherFields').show();
                $('#publisherName').show();
                $('#data_container').append('Performing Request...');
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
                $('#data_container').append('Performing Request...');
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
    </body>
</html>
