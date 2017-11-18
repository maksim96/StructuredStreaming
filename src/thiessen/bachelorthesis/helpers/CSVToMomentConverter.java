package thiessen.bachelorthesis.helpers;

import com.Ostermiller.util.CSVParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Maximilian Thiessen on 05.10.2017.
 */
public class CSVToMomentConverter {

    public static void main(String[] args) {
        String[][] data;
        try {
            data = CSVParser.parse(new FileReader("mushrooms.txt"), ' ');

            ArrayList<HashSet<Integer>> D = new ArrayList<>(data.length);

            for (int i = 0; i < data.length; i++) {
                D.add(new HashSet<>());
                //minus 1 wegen space im mushrooms file am Ende jeder Zeile
                for (int j = 0; j < data[i].length-1; j++) {
                    int x = Integer.parseInt(data[i][j]);
                    D.get(i).add(x);

                }
            }

            PrintWriter writer = new PrintWriter("momentMushrooms.txt", "UTF-8");

            int i = 1;
            for (HashSet<Integer> t : D) {
                writer.print(i + " " + i + " " + t.size() + " ");
                int j = 1;

                List<Integer> newT = new ArrayList<>(t);
                Collections.sort(newT);
                for (int x : newT) {
                    if (j != t.size()) {
                        writer.print(x + " ");
                    } else {
                        writer.print(x + "\n");
                    }

                    j++;
                }
                i++;
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }




}
