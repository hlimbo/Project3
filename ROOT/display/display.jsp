<%-- Show results--%>
<% if (request.getAttribute("displayResults") != null) { 
        Integer count = (Integer) request.getAttribute("displayGameCount");
        Integer offset = (Integer)request.getAttribute("displayOffset");
        Integer limit = (Integer)request.getAttribute("displayLimit"); %>
<%=     (String) request.getAttribute("displayResults")  %>
<%      if (count== null || count==-1 || offset==null || limit ==null) {
%>
<%      } else {
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
                <%= " <a href=\"/display/query"
                    +params+"offset=0"+paramsEnd
                    +"\"> First </a>"+
                    "<a href=\"/display/query"
                    +params+"offset="+Integer.toString(Math.max(0,offset-limit))+paramsEnd
                    +"\"> Previous </a> " %>
                <% 
            }
            int pageStart=Math.max(0,Math.min(offset/limit-10,
                count/limit-20+(count%limit==0 ? 0 : 1)));
            for (int i=pageStart;i<pages+pageStart;++i) {
            %>
            <%= " <a href=\"/display/query"
                +params+"offset="+Integer.toString(i*limit)+paramsEnd
                +"\">"+Integer.toString(i+1)+"</a> " %>
         <% }
            if (offset+limit<count) { %>
            <%= " <a href=\"/display/query"
                +params+"offset="+Integer.toString(offset+limit)+paramsEnd
                +"\">Next</a> "+
                "<a href=\"/display/query"
                +params+"offset="+Integer.toString(count-limit)+paramsEnd
                +"\">Last</a> "
            %>
            <% }
}
%>
<% } else { %>
<% } %>
