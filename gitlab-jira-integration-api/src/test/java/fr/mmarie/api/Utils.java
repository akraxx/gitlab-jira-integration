package fr.mmarie.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Date getDateFromString(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(date);
    }

}
