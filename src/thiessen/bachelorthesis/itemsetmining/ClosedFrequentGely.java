package thiessen.bachelorthesis.itemsetmining;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Maximilian Thiessen on 03.10.2017.
 */
public class ClosedFrequentGely extends FrequentGely {



    private Itemset toRemoveSupportSet = new Itemset();
    private Itemset currentSupportSet = new Itemset();

    @Override
    public boolean inF(Set itemset, int newItem) {
        return true;
    }

    @Override
    public boolean inF(Set itemset) {
        return true;
    }



    public ClosedFrequentGely(ArrayList<Transaction> d, Set<Integer> e) {
        super(d, e);
    }

    public ClosedFrequentGely(ArrayList<Transaction> d, Set<Integer> e, int minSupport) {
        super(d, e, minSupport);
    }

    @Override
    public Itemset closure(Itemset itemset, int newItem, Set<Integer> parentSupportSet, Set<Integer> currentSupportSet) {
        Itemset closure = null;
        boolean firstTime = true;
        Itemset newItems = new Itemset();

        for (int i : parentSupportSet) {
            Set transaction = D.get(i);
            if (transaction.contains(newItem)) {
                currentSupportSet.add(i);
                if (firstTime) {
                    closure =  new Itemset(itemset);
                    newItems = new Itemset(transaction);
                    newItems.removeAll(itemset);
                    firstTime = false;
                } else {
                    newItems.retainAll(transaction);
                }
                closure.support++;
                if (transaction.size() == itemset.size()) {
                    closure.countOfEqualTranscations++;
                }
            }

         }

        if (closure == null) {
            return null;
        }
        closure.addAll(newItems);

        return closure;
    }

    @Override
    public Itemset closure(Itemset itemset, Set<Integer> currentSupportSet) {
        Itemset closure = null;
        boolean firstTime = true;

        for (int i = 0; i < D.size(); i++) {
            Set transaction = D.get(i);
            if (transaction.containsAll(itemset)) {
                currentSupportSet.add(i);
                if (firstTime) {
                    closure =  new Itemset(transaction);
                    firstTime = false;
                } else {
                    closure.retainAll(transaction);
                }
                closure.support++;
                if (transaction.size() == itemset.size()) {
                    itemset.countOfEqualTranscations++;
                }
            }

        }

        return closure;
    }

}
