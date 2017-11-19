import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class Puzzle20 {

    private enum Stage {TEST, PART1, PART2};

    private static class Range implements Comparable {
        public long min;
        public long max;

        public Range(long min, long max) {
            this.min = min;
            this.max = max;
        }

        public boolean contains(long x) {
            return (min <= x && x <= max);
        }

        public int compareTo(Object obj) {
            Range other = (Range) obj;
            long result = 0;
            if (other.min == min && other.max == max) {
                result = 0;
            } else if (other.min == min) {
                result = max - other.max;
            } else {
                result = min - other.min;
            }

            if (result > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else if (result < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            } else {
                return (int)result;
            }
        }

        public boolean equals(Range other) {
            return (other.min == min && other.max == max);
        }

        public void update(Range other) {
            min = Math.min(min, other.min);
            max = Math.max(max, other.max);
        }

        public String toString() {
            return String.format("<%d,%d>", min, max);
        }
    }



    public static int solveFile(String inputFile){
        Scanner fileData = null;
        System.out.println("File is " + inputFile);
        try {
            fileData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        TreeSet<Range> blacklist = new TreeSet<Range>();

        while (fileData.hasNextLine()) {
            String ipRange = fileData.nextLine();
            String[] ips = ipRange.split("-");
            long min = Long.valueOf(ips[0]);
            long max = Long.valueOf(ips[1]);
            Range range = new Range(min, max);

            boolean updated = false;
            for (Range other: blacklist) {
                if (other.contains(min) || other.contains(max)) {
                    other.update(range);
                    updated = true;
                }
            }

            if (!updated) {
                blacklist.add(range);
            }
        }

        boolean updated = false;
        do {
            updated = false;
            Iterator<Range> rangeIter = blacklist.iterator();
            Range first = rangeIter.next();
            while (rangeIter.hasNext()) {
                Range next = rangeIter.next();
                if (first.contains(next.min) || first.contains(next.max) || next.contains(first.min) || next.contains(first.max)) {
                    first.update(next);
                    rangeIter.remove();
                    updated = true;
                } else {
                if (next.min < first.max) {
                    System.out.println(first + "  " + next);
                }
                    first = next;
                }
            }
        } while (updated);

        System.out.println(blacklist);
        System.out.println(blacklist.first().max+1);
        Iterator<Range> rangeIter = blacklist.iterator();
        Range current = rangeIter.next();
        long lastMax = current.max;
        long safeCount = current.min;
        while (rangeIter.hasNext()) {
            current = rangeIter.next();
            safeCount += current.min - lastMax - 1;
            //System.out.println("" + current.min + "  " + lastMax + "  " + safeCount);
            lastMax = current.max;
        }
        System.out.println(safeCount);
        // Max 32 bit unsigned int
        System.out.println(4294967295l - lastMax);

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

        solveFile(args[0]);
        System.exit(0);
    }
}

