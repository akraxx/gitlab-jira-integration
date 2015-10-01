package fr.mmarie.utils;

public final class Common {

    private Common() {
    }

    public static String sanitizeURL(String url) {
        if(!url.endsWith("/")) {
            url = url+"/";
        }

        return url;
    }
}
