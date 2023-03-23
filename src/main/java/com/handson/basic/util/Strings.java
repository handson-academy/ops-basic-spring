package com.handson.basic.util;

public class Strings {

    public static String likeLowerOrNull(String str) {
        return str != null ? "%" + str.toLowerCase() + "%" : null;
    }
}
