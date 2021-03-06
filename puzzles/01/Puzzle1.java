import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle1 {

    public enum Heading {
        NORTH (0, 1),
        EAST (1, 0),
        SOUTH (0, -1),
        WEST (-1, 0);

        public Heading turn(char direction) {
            if (direction == 'R') {
                switch (this) {
                    case NORTH:
                        return EAST;
                    case EAST:
                        return SOUTH;
                    case SOUTH:
                        return WEST;
                    case WEST:
                        return NORTH;
                }
            } else {
                switch (this) {
                    case NORTH:
                        return WEST;
                    case EAST:
                        return NORTH;
                    case SOUTH:
                        return EAST;
                    case WEST:
                        return SOUTH;
                }
            }
            return NORTH;
        }

        Heading(int x, int y) {
            this.xSign = x;
            this.ySign = y;
        }

        public int xSign;
        public int ySign;
    }

    private static class MyLocation {
        MyLocation(int x, int y, Heading heading) {
            this.x = x;
            this.y = y;
            this.heading = heading;
        }

        public void move(String move) {
            char direction = move.charAt(0);
            this.heading = this.heading.turn(direction);
            int distance = Integer.parseInt(move.substring(1).trim());
            this.x += distance * heading.xSign;
            this.y += distance * heading.ySign;
        }

        public Heading heading;
        public int x;
        public int y;
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

        ArrayList<Point> moves = new ArrayList<Point>(16);
        String move;
        MyLocation location = new MyLocation(0, 0, Heading.NORTH);
        Point firstCross = null;
        while (moveData.hasNext()) {
            move = moveData.next();
            Point oldPoint = new Point(location.x, location.y);
            System.out.println("Move is " + move);
            location.move(move);
            Point newPoint = new Point(location.x, location.y);
            if (firstCross == null) {
                // System.err.println(oldPoint + "yy" + newPoint);
                if (oldPoint.x == newPoint.x) {
                    for (int j = oldPoint.y; Math.abs(j) < Math.abs(newPoint.y); j += location.heading.ySign) {
                        Point currPoint = new Point(oldPoint.x, j);
                        if (moves.contains(currPoint)) {
                            firstCross = currPoint;
                            // System.err.println("found! " + currPoint);
                        } else {
                            moves.add(currPoint);
                            // System.err.println("checked " + currPoint);
                        }
                    }
                } else {
                    for (int i = oldPoint.x; Math.abs(i) < Math.abs(newPoint.x); i += location.heading.xSign) {
                        Point currPoint = new Point(i, oldPoint.y);
                        if (moves.contains(currPoint)) {
                            firstCross = currPoint;
                            // System.err.println("found! " + currPoint);
                        } else {
                            moves.add(currPoint);
                           //  System.err.println("checked " + currPoint);
                        }
                    }
                }
            }
        }
        // System.out.println(moves);
        System.out.println("Ending coords: " + location.x + ", " + location.y);
        System.out.println("Distance: " + (Math.abs(location.x) + Math.abs(location.y)));
        System.out.println("First revisit: " + firstCross);
        System.out.println(firstCross.x + firstCross.y);
        

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

