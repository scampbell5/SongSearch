package student;

import javafx.util.Pair;

import java.util.*;

public class SearchByLyricsWords {

    String commonWordsString = "the of and a to in is you that it he for was on\n" +
            " are as with his they at be this from I have or\n" +
            " by one had not but what all were when we there\n" +
            " can an your which their if do will each how them\n" +
            " then she many some so these would into has more\n" +
            " her two him see could no make than been its now\n" +
            " my made did get our me too";

    TreeMap<String, TreeSet<Song>> matchedSongs = new TreeMap<String, TreeSet<Song>>();
    TreeSet<String> commonWords = new TreeSet<>();

    public SearchByLyricsWords(SongCollection sc){
        //Adds string of common words to set. Songs should not be added to matchedSongs map based on these words.
        Collections.addAll(commonWords, commonWordsString.split("[^a-zA-Z]+"));

        Song[] allSongs = sc.getAllSongs();

        for (Song currentSong : allSongs){

            String[] currentLyrics = currentSong.getLyrics().split("[^a-zA-Z]+");

            for (String currentWord : currentLyrics){

                //Only adds song to list if word is not a common word and not a single letter.
                if (!commonWords.contains(currentWord) && currentWord.length() > 1 ){
                    TreeSet<Song> currentSet;
                    if (matchedSongs.containsKey(currentWord)){
                        currentSet = matchedSongs.get(currentWord);
                        currentSet.add(currentSong);
                    }else{
                        currentSet = new TreeSet<>();
                        currentSet.add(currentSong);
                    }
                    matchedSongs.put(currentWord, currentSet);
                }

            }
        }

    }

    public Song[] search(String lyricsWords){
        lyricsWords = lyricsWords.toLowerCase();
        TreeSet<Song> foundSongs = new TreeSet<>();
        TreeSet<String> lyricsWordsSet = new TreeSet<>();
        Collections.addAll(lyricsWordsSet, lyricsWords.split("[^a-zA-Z]+"));
        lyricsWordsSet.removeAll(commonWords);
        boolean initialSongs = false;

        for (String currentSearchWord : lyricsWordsSet){
            if (currentSearchWord.length() > 1){
                if (matchedSongs.containsKey(currentSearchWord)){
                    if (!initialSongs){
                        foundSongs.addAll(matchedSongs.get(currentSearchWord));
                        initialSongs = true;
                    }else{
                        foundSongs.retainAll(matchedSongs.get(currentSearchWord));
                    }

                }else{
                    Song[] returnedSongs = new Song[0];
                    return returnedSongs;
                }
            }
        }

        Song[] returnedSongs = new Song[foundSongs.size()];
        foundSongs.toArray(returnedSongs);
        return returnedSongs;
    }

    public void statistics(){
        int songCount = 0;

        //Iterates through and adds each song reference.
        for (String word : matchedSongs.keySet()){
            songCount += matchedSongs.get(word).size();
        }

        System.out.println("Number of keys: " + matchedSongs.size());
        System.out.println("Total number of song references: " + songCount);

    }

    public void printWordsInTree(){
        int songCount = 0;

        //Iterates through and adds each song reference.
        for (String word : matchedSongs.keySet()){
            System.out.println(word);
        }


    }

    //Finds the top10 words that occur the most.
    public Pair[] top10Words(){
        Pair[] top10Words = new Pair[10];
        int top10LeastOccurrence = 9;

        for (String word : matchedSongs.keySet()){
                int currentSize = matchedSongs.get(word).size();
                //Array is sorted from greatest to least, just need to compare last value to current value.
                //Sorting after insertion will ensure it is in order for next time we perform a compare.
                if (top10Words[top10LeastOccurrence] == null || (int) top10Words[top10LeastOccurrence].getValue() < currentSize){
                    top10Words[top10LeastOccurrence] = new Pair(word, currentSize);
                    Arrays.sort(top10Words, new SortByHighestPair());
            }
        }
        return top10Words;
    }


    public static void main(String[] args) {

        boolean top10Words = false;
        SongCollection sc = new SongCollection(args[0]);
        SearchByLyricsWords sblw = new SearchByLyricsWords(sc);
        sblw.statistics();

        if (args.length == 0) {
            System.err.println("usage: prog songfile [search string]");
            return;
        }

        Song[] foundSongs = sblw.search(args[1]);

        System.out.println("Searching for: " + args[1]);
        System.out.println("Number of songs found: " + foundSongs.length);
        for (int i = 0; i < foundSongs.length && i < 10; i++){
            System.out.println("Artist: " + foundSongs[i].getArtist() + " Title: " + foundSongs[i].getTitle());
        }


        //Handles additional arguments. Sets booleans value to true if it contains correct argument.
        if (args.length > 0){
            for (int i = 1; i < args.length; i++){
                if (args[i].equals("-top10words")){
                    top10Words = true;
                }
            }
        }

        if (top10Words){
            Pair[] tmp = sblw.top10Words();
            for (int i = 0; i < tmp.length; i++){
                System.out.println("Word: \"" + tmp[i].getKey() + "\" Count: " + tmp[i].getValue());
            }
        }

    }
}
