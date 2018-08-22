package mancrocrawler;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.ranges.DocumentRange;

/**
 *
 * @author luisdetlefsen
 */
public class MancroCrawler {

    private enum ZONES {
        ONE("01"), TWO("02"), THREE("03"), FOUR("04"), FIVE("05");

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

    private Property getPropertyDetails(Document doc) throws IOException {
        Elements price = doc.select("span#MasterMC_ContentBlockHolder_lblOp1 span");

        Property property = new Property();
        property.setPrice(price.get(0).text());

        return property;
    }

    //It does a postback. Jsoup cannot handle this. Project aborted :( 
    private Element getNextPage(Document doc) throws IOException {
        return doc.select("div#MasterMC_ContentBlockHolder_grdpropspagination a[disabled] + a").first();
    }

    private Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
    
    
    private Elements getPropertiesLinks(Document doc) throws IOException {
        return doc.select("div#title a[href]");
    }

    /**
     *
     *
     * A mancro URL is formed like this:
     *
     * http://mancro.com/[casas|apartamentos]-en-[venta|alquiler]/[guatemala]/[guatemala]/zona-[01|..|21]
     *
     * Example: http://mancro.com/casas-en-alquiler/guatemala/guatemala/zona-10
     */
    public void start() {
        String baseUrl = "http://mancro.com/";
        String url = baseUrl + ASSETS.CASAS.toString() + "-en-" + CONDITIONS.ALQUILER + "/" + DEPARTMENTS.GUATEMALA + "/" + TOWNS.GUATEMALA + "/zona-" + ZONES.FIVE;

        try {
            Document document = getDocument(url);
            
            Elements e = getPropertiesLinks(document);
            for (Element element : e) {
                Document propertyPage = Jsoup.connect(element.attr("abs:href") ).get();
                Property property = getPropertyDetails(propertyPage);
                System.out.println("Property price: " + property.getPrice());
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MancroCrawler crawler = new MancroCrawler();
        crawler.start();
    }

}
