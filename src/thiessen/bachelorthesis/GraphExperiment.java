package thiessen.bachelorthesis;

import com.Ostermiller.util.CSVParser;
import thiessen.bachelorthesis.closedstreaming.StreamGely;
import thiessen.bachelorthesis.closedstreaming.StreamGelyConnected;
import thiessen.bachelorthesis.graph.Graph;
import thiessen.bachelorthesis.itemsetmining.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GraphExperiment {

    public static void main(String[] args) {
        String graphFile = "data/roadNet-CA.txt";
        String transactionFile = "data/newtrafficjams.txt";
        int updateSteps = 100000;
        int[] slidingWindowSizes = {1000, 2000, 5000, 10000, 20000, 50000, 100000};

        if (args.length > 3) {
            graphFile = args[0];
            transactionFile = args[1];
            updateSteps = Integer.parseInt(args[2]);
            slidingWindowSizes = new int[args.length-3];
            for (int i = 3; i < args.length; i++) {
                slidingWindowSizes[i-3] = Integer.parseInt(args[i]);
            }
        }


        graphGelyRoadNetworkTest(graphFile, transactionFile, updateSteps, slidingWindowSizes);
    }

    /**
     *
     * @param D
     * @param start included
     * @param end included
     * @return returns D[start, end]
     */
    public static ArrayList<Transaction> getSubD(ArrayList<Transaction> D, int start, int end) {
        ArrayList<Transaction> subD = new ArrayList<>(end - start + 1);
        for (int i = start; i <= end; i++) {
            subD.add( D.get(i));
        }
        return subD;
    }

    private static void graphGelyRoadNetworkTest(String graphFile, String transactionFile, int updateSteps, int[] slidingWindowSizes) {
        String[][] data;
        try {
            CSVParser csvParser = new CSVParser(new FileReader(graphFile), '\t');
            csvParser.setCommentStart("#");
            data = csvParser.getAllValues();
            Set<Integer> E = new HashSet<>();
            ArrayList<Transaction> D = new ArrayList<>(data.length);


            for (String[] line : data) {
                for (String s : line) {
                    E.add(Integer.parseInt(s));
                }
            }



            Graph g = new Graph(Collections.max(E) + 2);
            //System.out.println(Collections.max(E) + 2);
            for (int i = 0; i < data.length; i++) {
                g.get(Integer.parseInt(data[i][0])).add(Integer.parseInt(data[i][1]));
            }

            System.out.println("Parsing graph done!");

            data = CSVParser.parse(new FileReader(transactionFile), ' ');

            for (int i = 0; i < 1000000; i++) {
                D.add(new Transaction());
                for (int j = 0; j < data[i].length; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    if (!E.contains(x)) {
                        E.add(x);
                    }
                    D.get(i).add(x);

                }
            }

            System.out.println("reading in transactions done");
            for (int z : slidingWindowSizes) {
                int minSupport = 12;

                StreamGelyConnected stream = new StreamGelyConnected(getSubD(D, 0, z), new HashSet<>(E), g, minSupport);
                long millisecGCFI = System.currentTimeMillis();
                stream.explore();
                millisecGCFI = System.currentTimeMillis() - millisecGCFI;
                System.out.println("First explore in " + millisecGCFI + "ms. StreamGely Found: "
                        + stream.getClosedItemsets().size() + " closed frequent Itemsets");
                long totalMillisec = 0;
                for (int i = 0; i < updateSteps; i++) {


                    millisecGCFI = System.currentTimeMillis();
                    stream.slidingWindowStep(D.get(z+1+i));
                    millisecGCFI = System.currentTimeMillis() - millisecGCFI;
                    totalMillisec+= millisecGCFI;


                }
                System.out.println("Sliding window size: " + z + ". Total runtime " + totalMillisec + "ms");
                System.out.println("================================================");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
