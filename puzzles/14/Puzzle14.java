import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Puzzle14 {

    private static class Candidate {
        public String hash;
        public char three;
        public HashSet<Character> fives;

        public Candidate(String hash) {
            this.hash = hash;
            three = 0;
            fives = new HashSet<Character>();

            char lastChar = 0;
            int count = 0;
            for (char curr: hash.toCharArray()) {
                if (curr == lastChar) {
                    count++;
                    if (count == 3 && three == 0) {
                        three = curr;
                    }
                    if (count == 5) {
                        fives.add(curr);
                    }
                } else {
                    lastChar = curr;
                    count = 1;
                }
            }
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

    public static void addCandidate(LinkedList<Candidate> history, HashMap<Character, Integer> fivesHistory, MessageDigest md_base, int nextIndex, boolean keyStretching) {
        //System.out.println("--------------" + nextIndex);
        Candidate candidate = new Candidate(generateHash(md_base, nextIndex, keyStretching));
        history.add(candidate);
        for (char five: candidate.fives) {
            int newCount = 1;
            if (fivesHistory.containsKey(five)) {
                newCount += fivesHistory.get(five);
            }
            fivesHistory.put(five, newCount);
        }
    }

    public static int solve(String input, boolean keyStretching) {
        int count = 0;
        String attempt = "";
        int charIndex = 0;
        int found = 0;

        MessageDigest md_base = null;
        try {
            md_base = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("WTF");
            System.exit(1);
        }
        md_base.update(input.getBytes());

        LinkedList<Candidate> history = new LinkedList<Candidate>();
        HashMap<Character, Integer> fivesHistory = new HashMap<Character, Integer>();
        int nextIndex;
        for (nextIndex = 0; nextIndex < 1000; nextIndex++) {
            addCandidate(history, fivesHistory, md_base, nextIndex, keyStretching);
        }

        while (found < 64) {
            Candidate candidate = history.poll();
            for (char five: candidate.fives) {
                int newCount = fivesHistory.get(five) - 1;
                if (newCount <= 0) {
                    fivesHistory.remove(five);
                } else {
                    fivesHistory.put(five, newCount);
                }
            }

            addCandidate(history, fivesHistory, md_base, nextIndex, keyStretching);

            if (fivesHistory.containsKey(candidate.three)) {
                found++;
                System.out.println((nextIndex - 1000) + "  " + candidate.hash);
            }


            nextIndex++;
            if (nextIndex % 100000 == 0) {
                System.out.println(nextIndex);
            }
        }
        
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
            doorId = "abc";
            keyStretching = true;
        } else if (args[0].equals("part1")) {
            doorId = "cuanljph";
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

