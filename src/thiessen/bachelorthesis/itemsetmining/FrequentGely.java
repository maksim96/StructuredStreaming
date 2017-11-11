package thiessen.bachelorthesis.itemsetmining;

import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;

import java.util.*;

/**
 * Created by Maximilian on 08.10.2017.
 * Adds a frequency constraint to Gely algorithm.
 * One can not put the frequency constraint on F, because it is not guaranteed that it stays confluent.
 * And we need the confluence of F for the decomposition
 * However one can still check the frequency and prune search, whenever the support falls below the minSupport.
 */
public abstract class FrequentGely extends Gely {

    protected int minSupport = 0;

    public FrequentGely(ArrayList<Transaction> d, Set<Integer> e) {
        super(d, e);
    }

    public FrequentGely(ArrayList<Transaction> d, Set<Integer> e, int minSupport) {
        this(d,e);
        this.minSupport = minSupport;
    }

    public boolean isFrequent(Itemset itemset) {
        int supportCount = 0;
        for (Set transaction : D) {
            if (transaction.containsAll(itemset)) {
                supportCount++;
                if (supportCount >= minSupport) {
                    return true;
                }
            }
        }

        return supportCount >= minSupport;
    }

    public boolean isFrequent(Itemset itemset, boolean calcSupport) {
        if (!calcSupport) {
            return isFrequent(itemset);
        } else {
            int supportCount = 0;
            for (Set transaction : D) {
                if (transaction.containsAll(itemset)) {
                    supportCount++;
                }
            }
            itemset.support = supportCount;
            return supportCount >= minSupport;
        }
    }

    public boolean isFrequent(Itemset itemset, int newItem, Set<Integer> currentSupportSet) {
        int supportCount = 0;
        for (int j : currentSupportSet) {
            Set transaction = D.get(j);
            if (transaction.contains(newItem)) {
                supportCount++;
                if (supportCount >= minSupport) {
                    return true;
                }
            }

        }
        return supportCount >= minSupport;
    }

    public boolean isFrequent(Itemset itemset, ArrayList<Integer> currentSupportSet) {
        int supportCount = 0;
        for (int j : currentSupportSet) {
            Set transaction = D.get(j);
            if (transaction.containsAll(itemset)) {
                supportCount++;
                if (supportCount >= minSupport) {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean isFrequent(int item) {
        int supportCount = 0;
        for (Set transaction : D) {
            if (transaction.contains(item)) {
                supportCount++;
                if (supportCount >= minSupport) {
                    return true;
                }
            }

        }
        return false;
    }

    private void removeInfrequentItems(Set<Integer> E) {
        //good idea maybe: merke dir für alle items die transaktionen, die dieses item enthalten
        //könnte sein, dass das ziemlich den support berechnungsprozess beschleunigen könnte
        Set<Integer> frequentItems = new HashSet<>();

       // int[] itemCount = new int[Collections.max(E)+2];

        HashMap<Integer, Integer> itemCount = new HashMap<>(E.size());

        for (Transaction d : D) {

           // System.out.println(d.getId() + " : " + D.size());
            for (int e : d) {
                if (!itemCount.containsKey(e)) {
                    itemCount.put(e, 0);
                }
                itemCount.replace(e, itemCount.get(e)+ 1);
                if (itemCount.get(e) >= minSupport) {
                    frequentItems.add(e);
                }
            }
        }

        E.retainAll(frequentItems);
    }

    @Override
    public ArrayList<Itemset> gely() {
        closedItemsets = new ArrayList<>(D.size()); //init with no. transactions

        Set<Integer> prunedE = new HashSet<>(E);

        removeInfrequentItems(prunedE);

        E = prunedE;

        list(new Itemset(), new HashSet<>(), null);
        // System.out.println("Final count!: " + closedItemsets.size());
        return closedItemsets;
    }

    public ArrayList<Itemset> gely(boolean notPrune) {
        if (!notPrune) {
            return gely();
        }
        closedItemsets = new ArrayList<>(D.size()); //init with no. transactions

        list(new Itemset(), new HashSet<>(), null);
        // System.out.println("Final count!: " + closedItemsets.size());
        return closedItemsets;
    }

    @Override
    protected void list(Itemset C, Set<Integer> B, Set<Integer> parentSupportSet) {
        Set<Integer> currentSupportSet = new HashSet<>();

        Set<Integer> BTemp = new HashSet<>(B);
        for (int e : E) {
           // System.out.println(B);
            currentSupportSet.clear();
            Itemset union = new Itemset(C);
            union.addAll(B);
            if (union.contains(e) || !inF(C, e)) {
                continue;
            }
            Itemset newC = new Itemset(C);
            newC.add(e);

            if (parentSupportSet == null ||parentSupportSet.isEmpty()) {
                if (!isFrequent(newC)) {
                    B.add(e);
                    continue;
                }
            } else {
                if (!isFrequent(C, e, parentSupportSet)) {
                    B.add(e);
                    continue;
                }
            }


            if (parentSupportSet == null) {
                newC = closure(newC, currentSupportSet);
            } else {
                newC = closure(C, e, parentSupportSet, currentSupportSet);
            }

            if (newC != null) {
                Itemset intersection = new Itemset(newC);
                intersection.retainAll(B);
                if (intersection.isEmpty()) {
                   // System.out.println((closedItemsets.size() + 1) + ". " + newC);
                    closedItemsets.add(newC);
                    list(newC, B, currentSupportSet);
                }
            }

            B.add(e);


        }

        B.clear();
        B.addAll(BTemp);
    }
}
