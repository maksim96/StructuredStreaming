package thiessen.bachelorthesis.helpers;

import com.Ostermiller.util.CSVParser;
import thiessen.bachelorthesis.itemsetmining.Transaction;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Maximilian Thiessen on 14.10.2017.
 * formats a path (-graph) file from simple one path = one line, one number = one vertex id
 * e.g.
 * 445231 438242
 * 1320700 1320701 1320702 1320719 1320703 1320696 1320714 1320715
 * 1321181 1320717 1320718 1320719 1320722
 * into NEL (nodes edge List) format with labels
 * e.g.
 * v 1 a
 * v 2 a
 * v 3 a
 * e 1 2 x
 * e 1 3 x
 * g Graph 1
 * x 0
 *
 * v 1 b
 * v 2 b
 * e 1 2 y
 * g Graph 2
 * x 1
 */
public class VertexToNELFormat {
    public static void format(String inputFile, String outputFile, String edgeLabel) {
        String[][] data = new String[0][];
        try {
            data = CSVParser.parse(new FileReader(inputFile), ' ');

            FileWriter writer = new FileWriter(outputFile);

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    writer.write("v " + data[i][j] + " " + data[i][j] + "\n");
                }

                for (int j = 1; j < data[i].length; j++) {
                    writer.write("e " + data[i][j-1] + " " + data[i][j] + " " + edgeLabel + "\n");
                }

                writer.write("\n");
            }


            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
