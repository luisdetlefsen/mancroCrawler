/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author luis detlefsen
 */
public class Utils {

    public static String scrubNumber(final String price) {
        return price.replaceAll("[^0-9\\.]", "");
    }

    public static String scrubConstructionArea(final String area) {
        return area.replaceAll("(m2|v2|\\s*)", "");
    }

    public static String scrubString(final String s) {
        return s.replaceAll("\"", "");
    }

    public static String toRDate(final String s) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("d/M/yyyy")).format(DateTimeFormatter.ISO_DATE);
    }

    public static String durationToString(final Duration d) {       
        return (d.toHours() % 24) + " hours, " + (d.toMinutes() % 60) + " minutes, " + (d.getSeconds() % 60) + " seconds, " + (d.toMillis() % 1000) + " ms.";
    }
}
