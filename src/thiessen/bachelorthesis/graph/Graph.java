package thiessen.bachelorthesis.graph;

import thiessen.bachelorthesis.itemsetmining.Itemset;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Maximilian on 06.10.2017.
 */
public class Graph extends ArrayList<ArrayList<Integer>> {

    public Graph(int initialCapacity) {
        super(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            this.add(new ArrayList<>());
        }
    }

    public Graph() {
    }

    public Graph(Collection<? extends ArrayList<Integer>> c) {
        super(c);
    }

    public Set<Integer> connectedComponent(int x) {
        Set<Integer> connectedComponent = new HashSet<>();

        connectedComponent.add(x);

        boolean[] marked = new boolean[this.size()];

        marked[x] = true;

        DFS(x, connectedComponent, marked);

        return connectedComponent;

    }

    public Set<Integer> connectedComponentConstrained(Set<Integer> itemset) {
        int x = itemset.iterator().next();

        Set<Integer> connectedComponent = new HashSet<>();

        connectedComponent.add(x);

        boolean[] marked = new boolean[this.size()];

        marked[x] = true;

        boolean[] constraint = new boolean[this.size()];

        for (int item : itemset) {
            constraint[item] = true;
        }

        DFSConstrained(x, connectedComponent, marked, constraint);

        return connectedComponent;

    }

    private void DFS(int x, Set<Integer> connectedComponent, boolean[] marked) {
        for (int neighbour : this.get(x)) {
            if (!marked[neighbour]) {
                connectedComponent.add(neighbour);
                marked[neighbour] = true;
                DFS(neighbour, connectedComponent, marked);
            }

        }
    }

    private void DFSConstrained(int x, Set<Integer> connectedComponent, boolean[] marked, boolean[] constraint) {
        for (int neighbour : this.get(x)) {
            if (!marked[neighbour] && constraint[neighbour]) {
                connectedComponent.add(neighbour);
                marked[neighbour] = true;
                DFSConstrained(neighbour, connectedComponent, marked, constraint);
            }

        }
    }

    public boolean areConnected(int x, int y) {
        return connectedComponent(x).contains(y);
    }


    public boolean areConnected(Set itemset) {
        Set<Integer> connectedComponentConstrained = connectedComponentConstrained(itemset);

        return connectedComponentConstrained.containsAll(itemset);


    }

    public boolean isNeighbour(Set<Integer> itemset, int newItem) {
        for (int item : itemset) {
            for (int neighbour : this.get(item)) {
                if (neighbour == newItem) {
                    return true;
                }
            }
        }

        return false;
    }

    public Set<Integer> randomWalk(int start, int length) {
        Set<Integer> walk = new HashSet<>(length);

        walk.add(start);

        int currentNode = start;

        for (int i = 0; i < length; i++) {
            int neighbourCount = this.get(start).size();
            if (neighbourCount == 1 && walk.contains(this.get(start).get(0))) {
                System.err.println("Sackgasse!");
                return walk;
            }
            int randomNeighbour;
            Set<Integer> alreadySeen = new HashSet<>();
            do {
                if (get(currentNode).size() == 0) {
                    return walk;
                }
                randomNeighbour = ThreadLocalRandom.current().nextInt(0, get(currentNode).size());
                alreadySeen.add(this.get(currentNode).get(randomNeighbour));
                if (alreadySeen.size() == get(currentNode).size() && walk.contains(this.get(currentNode).get(randomNeighbour))) {
                 //   System.err.println("Error");
                    break;
                }
            } while (walk.contains(this.get(currentNode).get(randomNeighbour)));
            currentNode = this.get(currentNode).get(randomNeighbour);
            walk.add(currentNode);

        }
        //just a test
      /*  if (!this.areConnected(walk)) {
            System.err.println("" + walk + " is not connected!!!");

        }*/
        return walk;
    }


}
