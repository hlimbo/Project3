<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" 
    import="java.util.*" %>
Browse
<ul>
    <%  ArrayList<String> tables = new ArrayList<String>();
        Hashtable<String,String> ignores = new Hashtable<String,String> ();
        ignores.put("games","8");
        ignores.put("publishers","068");
        tables.add("games");
        tables.add("publishers");
        //tables.add("genres");
        //tables.add("platforms");
        for (String table : tables) { 
            String column;
            if (table.compareToIgnoreCase("games")!=0) { 
                column = table.substring(0,table.length()-1);
            } else { 
                column = "name";
            }  %>
        <%= "<li class=\"browseList\" ><a href=\"/search/query?table="+table+"&order="+column+"\">"+table+"</a>" %>
            <ul class="letterList" >
                <% for (char firstLetter = '0';firstLetter<='z';++firstLetter) { 
                    if (ignores.containsKey(table) && ignores.get(table).indexOf(firstLetter) != -1) {
                        continue;
                    }
                    %>
                    <%= "<li class=\"letterList\"><a href=\"/search/query?table="+table+"&columnName="+column
                        +"&"+column+"="+firstLetter+"%25&match=true&order="+column+"\" >"+(""+firstLetter).toUpperCase()
                        +"</a></li>" %>
                <% 
                    if (firstLetter=='9') {
                        firstLetter='a';
                        --firstLetter;
                    }
                } %>
            </ul>
        </li>
    <% } %>
    <li class="browseList" ><a href="/search/query?table=genres">genres</a>
            <ul class="letterList" >
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=a%25&match=true" >A</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=c%25&match=true" >C</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=f%25&match=true" >F</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=h%25&match=true" >H</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=l%25&match=true" >L</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=m%25&match=true" >M</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=p%25&match=true" >P</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=r%25&match=true" >R</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=s%25&match=true" >S</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=genres&columnName=genre&genre=v%25&match=true" >V</a>
                    </li>
            </ul>
    </li>
    <li class="browseList" ><a href="/search/query?table=platforms">platforms</a>
            <ul class="letterList" >
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=2%25&match=true" >2</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=3%25&match=true" >3</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=d%25&match=true" >D</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=g%25&match=true" >G</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=n%25&match=true" >N</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=p%25&match=true" >P</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=s%25&match=true" >S</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=t%25&match=true" >T</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=w%25&match=true" >W</a>
                    </li>
                    <li class="letterList">
                        <a href="/search/query?table=platforms&columnName=platform&platform=x%25&match=true" >X</a>
                    </li>
            </ul>
    </li>
</ul>
