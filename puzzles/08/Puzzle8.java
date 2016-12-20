import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Puzzle8 {

    private enum Stage {TEST, PART1, PART2};

    private static class Screen {
        private boolean[][] screen;
        private int width;
        private int height;

        Screen(int x, int y) {
            width = x;
            height = y;
            screen = new boolean[x][y];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    screen[i][j] = false;
                }
            }
        }

        public void rect(int x, int y) {
            System.out.println("Rect " + x + "," + y);
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    screen[i][j] = true;
                }
            }
        }

        public void rotateRow(int row, int num) {
            System.out.println("Row " + num + "," + row);
            int actualMove = num % width;
            boolean[] newRow = new boolean[width];
            for (int i = 0; i < width; i++) {
                int sourceIndex = actualMove <= i ? i - actualMove : width - (actualMove - i);
                System.out.println("" + i + "  " + actualMove + "  " + sourceIndex);
                newRow[i] = screen[sourceIndex][row];
            }

            for (int i = 0; i < width; i++) {
                screen[i][row] = newRow[i];
            }
        }

        public void rotateCol(int col, int num) {
            System.out.println("Col " + num + "," + col);
            int actualMove = num % height;
            boolean[] newCol = new boolean[height];
            for (int i = 0; i < height; i++) {
                int sourceIndex = actualMove <= i ? i - actualMove : height - (actualMove - i);
                System.out.println("" + i + "  " + actualMove + "  " + sourceIndex);
                newCol[i] = screen[col][sourceIndex];
            }

            for (int i = 0; i < height; i++) {
                screen[col][i] = newCol[i];
            }

        }

        public String toString() {
            String result = "";
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    char out = screen[j][i] ? '#' : '.';
                    result += out;
                }
                result += "\n";
            }
            return result;
        }

        public int numLit() {
            int result = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (screen[j][i]) {
                        result++;
                    }
                }
            }
            return result;
        }

    }

    public static int solveFile(String inputFile){
        Scanner fileData = null;
        System.out.println("File is " + inputFile);
        Screen screen = null;
        if (inputFile.equals("test.txt")) {
            screen = new Screen(7, 3);
        } else {
            screen = new Screen(50, 6);
        }
        String RECT = "rect (\\d+)x(\\d+)";
        String ROTCOL = "rotate column x=(\\d+) by (\\d+)";
        String ROTROW = "rotate row y=(\\d+) by (\\d+)";


        Pattern rectPatt = Pattern.compile(RECT);
        Pattern rowPatt = Pattern.compile(ROTROW);
        Pattern colPatt = Pattern.compile(ROTCOL);

        try {
            fileData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        while (fileData.hasNextLine()) {
            String action = fileData.nextLine();
            Matcher rectMatch = rectPatt.matcher(action);
            Matcher rowMatch = rowPatt.matcher(action);
            Matcher colMatch = colPatt.matcher(action);
            if (rectMatch.matches()) {
                screen.rect(Integer.valueOf(rectMatch.group(1)), Integer.valueOf(rectMatch.group(2)));
            } else if (rowMatch.matches()) {
                screen.rotateRow(Integer.valueOf(rowMatch.group(1)), Integer.valueOf(rowMatch.group(2)));
            } else if (colMatch.matches()) {
                screen.rotateCol(Integer.valueOf(colMatch.group(1)), Integer.valueOf(colMatch.group(2)));
            } else {
                System.out.println("Qu'est que le fuck?");
            }
            System.out.println(screen);
            System.out.println("-------------------");
        }
            System.out.println(screen.numLit());

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

