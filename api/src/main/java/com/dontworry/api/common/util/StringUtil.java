package com.dontworry.api.common.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringUtil {

    public static String extractLocation(String address) {
        Pattern p = Pattern.compile("\\s(\\S+)\\s\\d");
        Matcher m = p.matcher(address);
        return m.find() ? m.group(1) : null;
    }

}
