package com.threepointogames.esk8ph;

public class StringReplacer {
    public static String EncodeString(String string) {
        return string.replace(".", "_");
    }

    public static String DecodeString(String string) {
        return string.replace("_", ".");
    }
}
