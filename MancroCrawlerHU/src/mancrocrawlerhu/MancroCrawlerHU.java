package mancrocrawlerhu;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 *
 * @author luisdetlefsen
 */
class MancroCrawlerHU {

    private WebClient webClient;
    HtmlPage wholePage;
    String rootUrl;
    boolean applyFix = false;
    int fixCount = 0;
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger("WebScraper");

    protected enum ZONES {

        TWENTYONE("21"), ONE("01"), TWO("02"), THREE("03"), FOUR("04"), FIVE("05"), SIX("06"), SEVEN("07"), EIGHT("08"), NINE("09"), TEN("10"), ELEVEN("11"), TWELVE("12"), THIRTEEN("13"), FOURTEEN("14"), FIFTEEN("15"), SIXTEEN("16"), SEVENTEEN("17"), EIGHTEEN("18");

        private String zone;

        private ZONES(String zoneString) {
            zone = zoneString;
        }

        @Override
        public String toString() {
            return zone;
        }

    }

    protected enum DEPARTMENTS {

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

    protected enum TOWNS {

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

    protected enum ASSETS {

        CASAS("casas"),
        APARTEMENTOS("apartamentos");

        private String asset;

        private ASSETS(String aString) {
            asset = aString;
        }

        @Override
        public String toString() {
            return asset;
        }
    }

    protected enum CONDITIONS {

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

    private Integer getCurrentPage(HtmlPage page) {
        return Integer.valueOf(page.querySelector("div#MasterMC_ContentBlockHolder_grdpropspagination a[disabled]").asText());
    }

    private HtmlAnchor getNextPageLink(HtmlPage page) {
        return page.querySelector("div#MasterMC_ContentBlockHolder_grdpropspagination a[disabled] + a");
    }

    private DomNodeList<DomNode> getPropertiesLinks(HtmlPage page) {
        return page.querySelectorAll("div#title a[href]");
    }

    private String getPropertyField(HtmlPage page, String selector) {
        DomNode node = page.querySelector(selector);
        if (node == null)
            return "";
        return node.asText();
    }

    //TODO: get all details
    private Property getPropertyDetails(HtmlPage page) {

        Property property = new Property();
        property.setPriceSell(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblOp1 span"));
        property.setDescription(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblProse"));
        property.setMancroId(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblCode"));
        property.setLastEdit(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblAdMod"));
        property.setVisits(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblCount3"));
        property.setPriceRent(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblOp2 span"));
        property.setRooms(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblBed"));
        property.setBathrooms(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblBath"));
        property.setTerrain(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblLA span"));
        property.setConstruction(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblCA span"));
        property.setParking(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblPark"));
        property.setNewOrUsed(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblNew"));
        property.setAddress(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblAdrProp"));

        return property;
    }

    private List<Property> getAllProperties(String url) throws IOException {
        webClient = getNewWebClient();
        wholePage = webClient.getPage(url);
        final List<Property> properties = new ArrayList<>();

        applyFix = false;
        fixCount = 0;
        log.info("-------------------------------");
        HtmlAnchor nextPageAnchor;
        int c = 1;
        do {
            if (c != getCurrentPage(wholePage)) {
                log.error("Error while visiting " + rootUrl);
                log.error("Error: Expected page " + c + " but got " + getCurrentPage(wholePage));
                break;
            }
            log.info("Page " + c);
            //c++;
            final DomNodeList<DomNode> propertiesLinks = getPropertiesLinks(wholePage);
            int j = 1;
            for (DomNode node : propertiesLinks) {
                log.info("Visiting property #" + (j + ((c - 1) * 25)) + ": " + ((HtmlAnchor) node).asText());

                HtmlPage propertyPage = ((HtmlAnchor) node).click();
                Property property = getPropertyDetails(propertyPage);
                properties.add(property);
                j++;
                //    break;
            }

            nextPageAnchor = getNextPageLink(wholePage);

            if (nextPageAnchor != null) //page = ((HtmlAnchor) nextPage) .click(); // System.out.println(page.asText());{
            {
                String href = nextPageAnchor.getHrefAttribute();
                //log.info("Next page: "+ nextPage.asText());       
                webClient.close();
                webClient = getNewWebClient();
                log.info("Retrieving next page: " + nextPageAnchor.asText());
                wholePage = webClient.getPage(rootUrl);

                if (applyFix || nextPageAnchor.asText().equals("...")) {
                    wholePage = (HtmlPage) wholePage.executeJavaScript("javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl10$ctl00','')").getNewPage();
                    //     page = (HtmlPage) page.executeJavaScript("javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl10$ctl00','')").getNewPage();
                    applyFix = true;

                    if (fixCount != 0) {
                        log.info("Fix: " + "javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl0" + (fixCount + 1) + "$ctl00','')");

                        wholePage = (HtmlPage) wholePage.executeJavaScript("javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl0" + (fixCount + 1) + "$ctl00','')").getNewPage();
                    }

//page = (HtmlPage) wholePage.executeJavaScript("javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl0" + (fixCount + 1) + "$ctl00','')").getNewPage();
                    fixCount++;
                } else
                    wholePage = (HtmlPage) wholePage.executeJavaScript(href).getNewPage();
            } else {
                if (webClient != null)
                    webClient.close();
                log.info("~~~~~~~~~~~~~~Completed~~~~~~~~~~~~");
                break;
            }

            c++;
        } while (wholePage != null);

        return properties;
    }

    private WebClient getNewWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
        webClient.getOptions().setTimeout(120000);
        webClient.waitForBackgroundJavaScript(60000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        return webClient;
    }

    private List<Property> getPropertiesByCategory(final String url) throws Exception {
        //  webClient = getNewWebClient();
        try {
            //wholePage = webClient.getPage(url);
            rootUrl = url;

            List<Property> properties = getAllProperties(url);

            return properties;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace());
            throw e;
        } finally {

        }
    }

    public void crawlCategory(final String url, final String outputFilePath, final ZONES zone, final ASSETS asset, final CONDITIONS condition) throws Exception {
        log.info("Visiting " + url);
        final List<Property> properties = getPropertiesByCategory(url);

        log.info("Retrieved " + properties.size() + " properties from " + url);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
            writer.write(Property.getHeadersCSV());
            writer.newLine();
            for (Property p : properties) {
                p.setZone(zone);
                p.setAsset(asset);
                p.setCondition(condition);

                writer.write(p.toCsvLine());
                writer.newLine();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void crawl() {
        final String baseUrl = "http://mancro.com/";

        //String url = baseUrl + ASSETS.CASAS.toString() + "-en-" + CONDITIONS.ALQUILER + "/" + DEPARTMENTS.GUATEMALA + "/" + TOWNS.GUATEMALA + "/zona-" + ZONES.TEN;
        for (ASSETS asset : ASSETS.values())
            for (CONDITIONS condition : CONDITIONS.values())

                for (ZONES zone : ZONES.values()) {
                    final String url = baseUrl + asset + "-en-" + condition + "/" + DEPARTMENTS.GUATEMALA + "/" + TOWNS.GUATEMALA + "/zona-" + zone;
                    final String outputFilePath = "D:\\mancro\\";
                    final String fileName = asset + "_" + condition + "_" + zone + ".csv";
                    try {
                        crawlCategory(url, outputFilePath + fileName, zone, asset, condition);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        log.info("Starting");

        MancroCrawlerHU crawler = new MancroCrawlerHU();

        crawler.crawl();
        log.info("Completed");
    }

}
