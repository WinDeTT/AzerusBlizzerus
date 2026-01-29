package org.windett.azerusBlizzerus.utils;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static String formatColors(String message) {
        message = message.replaceAll("&", "§");
        return message;
    }

    public static List<String> formatColors(List<String> messageArray) {
        if (messageArray.isEmpty()) return null;
        final List<String> formatted = new ArrayList<>();
        for (String message : messageArray) {
            formatted.add(message.replaceAll("&", "§"));
        }
        return formatted;
    }
}
