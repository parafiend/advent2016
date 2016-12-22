import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Puzzle19 {

    private enum Stage {TEST, PART1, PART2};

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

    public static void solve(int numElves, boolean newAlgo) {
        //This should be a LinkedList for part1
        ArrayList<Integer> elves = new ArrayList<Integer>();
        LinkedList<Integer> nextElves = new LinkedList<Integer>();
        HashSet<Integer> stillPlaying = new HashSet<Integer>();
        for (int i = 0; i < numElves; i++) {
            elves.add(i);
            stillPlaying.add(i);
        }

        boolean stolen = false;
        while (elves.size() > 1) {
            if (newAlgo) {
                for (int i = 0; i < elves.size(); i++) {
                    int thief = i;
                    int markIndex = (thief + elves.size() / 2) % elves.size();
                    elves.remove(markIndex);

                    // Step back if we shrunk
                    if (markIndex < thief) {
                        i--;
                    }
                    if (i % 1000 == 0) {
                        System.out.println(i + "  " + elves.size());
                    }
                }
            } else {
                Iterator<Integer> elfIter = elves.iterator();
                while (elfIter.hasNext()) {
                    Integer elf = elfIter.next();
                    if (stolen) {
                        elfIter.remove();
                    }
                    stolen = !stolen;
                }
            }
            System.out.println(elves.size() + "  " + (elves.get(0) + 1));
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: solve.py <input text file>");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }

        int elves = 0;
        boolean newAlgo = false;
        switch (parseStage(args[0])) {
            case TEST:
                elves = 5;
                newAlgo = true;
                break;
            case PART2:
                newAlgo = true;
            case PART1:
                elves = 3014603;
                break;
        }

        solve(elves, newAlgo);
        System.exit(0);
    }
}

