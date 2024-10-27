//package COSC_2351.Web_Crawler_Stuff;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCat implements java.io.Serializable{ //Spider Leg class (Don't like Spiders)

    private static final String USER_AGENT = "COSC 2351 testing crawler (hc.edu)";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;


    //takes the web page and turns it into an HTML document
    //grabs the links and puts it in the links array
    //remains unchanged from original Spider Leg code
    public boolean crawl(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code
            // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }

    //gets all the words
    public String getWords() {
        // This method should only be used after a successful crawl.
        if (this.htmlDocument == null) {//no html document retrieved
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return "Error";
        }

        if(this.htmlDocument.body() !=null){//if there is an html document
            String bodyText = this.htmlDocument.body().text(); //returns all text within <p> <p/> (paragraph blocks)
            if(bodyText != null) {//checks to make sure that the body text is not null
                return bodyText.toLowerCase();//returns the body text
            }
        }
        return "";//returns nothing if otherwise
    }

    //returns the list of links retrieved from site
    public List<String> getLinks() {
        return this.links;
    }



}
