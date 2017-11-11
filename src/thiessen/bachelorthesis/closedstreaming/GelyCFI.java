package thiessen.bachelorthesis.closedstreaming;

import thiessen.bachelorthesis.itemsetmining.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Maximilian on 04.10.2017.
 */
public class GelyCFI {

    private FrequentGely gely;

    public ArrayList<Itemset> closedItemsets = new ArrayList<>();

    private int minSupport = 0;

    //Items must be positive integers (0,1,2,3,...)
    protected Set<Integer> E;
    public ArrayList<Transaction> D;

    public GelyCFI(ArrayList<Transaction> d, Set<Integer> e) {
        E = e;
        D = d;
    }
    public GelyCFI( ArrayList<Transaction> d, Set<Integer> e, int minSupport) {
        E = e;
        D = d;
        this.minSupport = minSupport;
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
        gely = new ClosedFrequentGely(D, E, minSupport);
        closedItemsets = gely.gely();
    }

    public void addition(Transaction X)  {


        gely = new ClosedFrequentGely(D, E, minSupport);

        if (!gely.inF(X)) {
            //partition blabla
        }

        Itemset itemsetX = new Itemset(X);
        //closedItemsets.contains ist hier furchtbar scheiße (linear. sollte aber in konstanter Zeit gehen!!!
        //auf hashmap umsteigen oder so
        if (hasNoneZeroSupport(X) && closedItemsets.contains(X) && gely.inF(X)) {
           //System.out.println("            juhu");
            if (!closedItemsets.contains(X)) {
                closedItemsets.add(itemsetX);
            }

            D.add(X);
            for(Itemset itemset : closedItemsets) {
               if (X.containsAll(itemset)) {
                   itemset.support++;
               }
           }
        } else {
           // System.out.println("            ohhhhhhhh");
            D.add(X);
           // System.out.println("Next line from gely subcall in addition");
            gely = new ClosedFrequentGely(D, new HashSet<>(X), minSupport); //E is restricted to items in X
            ArrayList<Itemset> newCloseds = gely.gely();
            for (Itemset newClosed : newCloseds) {
                if (!X.containsAll(newClosed)) {
                    continue;
                }
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
        gely = new ClosedFrequentGely(D, E, minSupport);
        ArrayList<Itemset> notMoreClosed = new ArrayList<>();
        if (this.transactionExistsInDatabase(X)) {
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
                /*} else if (closureCheckDeletion(stillClosed, Y)) {*/
                } else if (Y.equals(gely.closure(Y, new HashSet<>()))) {

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

    public void slidingWindowStep(Transaction X) {
        addition(X);
        deletion();
    }

}
