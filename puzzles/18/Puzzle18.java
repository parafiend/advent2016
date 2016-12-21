import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle18 {

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

    public static String nextRow(String row) {
        StringBuffer newRow = new StringBuffer();

        for (int i = 0; i < row.length(); i++) {
            char left = i > 0 ? row.charAt(i-1) : '.';
            char center = row.charAt(i);
            char right = i < row.length() - 1 ? row.charAt(i+1) : '.';

            if (left == right) {
                newRow.append('.');
            } else {
                newRow.append('^');
            }
        }

        return newRow.toString();
    }

    public static void solve(String input, int rowCount, boolean printTiles) {
        String[] rows = new String[rowCount];
        rows[0] = input;

        for (int i = 1; i < rowCount; i++) {
            rows[i] = nextRow(rows[i-1]);
        }

        int safeCount = 0;
        for (int i = 0; i < rowCount; i++) {
            if (printTiles) {
                System.out.println(rows[i]);
            }
            for (int j = 0; j < rows[i].length(); j++) {
                if (rows[i].charAt(j) == '.') {
                    safeCount += 1;
                }
            }
        }
        System.out.println(safeCount);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: solve.py <input text file>");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }
        
        String input = "";
        int rows = 0;
        boolean printTiles = true;
        switch(parseStage(args[0])) {
            case TEST:
                input = ".^^.^.^^^^";
                rows = 10;
                break;
            case PART1:
                input = "^..^^.^^^..^^.^...^^^^^....^.^..^^^.^.^.^^...^.^.^.^.^^.....^.^^.^.^.^.^.^.^^..^^^^^...^.....^....^.";
                rows = 40;
                break;
            case PART2:
                input = "^..^^.^^^..^^.^...^^^^^....^.^..^^^.^.^.^^...^.^.^.^.^^.....^.^^.^.^.^.^.^.^^..^^^^^...^.....^....^.";
                rows = 400000;
                printTiles = false;
                break;
        }
        solve(input, rows, printTiles);
        System.exit(0);
    }
}

