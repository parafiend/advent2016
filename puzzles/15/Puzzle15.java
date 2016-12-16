import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle15 {

    private static enum Stage {TEST, PART1, PART2};

    private static class Disc {
        public int positions;
        public int start;;

        public Disc(int positions, int start) {
            this.positions = positions;
            this.start = start;
        }

        public boolean isOpen(int time) {
            int current = start + time;
            //System.out.println(current + "  " + positions + "  " + time);
            return current % positions == 0;
        }
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

    public static void solve(Disc[] discs) {
        boolean fell = false;
        int start = 0;
        for (start = 0; !fell; start++) {
            fell = true;
            for (int i = 0; i < discs.length; i++) {
                int time = start + i + 1;
                if (!discs[i].isOpen(time)) {
                    fell = false;
                }
            }
        }
        System.out.println(start-1);
        

    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: solve.py <input text file>");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }
        
        Stage stage = parseStage(args[0]);
        Disc[] discs = null;
        if (stage == Stage.PART1) {
            discs = new Disc[]{new Disc(17,1), new Disc(7,0), new Disc(19, 2), new Disc(5, 0), new Disc(3, 0), new Disc(13, 5)};
        } else if (stage == Stage.PART2) {
            discs = new Disc[]{new Disc(17,1), new Disc(7,0), new Disc(19, 2), new Disc(5, 0), new Disc(3, 0), new Disc(13, 5), new Disc(11, 0)};
        } else {
            discs = new Disc[]{new Disc(5,4), new Disc(2,1)};
        }
        solve(discs);
        System.exit(0);
    }
}

