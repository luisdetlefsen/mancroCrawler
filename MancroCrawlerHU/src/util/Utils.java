/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

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

}
