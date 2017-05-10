import java.util.*;

class ParameterParse {
    public static HashMap<String,String> getQueryParameters (String queryString) {
        HashMap<String,String> parsedParams= new HashMap<String,String>();
        if (queryString!=null && queryString.trim().compareTo("")!=0) {
            for (String param : queryString.split("&")) {
                if (param.split("=").length > 1) {
                    String codedValue = "";
                    //if (param.split("=")[0].trim().compareToIgnoreCase("previousPage")==0) {
                        //%3F is the URI encoding of %
	    				/*try { 		
                            decodedValue = URLDecoder.decode(
                            param.split("=")[1].substring(param.indexOf("%3F")+3), "UTF-8"); 
	    				} catch (UnsupportedEncodingException e) {
	    					e.printStackTrace();
                        }*/
                    //} else {
	    			    /*try {
                         codedValue = URLEncoder.encode(param.split("=")[1], "UTF-8");
	    			    } catch (UnsupportedEncodingException e) { 
	    			        e.printStackTrace(); 
	    			    }*/
                    //}
	    		    parsedParams.put(param.split("=")[0],param.split("=")[1]);
                }
            }
        } 
        return parsedParams;
    }
}
