//package COSC_2351.Web_Crawler_Stuff;

import COSC_2351.Employee;

import java.io.*;
import java.util.*;

public class WebCatHub implements java.io.Serializable{ //Spider class (Changed to Cats because we don't like spiders)

    private int MAX_PAGES_TO_SEARCH;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
    private Dictionary repository = new Dictionary();
    private String[] punctuation = {"\\.", "\\?", "!", ",", "#", "@"}; //array of punctuation to get rid of
    public ArrayList<String> wordsToBlock = new ArrayList<>();

    //constructor, made it so that you can adjust maximum pages to visit
    public WebCatHub(int mps){
        this.MAX_PAGES_TO_SEARCH = mps;
    }

    //search function from Spider code
    public void searchParty(String url) {
        String currentUrl;
        this.pagesToVisit.add(url);
        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {//loop going for as long as it's less than Max_Pages_To_Visit
            WebCat Catnip = new WebCat(); //creating a new WebCat class
            if (this.pagesToVisit.isEmpty()) {
                System.out.println("No more web pages to visit");
                break;
            } else {//moves on to the next url in the list
                currentUrl = this.nextUrl(); //calls the nextUrl function to move on to the next url
            }

            Catnip.crawl(currentUrl); //calling the crawl function from WebCat

            this.pagesToVisit.addAll(Catnip.getLinks());//returns list of links to add to the pagesToVisit list
            addToDict(Catnip.getWords(), currentUrl);//adds all the words found in the list to the repository(dictionary)
        }
        System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
    }

    //quick function to create a list of words to block adding to the dictionary
    public void setWordsToBlock(String filename) throws IOException {//throws exception needed in order to run function
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename))); // creates new BufferReader to read txt file
        String inputLine = null;

        while((inputLine = reader.readLine()) != null) {//iterates through each line in the txt
            // Split the input line.
            // \\s+ represents whitespace characters, like Tab, space, enter, etc.
            String[] words = inputLine.split("\\s+");

            // Ignore empty lines.
            if(inputLine.equals(""))
                continue;

            for(String word: words) {
                // Remove any commas and dots.
                word = word.replace(".", "");
                word = word.replace(",", "");
                this.wordsToBlock.add(word);
            }
        }
        reader.close();
    }

    //moves on to the next url
    //remains unchanged form original Spider code
    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }

    //adds words to the repository(dictionary)
    private void addToDict(String wordString, String url){ //all the words and the current url

        ArrayList<String> wordList = new ArrayList<>(List.of(wordString.split("\\s+"))); //splits giant word string into individual words
        System.out.println(wordString); //prints original unparsed body text
        for (int n = 0; n < wordList.toArray().length; n++){ //for every word within the body string
            for (String punc : this.punctuation){//loop that removes as much punctuation as possible
                wordList.set(n, wordList.get(n).replaceAll(punc, ""));
            }
            // adding to repository
            if (wordList.get(n).length() >= 3 && !this.wordsToBlock.contains(wordList.get(n))) { //makes sure that the word is not in the blocked list and is at least 3 letters long
                repository.add(wordList.get(n), url);
            }
        }



    }

    //returns a certain number of top urls for a certain word
    public void getUrlFreq(String word, int numOfSearches){
        ArrayList<String> topUrls = this.repository.searchFor(word, numOfSearches);

        for (String url : topUrls){
            System.out.printf("Url = %s\tFrequency = %s\n", url, this.repository.Dict.get(word).get(url));
        }
    }

    //or search function
    public void orSearch(String query, int numSearches){
        //split query
        String[] queryList = query.split(" ");
        HashMap<String, Integer> combinationUrlResults = new HashMap<>();
        //combines all results of word searches into one list
        for (String word : queryList){
            ArrayList<String> wordUrls = this.repository.searchFor(word, numSearches); //returns a list of topURLs for each word
            for(String url : wordUrls){
                if (combinationUrlResults.keySet().contains(url)){  //if the url is already in the list collected
                    combinationUrlResults.replace(url, combinationUrlResults.get(url) + this.repository.Dict.get(word).get(url));
                }
                else combinationUrlResults.put(url, this.repository.Dict.get(word).get(url));
            }
        }

        int max = 0;
        String top_url = "";
        ArrayList<String> topUrlList = new ArrayList<>();
        ArrayList<Integer> topUrlFreqs = new ArrayList<>();


        for (int i = 0; i < numSearches; i++) { //loop for number of searches
            for (String url : combinationUrlResults.keySet()) { //iterates through repository(dictionary)
                if (combinationUrlResults.get(url) >= max && !topUrlList.contains(url)) { //checks to see if url freq is larger than current max and hasn't been found already
                    max = combinationUrlResults.get(url); //sets the new max
                    top_url = url; //sets the new top url to be added
                }
            }
            //given in lists to preserve descending order
            topUrlList.add(top_url); //adds the top url to a list
            topUrlFreqs.add(max); //adds the corresponding max to a different list
            max = 0; //resets the max to restart search
        }

        //prints all the urls and their matching freq
        System.out.println("Or Search results for " + query + ":");
        for (int i = 0; i < numSearches; i++){
            System.out.printf("Url = %s\tFrequency = %s\n", topUrlList.get(i), topUrlFreqs.get(i));
        }

    }

    //initializes the and search algorithm by first getting a list of links with the highest frequencies that are within all the words' treemaps
    private HashMap<String, Integer> andStart(String query, int numSearches) {
        //split query
        String[] queryList = query.split(" ");
        int qLl = queryList.length;
        HashMap<String, Integer> combinationUrlResults = new HashMap<>();
        //combines all results of word searches into one list
        for (String word : queryList) {
            ArrayList<String> wordUrls = this.repository.searchFor(word, numSearches * qLl);
            for (String url : wordUrls) {
                if (combinationUrlResults.keySet().contains(url)){  //if the url is already in the list collected
                    combinationUrlResults.replace(url, combinationUrlResults.get(url) + this.repository.Dict.get(word).get(url));
                }
                else combinationUrlResults.put(url, this.repository.Dict.get(word).get(url));
            }
        }


        for(String url : combinationUrlResults.keySet()){ //iterates through each url in the combination list of found urls
            int isIn = 0; //counter to see if url appears in each word's url treemap
            for(int i = 0; i < qLl; i++){ //iterates for each word in query list
                if(this.repository.Dict.get(queryList[i]).keySet().contains(url)){ //checks to see if it is in the keySet for the word
                    isIn++; //increments the amount of times it's found
                }
            }
            if(isIn < qLl){ //if it is not within all the words
                combinationUrlResults.remove(url); //remove the url from the list
            }
        }

        //recursion to make sure that the list is as long as the number of searches(urls to return) that the user requested
        if (numSearches > combinationUrlResults.keySet().toArray().length){
            return andStart(query, numSearches * 2);//multiplies the data sample size by two if it's not big enough
        }
        //returns the list of results
        return combinationUrlResults;
    }

    //actual and search algorithm. Same as the or Seach algorithm
    public void andSearch(String query, int numSearches){

        //calls the and Start function to get a list of urls that are within all the words' treemaps
        HashMap<String, Integer> containsAllList = andStart(query, numSearches);

        //or search algorithm but with the list of urls guaranteed to be inside each word's treemap
        int max = 0;
        String top_url = "";
        ArrayList<String> topUrlList = new ArrayList<>();
        ArrayList<Integer> topUrlFreqs = new ArrayList<>();

        for (int i = 0; i < numSearches; i++) {
            for (String url : containsAllList.keySet()) {
                if (containsAllList.get(url) >= max && !topUrlList.contains(url)) {
                    max = containsAllList.get(url);
                    top_url = url;
                }
            }
            topUrlList.add(top_url);
            topUrlFreqs.add(max);
            max = 0;
        }

        System.out.println("And Search results for " + query + ":");
        for (int i = 0; i < numSearches; i++){
            System.out.printf("Url = %s\tFrequency = %s\n", topUrlList.get(i), topUrlFreqs.get(i));
        }

    }

    //serializes a WebCat Hub. Static function so that no object needs to be created
    public static void SerializeHub(WebCatHub wch){
        try{
            //create file object from FileOutputStream
            FileOutputStream fileOut = new FileOutputStream("WebCat_Hub.ser");

            //create file ObjectOutputStream object
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            //write object with WriteObject method
            out.writeObject(wch);

            //close resources
            out.close();
            fileOut.close();
            System.out.println("WebCat Hub successfully saved!");
        }
        catch(Exception error){
            System.out.println("Error. Something went wrong with serialization");
            System.out.println(error.getMessage());
        }
    }

    //deserializes a WebCat Hub from matching file name
    public static WebCatHub DeserializeHub(){
        try{

            FileInputStream fileIn = new FileInputStream("WebCat_Hub.ser");

            ObjectInputStream in = new ObjectInputStream(fileIn);

            WebCatHub wch = (WebCatHub) in.readObject();

            in.close();
            fileIn.close();
            System.out.println("Loading previously saved WebCat Hub");

            return wch;
        }
        catch(Exception error){
            System.out.println("Error. Something went wrong with deserialization");
            System.out.println(error.getMessage());
            return null;
        }
    }






}
