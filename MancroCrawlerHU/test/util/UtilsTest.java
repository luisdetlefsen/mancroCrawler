/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author lgdet
 */
public class UtilsTest {

    public UtilsTest() {
    }

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


    @Test
    public void testScrubPriceWithDecimals() {
        String price = "US$ 123,456.00";
        String expResult = "123456.00";
        String result = Utils.scrubNumber(price);
        assertEquals(expResult, result);     
    }

    @Test
    public void testScrubPrice() {
        String price = "US$ 123,456";
        String expResult = "123456";
        String result = Utils.scrubNumber(price);
        assertEquals(expResult, result);
    }

    @Test
    public void testScrubAreaM2() {
        String area = "270.12 m2";
        String expResult = "270.12";
        String result = Utils.scrubConstructionArea(area);        
        assertEquals(expResult, result);
    }

    @Test
    public void testScrubAreaV2() {
        String area = "270.00 v2";
        String expResult = "270.00";
        String result = Utils.scrubConstructionArea(area);
        assertEquals(expResult, result);
    }

    @Test
    public void testScrubString() {
        String s = "Tv of 32\"";
        String expResult = "Tv of 32";
        String result = Utils.scrubString(s);
        assertEquals(expResult, result);
    }

}
