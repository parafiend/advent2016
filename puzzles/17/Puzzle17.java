import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Puzzle17 {

    private enum Direction {
        
        UP("U", 0, -1),
        DOWN("D", 0, 1),
        LEFT("L", -1, 0),
        RIGHT("R", 1, 0);

        public String code;
        public int dx;
        public int dy;

        Direction(String code, int dx, int dy) {
            this.code = code;
            this.dx = dx;
            this.dy = dy;
        }
        
        public String toString() {
            return this.code;
        }

        public Point move(Point location) {
            Point result = new Point(location);
            result.translate(this.dx, this.dy);
            return result;
        }
    }

    private static class Candidate {
        public String doors;
        public String seed;
        public String moves;
        public Point location;

        public Candidate(MessageDigest seeded_md, String moves, Point location) {
            this.location = location;
            MessageDigest md = null;
            try {
                md = (MessageDigest)seeded_md.clone();
            } catch (CloneNotSupportedException e) {
                System.out.println("WTF2");
                System.exit(1);
            }
            this.moves = moves;
            md.update(moves.getBytes());
            byte[] check = md.digest();
            this.doors = toHexString(check).substring(0, 4);
        }

        public boolean isDoorOpen(Direction dir) {
            char doorCode = doors.charAt(dir.ordinal());
            boolean result = false;
            if (doorCode >= 'b' && doorCode <= 'f') {
                result = true;
            }
            //System.out.println("--" + doors + " < " + dir + "  " + result);
            return result;
        }

        public boolean isGoal() {
            return this.location.equals(new Point(3, 3));
        }
    }

    private static String toHexString(byte[] input) {
        String result = "";
        for (byte twoChar: input) {
            String addition = Integer.toHexString(twoChar & 0xff);
            if (addition.length() == 1) {
                addition = "0" + addition;
            }
            result += addition;
            // System.out.println("    " + (twoChar&0xff) + " -- " + result);
        }
        // System.out.println("xx  " + input.length + "  " + result);

        return result;
    }

    private static String generateHash(MessageDigest seeded_md, int index, boolean keyStretching) {
        MessageDigest md = null;
        try {
            md = (MessageDigest)seeded_md.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("WTF2");
            System.exit(1);
        }
        md.update(String.format("%d", index).getBytes());
        
        byte[] check = md.digest();
        String result = toHexString(check);
        
        if (keyStretching) {
            for (int i = 0; i < 2016; i++) {
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("Qu'est que le fuck??");
                }
                md.update(result.getBytes());
                check = md.digest();
                result = toHexString(check);
            }
        }

        return result;
    }

    public static boolean processCandidate(MessageDigest md_base, Candidate candidate, LinkedList<Candidate> queue) {
        // System.out.println(candidate.moves);
        for (Direction dir: Direction.values()) {
            if (candidate.isDoorOpen(dir) && !candidate.isGoal()) {
                Point newLocation = dir.move(candidate.location);
                // System.out.println(newLocation + "," + candidate.location);
                if (newLocation.x >= 0 && newLocation.x <= 3 && newLocation.y >= 0 && newLocation.y <= 3) {
                    Candidate next = new Candidate(md_base, candidate.moves + dir, newLocation);
                    queue.add(next);
                }
            }
        }
        return candidate.isGoal();
    }

    public static int solve(String input, boolean keyStretching) {
        int count = 0;
        String attempt = "";
        int charIndex = 0;

        MessageDigest md_base = null;
        try {
            md_base = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("WTF");
            System.exit(1);
        }
        md_base.update(input.getBytes());

        LinkedList<Candidate> queue = new LinkedList<Candidate>();
        Candidate start = new Candidate(md_base, "", new Point(0,0));
        queue.add(start);

        boolean found = false;
        Candidate candidate = null;
        while (queue.size() > 0) {
            candidate = queue.poll();
            boolean wasFound = processCandidate(md_base, candidate, queue);
            if (!found && wasFound) {
                System.out.println(candidate.isGoal() + " --> " + candidate.moves);
                found = true;
            }
            if (candidate.isGoal()) {
                System.out.println(candidate.isGoal() + "  " + candidate.moves.length() + " --> ");
            }
        }
        System.out.println(candidate.isGoal() + " --> " + candidate.moves);
        return 0;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: solve.py <part>");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }

        String doorId = "";
        boolean keyStretching = false;
        if (args[0].equals("test")) {
            doorId = "hijkl";
            keyStretching = true;
        } else if (args[0].equals("part1")) {
            doorId = "mmsxrhfx";
        } else if (args[0].equals("part2")) {
            doorId = "cuanljph";
            keyStretching = true;
        } else {
            System.err.println("Invalid part! Expected one of test|part1|part2");
            System.exit(1);
        }

        solve(doorId, keyStretching);
        System.exit(0);
    }
}

