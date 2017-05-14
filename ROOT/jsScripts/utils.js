function printXmlException ($xml,tag) {
    error = $xml.find("exception");
    if (error.length > 0) {
        errors="<ul class=\"exception\">";
        message = error.find("msg");
        for (i=0;i<error.length;i++) {
            errors+="<li>"+message.eq(i).text()+"</li>";
        }
        stack = error.find("stack");
        for (i=0;i<stack.length;i++) {
            errors+="<li>"+stack.eq(i).text()+"</li>";
        }
        errors+="</ul>";
        $(tag).empty();
        $(tag).append(errors);
        return true;
    } else {
        return false;
    }
}
