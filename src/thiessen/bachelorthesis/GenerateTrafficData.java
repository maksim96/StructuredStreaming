package thiessen.bachelorthesis;

import com.Ostermiller.util.CSVParser;
import thiessen.bachelorthesis.closedstreaming.StreamGelyConnected;
import thiessen.bachelorthesis.graph.Graph;
import thiessen.bachelorthesis.itemsetmining.ConnectedGely;
import thiessen.bachelorthesis.itemsetmining.Gely;
import thiessen.bachelorthesis.itemsetmining.Transaction;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Maximilian Thiessen on 12.11.2017.
 * Generates some random traffic data of the roadNet Graph.
 * Produces "transactionsCount" many random paths (transactions),
 * where at least "repeatStartingNode" many always start at the same node.
 * Can launched with only two or all 6 command line arguments
 */
public class GenerateTrafficData {

    public static void main(String[] args) {
        String inputGraphfile = "data/roadNet-CA.txt";
        String outputTransationsFile = "data/newtrafficjams.txt";
        int transactionsCount = 1000000;
        int repeatStartingNode = 10;
        int averageLengthOfTransaction = 10;
        int standardDeviationOfLength = 10;
        if (args.length == 2) {
            transactionsCount = Integer.parseInt(args[0]);
            averageLengthOfTransaction = Integer.parseInt(args[1]);
        } else if (args.length == 6) {
            inputGraphfile = args[0];
            outputTransationsFile = args[1];
            transactionsCount =  Integer.parseInt(args[2]);
            repeatStartingNode =  Integer.parseInt(args[3]);
            averageLengthOfTransaction = Integer.parseInt(args[4]);
            standardDeviationOfLength =  Integer.parseInt(args[5]);
        }

        String[][] data;
        try {
            CSVParser csvParser = new CSVParser(new FileReader(inputGraphfile), '\t');
            csvParser.setCommentStart("#");
            data = csvParser.getAllValues();
            Set<Integer> E = new HashSet<>();


            for (String[] line : data) {
                for (String s : line) {
                    E.add(Integer.parseInt(s));
                }
            }

            Graph g = new Graph(Collections.max(E) + 2);
            for (int i = 0; i < data.length; i++) {
                g.get(Integer.parseInt(data[i][0])).add(Integer.parseInt(data[i][1]));
            }

            System.out.println("Parsing graph done!");

            FileWriter trafficJams = new FileWriter(outputTransationsFile);

            for (int i = 0; i < transactionsCount/repeatStartingNode; i++) {
                Random random = new Random();
                int start  = ThreadLocalRandom.current().nextInt(0, g.size());
                for (int j = 0; j < repeatStartingNode; j++) {

                    int length = Math.max(2,(int) random.nextGaussian()*standardDeviationOfLength + averageLengthOfTransaction);

                    Set<Integer> randomWalk = g.randomWalk(start, length);
                    StringBuilder sb = new StringBuilder();
                    for (int node : randomWalk) {
                        sb.append(node + " ");
                    }
                    trafficJams.write(sb.toString().trim() + "\n");
                }
                System.out.println(i);

            }
            trafficJams.close();

            System.out.println("Generating traffic jam transaction done!");







        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
