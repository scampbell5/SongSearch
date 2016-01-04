package student;

import javafx.util.Pair;

import java.util.Comparator;

/**
 * Created by Sean on 11/30/15.
 */
public class SortByLowestPair implements Comparator<Pair> {
    @Override
    public int compare(Pair o1, Pair o2) {

        if (o1 == null){
            return -1;
        }
        if (o2 == null){
            return 1;
        }
        int o1Value = (int) o2.getValue();
        int o2Value = (int) o1.getValue();
        if (o1Value == o2Value){
            String o2Artist = ((Song) o2.getKey()).getArtist();
            String o1Artist = ((Song) o1.getKey()).getArtist();
            if (o2Artist.equals(o1Artist)){
                String o2Title = ((Song) o2.getKey()).getTitle();
                String o1Title = ((Song) o1.getKey()).getTitle();
                return o1Title.compareTo(o2Title);
            }else{
                return o1Artist.compareTo(o2Artist);
            }
        }
        return o2Value - o1Value;

    }
}
