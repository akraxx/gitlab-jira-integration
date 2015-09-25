package fr.mmarie.utils;

public class Common {

    public static String sanitizeURL(String url) {
        if(!url.endsWith("/")) {
            url = url+"/";
        }

        return url;
    }
}
