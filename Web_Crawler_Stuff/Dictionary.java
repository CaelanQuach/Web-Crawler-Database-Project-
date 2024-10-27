//package COSC_2351.Web_Crawler_Stuff;

import java.util.*;
import java.util.ArrayList;


public class Dictionary implements java.io.Serializable {
    public HashMap<String, TreeMap<String, Integer>> Dict;

    //constructor
    public Dictionary() {
        this.Dict = new HashMap<>();
    }

    //functions
    public void add(String W, String url) {
        int freq;

        if (Dict.get(W) == null) { //if word is non-existent, add new entry in hashmap
            TreeMap<String, Integer> newtreeMap = new TreeMap<>();//populate new treemap
            Dict.put(W, newtreeMap);
            Dict.get(W).put(url, 1);//have new treemap into Dict.
        } else if (Dict.get(W).get(url) == null) { //word exists, but it's a new url, add new entry in treemap
            Dict.get(W).put(url, 1);
            // if the word has been seen before then it would meet the second condition
        } else { // if word and url already exist, add one more to url freq
            freq = Dict.get(W).get(url);
            Dict.get(W).replace(url, freq + 1);
        }
    }


    public void remove(String W) { // removes word from hashmap
        Dict.remove(W);
    }

    public ArrayList<String> searchFor(String W, int searches) { // searches for a certain number of top urls for a word
        TreeMap<String, Integer> wordTree = Dict.get(W); // grabs the treemap for a certain word

        int max = 0;
        String top_url = "";
        ArrayList<String> topUrlList = new ArrayList<>();

        for (int i = 0; i < searches; i++) {// for number of urls
            for (String url : wordTree.keySet()) { // iterates through dictionary
                if (wordTree.get(url) >= max && !topUrlList.contains(url)) { //finds the max url that hasn't already been found yet
                    max = wordTree.get(url);
                    top_url = url;
                }
            }
            topUrlList.add(top_url); //adds to list of urls to return, ordered in descending order
            max = 0;
        }

        return topUrlList;
    }


}



