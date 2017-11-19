import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle16 {

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

    public static String growData(String input, int desiredSize) {
        while (input.length() < desiredSize) {
            char[] addition = new StringBuilder(input).reverse().toString().toCharArray();
            StringBuilder next = new StringBuilder(input);
            next.append("0");
            for (char c: addition) {
                char newChar = c == '0' ? '1' : '0';
                next.append(newChar);
            }
            System.out.println(input.length() + " < " + desiredSize);
            if (input.length() > desiredSize) {
                break;
            }
            input = next.toString();
        }
        return input.substring(0, desiredSize);
    }

    private static String calcChecksum(String input) {
        while (input.length() % 2 == 0) {
            StringBuilder checksum = new StringBuilder();
            for (int i = 0; i < input.length() - 1; i+=2) {
                char l = input.charAt(i);
                char r = input.charAt(i+1);
                char next = l == r ? '1' : '0';
                checksum.append(next);
            }
            input = checksum.toString();
        }
        return input;
    }

    public static void solve(String input, int diskSize) {
        String newData = growData(input, diskSize);
        System.out.println(newData.length());
        
        String checksum = calcChecksum(newData);
        System.out.println(checksum);

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
        int diskSize = 0;
        switch(parseStage(args[0])) {
            case TEST:
                input = "10000";
                diskSize = 20;
                break;
            case PART1:
                input = "10111011111001111";
                diskSize = 272;
                break;
            case PART2:
                input = "10111011111001111";
                diskSize = 35651584;
                break;
            default:
        }


        solve(input, diskSize);
        System.exit(0);
    }
}

