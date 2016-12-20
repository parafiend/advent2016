import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.PriorityQueue;

public class Puzzle13 {

    public static boolean isWall(Point pos, int fave) {
        long val = pos.x*pos.x + 3*pos.x + 2*pos.x*pos.y + pos.y + pos.y*pos.y + fave;
        int bits = Long.bitCount(val);

        // even, it's an open space.
        // odd, it's a wall.
        boolean isWall = !(bits % 2 == 0);
        return isWall;

    }

    public static Point[] getNeighbours(Point curr) {
        Point[] neighbours = new Point[4];
        neighbours[0] = new Point(curr.x + 1, curr.y);
        if (curr.x > 0) {
            neighbours[1] = new Point(curr.x - 1, curr.y);
        }
        neighbours[2] = new Point(curr.x, curr.y + 1);
        if (curr.y > 0) {
            neighbours[3] = new Point(curr.x, curr.y - 1);
        }
        return neighbours;
    }

    public static int solve(int favourite, Point dest){
        Point start = new Point(1,1);
        PriorityQueue<Point> toCheck = new PriorityQueue<Point>(100, (Point o1, Point o2) -> o1.x - o2.x + o1.y - o2.y);
        PriorityQueue<Point> nextCheck = new PriorityQueue<Point>(100, (Point o1, Point o2) -> o1.x - o2.x + o1.y - o2.y);
        HashSet<Point> seen = new HashSet<Point>();
        toCheck.add(start);

        int steps = 1;
        boolean found = false;
        for (int count = 0; !found && count < 1000000; count++) {
            Point current = toCheck.poll();
            if (current == null) {
                if (steps == 50) {
                    System.out.println("-----" + seen.size());
                }
                toCheck = nextCheck;
                nextCheck = new PriorityQueue<Point>(100, (Point o1, Point o2) -> o1.x - o2.x + o1.y - o2.y);
                steps++;
                System.out.println(steps);
            } else {
                Point[] neighbours = getNeighbours(current);
                for (Point next: neighbours) {
                    if (next == null) {
                        continue;
                    }
                    if (dest.equals(next)) {
                        System.out.println("GOTCHA" + dest + "  " + next);
                        System.out.println(steps);
                        found = true;
                    } else if (!isWall(next, favourite) && !seen.contains(next)) {
                        nextCheck.add(next);
                        seen.add(next);
                        // System.out.println(next);
                    }
                }
            }   
        }
        return 0;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: solve.py <favourite number> <dest x> <dest y>");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }
        
        Point dest = new Point(Integer.valueOf(args[1]), Integer.valueOf(args[2]));
        solve(Integer.valueOf(args[0]), dest);
        System.exit(0);
    }
}

