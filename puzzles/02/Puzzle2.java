import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle2 {
    private static char[][] layout = {{'Z','Z','1','Z','Z'},
                               {'Z','2','3','4','Z'},
                               {'5','6','7','8','9'},
                               {'Z','A','B','C','Z'},
                               {'Z','Z','D','Z','Z'}};

    public static Point handleRow2(String row, Point position) {
        Point newPosition = new Point(position);
        for (int i = 0; i < row.length(); i++) {
            char move = row.charAt(i);
            switch (move) {
                case 'U':
                    newPosition.y -= 1;
                    if (newPosition.y < 0 || layout[newPosition.y][newPosition.x] == 'Z') {
                        newPosition.y += 1;
                    }
                    break;
                case 'R':
                    newPosition.x += 1;
                    if (newPosition.x > 4 || layout[newPosition.y][newPosition.x] == 'Z') {
                        newPosition.x -= 1;
                    }
                    break;
                case 'D':
                    newPosition.y += 1;
                    if (newPosition.y > 4 || layout[newPosition.y][newPosition.x] == 'Z') {
                        newPosition.y -= 1;
                    }
                    break;
                case 'L':
                    newPosition.x -= 1;
                    if (newPosition.x < 0 || layout[newPosition.y][newPosition.x] == 'Z') {
                        newPosition.x += 1;
                    }
                    break;
            }
        }
        return newPosition;
    }

    public static int handleRow1(String row, int position) {
        int start = position;
        for (int i = 0; i < row.length(); i++) {
            char move = row.charAt(i);
            int delta = 0;
            switch (move) {
                case 'U':
                    delta = -3;
                    break;
                case 'R':
                    if (position % 3 != 0) {
                        delta = 1;
                    }
                    break;
                case 'D':
                    delta = 3;
                    break;
                case 'L':
                    if (position % 3 != 1) {
                        delta = -1;
                    }
                    break;
            }
            position += delta;
            if (position < 1 || position > 9) {
                position -= delta;
            }
        }
        return position;
    }


    public static int solve(String inputFile){
        Scanner moveData = null;
        System.out.println("File is " + inputFile);
        try {
            moveData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        int start = 5;
        Point realStart = new Point(0,2);
        while (moveData.hasNextLine()) {
            String move = moveData.nextLine();
            //System.out.println("xx" + move);
            start = handleRow1(move, start);
            realStart = handleRow2(move, realStart);


            // System.out.println("Digit: " + start);
            System.out.println("real: " + layout[realStart.y][realStart.x]);
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
