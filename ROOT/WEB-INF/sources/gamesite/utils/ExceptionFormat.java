package gamesite.utils;

//Template class for other Exception Format handlers
public class ExceptionFormat {
    //Ensures static class but allows to inherit static
    //methods
    protected ExceptionFormat () {}

    public static String htmlHeader () {
        return ("<HTML>\n<HEAD><TITLE>\ngamedb: Error"
            +"</TITLE>\n</HEAD>\n<BODY>\n" +
                    "<P>");
    }

    public static String htmlFooter () {
            return "</P></BODY></HTML>";
    }

    public static String xmlHeader () {
        return "<exception>";
    }

    public static String xmlFooter () {
            return "</exception>";
    }

    public static String getHtmlTrace (Exception ex) {
        StackTraceElement [] trace = ex.getStackTrace();
        String traceString = "";
        for (StackTraceElement stackTop : trace) {
            traceString+=stackTop.toString()+"\n";
        }
        return traceString;
    }

    public static String toHtml (Exception ex) {
        return htmlHeader()+"\n"+ex.getMessage()+"\n"+getHtmlTrace(ex)+"\n"+htmlFooter();
    }

    public static String getXmlTrace (Exception ex) {
        StackTraceElement [] trace = ex.getStackTrace();
        String traceString = "<trace>";
        for (StackTraceElement stackTop : trace) {
            traceString+="<stack>"+stackTop.toString()+"</stack>\n";
        }
        traceString += "</trace>";
        return traceString;
    }

    public static String toXml (Exception ex) {
        return xmlHeader()+"<exception_class>Exception</exception_class>\n"
            +"<msg>"+ex.getMessage()+"</msg>\n"+getXmlTrace(ex)+"\n"+xmlFooter();
    }
}
