import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Scanner;

public class Puzzle5 {
    public static int solve(String inputFile) {
        System.out.println("File is " + inputFile);
        Scanner transData = null;
        try {
            transData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        ArrayList columns = new ArrayList<Map>();
        while (transData.hasNextLine()) {
            String transmission = transData.nextLine();
            int charIndex = 0;
            for (char c: transmission.toCharArray()) {
                while (charIndex >= columns.size()) {
                    columns.add(new HashMap<Character, Integer>());
                }
                Map<Character, Integer> charCounts = (Map<Character, Integer>) columns.get(charIndex);

                Integer count = charCounts.get(c);
                if (count == null) {
                    charCounts.put(c, 1);
                } else {
                    charCounts.put(c, count + 1);
                }
                charIndex++;
            }
        }

        for (Object column: columns) {
            Map<Character, Integer> charCounts = (Map<Character, Integer>)column;
            // System.out.println(charCounts);
            ArrayList<Map.Entry<Character, Integer>> entries = new ArrayList(charCounts.entrySet());
            Collections.sort(entries, Collections.reverseOrder(Map.Entry.comparingByValue()));
            int last = entries.size() - 1;
            System.out.println(entries.get(0).getKey() + "  " + entries.get(last).getKey());
        }
        return 0;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: solve.py <input text file>");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }

        solve(args[0]);
        System.exit(0);
    }
}

