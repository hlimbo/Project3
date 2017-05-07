
<!-- to use jstl tags include this line -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- to use jstl with creating lists functionality... use this line -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 

 
 <!-- need to import this so jsp knows what an ArrayList is -->
<%@page import="java.util.ArrayList" %>
 
 
<% ArrayList<String> words = new ArrayList<String>();
   words.add("flimsy"); 
   System.out.println(words.get(0)); 
   request.setAttribute("words", words); %>

<c:set scope="request" var="randomWord" value="dongle"/>
<p>Gretting: <c:out value="${randomWord}"/> </p>
<p>Something else: <c:out value="${words[0]}"/> </p>

<c:forEach var="word" items="${words}">
	<p> ${word} </p>
</c:forEach>

<c:set var="alphabet" value="${['A','B','C']}" scope="request" />
<c:forEach var="some" items="${alphabet}">
	<p> <c:out value="${some}"/> </p>
</c:forEach>