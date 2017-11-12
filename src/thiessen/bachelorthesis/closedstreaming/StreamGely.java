package thiessen.bachelorthesis.closedstreaming;

import thiessen.bachelorthesis.itemsetmining.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Maximilian Thiessen on 04.10.2017.
 * Basic implementation of the StreamGely algorithm.
 * Uses any FrequentGely instance.
 */
public class StreamGely {

    protected FrequentGely gely;

    protected ArrayList<Itemset> closedItemsets = new ArrayList<>();

    //StreamGely has its own set of parameters minSupport E, D, since it uses Gely with different options
    protected int minSupport = 1;

    //Items must be positive integers (0,1,2,3,...)
    protected Set<Integer> E;
    protected ArrayList<Transaction> D;

    //holds the support of each singleton item. has to be calculated once
    //is efficiently updatet during sliding window update
    protected int[] itemSupport;

    //store for each decomposed transaction the number of the new transactions
    //this is needed to delete all the decomposed transaction, which where added as one, together
    protected HashMap<Long, Integer> combinedTransactions = new HashMap<>();

    public StreamGely(ArrayList<Transaction> d, Set<Integer> e, FrequentGely gely) {
        E = e;
        D = d;
        this.gely = gely;
        this.itemSupport = new int[Collections.max(E)+2];
        initItemSupport();
    }
    public StreamGely(ArrayList<Transaction> d, Set<Integer> e, FrequentGely gely, int minSupport) {
        E = e;
        D = d;
        this.gely = gely;
        this.minSupport = minSupport;
        this.itemSupport = new int[Collections.max(E)+2];
        initItemSupport();
    }

    //calculate once the support of each singleton item
    protected void initItemSupport() {
        for (Transaction d : D) {
            for (int e : d) {
                itemSupport[e]++;
            }
        }
    }

    //just increase the support of each singleton item when a new transaction arises
    protected void addItemSupport(Transaction X) {
        for (int e : X) {
            itemSupport[e]++;
        }
    }

    //decrease
    protected void minusItemSupport(Transaction X) {
        for (int e : X) {
            itemSupport[e]--;
        }
    }

    //|D(X)| > 0?
    protected boolean hasNoneZeroSupport(Set<Integer> X) {
        for (Set<Integer> transaction : D) {
            if (transaction.containsAll(X)) {
                return true;
            }

        }
        return false;
    }

    //X \in D(X)?
    protected boolean transactionExistsInDatabase(Set<Integer> X) {
        for (Set<Integer> transaction : D) {
            if (transaction.equals(X)) {
                return true;
            }

        }
        return false;
    }

    //return the currently frequent items
    protected Set<Integer> getFrequentItems(Set<Integer> someSet) {
        Set<Integer> prunedE = new HashSet<>(someSet);
        for (int e: someSet) {
            if (itemSupport[e] < minSupport) {
                prunedE.remove(e);
            }
        }
        return prunedE;
    }

    //set gely to the regular parameters
    protected void setUpGely() {
        gely.setD(D);
        gely.setE(getFrequentItems(E));
        gely.setMinSupport(minSupport);
    }

    //set gely to some fixed parameters. usefull for the special cases in addition
    protected void setUpGely(ArrayList<Transaction> transactionSet, Set<Integer> items, int minSupport) {
        gely.setD(transactionSet);
        gely.setE(items);
        gely.setMinSupport(minSupport);
    }

    public void explore() {
       setUpGely();
        //gely = new ClosedFrequentGely(D, getFrequentItems(E), minSupport);
        closedItemsets = gely.gely(true);
    }

    public void addition(Transaction X)  {
     //   setUpGely();
        if (!gely.inF(X)) {
            ArrayList<Long> combinedTransaction = new ArrayList<>();
            Set<Integer> alreadySeen = new HashSet();
            ArrayList<Transaction> smallD = new ArrayList<>(2);
            smallD.add(new Transaction());
            smallD.add(X);
            setUpGely(smallD, X, 1);

            for (int x : X) {
                if (alreadySeen.contains(x)) { //two different items could be in the same closure!
                    continue;
                }
                //gely = new ConnectedGely(smallD, X, graph);
                Itemset singleton = new Itemset();
                singleton.add(x);
                Transaction xi = new Transaction(gely.closure(singleton, new HashSet<>()));
                alreadySeen.addAll(xi);
                addition(xi); //add each xi as single transation. they will be definitely in F
                combinedTransaction.add(xi.getId());
            }
            if (combinedTransaction.size() > 1) { //if the X was only decomposed in one new transaction, this is not neccessary
                combinedTransactions.put(combinedTransaction.get(0), combinedTransaction.size());
            }
            return;
        }
        //efficient update of the supports of all singleton items
        addItemSupport(X);
        Itemset itemsetX = new Itemset(X);
        setUpGely(D, E, minSupport-1);
        //condition for fast update
        if (hasNoneZeroSupport(X) && itemsetX.equals(gely.closure(itemsetX, new HashSet<>())) && gely.isFrequent(itemsetX, true)) {
            // setUpGely(D, E, minSupport);
            if (!closedItemsets.contains(X)) {
                //X support was calculated during isFrequent(..,true)
                closedItemsets.add(itemsetX);
            }

            D.add(X);
            //update support of all closed Itemsets which are subsets of X
            for(Itemset itemset : closedItemsets) {
               if (X.containsAll(itemset)) {
                   itemset.support++;
               }
           }
        } else { //update over gely subcall
            D.add(X);
           // gely = new ClosedFrequentGely(D, new HashSet<>(X), minSupport); //E is restricted to items in X
            setUpGely(D, getFrequentItems(new HashSet<>(X)), minSupport); //gely subcall but search only in F_X

            ArrayList<Itemset> newCloseds = gely.gely(true);
            for (Itemset newClosed : newCloseds) {
                if (!closedItemsets.contains(newClosed)) {
                    closedItemsets.add(newClosed);
                } else {
                    int index = this.closedItemsets.indexOf(newClosed); //get closed itemsets which equals newClosed
                    closedItemsets.get(index).support++; //update its support
                }
            }
        }
    }

    public void deletion() {
        deletion(true); //start the deletion. the next few transactions could belong together, so they should all be deleted
    }

    public void deletion(boolean firstOfCombined) {
        if (firstOfCombined) { //if its the first of a decomposed sequence, delete iteratively the others too
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
        minusItemSupport(X); //efficiently update support for all singleton items
        //gely = new ClosedFrequentGely(D, E, minSupport);
        //setUpGely();
        ArrayList<Itemset> notMoreClosed = new ArrayList<>();
        if (this.transactionExistsInDatabase(X)) { //fast update
            for (Itemset Y : closedItemsets) {
                if (X.containsAll(Y)) {
                    Y.support--;
                    if (Y.support < minSupport) { //if support falls under minSupport, delete it
                        notMoreClosed.add(Y);
                    }
                }
            }
        } else {
            ArrayList<Itemset> stillClosed = new ArrayList<>();

            for (Itemset Y: closedItemsets) {
                setUpGely(D, E, minSupport);
                if (!X.containsAll(Y)) { //only iterate over subsets of X
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

    //one can remove arbitrary transactions from the sliding window
    public void deletion(Transaction X) {
        Collections.swap(D, 0, D.indexOf(X));
        deletion();
    }

    //adds X to the front. deletes oldest transaction
    public void slidingWindowStep(Transaction X) {
        addition(X);
        deletion();
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

    public int getMinSupport() {
        return minSupport;
    }

    public void setMinSupport(int minSupport) {
        this.minSupport = minSupport;
    }

    public ArrayList<Itemset> getClosedItemsets() {
        return closedItemsets;
    }

    public void setClosedItemsets(ArrayList<Itemset> closedItemsets) {
        this.closedItemsets = closedItemsets;
    }

    public FrequentGely getGely() {
        return gely;
    }

    public void setGely(FrequentGely gely) {
        this.gely = gely;
    }

}
