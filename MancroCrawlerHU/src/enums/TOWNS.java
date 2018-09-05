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
public enum TOWNS {
    GUATEMALA("guatemala");
    private String town;

    private TOWNS(String townString) {
        town = townString;
    }

    @Override
    public String toString() {
        return town;
    }
    
}
