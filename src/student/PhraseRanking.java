package student;

import javafx.util.Pair;

import java.util.ArrayList;

public class PhraseRanking {


    static int rankPhrase(String lyrics, String lyricsPhrase) {

        String[] phraseArray = stringToArray(lyricsPhrase.toLowerCase());

        lyrics = lyrics.toLowerCase();

        //String[] lyricsArray = stringToArray(lyrics.toLowerCase());

        ArrayList<Pair> phraseIndexes = new ArrayList<>();
        ArrayList<Integer> completedPhrasesRank = new ArrayList<>();

        int currentPhraseIndex;
        int lastPhraseIndex = phraseArray.length - 1;
        int currentRank;
        int lowestPossibleRank = findLowestPossibleRank(phraseArray);
        boolean singleWord = false;
        int lyricsSize = lyrics.length();

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
        for (int i = 0; i < lyricsSize;) {

            String currentLyricWord;

            if (Character.isLetter(lyrics.charAt(i))) {
                int startIndex = i;
                while (i < lyricsSize && Character.isLetter(lyrics.charAt(i))) {
                    i++;
                }
                currentLyricWord = lyrics.substring(startIndex, i);
            } else {
                currentLyricWord = " ";
                i++;
            }



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

    private static String[] stringToArray(String lyrics) {
      //  char[] characterArray = lyrics.toCharArray();
        ArrayList<String> words = new ArrayList<>();

        int lyricsSize = lyrics.length();

        for (int i = 0; i < lyricsSize; ) {

          //  StringBuilder tempString = new StringBuilder();

            if (Character.isLetter(lyrics.charAt(i))) {
                int startIndex = i;
                while (i < lyricsSize && Character.isLetter(lyrics.charAt(i))) {
                   // tempString.append(characterArray[i]);
                    i++;
                }
                words.add(lyrics.substring(startIndex, i));
            } else {
                words.add(" ");
                i++;
            }
        }

        String[] returnArray = new String[words.size()];
        words.toArray(returnArray);
        return returnArray;
    }

    public static void main(String[] args) {

        SongCollection sc = new SongCollection(args[0]);
        int songCount = 0;
        for (Song s : sc.getAllSongs()) {
            int rank = rankPhrase(s.getLyrics(), args[1]);
            if (rank > 0) {
                System.out.println(rank + " " + s.getArtist() + " " + "\"" + s.getTitle() + "\"");
                songCount++;
            }
        }

        System.out.println("Number of songs: " + songCount);

    }
}
