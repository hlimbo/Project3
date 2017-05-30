<%@ page import="java.net.*" %>
<%@ page import="java.io.*" %>

<style>
    .games_row {
        display: inline-block;
        border-size:2px;
        border-style: solid;
        border-color: black;
    }
</style>

<%-- Show results--%>
<% if (request.getAttribute("searchResults") != null) { %>
    <form action="/search/query" method="GET">
    <%        boolean descend = false;
          String lim = "50";
          if (request.getQueryString() != null) {
            String descendQuery = request.getQueryString();
            if (request.getParameter("descend")!=null
                && ((String)request.getParameter("descend")
                ).trim().compareToIgnoreCase("true")==0) {
                descend=true;
            }
            if (descendQuery != null) {
                for (String param : descendQuery.split("&")) {
					 if( param.split("=").length > 1 && param.split("=")[0].trim().compareToIgnoreCase("descend")!=0
                     && param.split("=")[0].trim().compareToIgnoreCase("limit")!=0) { 
				        String parsedValue = "name=\'" + param.split("=")[0] + "\'" + " value=\'"; 
					    try {
                            String decodedValue = URLDecoder.decode(param.split("=")[1], "UTF-8");
                            parsedValue += decodedValue + "\'";  %>
                            <input type="hidden" <%= parsedValue   %> />
					<% } catch (UnsupportedEncodingException e) { 
						e.printStackTrace(); 
					   } 
                       } else if (param.split("=").length > 1 && param.split("=")[0].trim().compareToIgnoreCase("limit")==0) { 
                        try {
                            Integer limInt = Integer.parseInt(param.split("=")[1].trim());
                            if (limInt < 50) {
                                if (limInt == 0) {
                                    lim = "1";
                                } else if (limInt > 0) {
                                    lim = limInt.toString();
                                } else {
                                    lim = ((Integer)(-limInt)).toString();
                                }
                            }
                        } catch (NumberFormatException ex) {}
                     }
                }
            }
          } 
          %>
        <% if (descend==true) { %> 
            Reverse Order? <input type="checkbox" name="descend" value="true" checked="checked" /> <br />
        <% } else { %>
            Reverse Order? <input type="checkbox" name="descend" value="true" /> <br />
        <% } %>
        <%-- input type="checkbox" value="Reverse Sort?" /> <br / --%>
        results per page (max 50): <input type="text" name="limit" value<%= "=\""+lim+"\""%> /> <br />
        <input type="submit" value="Reorder Search" />
</form>
    </form>
    <%-- TODO: Add javascript pop-up window to searchResults game names --%>
    <%= (String) request.getAttribute("searchResults")  %>
    <br />
    <%  if (((String)request.getAttribute("searchResults")).trim().compareTo("")==0) {
    %>  <p> No Search Results </p> <%
    } %>
    <%
        Integer count = (int) request.getAttribute("searchCount");
        Integer offset = (int)request.getAttribute("searchOffset");
        Integer limit = (int)request.getAttribute("searchLimit");
        if (offset == null || limit==null) {
        } else if (offset > -1 && limit > -1) {
            if (limit==0) {
                limit = 1;
            }
            int pages = count/limit;
            if (count % limit != 0) {
                ++pages;
            }
            String params = "?"+request.getQueryString();
            String paramsEnd = "";
            int offsetStart =  params.indexOf("offset=");
            if  (offsetStart > -1) {
                int offsetEnd = params.substring(offsetStart).indexOf("&");
                if (offsetEnd == -1) {
                    params=params.substring(0,offsetStart);
                } else {
                    paramsEnd=params.substring(offsetStart+offsetEnd);
                    params=params.substring(0,offsetStart);
                }
            } else {
                params=params+"&";
            }
            boolean limitPages = false;
            if (pages > 20) {
                limitPages = true;
                pages = 20;
            }
            if (offset > 0) { %>
                <%= " <a href=\"/search/query"
                    +params+"offset=0"+paramsEnd
                    +"\"> First </a>"+
                    "<a href=\"/search/query"
                    +params+"offset="+Integer.toString(Math.max(0,offset-limit))+paramsEnd
                    +"\"> Previous </a> " %>
                <% 
            }
            int pageStart=Math.max(0,Math.min(offset/limit-10,
                count/limit-20+(count%limit==0 ? 0 : 1)));
            for (int i=pageStart;i<pages+pageStart;++i) {
            %>
            <%= " <a href=\"/search/query"
                +params+"offset="+Integer.toString(i*limit)+paramsEnd
                +"\">"+Integer.toString(i+1)+"</a> " %>
         <% }
            if (offset+limit<count) { %>
            <%= " <a href=\"/search/query"
                +params+"offset="+Integer.toString(offset+limit)+paramsEnd
                +"\">Next</a> "+
                "<a href=\"/search/query"
                +params+"offset="+Integer.toString(count-limit)+paramsEnd
                +"\">Last</a> "
            %>
            <% }
        } else { %>
    <% } %>
<%-- default is to ask for the search--%>
<div id="hidden" hidden="hidden"></div>
<script>
$('.games_name').hover(function(ev,ui){
        $('#hidden').empty();
        var id = $(this).parent().find('.games_id').text();
        $('#hidden').load("/dbox/query?id="+id);
        $('#hidden').dialog({
            position: {my: "left top",
            at: "left bottom",
            of: $(this)},
            maxHeight:300});
    },
    function(ev,ui){});
</script>
<% } else { %>
Search
<form action="/search/query" method="GET">
    title: <input id="gameNameField" type="TEXT" name="name" />
<%--    year: <input type="TEXT" name="year" /> <BR />
    genre: <input type="TEXT" name="genre" /> <BR />
    platform: <input type="TEXT" name="platform" /> <BR />
    publisher: <input type="TEXT" name="publisher" /> <BR />
    results per page (max 50): <input type="text" name="limit" /> <BR />
    exact search?: <input type="checkbox" name="match" value="true" /> <BR />
    <input type="HIDDEN" name="forward" <%= "value="+request.getRequestURI() %> /> --%>
    <input type="SUBMIT" value="Search" /> <br />
    string distance: <input type="TEXT" name="leda" value="0"/> <br />
</form>
<script>
    var query = [];
    var callID = 0;
    $('#gameNameField').keydown(function (ev){
        //var letter = ev.which;
        //avoid ASCII control characters
        if (ev.which >= 32) {
            typed = $('#gameNameField').val()+String.fromCharCode(ev.which);
        } else {
            typed = $('#gameNameField').val();
            if (ev.which==8) {
                typed=typed.substring(0,typed.length-1);
            }
        }
        var ajaxID = ++callID;
        $.ajax({ 
            url : "/search/xquery",
            //data : "name="+encodeURIComponent(typed)+"&limit=10&match=3",
            data : {
            //name : encodeURIComponent(typed.trim()).replace("%20","+"),
            name : typed.trim(),
            limit : 10,
            match : 3,
            leda : 0},
            success: function (data) {
                //$('#gameNameField').autocomplete("disable");
                xmlDoc = $.parseXML(data);
                xmlDoc = $(data);
                if (xmlDoc != null && ajaxID==callID) {
                   query.length=0;
                   $xml = $(xmlDoc);
                   names = $xml.find('.games_name');
                   for (i=0;i<names.length;++i) {
                       texts=names.eq(i).find('atext');
                       for (j=0;j<texts.length;++j) {
                         query.push(texts.eq(j).text());
                       }
                   }
                }
                //alert(query);
                var quSource = function (ev,ui) {
                        ui(query);
                }
                var complete = $('#gameNameField').autocomplete({
                    //source: query
                    source : quSource
                });
                //$('#gameNameField').autocomplete("destroy");
                //$('#gameNameField').autocomplete("search",typed.trim());
                //$('#gameNameField').autocomplete("enable");
                $('#gameNameField').autocomplete("search",typed.trim());
                //$('#gameNameField').autocomplete("option",{source : quSource});
            },
            failure : function (data) {
                alert("AJAX request failed!");
            }
        });
    });
</script>

<% } %>
