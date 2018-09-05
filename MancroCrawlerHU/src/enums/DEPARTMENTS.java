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
public enum DEPARTMENTS {
    GUATEMALA("guatemala");
    private String department;

    private DEPARTMENTS(String dString) {
        department = dString;
    }

    @Override
    public String toString() {
        return department;
    }
    
}
