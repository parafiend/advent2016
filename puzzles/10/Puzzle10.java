import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Puzzle10 {

    private static String INJECT = "value (\\d+) goes to bot (\\d+)";
    private static String COMPARE = "bot (\\d+) gives low to (\\w+) (\\d+) and high to (\\w+) (\\d+)";

    private enum Stage {TEST, PART1, PART2};

    private static class Bot {
        public int index;
        public int inl;
        public int inr;
        public Bot outLow;
        public Bot outHigh;

        public Bot(int index) {
            this.index = index;
            this.outLow = null;
            this.outHigh = null;
            this.inl = -1;
            this.inr = -1;
        }

        public void in (int in) {
            if (inl < 0) {
                inl = in;
            } else if (inr < 0) {
                inr = in;
                if (inr + inl == 61+17) {
                    System.out.println("" + index + "  " + inl + " <? " + inr);
                }
                if (outLow != null && outHigh != null) {
                    this.trigger();
                }
            } else {
                System.out.println("Qu'est que le fuck??");
            }
        }

        public void setOutputs(Bot outLow, Bot outHigh) {
            this.outLow = outLow;
            this.outHigh = outHigh;

            if (inl >= 0 && inr >= 0) {
                this.trigger();
            }   
        }

        public void trigger() {
            outLow.in(Math.min(inl, inr));
            outHigh.in(Math.max(inl, inr));
        }

    }

    private static class Out extends Bot {
        public int in;
        public int index;

        public Out(int index) {
            super(index);
            in = -1;
        }
        
        public String toSring() {
            return "OUT " + index + ": " + in;
        }

        public void in(int in) {
            if (this.in >= 0) {
                System.out.println("Qu'est que le fuck??");
            }
            this.in = in;
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

        Pattern injectPatt = Pattern.compile(INJECT);
        Pattern comparePatt = Pattern.compile(COMPARE);
        Bot[] bots = new Bot[256];
        for (int i = 0; i < 256; i++) {
            bots[i] = new Bot(i);
        }
        Out[] outs = new Out[256];
        for (int i = 0; i < 256; i++) {
            outs[i] = new Out(i);
        }

        while (fileData.hasNextLine()) {
            String action = fileData.nextLine();

            Matcher injectMatch = injectPatt.matcher(action);
            Matcher compareMatch = comparePatt.matcher(action);

            if (injectMatch.matches()) {
                int in = Integer.valueOf(injectMatch.group(1));
                int bot = Integer.valueOf(injectMatch.group(2));
                System.out.println("IN: " + in + "->" + bot);
                bots[bot].in(in);
            } else if (compareMatch.matches()) {
                int comp = Integer.valueOf(compareMatch.group(1));
                String ltype = compareMatch.group(2).equals("bot") ? "b" : "o";
                int low = Integer.valueOf(compareMatch.group(3));
                String htype = compareMatch.group(4).equals("bot") ? "b" : "o";
                int high = Integer.valueOf(compareMatch.group(5));
                System.out.println("COMP: " + comp + ": " + ltype + low + " < " + htype + high);
                Bot highb = htype.equals("b") ? bots[high] : outs[high];
                Bot lowb = ltype.equals("b") ? bots[low] : outs[low];
                bots[comp].setOutputs(lowb, highb);
            } else { 
                System.out.println("Qu'est que le fuck??");
            }
        }
        System.out.println(outs[0].in * outs[1].in * outs[2].in);

        return 0;
    }

    public static Stage parseStage(String inputStage) {
        Stage result;
        switch(inputStage) {
            case "part1":
                result = Stage.PART1;
                break;
            case "part2": 
                result = Stage.PART2;
                break;
            default:
                result = Stage.TEST;
                break;
            }
        System.out.println(result);
        return result;
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

