package thiessen.bachelorthesis.closedstreaming;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by Maximilian on 04.10.2017.
 */
public class ClosedItemsetTree extends TreeSet {
    public ClosedItemsetTree() {
        super(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 0;
            }
        });
    }

}
