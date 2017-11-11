package thiessen.bachelorthesis.itemsetmining;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Maximilian on 03.10.2017.
 */
public class ClosedFrequentGelyOptimized /*extends thiessen.bachelorthesis.itemsetmining.Gely*/ {

/*
    protected int minSupport = 0;
    private Itemset toRemoveSupportSet = new Itemset();
    private Itemset currentSupportSet = new Itemset();

    @Override
    public boolean inF(Set itemset, int newItem) {
        int supportCount = 0;

        if(currentSupportSet.isEmpty()) {
            return inF(itemset);
        } else {
            for (int j : currentSupportSet) {
                Set transaction = D.get(j);
                if (transaction.contains(newItem)) {
                    supportCount++;
                    if (supportCount >= minSupport) {
                        return true;
                    }
                }
            }
        }


        return supportCount >= minSupport;
    }

    @Override
    public boolean inF(Set itemset) {
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

    public ClosedFrequentGelyOptimized(ArrayList<HashSet<Integer>> d, Set<Integer> e) {
        super(d, e);
        sortedE = new ArrayList<>(E);

        Collections.sort(sortedE, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return calcSupport(o1) - calcSupport(o2);
            }
        });

    }

    @Override
    public void resetSupport(Itemset ancestorSupportSet) {
        currentSupportSet.clear();
        currentSupportSet.addAll(ancestorSupportSet);
    }

    @Override
    public void setCurrentSupport() {
        ancestorSupport.clear();
        ancestorSupport.addAll(currentSupportSet);
        currentSupportSet.removeAll(toRemoveSupportSet);
    }

    public ClosedFrequentGelyOptimized(ArrayList<HashSet<Integer>> d, Set<Integer> e, int minSupport) {
        this(d, e);
        this.minSupport = minSupport;
    }

    @Override
    public Itemset closure(Itemset itemset, int newItem) {
        Itemset closure = null;
        boolean firstTime = true;
        Set<Integer> newToRemoveSupport = new HashSet<>();
        if (currentSupportSet.isEmpty()) {
            currentSupportSet.addAll(IntStream.range(0,D.size()).boxed().collect(Collectors.toList()));
            //  System.out.println("hupps");
        }

        Itemset newItems = new Itemset();

        for (int i : currentSupportSet) {

            Set transaction = D.get(i);

            if (transaction.contains(newItem)) {
                if (firstTime) {
                    closure =  new Itemset(itemset);
                    newItems = new Itemset(transaction);
                    newItems.removeAll(itemset);
                    firstTime = false;
                } else {
                    newItems.retainAll(transaction);
                }
                closure.support++;
            } else {
                newToRemoveSupport.add(i);
            }

        }
        toRemoveSupportSet.clear();
        toRemoveSupportSet.addAll(newToRemoveSupport);

        if (closure == null) {
            return null;
        }
        closure.addAll(newItems);

        return closure;
    }

    @Override
    public Itemset closure(Itemset itemset) {
        Itemset closure = null;
        boolean firstTime = true;

        for (Set transaction : D) {

            if (transaction.containsAll(itemset)) {
                if (firstTime) {
                    closure =  new Itemset(transaction);
                    firstTime = false;
                } else {
                    closure.retainAll(transaction);
                }

            }

        }

        return closure;
    }

    private int calcSupport(int e) {
        int support = 0;
        for (Set d : D) {
            if (d.contains(e)) {
                support++;
            }
        }

        return support;
    }

    private ArrayList<Integer> sortedE;

    @Override
    protected void list(Itemset C, Set<Integer> B) {
        *//*int e = augment(C, B);
        if (e == NOT_FOUND_AUGMENTATION) {
            return;
        }*//*

        Itemset realAncestorSupport = new Itemset(ancestorSupport);

        Set<Integer> BTemp = new HashSet<>(B);



        for (int e : sortedE) {
            Itemset union = new Itemset(C);
            union.addAll(B);
            if (union.contains(e)) {
                continue;
            }
            Itemset newC = new Itemset(C);
            newC.add(e);
            if (!inF(newC, e)) {
                B.add(e);
                continue;
            }

            newC = closure(newC, e);
            if (newC != null) {
                Itemset intersection = new Itemset(newC);
                intersection.retainAll(B);
                if (intersection.isEmpty()) {
                    closedItemsets.add(newC);
                    if (closedItemsets.size() % 100 == 0) {
                        //  System.out.println(closedItemsets.size() + ": " + newC);
                    }
                    setCurrentSupport();
                    list(newC, B);
                }
            }

            B.add(e);


        }

        B.clear();
        B.addAll(BTemp);
        resetSupport(realAncestorSupport);
    }*/
}
