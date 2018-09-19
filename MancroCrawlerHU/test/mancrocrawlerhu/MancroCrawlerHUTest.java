/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mancrocrawlerhu;

import enums.ASSETS;
import enums.CONDITIONS;
import enums.ZONES;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author lgdet
 */
public class MancroCrawlerHUTest {

    

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of crawlCategory method, of class MancroCrawlerHU.
     */
    @Ignore
    public void testCrawlCategory() throws Exception {
        System.out.println("crawlCategory");
        String url = "";
        String outputFilePath = "";
        ZONES zone = null;
        ASSETS asset = null;
        CONDITIONS condition = null;
        MancroCrawlerHU instance = new MancroCrawlerHU();
        instance.crawlCategory(url, outputFilePath, zone, asset, condition);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scrubProperty method, of class MancroCrawlerHU.
     */
    @Test
    public void testScrubPropertyNullRentPrice() {
        System.out.println("scrubProperty");
        String priceRent = "153.88";
        Property p = new Property();
        p.setCondition(CONDITIONS.ALQUILER);
        p.setPriceSell(priceRent);
        p.setPriceRent(null);

        MancroCrawlerHU instance = new MancroCrawlerHU();
        instance.scrubProperty(p);

        assert (p.getPriceRent().equalsIgnoreCase(priceRent));
    }

    @Test
    public void testScrubPropertyNotNullRentPrice() {
        System.out.println("scrubProperty");
        String priceRent = "153.88";
        Property p = new Property();
        p.setCondition(CONDITIONS.ALQUILER);
        p.setPriceSell(priceRent);
        p.setPriceRent("");

        MancroCrawlerHU instance = new MancroCrawlerHU();
        instance.scrubProperty(p);

        assert (p.getPriceRent().equalsIgnoreCase(priceRent));
    }

    /**
     * Test of crawl method, of class MancroCrawlerHU.
     */
    @Ignore
    public void testCrawl() {
        System.out.println("crawl");
        MancroCrawlerHU instance = new MancroCrawlerHU();
        instance.crawl();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class MancroCrawlerHU.
     */
    @Ignore
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        MancroCrawlerHU.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
