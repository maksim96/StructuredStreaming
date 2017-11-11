package thiessen.bachelorthesis;

import com.Ostermiller.util.CSVParser;
import thiessen.bachelorthesis.closedstreaming.GelyCFI;
import thiessen.bachelorthesis.closedstreaming.GelyCFIConnected;
import thiessen.bachelorthesis.closedstreaming.GelyCFIOptimized;
import thiessen.bachelorthesis.graph.Graph;
import thiessen.bachelorthesis.graph.VertexToNELFormat;
import thiessen.bachelorthesis.itemsetmining.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {
	// write your code here

        int[] supports = {100, 50, 25, 10, 5, 2, 1};

        for (int s: supports) {
           slidingWindowTest(s);
           //compareStreamGelyWithDanielsOptimization(s);

            //testStreamGelyWithoutFrequencyConstraint(s);
        }

       //    graphGelyTest();
     graphGelyRoadNetworkTest();

      //  graphGelSmallTest();

       // VertexToNELFormat.format("trafficjams.txt", "trafficjamsConverted.txt", "x" );

    }

    private static void graphGelyRoadNetworkTest() {
        String[][] data = new String[0][];
        try {
            CSVParser csvParser = new CSVParser(new FileReader("roadNet-CA.txt"), '\t');
            csvParser.setCommentStart("#");
            data = csvParser.getAllValues();
            Set<Integer> E = new HashSet<>(1965206);
            ArrayList<Transaction> D = new ArrayList<>(data.length);


            for (String[] line : data) {
                for (String s : line) {
                    E.add(Integer.parseInt(s));
                }
            }



            Graph g = new Graph(2000000);
            for (int i = 0; i < data.length; i++) {
                    g.get(Integer.parseInt(data[i][0])).add(Integer.parseInt(data[i][1]));
            }

            System.out.println("Parsing graph done!");
/*
            FileWriter trafficJams = new FileWriter("trafficjams.txt");

            for (int i = 0; i < 100000; i++) {
                Random random = new Random();
                int start  = ThreadLocalRandom.current().nextInt(0, g.size());
                for (int j = 0; j < 10; j++) {

                    int length = Math.max(1,(int) random.nextGaussian()*10 + 10);

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

            System.out.println("Generating traffic jam transaction done!");*/


            /*
            for (int i = 0; i < 10; i++) {
                Gely gely = new ConnectedGely(getSubD(D,i,i+3), E, g, 1);
                // gely.gely();
                //System.out.println((i+1) + ". " + gely.closedItemsets +  " || " + getSubD(D,i,i+2));
                gely = new ConnectedGely(getSubD(D,i,i+2), E, g, 1);
                gely.gely();
                System.out.println(gely.closedItemsets);
            }

            System.out.println("==============================================");

            GelyCFIConnected stream = new GelyCFIConnected(getSubD(D, 0, 2), E, g, 1);
            stream.explore();

            System.out.println(stream.closedItemsets);

            for (int i = 0; i < 9 ; i++) {
                stream.slidingWindowStep(D.get(i+3));
                System.out.println(stream.closedItemsets);
            }


            */

            data = CSVParser.parse(new FileReader("trafficjams.txt"), ' ');

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

            System.out.println("reading in first 51000 trafficjams done");
            int[] sizes = {1000, 2000, 5000, 10000, 20000, 50000, 100000};
            for (int z = 0; z < 7; z++) {
                int minSupport = 12;

                int startInterval = sizes[z];

                ArrayList<Transaction> subD = getSubD(D, 0, startInterval);
                ArrayList<Transaction> D2 = new ArrayList<>(D);
                GelyCFIConnected stream = new GelyCFIConnected(getSubD(D2, 0, startInterval), new HashSet<>(E), g, minSupport);
                long millisecGCFI = System.currentTimeMillis();
                stream.explore();
                millisecGCFI = System.currentTimeMillis() - millisecGCFI;
                Gely gely = new ConnectedGely(subD, new HashSet<>(E), g, minSupport);
                gely.gely();
                System.out.println("explored first 50000 in " + millisecGCFI + "ms. StreamGely Found: "
                        + stream.closedItemsets.size() + " Gely found: " + gely.closedItemsets.size());
                long millisecgely = 0;
                long totalMillisec = 0;
                for (int i = 0; i < 100000; i++) {

                    //subD.add(D.get(startInterval+1+i));
                   // subD.remove(0);

                    //gely = new ConnectedGely(subD, new HashSet<>(E), g, minSupport);
                    millisecgely = System.currentTimeMillis();
                    //  gely.gely();
                    millisecgely = System.currentTimeMillis() - millisecgely;


                    millisecGCFI = System.currentTimeMillis();
                    stream.slidingWindowStep(D2.get(startInterval+1+i));
                    millisecGCFI = System.currentTimeMillis() - millisecGCFI;
                    totalMillisec+= millisecGCFI;
               /* if (i %1000 == 0 || i < 10000) {
                    System.out.println("Added " + (i+1) + " . transaction in gely|stream " + millisecgely +"|" + millisecGCFI + "ms. StreamGely Found: "
                            + stream.closedItemsets.size() + " Gely Found: " + gely.closedItemsets.size()   );

                }*/


                }
                System.out.println("Sliding Window Größe: " + startInterval + ". Gesamtlaufzeit " + totalMillisec + "ms");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void graphGelSmallTest() {
        String[][] data = new String[0][];
        try {
            CSVParser csvParser = new CSVParser(new FileReader("smallGraphs.txt"), ' ');
            csvParser.setCommentStart("#");
            data = csvParser.getAllValues();
            Set<Integer> E = new HashSet<>(23);
            ArrayList<Transaction> D = new ArrayList<>(data.length);


            for (String[] line : data) {
                for (String s : line) {
                    E.add(Integer.parseInt(s));
                }
            }



            Graph g = new Graph(10);
            for (int i = 0; i < data.length; i++) {
                g.get(Integer.parseInt(data[i][0])).add(Integer.parseInt(data[i][1]));
            }

            System.out.println("Parsing graph done!");

            FileWriter trafficJams = new FileWriter("trafficjamsSmall.txt");

            Itemset test =new Itemset();
            test.add(5);
            test.add(3);

            g.areConnected(test);

            for (int i = 0; i < 100; i++) {
                Random random = new Random();
                int start  = ThreadLocalRandom.current().nextInt(1, g.size());
                for (int j = 0; j < 10; j++) {

                    int length = Math.max(1,(int) random.nextGaussian()*2 + 2);

                    Set<Integer> randomWalk = g.randomWalk(start, length);
                    StringBuilder sb = new StringBuilder();
                    for (int node : randomWalk) {
                        sb.append(node + " ");
                    }
                    trafficJams.write(sb.toString().trim() + "\n");
                }
               // System.out.println(i);

            }
            trafficJams.close();

            data = CSVParser.parse(new FileReader("trafficjamsSmall.txt"), ' ');

            for (int i = 0; i < 1000; i++) {
                D.add(new Transaction());
                for (int j = 0; j < data[i].length; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    if (!E.contains(x)) {
                        E.add(x);
                    }
                    D.get(i).add(x);

                }
            }

            GelyCFIConnected stream = new GelyCFIConnected(getSubD(D, 0, 20), E, g, 2);
            stream.explore();

            ArrayList<Transaction> D2 = new ArrayList<>(D);

            for (int i = 0; i < 100; i++) {
                stream.slidingWindowStep(D.get(i+21));
                Gely gely = new ConnectedGely(getSubD(D2,i+1,i+21), E, g, 2);
                gely.gely();

                System.out.println("(" + stream.closedItemsets.size() + " | " +  gely.closedItemsets.size() + ")");
                if (stream.closedItemsets.size() !=  gely.closedItemsets.size()) {
                    System.out.println("ahhhh");
                }
            }




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void graphGelyTest() {
        String[][] data = new String[0][];
        try {
            data = CSVParser.parse(new FileReader("small.txt"), ' ');
            Set<Integer> E = new HashSet<>();
            ArrayList<Transaction> D = new ArrayList<>(data.length);

            for (int i = 0; i < data.length; i++) {
                D.add(new Transaction());
                //minus 1 wegen space im mushrooms file am Ende jeder Zeile
                for (int j = 0; j < data[i].length; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    if (!E.contains(x)) {
                        E.add(x);
                    }
                    D.get(i).add(x);

                }
            }


            Graph g = new Graph();
            g.add(new ArrayList<>());
            g.add(new ArrayList<>(Arrays.asList(2,4)));
            g.add(new ArrayList<>(Arrays.asList(1,4,5)));
            g.add(new ArrayList<>(Arrays.asList(4,5)));
            g.add(new ArrayList<>(Arrays.asList(1,2,3)));
            g.add(new ArrayList<>(Arrays.asList(2,3)));
            g.add(new ArrayList<>(Arrays.asList(7)));
            g.add(new ArrayList<>(Arrays.asList(6)));


            for (int i = 0; i < 10; i++) {
                Gely gely = new ConnectedGely(getSubD(D,i,i+3), E, g, 1);
               // gely.gely();
                //System.out.println((i+1) + ". " + gely.closedItemsets +  " || " + getSubD(D,i,i+2));
                gely = new ConnectedGely(getSubD(D,i,i+2), E, g, 1);
                gely.gely();
                System.out.println(gely.closedItemsets);
            }

            System.out.println("==============================================");

            GelyCFIConnected stream = new GelyCFIConnected(getSubD(D, 0, 2), E, g, 1);
            stream.explore();

            System.out.println(stream.closedItemsets);

            for (int i = 0; i < 9 ; i++) {
                stream.slidingWindowStep(D.get(i+3));
                System.out.println(stream.closedItemsets);
            }




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testStreamGelyWithoutFrequencyConstraint(int support) {
        String[][] data = new String[0][];
        try {
            data = CSVParser.parse(new FileReader("mushrooms.txt"), ' ');
            Set<Integer> E = new HashSet<>();
            ArrayList<Transaction> D = new ArrayList<>(data.length);

            for (int i = 0; i < data.length; i++) {
                D.add(new Transaction());
                //minus 1 wegen space im mushrooms file am Ende jeder Zeile
                for (int j = 0; j < data[i].length-1; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    if (!E.contains(x)) {
                        E.add(x);
                    }
                    D.get(i).add(x);

                }
            }

            int stepCount = 500;
            int startingPoint = 0;

            ArrayList<Transaction> subD = getSubD(D, 0, 0);

            long millisecGCFI = System.currentTimeMillis();

            Set<Integer> fullE = new HashSet<>(E);

            GelyCFI gcfi = new GelyCFI(subD, E, support*250/500);

            gcfi.explore();

            //int[] solutionsGCFI = new int[stepCount];
            //System.out.println("Explore done!");
            //System.out.println("==================================");
           // solutionsGCFI[0] = gcfi.closedItemsets.size();
            int temp = 0;
            for (int i = 1; i < stepCount; i++) {
              //  gcfi.slidingWindowStep(new Itemset(D.get(slidingWindowSize+i)));
                //solutionsGCFI[i-startingPoint] = gcfi.closedItemsets.size();
                gcfi.addition(D.get(i));


                temp = gcfi.closedItemsets.size();
              //  System.out.println((i-startingPoint) + ". Slidingwindowstep done!");
                //System.out.println("==================================");
            }

            System.out.println("Found " + temp + "itemsets!");

                millisecGCFI = System.currentTimeMillis() - millisecGCFI;

                System.out.println("GCFI took: " + millisecGCFI + "ms total time for support " + support*250/500);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compareStreamGelyWithDanielsOptimization(int support) {
        try {

            System.out.println("===============================");
            System.out.println("=====Support is " + support + "=====");
            System.out.println("===============================");

            String[][] data =  CSVParser.parse(new FileReader("mushrooms.txt"), ' ');

            Set<Integer> E = new HashSet<>();
            ArrayList<Transaction> D = new ArrayList<>(data.length);

            for (int i = 0; i < data.length; i++) {
                D.add(new Transaction());
                //minus 1 wegen space im mushrooms file am Ende jeder Zeile
                for (int j = 0; j < data[i].length-1; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    if (!E.contains(x)) {
                        E.add(x);
                    }
                    D.get(i).add(x);

                }
            }

            int stepCount = 50;
            int startingPoint = 0;

            int slidingWindowSize = 5000;

            //int support = 200;

            ArrayList<Transaction> subD;

            int[] solutionsGCFIOptimized  = new int[stepCount];
            long millisecGCFIOptimized = System.currentTimeMillis();

            subD = getSubD(D, startingPoint, slidingWindowSize+startingPoint);

            GelyCFI/*Optimized*/ gcfiOptimized = new GelyCFI/*Optimized*/(subD, E, support);

            gcfiOptimized.explore();
            //System.out.println("Explore done!");
            //System.out.println("==================================");
            solutionsGCFIOptimized[0] = gcfiOptimized.closedItemsets.size();
            for (int i = startingPoint+1; i < stepCount + startingPoint; i++) {
                gcfiOptimized.slidingWindowStep(D.get(slidingWindowSize+i));
                solutionsGCFIOptimized[i-startingPoint] = gcfiOptimized.closedItemsets.size();
                //System.out.println((i-startingPoint) + ". Slidingwindowstep done!");
                //System.out.println("==================================");
            }

            millisecGCFIOptimized = System.currentTimeMillis() - millisecGCFIOptimized;


            System.out.println("GCFI took: " + millisecGCFIOptimized/1000 + "s total time ");
            System.out.println("GCFI has : " + solutionsGCFIOptimized[stepCount-1] + " closed Itemsets");

            subD = getSubD(D, startingPoint, slidingWindowSize+startingPoint);

            long millisecGCFI = System.currentTimeMillis();
            /*
            GelyCFI gcfi = new GelyCFI(subD, E, support);

            gcfi.explore();

            int[] solutionsGCFI = new int[stepCount];
            //System.out.println("Explore done!");
            //System.out.println("==================================");
            solutionsGCFI[0] = gcfi.closedItemsets.size();
            for (int i = startingPoint+1; i < stepCount + startingPoint; i++) {
                gcfi.slidingWindowStep(new Itemset(D.get(slidingWindowSize+i)));
                solutionsGCFI[i-startingPoint] = gcfi.closedItemsets.size();
                //System.out.println((i-startingPoint) + ". Slidingwindowstep done!");
                //System.out.println("==================================");
            }

            millisecGCFI = System.currentTimeMillis() - millisecGCFI;

            System.out.println("GCFI took: " + millisecGCFI/1000 + "s total time ");
            System.out.println("GCFI has : " + solutionsGCFI[stepCount-1] + " closed Itemsets");

            for (int i = 0; i < stepCount; i++) {
                //System.out.println((i+1) +". Gely: " + solutionsGely[i] + " || " + solutionsGCFI[i]);
            }
*/



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void slidingWindowTest(int support) {
        try {
            System.out.println("===============================");
            System.out.println("=====Support is " + support + "=====");
            System.out.println("===============================");

            String[][] data =  CSVParser.parse(new FileReader("mushrooms.txt"), ' ');

            Set<Integer> E = new HashSet<>();
            ArrayList<Transaction> D = new ArrayList<>(data.length);

            for (int i = 0; i < data.length; i++) {
                D.add(new Transaction());
                //minus 1 wegen space im mushrooms file am Ende jeder Zeile
                for (int j = 0; j < data[i].length-1; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    if (!E.contains(x)) {
                        E.add(x);
                    }
                    D.get(i).add(x);

                }
            }

            int stepCount = 200;
            int startingPoint = 0;
            
            int slidingWindowSize = 500;

            //int support = 200;

            
            ArrayList<Transaction> subD;
            int lastSolution = -1;

            long millisecsGely = System.currentTimeMillis();

            for (int i = startingPoint; i <= stepCount + startingPoint; i++) {

                subD = getSubD(D, i, slidingWindowSize+i);
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
            System.out.println("Gely took: " + millisecsGely/1000 + "s total time ");
            System.out.println("Gely has : " + lastSolution + " closed Itemsets");
            //System.out.println("==================================");


            subD = getSubD(D, startingPoint, slidingWindowSize+startingPoint);

            long millisecGCFI = System.currentTimeMillis();

            GelyCFI gcfi = new GelyCFI(subD, E, support);

            gcfi.explore();

            lastSolution = -1;
            //System.out.println("Explore done!");
            //System.out.println("==================================");
            for (int i = startingPoint+1; i <= stepCount + startingPoint; i++) {
                gcfi.slidingWindowStep(D.get(slidingWindowSize+i));
                lastSolution = gcfi.closedItemsets.size();
               // System.out.println((i-startingPoint) + ". Slidingwindowstep done!");
                //System.out.println("==================================");
            }

            millisecGCFI = System.currentTimeMillis() - millisecGCFI;

            System.out.println("GCFI took: " + millisecGCFI/1000 + "s total time ");
            System.out.println("GCFI has : " + lastSolution + " closed Itemsets");

            for (int i = 0; i < stepCount; i++) {
                //System.out.println((i+1) +". Gely: " + solutionsGely[i] + " || " + solutionsGCFI[i]);
             }




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
