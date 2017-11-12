package thiessen.bachelorthesis.closedstreaming;

import thiessen.bachelorthesis.graph.Graph;
import thiessen.bachelorthesis.itemsetmining.*;

import java.util.*;

/**
 * Created by Maximilian Thiessen on 04.10.2017.
 * Special implementation for the connected case
 * Adds a graph, so inF can check whether the set of items is connected.
 */
public class StreamGelyConnected extends StreamGely {
    protected Graph graph;

    @Override
    protected void setUpGely() {
        super.setUpGely();
        ((ConnectedGely) gely).setGraph(this.graph);
    }

    @Override
    protected void setUpGely(ArrayList<Transaction> transactionSet, Set<Integer> items, int minSupport) {
        super.setUpGely(transactionSet, items, minSupport);
        ((ConnectedGely) gely).setGraph(this.graph);
    }

    public StreamGelyConnected(ArrayList<Transaction> d, Set<Integer> e, Graph graph) {
        super(d,e, new ConnectedGely(d, e, graph));
        this.graph = graph;
        this.itemSupport = new int[Collections.max(E)+2];
        initItemSupport();
    }
    public StreamGelyConnected(ArrayList<Transaction> d, Set<Integer> e, Graph graph, int minSupport) {
        super(d,e,new ConnectedGely(d, e, graph, minSupport), minSupport);
        this.graph = graph;
        this.minSupport = minSupport;
        this.itemSupport = new int[Collections.max(E)+2];
        initItemSupport();
    }


}
