package thiessen.bachelorthesis.closedstreaming;

import thiessen.bachelorthesis.graph.Graph;
import thiessen.bachelorthesis.itemsetmining.*;

import java.util.*;

/**
 * Created by Maximilian on 04.10.2017.
 */
public class GelyCFIConnected {

    private FrequentGely gely;

    public ArrayList<Itemset> closedItemsets = new ArrayList<>();

    private int minSupport = 0;

    //Items must be positive integers (1,2,3,...)
    protected Set<Integer> E;

    protected int[] itemSupport;

    public ArrayList<Transaction> D;

    protected Graph graph;
    HashMap<Long, Integer> combinedTransactions = new HashMap<>();


    public GelyCFIConnected( ArrayList<Transaction> d, Set<Integer> e, Graph graph) {
        E = e;
        D = d;
        this.graph = graph;
        this.itemSupport = new int[Collections.max(E)+2];
        initItemSupport();
    }
    public GelyCFIConnected( ArrayList<Transaction> d, Set<Integer> e, Graph graph, int minSupport) {
        E = e;
        D = d;
        this.graph = graph;
        this.minSupport = minSupport;
        this.itemSupport = new int[Collections.max(E)+2];
        initItemSupport();
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
        gely = new ConnectedGely(D, getFrequentItems(E),graph, minSupport);
        closedItemsets = gely.gely(true);
    }

    private void initItemSupport() {

        for (Transaction d : D) {
            for (int e : d) {
                itemSupport[e]++;
            }
        }
    }

    private Set<Integer> getFrequentItems(Set<Integer> someSet) {
        Set<Integer> prunedE = new HashSet<>(someSet);

        for (int e: someSet) {
            if (itemSupport[e] < minSupport) {
                prunedE.remove(e);
            }
        }

        return prunedE;
    }

    private void addItemSupport(Transaction X) {
        for (int e : X) {
            itemSupport[e]++;
        }
    }

    private void minusItemSupport(Transaction X) {
        for (int e : X) {
            itemSupport[e]--;
        }
    }

    public void addition(Transaction X)  {
        addItemSupport(X);
        if (!gely.inF(X)) {
            ArrayList<Long> combinedTransaction = new ArrayList<>();
            Itemset alreadySeen = new Itemset();
            for (int x : X) {
                if (alreadySeen.contains(x)) {
                    continue;
                }
                ArrayList<Transaction> smallD = new ArrayList<>(2);
                smallD.add(new Transaction());
                smallD.add(X);
                gely = new ConnectedGely(smallD, X, graph);
                Itemset singleton = new Itemset();
                singleton.add(x);
                Transaction xi = new Transaction(gely.closure(singleton, new HashSet<>()));
                alreadySeen.addAll(xi);
                addition(xi);
                combinedTransaction.add(xi.getId());
            }
            if (combinedTransaction.size() > 1) {
                combinedTransactions.put(combinedTransaction.get(0), combinedTransaction.size());

            }
            return;
        }

        gely = new ConnectedGely(D, E, graph, minSupport-1);

        Itemset itemsetX = new Itemset(X);
        //transactionExistsInDatabase(X) && this.closedItemsets.contains(itemsetX) should be way faster!
        if (hasNoneZeroSupport(X) && itemsetX.equals(gely.closure(itemsetX, new HashSet<>())) && gely.isFrequent(itemsetX, true)) {
      //  if (hasNoneZeroSupport(X) && this.closedItemsets.contains(itemsetX)) {
            D.add(X);
            // System.out.println("Next line from gely subcall in addition");
            gely = new ConnectedGely(D, E, this.graph, minSupport);
            if (!closedItemsets.contains(itemsetX)) {
                closedItemsets.add(itemsetX);
            }

            for(Itemset itemset : closedItemsets) {
                if (X.containsAll(itemset)) {
                    itemset.support++;
                }
            }
        } else {
            D.add(X);
            // System.out.println("Next line from gely subcall in addition");
            gely = new ConnectedGely(D, getFrequentItems(X), this.graph, minSupport); //E is restricted to items in X
            ArrayList<Itemset> newCloseds = gely.gely(true);
            for (Itemset newClosed : newCloseds) {
                if (!closedItemsets.contains(newClosed)) {
                    closedItemsets.add(newClosed);
                } else {
                    int index = this.closedItemsets.indexOf(newClosed);
                   // if (!(newClosed.size() == X.size())) {
                        closedItemsets.get(index).support++;
                    //}

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
        deletion(true);
    }

    public void deletion(boolean firstOfCombined) {
        if (firstOfCombined) {
            Transaction toDeleted = D.get(0);
            if (combinedTransactions.containsKey(toDeleted.getId())) {
                int combinedSize = combinedTransactions.get(toDeleted.getId());
                combinedTransactions.remove(toDeleted);
                for (int i = 0; i < combinedSize; i++) {
                    deletion(false);
                }
                return;
            }

        }

        Transaction X = D.remove(0);
        minusItemSupport(X);
        gely = new ConnectedGely(D, E, this.graph, minSupport);
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
                } else if (Y.size() == gely.closure(Y, new HashSet<>()).size()) {

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
