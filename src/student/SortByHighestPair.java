package student;

import javafx.util.Pair;

import java.util.Comparator;

public class SortByHighestPair implements Comparator<Pair> {
    @Override
    public int compare(Pair o1, Pair o2) {

        if (o2 == null){
            return -1;
        }

        if (o1 == null){
            return 1;
        }

        int o2Value = (int) o2.getValue();
        int o1Value = (int) o1.getValue();
        return o2Value - o1Value;

    }
}
