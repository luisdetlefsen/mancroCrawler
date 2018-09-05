/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

/**
 *
 * @author luisdetlefsen
 */
public enum CONDITIONS {
    VENTA("venta"), ALQUILER("alquiler");
    private String condition;

    private CONDITIONS(String cString) {
        condition = cString;
    }

    @Override
    public String toString() {
        return condition;
    }
    
}
