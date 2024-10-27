//package COSC_2351.Web_Crawler_Stuff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class WebDriver {
    public static void main(String[] args) throws IOException {

        //initialize scanner and WebCatHub variables
        Scanner scanObj = new Scanner(System.in);
        WebCatHub hq = null;

        //asks user if they want to load a previous save
        System.out.println("Would you like to load a previous save?:");
        String load = scanObj.nextLine().toLowerCase();

        //will attempt to deserialze a file if it exists
        if(load.equals("yes") || load.equals("y")){
            hq = WebCatHub.DeserializeHub();
        }

        //if file does not exist, then create new WebCatHub and serialize database
        if(hq == null){
            System.out.println("Creating new WebCat Hub");

            //set max pages to visit
            System.out.println("Please enter maximum number of web pages to visit: ");
            int maxPages = scanObj.nextInt();
            scanObj.nextLine(); //consumes the 'newLine' character

            hq = new WebCatHub(maxPages);
            hq.setWordsToBlock("stopwords.txt"); //adding words to block

            //gathers data with starting url
            System.out.println("Please enter the starting url to visit:");
            String startURL = scanObj.nextLine();
            hq.searchParty(startURL); //data gathering

            //asking whether or not we want to save the WebCat Hub
            System.out.println("Would you like to save this Hub?:");
            String save =  scanObj.nextLine().toLowerCase();

            if(save.equals("yes") || save.equals("y")){
                WebCatHub.SerializeHub(hq);
            }
        }



        while (true){ //loop for continual query asking

            System.out.println("Enter query: ");
            String query = scanObj.nextLine().toLowerCase();

            System.out.println("Number of urls to provide: ");
            int searches = scanObj.nextInt();
            scanObj.nextLine(); //consumes 'newLine' character

            System.out.println("Would you like to do an And search, or an Or search?:");
            String ao = scanObj.nextLine().toLowerCase(); //asks user for And or Or search

            //try-catch statement to keep loop running in case of errors
            try {
                System.out.println("");
                if (ao.equals("and") || ao.equals("a")) { // checks to see if they asked for and search
                    hq.andSearch(query, searches);
                } else hq.orSearch(query, searches); //defaults to or search otherwise
            }
            catch (Exception e){
                System.out.println("Sorry, something went wrong with your query or there is a word that is not within the Hub database.");
            }

            //asking if the user wants to continue
            System.out.println("Do another search in database?:");
            String doLoop = scanObj.nextLine().toLowerCase();
            if(!doLoop.equals("yes") && !doLoop.equals("y")){
                break;
            }

        }


    }
}
