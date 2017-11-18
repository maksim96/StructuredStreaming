package thiessen.bachelorthesis;

import com.Ostermiller.util.CSVParser;
import thiessen.bachelorthesis.closedstreaming.StreamGely;
import thiessen.bachelorthesis.itemsetmining.ClosedFrequentGely;
import thiessen.bachelorthesis.itemsetmining.Itemset;
import thiessen.bachelorthesis.itemsetmining.Transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maximilian Thiessen on 12.11.2017.
 */
public class GelyVSStreamGelyExperiment {
    public static void main(String[] args) {
        // write your code here

        String transactionsFile = "data/mushrooms.txt";
        int[] supports = {500, 400, 300, 200, 100, 50, 25, 10};
        int updateSteps = 100;
        int slidingWindowSize = 1000;

        if (args.length >= 4) {
            transactionsFile = args[0];
            updateSteps = Integer.parseInt(args[1]);
            slidingWindowSize = Integer.parseInt(args[2]);

            supports = new int[args.length - 3];
            for (int i = 3; i < args.length; i++) {
                supports[i-3] = Integer.parseInt(args[i]);
            }
        }

        for (int s : supports) {
            slidingWindowTest(transactionsFile, s, updateSteps, slidingWindowSize);
        }
    }

    public static void slidingWindowTest(String transactionFile, int support, int updateSteps, int slidingWindowSize) {
        try {
            System.out.println("===============================");
            System.out.println("Support is " + support);
            System.out.println("===============================");

            String[][] data = CSVParser.parse(new FileReader(transactionFile), ' ');

            Set<Integer> E = new HashSet<>();
            ArrayList<Transaction> D = new ArrayList<>(data.length);

            for (int i = 0; i < data.length; i++) {
                D.add(new Transaction());
                for (int j = 0; j < data[i].length; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    if (!E.contains(x)) {
                        E.add(x);
                    }
                    D.get(i).add(x);

                }
            }



            ArrayList<Transaction> subD;
            int lastSolution = -1;

            long millisecsGely = System.currentTimeMillis();

            for (int i = 0; i <= updateSteps; i++) {

                subD = getSubD(D, i, slidingWindowSize + i);
                ClosedFrequentGely closedItemsetMiner = new ClosedFrequentGely(subD, E, support);

                ArrayList<Itemset> closedItemsets = closedItemsetMiner.gely();

                lastSolution = closedItemsets.size();

               /* if (i > 190) {
                    System.out.println((i-startingPoint+1) + ". Gely done! Found: " + closedItemsets.size() + " in " + i + "-" + (i + slidingWindowSize));

                }*/
            }


            millisecsGely = System.currentTimeMillis() - millisecsGely;

            //System.out.println("==================================");
            //System.out.println("========Gely has finished=========");
            System.out.println("Gely took: " + millisecsGely / 1000 + "s total time ");
            System.out.println("Gely has : " + lastSolution + " closed Itemsets");
            //System.out.println("==================================");


            subD = getSubD(D, 0, slidingWindowSize);

            long millisecGCFI = System.currentTimeMillis();

            StreamGely gcfi = new StreamGely(subD, E, new ClosedFrequentGely(subD, E, support), support);

            gcfi.explore();

            lastSolution = -1;
            //System.out.println("Explore done!");
            //System.out.println("==================================");
            for (int i = 1; i <= updateSteps; i++) {
                gcfi.slidingWindowStep(D.get(slidingWindowSize + i));
                lastSolution = gcfi.getClosedItemsets().size();
                // System.out.println((i-startingPoint) + ". Slidingwindowstep done!");
                //System.out.println("==================================");
            }

            millisecGCFI = System.currentTimeMillis() - millisecGCFI;

            System.out.println("GCFI took: " + millisecGCFI / 1000 + "s total time ");
            System.out.println("GCFI has : " + lastSolution + " closed Itemsets");

            for (int i = 0; i < updateSteps; i++) {
                //System.out.println((i+1) +". Gely: " + solutionsGely[i] + " || " + solutionsGCFI[i]);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param D
     * @param start included
     * @param end   included
     * @return returns D[start, end]
     */
    public static ArrayList<Transaction> getSubD(ArrayList<Transaction> D, int start, int end) {
        ArrayList<Transaction> subD = new ArrayList<>(end - start + 1);
        for (int i = start; i <= end; i++) {
            subD.add(D.get(i));
        }
        return subD;
    }
}
