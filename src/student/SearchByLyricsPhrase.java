package student;

import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class SearchByLyricsPhrase {
    HashMap<String, HashSet<Song>> matchedSongs = new HashMap<>();
    HashMap<Song, String[]> splitLyrics = new HashMap<>();
    HashMap<String, Song[]> foundRanks;
    HashMap<String, Song> songs = new HashMap<>();

    public SearchByLyricsPhrase(SongCollection sc){
        Song[] allSongs = sc.getAllSongs();
        for (Song currentSong : allSongs) {
            String[] currentLyrics = stringToArray(currentSong.getLyrics().toLowerCase());
            splitLyrics.put(currentSong, currentLyrics);
            songs.put(currentSong.getTitle(), currentSong);

            for (String currentWord : currentLyrics) {
                HashSet<Song> currentSet;
                if (matchedSongs.containsKey(currentWord)) {
                    currentSet = matchedSongs.get(currentWord);
                    currentSet.add(currentSong);
                } else {
                    currentSet = new HashSet<>();
                    currentSet.add(currentSong);
                }
                matchedSongs.put(currentWord, currentSet);
            }

        }

        //Loads any found ranks previously from a file into foundRanks.
        loadfoundRanks();
    }

    private boolean loadfoundRanks(){
        try{
            File file = new File("foundRanks.ser");
            if (file.exists()){
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                foundRanks = (HashMap<String, Song[]>) objectIn.readObject();
                objectIn.close();
                return true;
            }else{
                foundRanks = new HashMap<>();
                return false;
            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Song[] search(String lyricsWords){
        String[] lyricsWordsArray = stringToArray(lyricsWords.toLowerCase());
        if (foundRanks.containsKey(lyricsWords)){
            return foundRanks.get(lyricsWords);
        }
        HashSet<Song> songsToRank = new HashSet<>();
        for (int i = 0; i < lyricsWordsArray.length; i++){
            if (!matchedSongs.containsKey(lyricsWordsArray[i])){
                return new Song[0];
            }else{
                if (i == 0){
                    songsToRank.addAll(matchedSongs.get(lyricsWordsArray[i]));
                }else{
                    songsToRank.retainAll(matchedSongs.get(lyricsWordsArray[i]));
                }
            }
        }
        ArrayList<Pair> foundSongs = new ArrayList<>();
        for (Song currentSong : songsToRank){
            int rank = rankPhrase(splitLyrics.get(currentSong), lyricsWordsArray);
            if (rank > 0){
                Pair currentPair = new Pair(currentSong, rank);
                foundSongs.add(currentPair);
            }
        }

        foundSongs.sort(new SortByLowestPair());
        Song[] returnedSongs = new Song[foundSongs.size()];

        for (int i = 0; i < foundSongs.size(); i++){
            returnedSongs[i] = (Song) foundSongs.get(i).getKey();
        }

        //Adds found rank to foundRanks and saves to file.
        savefoundRanks(lyricsWords, returnedSongs);
        return returnedSongs;
    }

    private boolean savefoundRanks(String lyricsWords, Song[] returnedSongs){
        foundRanks.put(lyricsWords, returnedSongs);
        try {
            File file = new File("foundRanks.ser");
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(foundRanks);
                objectOut.close();
                return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static int rankPhrase(String[] lyrics, String[] lyricsPhrase) {

        String[] phraseArray = lyricsPhrase;
        ArrayList<Pair> phraseIndexes = new ArrayList<>();
        ArrayList<Integer> completedPhrasesRank = new ArrayList<>();

        int currentPhraseIndex;
        int lastPhraseIndex = phraseArray.length - 1;
        int currentRank;
        int lowestPossibleRank = findLowestPossibleRank(phraseArray);
        boolean singleWord = false;
        int lyricsSize = lyrics.length;

        //If there is nothing in phraseArray returns -1;
        if (lowestPossibleRank == 0) {
            return -1;
        }

        if (phraseArray.length == 1){
            singleWord = true;
        }


        //Iterates through lyrics, adding a new Pair(phraseIndex, rank) to phraseIndex each time it find the first occurrence of first word in phrase
        //Continues to add to each pair's rank as next word does not match any pair's phraseIndex word.
        //When next words matches any phraseIndex word, it increments phrase index for that current array
        //When phrase has been found, adds the rank to a permanent array list. If rank == lowest possible rank, returns that, otherwise continues the search
        //At end it finds the lowest possible rank by iterating through completedPhrasesRank.
        for (int i = 0; i < lyricsSize; i++) {
            String currentLyricWord = lyrics[i];
            //Continues until it finds the first phrase word.
            if (phraseIndexes.size() == 0 && !phraseArray[0].equals(currentLyricWord)) {
                continue;
            }

            currentRank = currentLyricWord.length();

            if (phraseIndexes.size() > 0 && phraseArray[0].equals(currentLyricWord)) {
                currentPhraseIndex = 1;
                if (phraseIndexEqualsLastPhraseIndex(phraseIndexes, phraseArray, currentLyricWord, lastPhraseIndex)) {
                    if (addCompletedPhrasesRank(phraseIndexes, currentRank, lastPhraseIndex, completedPhrasesRank, lowestPossibleRank)) {
                        return lowestPossibleRank;
                    }
                }
                increaseCurrentRankIndexAndRank(phraseIndexes, phraseArray, currentRank, currentLyricWord);
                Pair currentPair = new Pair(currentPhraseIndex, currentRank);
                phraseIndexes.add(currentPair);
            } else {
                if (phraseIndexes.size() == 0) {
                    currentPhraseIndex = 1;
                    Pair currentPair = new Pair(currentPhraseIndex, currentRank);
                    phraseIndexes.add(currentPair);
                    if (singleWord){
                        return lowestPossibleRank;
                    }
                    continue;
                }
                if (phraseArray[lastPhraseIndex].equals(currentLyricWord) && phraseIndexEqualsLastPhraseIndex(phraseIndexes, phraseArray, currentLyricWord, lastPhraseIndex)) {
                    if (addCompletedPhrasesRank(phraseIndexes, currentRank, lastPhraseIndex, completedPhrasesRank, lowestPossibleRank)) {
                        return lowestPossibleRank;
                    }
                } else {
                    increaseCurrentRankIndexAndRank(phraseIndexes, phraseArray, currentRank, currentLyricWord);
                }
            }

        }
        if (completedPhrasesRank.size() == 0) {
            return -1;
        } else {
            return findLowestCompletedPhrasesRank(completedPhrasesRank);
        }
    }

    private static int findLowestPossibleRank(String[] phraseArray) {

        int lowestPossibleRank = 0;

        for (String currentLyricsWord : phraseArray) {
            String currentWord = currentLyricsWord;
            lowestPossibleRank += currentWord.length();
        }
        return lowestPossibleRank;
    }

    private static int findLowestCompletedPhrasesRank(ArrayList<Integer> completedPhrasesRank) {

        int lowestRank = -1;
        for (Integer currentRank : completedPhrasesRank) {
            if (lowestRank != -1) {
                if (currentRank < lowestRank) {
                    lowestRank = currentRank;
                }
            } else {
                lowestRank = currentRank;
            }
        }
        return lowestRank;
    }

    private static boolean addCompletedPhrasesRank(ArrayList<Pair> phraseIndexes, int currentRank, int lastPhraseIndex, ArrayList<Integer> completedPhrasesRank, int lowestPossibleRank) {
        for (int i = 0; i < phraseIndexes.size(); i++) {
            int currentIndex = (int) phraseIndexes.get(i).getKey();
            if (currentIndex == lastPhraseIndex) {
                int currentPermanentRank = (int) phraseIndexes.get(i).getValue() + currentRank;
                if (currentPermanentRank == lowestPossibleRank) {
                    return true;
                }
                completedPhrasesRank.add(currentPermanentRank);
                phraseIndexes.remove(i);
                i = i - 1;
            }
        }
        return false;
    }

    private static void increaseCurrentRankIndexAndRank(ArrayList<Pair> phraseIndexes, Object[] phraseArray, int currentRank, String currentWord) {
        for (int i = 0; i < phraseIndexes.size(); i++) {
            int currentIndex = (int) phraseIndexes.get(i).getKey();
            int currentPhraseRank = (int) phraseIndexes.get(i).getValue();
            if (phraseArray[currentIndex].equals(currentWord)) {
                Pair newPair = new Pair(++currentIndex, currentPhraseRank + currentRank);
                phraseIndexes.set(i, newPair);
            } else {
                Pair newPair = new Pair(currentIndex, currentPhraseRank + currentRank);
                phraseIndexes.set(i, newPair);
            }
        }
    }

    private static boolean phraseIndexEqualsLastPhraseIndex(ArrayList<Pair> phraseIndexes, Object[] phraseArray, String currentWord, int lastPhraseIndex) {
        for (Pair i : phraseIndexes) {
            int currentIndex = (int) i.getKey();
            if (phraseArray[currentIndex].equals(currentWord) && currentIndex == lastPhraseIndex) {
                return true;
            }
        }
        return false;
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

    public  String[] stringToArray(String lyrics) {
        ArrayList<String> words = new ArrayList<>();

        int lyricsSize = lyrics.length();

        for (int i = 0; i < lyricsSize; ) {
            if (Character.isLetterOrDigit(lyrics.charAt(i))) {
                int startIndex = i;
                while (i < lyricsSize && Character.isLetterOrDigit(lyrics.charAt(i))) {
                    i++;
                }
                words.add(lyrics.substring(startIndex, i));
            } else {
                i++;
            }
        }

        String[] returnArray = new String[words.size()];
        words.toArray(returnArray);
        return returnArray;
    }

    //Finds the top10 words that occur the most.
    public Pair[] top10Words(){
        Pair[] top10Words = new Pair[100];
        int top10LeastOccurrence = 99;

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
        SearchByLyricsPhrase sblw = new SearchByLyricsPhrase(sc);
       // sblw.statistics();

        if (args.length == 0) {
            System.err.println("usage: prog songfile [search string]");
            return;
        }

        Song[] foundSongs = sblw.search(args[1]);

        System.out.println("Searching for: " + args[1]);
        System.out.println("Number of songs found: " + foundSongs.length);
        String[] phraseArray = sblw.stringToArray(args[1]);
        for (int i = 0; i < foundSongs.length && i < 10; i++){
            int rank = sblw.rankPhrase(sblw.splitLyrics.get(foundSongs[i]), phraseArray);
            System.out.println(rank + " " + foundSongs[i].getArtist() + ", \"" + foundSongs[i].getTitle() + "\"");
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
            for (Pair aTmp : tmp) {
                System.out.println("Word: \"" + aTmp.getKey() + "\" Count: " + aTmp.getValue());
            }
        }

    }
}
