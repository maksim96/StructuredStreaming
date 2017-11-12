package thiessen.bachelorthesis.itemsetmining;

import thiessen.bachelorthesis.graph.Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Maximilian Thiessen on 06.10.2017.
 * Concrete implementation of gely for the connected graphs with unique labels scenario.
 */
public class ConnectedGely extends FrequentGely {

    //Adjazenzliste des unterliegenden Graphen
    Graph graph = new Graph();

    @Override
    public boolean inF(Set itemset, int newItem) {
        if (itemset.isEmpty()) {
            return true;
        }
        return graph.isNeighbour(itemset, newItem);
    }

    @Override
    public boolean inF(Set itemset) {
        return  graph.areConnected(itemset);
    }

    @Override
    public Itemset closure(Itemset itemset, int e, Set<Integer> parentSupportSet, Set<Integer> currentSupportSet) {
        Itemset closure = null;
        boolean firstTime = true;

        Itemset newItems = new Itemset();

        int support = 0;
        for (int i : parentSupportSet) {

            Set transaction = D.get(i);

            if (transaction.contains(e)) {
                support++;
                if (firstTime) {
                    closure =  new Itemset(itemset);
                    newItems = new Itemset(transaction);
                    newItems.removeAll(itemset);
                    firstTime = false;
                } else {
                    newItems.retainAll(transaction);
                }
                currentSupportSet.add(i);
            }

        }

        if (closure == null) {
            return null;
        }
        closure.support = support;
        boolean done = false;

        closure.add(e);
        newItems.remove(e);
        while(!done){
            done = true;
            int toRemove = 0;
            for (int newItem : newItems) {
                if (inF(closure, newItem)) {
                    closure.add(newItem);
                    done = false;
                    toRemove = newItem;
                    break;
                }
            }
            if (toRemove != 0) {
                newItems.remove(toRemove);
            }

        }

        return closure;
    }

    @Override
    public Itemset closure(Itemset itemset, Set<Integer> currentSupportSet) {
        Itemset newItems = null;
        boolean firstTime = true;
        int support = 0;
        for (int i = 0; i < D.size(); i++) {
            Set transaction = D.get(i);
            if (transaction.containsAll(itemset)) {
                support++;
                if (firstTime) {
                    newItems =  new Itemset(transaction);
                    firstTime = false;
                } else {
                    newItems.retainAll(transaction);
                }
                currentSupportSet.add(i);
            }
        }

        Itemset closure = new Itemset(itemset, support);

        if (newItems == null) {
            return closure;
        }
        newItems.removeAll(itemset);



        boolean done = false;
        //is extremely inefficient
        while(!done){
            done = true;
            int toRemove = 0;
            for (int newItem : newItems) {
                if (inF(closure, newItem)) {
                    closure.add(newItem);
                    done = false;
                    toRemove = newItem;
                    break;
                }
            }
            if (toRemove != 0) {
                newItems.remove(toRemove);
            }

        }

        return closure;
    }
    public ConnectedGely(ArrayList<Transaction> d, Set<Integer> e, Graph graph) {
        super(d, e);
        this.graph = graph;
        this.minSupport = 0;
    }

    public ConnectedGely(ArrayList<Transaction> d, Set<Integer> e, Graph graph, int minSupport) {
        super(d, e);
        this.graph = graph;
        this.minSupport = minSupport;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}

