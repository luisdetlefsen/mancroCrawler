package mancrocrawlerhu;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisdetlefsen
 */
public class MancroCrawlerHU {

    private enum ZONES {

        ONE("01"), TWO("02"), THREE("03"), FOUR("04"), FIVE("05"), SIX("06"), SEVEN("07"), EIGHT("08"), NINE("09"), TEN("10");

        private String zone;

        private ZONES(String zoneString) {
            zone = zoneString;
        }

        @Override
        public String toString() {
            return zone;
        }

    }

    private enum DEPARTMENTS {

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

    private enum TOWNS {

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

    private enum ASSETS {

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

    private enum CONDITIONS {

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

    private DomNode getNextPage(HtmlPage page) {
        return page.querySelector("div#MasterMC_ContentBlockHolder_grdpropspagination a[disabled] + a");
    }

    private DomNodeList<DomNode> getPropertiesLinks(HtmlPage page) {
        return page.querySelectorAll("div#title a[href]");
    }

    //TODO: get all details
    private Property getPropertyDetails(HtmlPage page) {
        DomNode price = page.querySelector("span#MasterMC_ContentBlockHolder_lblOp1 span");

        Property property = new Property();
        property.setPrice(price.asText());

        return property;
    }

    private List<Property> getAllProperties(HtmlPage page) throws IOException {
        List<Property> properties = new ArrayList<>();

        DomNode nextPage;
        int c = 0;
        do {
            c++;
            DomNodeList<DomNode> propertiesLinks = getPropertiesLinks(page);
            for (DomNode node : propertiesLinks) {
                HtmlPage propertyPage = ((HtmlAnchor) node).click();

                Property property = getPropertyDetails(propertyPage);
                properties.add(property);
            }

            nextPage = getNextPage(page);
            if (nextPage != null)
                page = ((HtmlAnchor) nextPage).click(); // System.out.println(page.asText());
        } while (page != null && nextPage != null && c < 3);

        return properties;
    }

    private List<Property> getPropertiesByCategory(final String url) {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);

            HtmlPage wholePage = webClient.getPage(url);

            List<Property> properties = getAllProperties(wholePage);

            return properties;
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public void crawl() {
        String baseUrl = "http://mancro.com/";
        String url = baseUrl + ASSETS.CASAS.toString() + "-en-" + CONDITIONS.ALQUILER + "/" + DEPARTMENTS.GUATEMALA + "/" + TOWNS.GUATEMALA + "/zona-" + ZONES.TEN;

        List<Property> properties;

        properties = getPropertiesByCategory(url);
        for (Property p : properties)
            System.out.println("Price: " + p.getPrice());

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MancroCrawlerHU crawler = new MancroCrawlerHU();

        crawler.crawl();
    }

}
