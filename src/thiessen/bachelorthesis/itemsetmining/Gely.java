package thiessen.bachelorthesis.itemsetmining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maximilian Thiessen on 03.10.2017.
 * Gely algorithm to list all closed Itemsets of a strongly accessible and confluent set system.
 * This is just the basic class without frequency constraints.
 * One can implement their own derived classes of this
 * with their own closure and inF Functions to speed up the computation in some special cases (e.g. connected subgraphs).
 */



public abstract class Gely {
    //Items must be positive integers (0,1,2,3,...)
    protected Set<Integer> E;
    protected ArrayList<Transaction> D;

    protected HashMap<Itemset, Itemset> closedItemsets;

    private final static int NOT_FOUND_AUGMENTATION = -1;

    //abstract to methods, to be implemented by concrete gelyClasses like ConnectedGely or ClosedFrequentGely
    public abstract boolean inF(Set itemset, int newItem);
    public abstract boolean inF(Set itemset);

    public abstract Itemset closure(Itemset itemset, int newItem, Set<Integer> parentSupportSet, Set<Integer> currentSupportSet);
    public abstract Itemset closure(Itemset itemset, Set<Integer> currentSupportSet);

    /**
     * Constructor to initialize D and E. F is delivered as a set, but exists implicitly through the function inF.
     * @param d transaction database
     * @param e possible items
     */
    public Gely(ArrayList<Transaction> d, Set<Integer> e) {
        D = d;
        E = e;
    }

    public Gely() {
    }

    /**
     * Finds an valid (C+e is in F, C+e not overlaps with B) augmentation element e for the itemset C
     * @param C current itemset
     * @param B set of banned items
     * @return the augmentation element e
     */
    public int augment(Itemset C, Set<Integer> B) {
        for (int e : E) {
            Itemset union = new Itemset(C);
            union.addAll(B);
            if (!union.contains(e) && inF(union, e)) {
                return e;
            }
        }
        return NOT_FOUND_AUGMENTATION;
    }



    /**
     * Starts the gely algorithm. Finds and returns all closed itemsets in (E, F) regarding to D.
     * @return
     */
    public HashMap<Itemset, Itemset> gely() {
        closedItemsets = new HashMap<>(D.size()); //init with no. transactions

        list(new Itemset(), new HashSet<>(), null);
       // System.out.println("Final count!: " + closedItemsets.size());
        return closedItemsets;
    }


    /*


     */

    /**
     * Finds all proper closed subsets of C which do not overlap with B.
     * Can be further optimized!
     * @param C current itemset
     * @param B set of banned items
     * @param parentSupportSet Only for speedup purpuses.
     *                         the supportset of the parent itemset.
     *                         Because of the Apriori-principle the support set of the new itemset will be a subset of this.
     */
    protected void list(Itemset C, Set<Integer> B, Set<Integer> parentSupportSet) {
        /*int e = augment(C, B);
        if (e == NOT_FOUND_AUGMENTATION) {
            return;
        }*/

        Set<Integer> currentSupportSet = new HashSet<>();

        Set<Integer> BTemp = new HashSet<>(B);
        for (int e : E) {
            Itemset union = new Itemset(C);
            union.addAll(B);
            if (union.contains(e) || !inF(C, e)) {
                continue;
            }
            Itemset newC = new Itemset(C);


            if (parentSupportSet == null) {
                newC.add(e);
                newC = closure(newC, currentSupportSet);
            } else { //do not calculate from scratch. it has to be a subset of the parent's supportset.
                newC = closure(newC, e, parentSupportSet, currentSupportSet);
            }

            if (newC != null) {
                Itemset intersection = new Itemset(newC);
                intersection.retainAll(B);
                //if not seen this closure before
                if (intersection.isEmpty()) {
                    closedItemsets.put(newC, newC);
                    list(newC, B, currentSupportSet);
                }
            }

            B.add(e);


        }

        B.clear();
        //reset B
        B.addAll(BTemp);
    }

    public Set<Integer> getE() {
        return E;
    }

    public void setE(Set<Integer> e) {
        E = e;
    }

    public ArrayList<Transaction> getD() {
        return D;
    }

    public void setD(ArrayList<Transaction> d) {
        D = d;
    }

    public HashMap<Itemset, Itemset> getClosedItemsets() {
        return closedItemsets;
    }

}
