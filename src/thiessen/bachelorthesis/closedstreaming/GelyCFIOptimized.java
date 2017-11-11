package thiessen.bachelorthesis.closedstreaming;

import thiessen.bachelorthesis.itemsetmining.ClosedFrequentGely;
import thiessen.bachelorthesis.itemsetmining.ClosedFrequentGelyOptimized;
import thiessen.bachelorthesis.itemsetmining.Gely;
import thiessen.bachelorthesis.itemsetmining.Itemset;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Maximilian on 04.10.2017.
 */
public class GelyCFIOptimized {

/*
    private Gely gely;

    public ArrayList<Itemset> closedItemsets = new ArrayList<>();

    private int minSupport = 0;

    //Items must be positive integers (0,1,2,3,...)
    protected Set<Integer> E;
    protected ArrayList<HashSet<Integer>> D;

    public GelyCFIOptimized( ArrayList<HashSet<Integer>> D, Set<Integer> E) {
        this.E = E;
        this.D = D;

        Itemset oneItem = new Itemset();

        Set<Integer> toRemove = new HashSet<>();

        for (int e : E) {
            oneItem.clear();
            oneItem.add(e);
            if (!isFrequent(oneItem)) {
                toRemove.add(e);
            }
        }

        for (int e : toRemove) {
            E.remove(e);
        }
    }
    public GelyCFIOptimized( ArrayList<HashSet<Integer>> d, Set<Integer> e, int minSupport) {
        this(d, e);
        this.minSupport = minSupport;
    }

    private boolean isFrequent(Set<Integer> X) {
        int support = 0;
        for (Set<Integer> transaction : D) {
            if (transaction.containsAll(X)) {
                support++;
                if (support >= minSupport) {
                    return true;
                }
            }

        }
        return false;
    }

    private boolean hasNoneZeroSupport(Set<Integer> X) {
        for (Set<Integer> transaction : D) {
            if (transaction.containsAll(X)) {
                return true;
            }

        }
        return false;
    }

    private boolean transactionExistsInDatabase(Set<Integer> X) {
        for (Set<Integer> transaction : D) {
            if (transaction.equals(X)) {
                return true;
            }

        }
        return false;
    }

    public void explore() {
        gely = new ClosedFrequentGelyOptimized(D, E, minSupport);
        closedItemsets = gely.gely();
    }

    public void addition(Itemset X)  {


        gely = new ClosedFrequentGelyOptimized(D, E, minSupport);

        if (!gely.inF(X)) {
            //partition blabla
        }

        if (hasNoneZeroSupport(X) && X.containsAll(gely.closure(X)) && gely.inF(X)) {
            if (!closedItemsets.contains(X)) {
                closedItemsets.add(X);
            }

            D.add(X);
            for(Itemset itemset : closedItemsets) {
                if (X.containsAll(itemset)) {
                    itemset.support++;
                }
            }
        } else {
            D.add(X);
            // System.out.println("Next line from gely subcall in addition");
            gely = new ClosedFrequentGelyOptimized(D, X, minSupport); //E is restricted to items in E
            ArrayList<Itemset> newCloseds = gely.gely();
            for (Itemset newClosed : newCloseds) {
                if (closedItemsets.contains(newClosed)) {
                    int index = this.closedItemsets.indexOf(newClosed);
                    closedItemsets.get(index).support++;
                } else {
                    closedItemsets.add(newClosed);
                }
            }
        }
        // System.out.println("Final count after addition !: " + closedItemsets.size());
    }

    public boolean closureCheckDeletion(ArrayList<Itemset> stillClosed, Itemset Y) {

        if (stillClosed.size() == 0) {
            return true;
        }

        Itemset intersection = new Itemset(stillClosed.get(0));

        boolean foundU = false;
        for (Itemset u : stillClosed) {
            if (u.containsAll(Y)) {
                intersection.retainAll(u);
                foundU = true;
            }

        }

        if (!foundU) {
            return true;
        }
        return intersection.equals(Y);
    }

    public void deletion() {
        Set X = D.remove(0);
        gely = new ClosedFrequentGelyOptimized(D, E, minSupport);
        ArrayList<Itemset> notMoreClosed = new ArrayList<>();
        if (this.hasNoneZeroSupport(X)) {
            for (Itemset Y : closedItemsets) {
                if (X.containsAll(Y)) {
                    Y.support--;
                    if (Y.support < minSupport) {
                        notMoreClosed.add(Y);
                    }
                }
            }
        } else {
            Collections.sort(closedItemsets, new Comparator<Itemset>() {
                @Override
                public int compare(Itemset o1, Itemset o2) {
                    return o2.size() - o1.size();
                }
            }); //sort in descending size
            ArrayList<Itemset> stillClosed = new ArrayList<>();

            for (Itemset Y: closedItemsets) {
                if (!X.containsAll(Y)) {
                    continue;
                }
                if (transactionExistsInDatabase(Y)) {
                    Y.support--;
                    if (Y.support < minSupport) {
                        notMoreClosed.add(Y);
                    }
                *//*} else if (closureCheckDeletion(stillClosed, Y)) {*//*
                } else if (gely.closure(Y).equals(Y)) {

                    Y.support--;
                    if (Y.support < minSupport) {
                        notMoreClosed.add(Y);
                    } else {
                        stillClosed.add(Y);
                    }
                } else {
                    notMoreClosed.add(Y);
                }
            }


        }
        for (Itemset Y: notMoreClosed) {
            closedItemsets.remove(Y);
        }
    }

    public void deletion(Itemset X) {
        Collections.swap(D, 0, D.indexOf(X));
        deletion();
    }

    public void slidingWindowStep(Itemset X) {
        addition(X);
        deletion();
    }*/

}
