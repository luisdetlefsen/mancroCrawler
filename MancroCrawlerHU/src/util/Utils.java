/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author luis detlefsen
 */
public class Utils {

    public static String scrubNumber(String price) {
        return price.replaceAll("[^0-9\\.]", "");
    }

    public static String scrubConstructionArea(String area) {
        return area.replaceAll("(m2|v2|\\s*)", "");
    }

    public static String scrubString(String s) {
        return s.replaceAll("\"", "");
    }

    public static String toRDate(String s) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("d/M/yyyy")).format(DateTimeFormatter.ISO_DATE);
    }

}
