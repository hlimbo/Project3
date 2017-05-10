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

    public static String getTrace (Exception ex) {
        StackTraceElement [] trace = ex.getStackTrace();
        String traceString = "";
        for (StackTraceElement stackTop : trace) {
            traceString+=stackTop.toString()+"\n";
        }
        return traceString;
    }

    public static String toHtml (Exception ex) {
        return htmlHeader()+"\n"+ex.getMessage()+"\n"+getTrace(ex)+"\n"+htmlFooter();
    }
}
