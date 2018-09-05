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
public enum ASSETS {
    CASAS("casas"), APARTEMENTOS("apartamentos");
    private String asset;

    private ASSETS(String aString) {
        asset = aString;
    }

    @Override
    public String toString() {
        return asset;
    }
    
}
