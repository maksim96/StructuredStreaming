package thiessen.bachelorthesis.itemsetmining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maximilian Thiessen on 03.10.2017.
 * Reresents an itemset. Is basically just a HashSet with an int for the absolute support.
 */
public class Itemset extends HashSet<Integer> {
    //absolute support
    public int support = 0;
    public int countOfEqualTranscations = 0; //the number of transactions equal to this itemset

    /**
     * Constructor
     * @param itemset any set of items from E
     * @param support the support of this itemset
     */
    public Itemset(Collection itemset, int support) {
        super(itemset);
        this.support = support;
    }

    public Itemset() {
    }

    public Itemset(Collection itemset) {
        super(itemset);
    }

    public Itemset(int initialCapacity) {
        super(initialCapacity);
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }


}
