package com.tangobyte.yazarlar.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by erk on 8.4.2015.
 */
public class StringUtils {

    public static String clean(String unsafe){
        Whitelist whitelist = Whitelist.none();
        whitelist.addTags(new String[]{"p","br","ul","b","strong"});

        String safe = Jsoup.clean(unsafe, whitelist);
        return StringEscapeUtils.unescapeXml(safe);
    }

    public static Date rssDate(String pubDate) {
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        Date date = null;
        try {
            date = formatter.parse(pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
