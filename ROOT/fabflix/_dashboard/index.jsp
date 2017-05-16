<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>

<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="en">
    <head>
        <meta charset="UTF-8" />
		<TITLE>Employee Dashboard</TITLE>
<style>
    ul.metaTable {
        display: inline-block;
        border-style: solid;
        border-color: green black black black;
    }
    ul.metaTable li.metaTableName {
        color: green;
    }
    li {
        list-style-type: none;
    }
</style>
    </head>

    <body>
        <script src="/jsScripts/jquery.js"></script>
        <script src="/jsScripts/utils.js"></script>
        <c:if test='${sessionScope.employee != null}'>
            <form hidden="hidden" id="addGameFields">
                <div class="label"> Game Name: <input  id="gameName" type="text" value="" required /></div>
                <div class="label"> Year: <input  id="gameYear" type="number" value="" required /></div>
                <div class="label"> Price: <input  id="gamePrice" type="number" value="" required /></div>
                <div class="label"> Platform: <input id="gamePlatform" type="text" value="" required /></div>
                <div class="label"> Publisher: <input id="gamePublisher" type="text" value="" required /></div>
                <div class="label"> Genre: <input id="gameGenre" type="text" value="" required /></div>
                <input id="addGame" type="submit" value="Add" /> 
            </form>
            <form hidden="hidden" id="insertPublisherFields">
                <div class="label"> Publisher Name: <input id="publisherName" type="text" value="" required /></div>
                <input id="insertPublisher" type="submit" value="Add" />
            </form>
            <input id="menuAddGame" type="button" value="Add Game" /> 
            <input id="menuInsertPublisher" type="button" value="Add Publisher" />
            <input id="meta" type="button" value="Get Database Meta Data" />
            <input hidden="hidden" id="menuReturn" type="button" value="Back to Main Menu" />
            <div hidden="hidden" id="data_container"></div>
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
                $('#addGameFields').submit( function (ev) {
                    ev.preventDefault();
                    hideMenu();
                    $('#addGameFields').show();
                    $('#data_container').append('Performing Request...');
                    $.get("/dashboard_command",{
                        command : "add_game",
                        name : $('#gameName').val(),
                        year : $('#gameYear').val(),
                        price : $('#gamePrice').val(),
                        publisher : $('#gamePublisher').val(),
                        platform : $('#gamePlatform').val(),
                        genre: $('#gameGenre').val()
                    }).done(function (data) {
                        xmlDoc = $.parseXML(data);
                        $xml = $( xmlDoc );
                        if (!printXmlException($xml,'#data_container')) {
                            insertCode = $xml.find("status_code").text();
                            if (insertCode == 1) {
                                $('#data_container').empty();
                                $('#data_container').append("Call successful. Necessary inserts performed.");
                            } else if (insertCode == 2) {
                                $('#data_container').empty();
                                $('#data_container').append("Updated in tables.");
                            } else {
                                failureMessage = $xml.find("message").text();
                                $('#data_container').empty();
                                $('#data_container').append(failureMessage);
                            }
                        }
                    }).fail(handleFailure);
                });
                $('#menuInsertPublisher').click( function () {
                    hideMenu();
                    $('#insertPublisherFields').show();
                });
                $('#insertPublisherFields').submit( function (ev) {
                    ev.preventDefault();
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
                        if (!printXmlException($xml,'#data_container')) {
                            insertCode = $xml.find("status_code").text();
                            if (insertCode == 1) {
                                $('#data_container').empty();
                                $('#data_container').append("Publisher inserted into table.");
                            } else if (insertCode == 2) {
                                $('#data_container').empty();
                                $('#data_container').append("Publisher already existed.");
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
                        if (!printXmlException($xml,'#data_container')) {
                            tables = $xml.find('meta_info');
                            tableList = "";
                            for (i=0;i<tables.length;i++) {
                                tableList += "<ul class=\"metaTable\">";
                                table = tables.eq(i);
                                tableList+="<li class=\"metaTableName\">"+table.find('meta_table').text()+"</li>\n";
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
        </c:if>
        <c:if test='${sessionScope.employee == null}'>
            <script src='https://www.google.com/recaptcha/api.js'></script>
            <div hidden="hidden" id="data_container"></div>
            Email: <input id="loginEmail" type="email" name="email" required /><br />
            Password: <input id="loginPassword" type="password" name="password" required /><br />
            <input id="employeeLogin" type="button" value="Login" /> 
            <script>
                $('#employeeLogin').click( function () {
                    $('#data_container').empty();
                    $('#data_container').append("Atempting Login...");
                    $.ajax({ url : "/dashboard_command",
                        data : {email : $('#loginEmail').val(),
                            password : $('#loginPassword').val(),
                            'g-recaptcha-response' : grecaptcha.getResponse() },
                        success : function(data) {
                            xmlDoc = $.parseXML(data);
                            if (xmlDoc == null) {
                                $xml = null;
                            } else {
                                $xml = $( xmlDoc );
                            }
                            if ($xml == null) {
                                $('#data_container').empty();
                                $('#data_container').show();
                                $('#data_container').append("Please fill out the form");
                                grecaptcha.reset();
                            } else if (!printXmlException($xml,'#data_container')) {
                                loginCode = $xml.find("status_code").text();
                                if (loginCode == 1) {
                                    window.location.reload();
                                } else {
                                    $('#data_container').empty();
                                    $('#data_container').show();
                                    failureMessage = $xml.find("message").text();
                                    $('#data_container').append(failureMessage);
                                    grecaptcha.reset();
                                }
                            } else {
                                    grecaptcha.reset();
                            }
                        }
                    })
                });
            </script>
            <div id="login-captcha" class="g-recaptcha" data-callback=setCaptcha data-sitekey="6LeuvCAUAAAAAB68t-UTWj7SyL02eIYRd3MOswzm"></div>
        </c:if>
    </body>
</html>
