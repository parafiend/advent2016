import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Puzzle4 {

    /** private static class CharCountComparator<K, V extends Comparable<V>> implements Comparator<Map.Entry<K, V>> {

        public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
            int countDiff = o1.getValue() - o2.getValue();
            if (countDiff == 0) {
                countDiff = new String(o1.getKey()).compareTo(new String(o2.getKey()));
            }
            return countDiff;
        }

    } **/

    public static boolean validateChecksum(String[] parts, String checksum) {
        Map<Character, Integer> charCounts = new HashMap<Character, Integer>();
        boolean validChecksum = true;

        // parts includes final sector+checksum section
        for (int i = 0; i < parts.length-1; i++) {
            // System.out.println(parts[i]);
            for (char c: parts[i].toCharArray()) {
                Integer count = charCounts.get(c);
                if (count == null) {
                    charCounts.put(c, 1);
                } else {
                    charCounts.put(c, count + 1);
                }
            }
        }

        List<Map.Entry<Character, Integer>> bigCharCounts = new ArrayList(charCounts.entrySet());
        Collections.sort(bigCharCounts, new Comparator<Map.Entry<Character, Integer>>() {
            public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2){
                // Intentionally descending order for counts
                int countDiff = o2.getValue() - o1.getValue();
                if (countDiff == 0) {
                    // Ascending for alphabetical order
                    countDiff = Character.toString(o1.getKey()).compareTo(Character.toString(o2.getKey()));
                }
                // System.out.println(o1 + "--" + o2 + " = " + countDiff);
                return countDiff;
            }
        });

        String calced = new String();
        for(int i = 0; i < 5; i++) {
            Map.Entry<Character, Integer> entry = bigCharCounts.get(i);
            calced += entry.getKey();
            if (checksum.charAt(i) != entry.getKey()) {
                validChecksum = false;
            }
        }
        // System.out.println(checksum + "  " + calced);

        return validChecksum;
    }

    public static String decrypt(String[] parts, int sector) {
        String output = new String();
        // parts includes final sector+checksum section
        for (int i = 0; i < parts.length-1; i++) {
            for (char c: parts[i].toCharArray()) {
                int ord = (int)c - 'a';
                ord += (sector % 26);
                ord = ord % 26;
                output += (char) ('a' + ord);
            }
            output += " ";
        }
        return output;
    }

    public static int solve(String inputFile){
        Scanner roomData = null;
        System.out.println("File is " + inputFile);
        try {
            roomData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        Pattern meta = Pattern.compile("(\\d+)\\[(\\w+)\\]");
        int sectorSum = 0;

        while (roomData.hasNextLine()) {
            String room = roomData.nextLine();
            String[] parts = room.split("-");
            Matcher metadata = meta.matcher(parts[parts.length-1]);
            metadata.find();
            int sector = Integer.parseInt(metadata.group(1));
            String checksum = metadata.group(2);

            if (validateChecksum(parts, checksum)) {
                sectorSum += sector;
                String realName = decrypt(parts, sector);
                if (realName.contains("north")) {
                    System.out.println(realName + " -- " + sector);
                }
            }
        }

        System.out.println(sectorSum);

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

