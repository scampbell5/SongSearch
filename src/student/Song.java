package student;

import java.util.*;

/*
 * song class to hold strings for a song's artist, title, and lyrics
 */
public class Song implements Comparable<Song> {

    private String artist, title, lyrics;


    // constructor
    public Song(String artist, String title, String lyrics) {
        this.artist = artist;
        this.title = title;
        this.lyrics = lyrics;
    }

    public static class CmpArtist extends CmpCnt implements Comparator<Song> {
        public int compare(Song s1, Song s2) {
            cmpCnt++;
            return s1.artist.compareToIgnoreCase(s2.artist);
        }
    }

    public String getArtist() {
        return artist;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return artist + ", \"" + title + "\"";
    }

    /*
     * the default comparison of songs
     * primary key: artist, secondary key: title
     * used for sorting and searching the song array
     * if two songs have the same artist and title they are considered the same
     */
    public int compareTo(Song song2) {

        int retVal = this.artist.compareToIgnoreCase(song2.artist);

        if (retVal != 0) {
            return retVal;
        } else {
            return this.title.compareToIgnoreCase(song2.title);
        }
    }

    // testing method to test this class
    public static void main(String[] args) {
        Song s1 = new Song("Professor B",
                "Small Steps",
                "Write your programs in small steps\n" +
                        "small steps, small steps\n" +
                        "Write your programs in small steps\n" +
                        "Test and debug every step of the way.\n");

        Song s2 = new Song("Brian Dill",
                "Ode to Bobby B",
                "Professor Bobby B., can't you see,\n" +
                        "sometimes your data structures mystify me,\n" +
                        "the biggest algorithm pro since Donald Knuth,\n" +
                        "here he is, he's Robert Boothe!\n");

        Song s3 = new Song("Professor B",
                "Debugger Love",
                "I didn't used to like her\n" +
                        "I stuck with what I knew\n" +
                        "She was waiting there to help me,\n" +
                        "but I always thought print would do\n\n" +
                        "Debugger love .........\n" +
                        "Now I'm so in love with you\n");
        Song s4 = new Song("Be", "123456", "1234567");
        Song s5 = new Song("Beach Boys", "123456", "1234567");
        Song s6 = new Song("Bfa", "123456", "1234567");


        System.out.println("testing getArtist: " + s1.getArtist());
        System.out.println("testing getTitle: " + s1.getTitle());
        System.out.println("testing getLyrics:\n" + s1.getLyrics());

        System.out.println("testing toString:\n");
        System.out.println("Song 1: " + s1);
        System.out.println("Song 2: " + s2);
        System.out.println("Song 3: " + s3);

        System.out.println("testing compareTo:");
        System.out.println("Song1 vs Song2 = " + s1.compareTo(s2));
        System.out.println("Song2 vs Song1 = " + s2.compareTo(s1));
        System.out.println("Song1 vs Song3 = " + s1.compareTo(s3));
        System.out.println("Song3 vs Song1 = " + s3.compareTo(s1));
        System.out.println("Song1 vs Song1 = " + s1.compareTo(s1));
        System.out.println("Song1 artist vs Song2 artist = " + new CmpArtist().compare(s1, s2));
        System.out.println("Song2 artist vs Song1 artist = " + new CmpArtist().compare(s2, s1));
        System.out.println("Song4 artist vs Song5 artist = " + new CmpArtist().compare(s4, s5));
        System.out.println("Song4 artist vs Song5 artist = " + new CmpArtist().compare(s4, s6));

    }
}