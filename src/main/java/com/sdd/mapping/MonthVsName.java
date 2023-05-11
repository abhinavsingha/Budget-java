package com.sdd.mapping;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class MonthVsName {

    public static HashMap<Integer,String> monthMapping = new HashMap<>();

    static {
        monthMapping.put(1,"JANUARY");
        monthMapping.put(2,"FEBRUARY");
        monthMapping.put(3, "MARCH");
        monthMapping.put(4,"APRIL");
        monthMapping.put(5,"MAY");
        monthMapping.put(6,"JUN");
        monthMapping.put(7,"JULY");
        monthMapping.put(8,"AUGUST");
        monthMapping.put(9,"SEPTEMBER");
        monthMapping.put(10,"OCTOBER");
        monthMapping.put(11,"NOVEMBER");
        monthMapping.put(12,"DECEMBER");
    }

    public static  SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    public static  SimpleDateFormat formatterYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
}
