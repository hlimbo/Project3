function printXmlException ($xml,tag) {
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
        $(tag).empty();
        $(tag).append(errors);
        return true;
    } else {
        return false;
    }
}
