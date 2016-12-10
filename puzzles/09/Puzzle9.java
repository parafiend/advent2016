import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Puzzle9 {

    private static long repeatString(String string) {
        Pattern repeatPattern = Pattern.compile("(?:\\((?<l>\\d+)x(?<r>\\d+)\\))");
        Matcher repeatMatch = repeatPattern.matcher(string);
        long length = 0;
        if (repeatMatch.find()) {
            int left = Integer.valueOf(repeatMatch.group(1));
            int right = Integer.valueOf(repeatMatch.group(2));
            String repeated = string.substring(repeatMatch.end(), repeatMatch.end() + left);
            String rest = string.substring(repeatMatch.end() + left, string.length());
            length += repeatMatch.start() + right * repeatString(repeated) + repeatString(rest);
        } else {
            length = string.length();
        }
        return length;
    }

    public static int solve(String inputFile){
        Scanner compressedData = null;
        System.out.println("File is " + inputFile);
        try {
            compressedData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        Pattern repeatPattern = Pattern.compile("(?:\\((?<l>\\d+)x(?<r>\\d+)\\))");
        while (compressedData.hasNextLine()) {
            String compressed = compressedData.nextLine();
            System.out.println(compressed);
            System.out.println(repeatString(compressed));
            /**
            Matcher repeatMatch = repeatPattern.matcher(compressed);
            boolean repeats = repeatMatch.find();
            int charLength = compressed.length();
            while(repeats) {
                int left = Integer.valueOf(repeatMatch.group(1));
                int right = Integer.valueOf(repeatMatch.group(2));
                int nextStart = repeatMatch.end() + left;
                charLength += left * (right-1) - (repeatMatch.end() - repeatMatch.start());
                System.out.println("" + left + "  " + right + "  " + nextStart);
                repeats = repeatMatch.find(nextStart);
            }
            System.out.println(charLength);
            **/
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

