package mancrocrawlerhu;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import enums.ASSETS;
import enums.CONDITIONS;
import enums.DEPARTMENTS;
import enums.TOWNS;
import enums.ZONES;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import static util.Utils.scrubConstructionArea;
import static util.Utils.scrubNumber;
import static util.Utils.scrubString;

/**
 *
 * To run it: java -Xms8g -Xmx8g -XX: +UseG1GC
 * -Dlog4j.configurationFile=log4j2.xml -jar MancroCrawlerHU.jar
 * -output=D:/mancro/ -archive=D:/mancro_archive/ -includeHeaders
 *
 * Headers: mancroId, precioVenta, precioRenta, ultimaEdicion, visitas,
 * habitaciones, banos, terreno, construccion, parqueo, condicion, zona,
 * tipo,proposito,direccion,descripcion
 *
 * @author luisdetlefsen
 */
class MancroCrawlerHU {

    boolean includeHeaders = false;
    boolean debug = false;
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger("WebScraper");
    public String outputBasePath = "~/";
    public String archiveFilePath = "~/";
    private final List<ZONES> zonesIgnoreList = new ArrayList<>();
    private final List<ASSETS> assestsIgnoreList = new ArrayList<>();
    private final List<CONDITIONS> conditionsIgnoreList = new ArrayList<>();
    private Set<String> propertiesScrappedPreviously = null;


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
        if (node == null) {
            return "";
        }
        return node.asText();
    }

    private Property getPropertyDetails(HtmlPage page) {
        Property property = new Property();
        property.setPriceSell(scrubNumber(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblOp1 span")));
        property.setDescription(scrubString(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblProse")));
        property.setMancroId(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblCode"));
        property.setLastEdit(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblAdMod"));
        property.setVisits(scrubNumber(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblCount3")));
        property.setPriceRent(scrubNumber(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblOp2 span")));
        property.setRooms(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblBed"));
        property.setBathrooms(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblBath"));
        property.setTerrain(scrubConstructionArea(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblLA span")));
        property.setConstruction(scrubConstructionArea(getPropertyField(page, "span#MasterMC_ContentBlockHolder_lblCA span")));
        property.setParking(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblPark"));
        property.setNewOrUsed(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblNew"));
        property.setAddress(scrubString(getPropertyField(page, "#MasterMC_ContentBlockHolder_lblAdrProp")));

        return property;
    }

    private Set<String> getSavedProperties() {
        final Set<String> savedProperties = new HashSet<>();

        log.info("Reading file: " + archiveFilePath);
        if (!Files.exists(Paths.get(archiveFilePath))) {
            log.info("Archive file does not exist, skipping it. " + archiveFilePath);
            return savedProperties;
        }
        try (Stream<String> stream = Files.lines(Paths.get(archiveFilePath))) {
            stream.forEach(x -> savedProperties.add(x.split(",")[0]));
        } catch (IOException e) {
            log.error("Error getting saved properties from archive " + archiveFilePath);
            log.error(e.getMessage());
        }
      //  for (String s : savedProperties.toArray(new String[0]))
        //      log.info(s);
        log.info("Retrieved " + savedProperties.size() + " saved properties from " + archiveFilePath);
        return savedProperties;
    }

    private List<Property> getAllProperties(final String url, final ZONES zone, final ASSETS asset, final CONDITIONS condition) throws IOException {
        WebClient webClient = getNewWebClient();
        HtmlPage wholePage = webClient.getPage(url);
        final List<Property> properties = new ArrayList<>();

        log.info("-------------------------------");
        HtmlAnchor nextPageAnchor;
        int c = 1;
        do {
            log.trace("Iterating");
            if (c != getCurrentPage(wholePage)) {
                log.error("Error while visiting " + url);
                log.error("Error: Expected page " + c + " but got " + getCurrentPage(wholePage));
                break;
            }
            log.info("Current page " + c);
            //c++;
            final DomNodeList<DomNode> propertiesLinks = getPropertiesLinks(wholePage);
            int j = 1;
            for (DomNode node : propertiesLinks) {
                log.info("Visiting property #" + (j + ((c - 1) * 25)) + ": " + ((HtmlAnchor) node).asText());
                final String propertyUrl = ((HtmlAnchor) node).getHrefAttribute();
                final String mancroId = propertyUrl.substring(propertyUrl.lastIndexOf("/") + 1);
                log.trace("Searching id in archive: " + mancroId);
                j++;
                if (propertiesScrappedPreviously.contains(mancroId)) {
                    log.trace("Skipping property id " + mancroId + " since it was scraped previously");
                    continue;
                }

                if (debug) 
                    break;

                log.trace("Click " + ((HtmlAnchor) node).getHrefAttribute());
                HtmlPage propertyPage = ((HtmlAnchor) node).click();
                log.trace("Retrieved data sucessfully");
                Property property = getPropertyDetails(propertyPage);
                properties.add(property);                
            }

            //start retrieving the next page
            nextPageAnchor = getNextPageLink(wholePage);

            if (nextPageAnchor != null) //page = ((HtmlAnchor) nextPage) .click(); // System.out.println(page.asText());{
            {
                webClient.close();
                webClient = getNewWebClient();
                log.info("Retrieving next page: " + nextPageAnchor.asText());
                wholePage = webClient.getPage(url);

                String nextPageLink = "javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl%s$ctl00','')";
                if (c < 10) {
                    nextPageLink = String.format(nextPageLink, "0" + (c));
                } else if (c == 10) {
                    nextPageLink = String.format(nextPageLink, "10");
                } else if (c > 10) {
                    int residue = (c + 1) % 10;
                    switch (residue) {
                        case 0:
                            nextPageLink = String.format(nextPageLink, "10");
                            break;
                        case 1:
                            nextPageLink = String.format(nextPageLink, "11");
                            break;
                        default:
                            nextPageLink = String.format(nextPageLink, "0" + (residue));
                    }
                }
                log.info("Next page link: " + nextPageLink);
                if (c <= 10) {
                    wholePage = (HtmlPage) wholePage.executeJavaScript(nextPageLink).getNewPage();
                } else if (c >= 11 && c <= 20) {
                    wholePage = (HtmlPage) wholePage.executeJavaScript("javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl10$ctl00','')").getNewPage();
                    wholePage = (HtmlPage) wholePage.executeJavaScript(nextPageLink).getNewPage();

                } else {
                    wholePage = (HtmlPage) wholePage.executeJavaScript("javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl10$ctl00','')").getNewPage();
                    int bound = 0;
                    if (c > 30) {
                        bound = c / 10;
                    } else {
                        bound = c / 20;
                    }
                    for (int i = 0; i < bound; i++) {
                        wholePage = (HtmlPage) wholePage.executeJavaScript("javascript:__doPostBack('MasterMC$ContentBlockHolder$rptPaging$ctl11$ctl00','')").getNewPage();
                    }
                    wholePage = (HtmlPage) wholePage.executeJavaScript(nextPageLink).getNewPage();
                }

            } else {
                if (webClient != null) {
                    webClient.close();
                }
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

    public void crawlCategory(final String url, final String outputFilePath, final ZONES zone, final ASSETS asset, final CONDITIONS condition) throws Exception {
        log.info("Visiting " + url);
        final List<Property> properties = getAllProperties(url, zone, asset, condition);

        log.info("Retrieved " + properties.size() + " properties from " + url);
        if (properties.size() == 0)
            return;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
            if (includeHeaders) {
                writer.write(Property.getHeadersCSV());
                writer.newLine();
            }
            for (Property p : properties) {
                if (p.getMancroId().isEmpty())
                    continue;
                p.setZone(zone);
                p.setAsset(asset);
                p.setCondition(condition);
                scrubProperty(p);
                writer.write(p.toCsvLine());
                writer.newLine();
            }

        } catch (Exception e) {
            log.error("Error while writing to file " + outputFilePath + " | " + e.getMessage());
        }

    }

    public void scrubProperty(Property p) {
        if (p.getCondition() == CONDITIONS.ALQUILER) {
            if ((p.getPriceRent() == null || p.getPriceRent().isEmpty()) && (p.getPriceSell() != null && !p.getPriceSell().isEmpty())) {
                p.setPriceRent(p.getPriceSell());
                p.setPriceSell("");
            }
                
        }
    }

    private void fillAssestsWhiteList() {
        Collections.addAll(assestsIgnoreList, ASSETS.values());
        assestsIgnoreList.remove(ASSETS.CASAS);
        assestsIgnoreList.remove(ASSETS.APARTEMENTOS);
    }

    private void fillConditionsWhiteList() {
        Collections.addAll(conditionsIgnoreList, CONDITIONS.values());
        conditionsIgnoreList.remove(CONDITIONS.VENTA);
        conditionsIgnoreList.remove(CONDITIONS.ALQUILER);
    }

    private void fillZonesWhiteList() {
        Collections.addAll(zonesIgnoreList, ZONES.values());
        zonesIgnoreList.remove(ZONES.ONE);
        zonesIgnoreList.remove(ZONES.TWO);
        zonesIgnoreList.remove(ZONES.THREE);
        zonesIgnoreList.remove(ZONES.FOUR);
        zonesIgnoreList.remove(ZONES.FIVE);
        zonesIgnoreList.remove(ZONES.SIX);
        zonesIgnoreList.remove(ZONES.SEVEN);
        zonesIgnoreList.remove(ZONES.EIGHT);
        zonesIgnoreList.remove(ZONES.NINE);
        zonesIgnoreList.remove(ZONES.TEN);
        zonesIgnoreList.remove(ZONES.ELEVEN);
        zonesIgnoreList.remove(ZONES.TWELVE);
        zonesIgnoreList.remove(ZONES.THIRTEEN);
        zonesIgnoreList.remove(ZONES.FOURTEEN);
        zonesIgnoreList.remove(ZONES.FIFTEEN);
        zonesIgnoreList.remove(ZONES.SIXTEEN);
        zonesIgnoreList.remove(ZONES.SEVENTEEN);
        zonesIgnoreList.remove(ZONES.EIGHTEEN);
        zonesIgnoreList.remove(ZONES.TWENTYONE);
    }

    private void fillWhileList() {
        fillAssestsWhiteList();
        fillConditionsWhiteList();
        fillZonesWhiteList();
    }

    public void crawl() {
        fillWhileList();
        final String baseUrl = "http://mancro.com/";
        if (debug) 
            log.info("Running in debug mode.");

        propertiesScrappedPreviously = getSavedProperties();

        //String url = baseUrl + ASSETS.CASAS.toString() + "-en-" + CONDITIONS.ALQUILER + "/" + DEPARTMENTS.GUATEMALA + "/" + TOWNS.GUATEMALA + "/zona-" + ZONES.TEN;
        for (ASSETS asset : ASSETS.values()) {
            for (CONDITIONS condition : CONDITIONS.values()) {
                for (ZONES zone : ZONES.values()) {
                    if (zonesIgnoreList.contains(zone)) {
                        log.warn("Ignoring zone: " + zone);
                        continue;
                    }
                    if (assestsIgnoreList.contains(asset)) {
                        log.warn("Ignoring: " + asset);
                        continue;
                    }
                    if (conditionsIgnoreList.contains(condition)) {
                        log.warn("Ignoring: " + condition);
                        continue;
                    }

                    final String url = baseUrl + asset + "-en-" + condition + "/" + DEPARTMENTS.GUATEMALA + "/" + TOWNS.GUATEMALA + "/zona-" + zone;
                    final String fileName = asset + "_" + condition + "_" + zone + ".csv";
                    try {
                        crawlCategory(url, outputBasePath + fileName, zone, asset, condition);
                    } catch (Exception e) {
                        log.error("Error while crawling url " + url);
                        log.error(e.getMessage());
                    }
                    log.info("Pausing for 10 minutes...");
                    try {
                        TimeUnit.MINUTES.sleep(10);
                    } catch (InterruptedException ex) {
                        log.error("Pause interrupted");
                        log.error(ex.getLocalizedMessage());
                    }
                    log.info("Continue");
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final LocalDate startTime = LocalDate.now();

        log.info("Starting");

        final MancroCrawlerHU crawler = new MancroCrawlerHU();

        if (args.length > 1) {
            for (int i = 0; i < args.length; i++) {
                // log.info("Parameter: " + args[i]);
                if (args[i].startsWith("-output")) {
                    crawler.outputBasePath = args[i].split("=")[1];
                    if (!crawler.outputBasePath.endsWith("/"))
                        crawler.outputBasePath += "/";
                    log.info("Setting output path to " + args[i].split("=")[1]);
                    continue;
                }
                if (args[i].startsWith("-archive")) {
                    crawler.archiveFilePath = args[i].split("=")[1];
                    log.info("Setting archive file path to " + args[i].split("=")[1]);
                    continue;
                }
                if (args[i].startsWith("-includeHeaders")) {
                    crawler.includeHeaders = true;
                    continue;
                }
            }
        }

        crawler.crawl();
        final LocalDate endTime = LocalDate.now();
        final Duration duration = Duration.between(startTime, endTime);

        log.info("Completed getting all properties in " + duration);
    }

}
