package org.mentorbridge.utilities;

import org.apache.commons.lang3.StringUtils;

public class DBUtility {

    public static String dbNameFormatter(String name) {
        return StringUtils.deleteWhitespace(name).toLowerCase();//name.trim().toLowerCase();
    }
}
