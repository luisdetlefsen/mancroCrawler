
package mancrocrawlerhu;

import mancrocrawlerhu.MancroCrawlerHU.ASSETS;
import mancrocrawlerhu.MancroCrawlerHU.CONDITIONS;

/**
 *
 * @author luisdetlefsen
 */
public class Property {
    
    private String priceSell;//
    private String priceRent;//
    private String description;
    private String lastEdit;//
    private String visits;//
    private String mancroId;//
    private String rooms; //
    private String bathrooms;//
    private String terrain;//
    private String construction;//
    private String parking;//
    private String newOrUsed;//
    private String address;//
    
    private MancroCrawlerHU.ZONES zone;
    private ASSETS asset;
    private CONDITIONS condition;
    
    
    static public String getHeadersCSV(){
        return "mancroId, precioVenta, precioRenta, ultimaEdicion, visitas, habitaciones, banos, terreno, construccion, parqueo, condicion, zona, tipo,proposito,direccion,descripcion";
    }
    
    public String toCsvLine(){
        return mancroId + ",\"" + priceSell + "\",\""+priceRent +"\","+lastEdit+",\""+visits+"\","+rooms+","+bathrooms+",\""+terrain+"\",\""+construction+"\","+parking+","+newOrUsed+","+zone+","+asset+","+condition+",\""+address+"\",\""+description+"\"";
    }

    /**
     * @return the price
     */
    public String getPriceSell() {
        return priceSell;
    }

    /**
     * @param price the price to set
     */
    public void setPriceSell(String price) {
        this.priceSell = price;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the lastEdit
     */
    public String getLastEdit() {
        return lastEdit;
    }

    /**
     * @param lastEdit the lastEdit to set
     */
    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    /**
     * @return the visits
     */
    public String getVisits() {
        return visits;
    }

    /**
     * @param visits the visits to set
     */
    public void setVisits(String visits) {
        this.visits = visits;
    }

    /**
     * @return the mancroId
     */
    public String getMancroId() {
        return mancroId;
    }

    /**
     * @param mancroId the mancroId to set
     */
    public void setMancroId(String mancroId) {
        this.mancroId = mancroId;
    }

    /**
     * @return the priceRent
     */
    public String getPriceRent() {
        return priceRent;
    }

    /**
     * @param priceRent the priceRent to set
     */
    public void setPriceRent(String priceRent) {
        this.priceRent = priceRent;
    }

    /**
     * @return the rooms
     */
    public String getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    /**
     * @return the bathrooms
     */
    public String getBathrooms() {
        return bathrooms;
    }

    /**
     * @param bathrooms the bathrooms to set
     */
    public void setBathrooms(String bathrooms) {
        this.bathrooms = bathrooms;
    }

    /**
     * @return the terrain
     */
    public String getTerrain() {
        return terrain;
    }

    /**
     * @param terrain the terrain to set
     */
    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    /**
     * @return the construction
     */
    public String getConstruction() {
        return construction;
    }

    /**
     * @param construction the construction to set
     */
    public void setConstruction(String construction) {
        this.construction = construction;
    }

    /**
     * @return the parking
     */
    public String getParking() {
        return parking;
    }

    /**
     * @param parking the parking to set
     */
    public void setParking(String parking) {
        this.parking = parking;
    }

    /**
     * @return the newOrUsed
     */
    public String getNewOrUsed() {
        return newOrUsed;
    }

    /**
     * @param newOrUsed the newOrUsed to set
     */
    public void setNewOrUsed(String newOrUsed) {
        this.newOrUsed = newOrUsed;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the zone
     */
    public MancroCrawlerHU.ZONES getZone() {
        return zone;
    }

    /**
     * @param zone the zone to set
     */
    public void setZone(MancroCrawlerHU.ZONES zone) {
        this.zone = zone;
    }

    /**
     * @return the asset
     */
    public ASSETS getAsset() {
        return asset;
    }

    /**
     * @param asset the asset to set
     */
    public void setAsset(ASSETS asset) {
        this.asset = asset;
    }

    /**
     * @return the condition
     */
    public CONDITIONS getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(CONDITIONS condition) {
        this.condition = condition;
    }
    
    
}
