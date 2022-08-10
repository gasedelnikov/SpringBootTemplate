package com.gri.template.impl;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


    public static Date getDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        }
        catch (ParseException e) {
            log.error("getDate: dateString = {}", dateString);
        }
        catch (NullPointerException e) {
            log.debug("getDate: dateString is null");
        }
        return null;
    }

    public static String getString(Date date) {
        if (date == null){
            return null;
        }
        else{
            return DATE_FORMAT.format(date);
        }

    }

}
