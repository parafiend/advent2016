import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.util.Clon
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle5 {
    public static int solve(String input, boolean varIndex) {
        Character password[] = new Character[8];
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
        while (found < 8) {
            MessageDigest md = null;
            try {
                md = (MessageDigest)md_base.clone();
            } catch (CloneNotSupportedException e) {
                System.out.println("WTF2");
                System.exit(1);
            }
            md.update(String.format("%d", count).getBytes());

            byte[] check = md.digest();
            if (check[0] == 0 && check[1] == 0 && (check[2] & 0xf0) == 0) {
                if (!varIndex) {
                    password[charIndex++] = Integer.toHexString(check[2] & 0x0f).charAt(0);
                    found++;
                } else {
                    charIndex = Integer.toHexString(check[2] & 0x0f).charAt(0) - '0';
                    if (charIndex < 8 && charIndex >= 0 && password[charIndex] == null) {
                        found++;
                        password[charIndex] = Integer.toHexString(check[3] & 0xf0).charAt(0);
                    }
                }
            }
            count++;
            if (count % 100000 == 0) {
                //System.out.println(count);
            }
        }
        for (int i = 0; i < 8; i++) {
            System.out.print(password[i]);
        }
        System.out.println("");
        
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
        boolean variantIndex = false;
        if (args[0].equals("test")) {
            doorId = "abc";
            variantIndex = true;
        } else if (args[0].equals("part1")) {
            doorId = "cxdnnyjw";
        } else if (args[0].equals("part2")) {
            doorId = "cxdnnyjw";
            variantIndex = true;
        } else {
            System.err.println("Invalid part! Expected one of test|part1|part2");
            System.exit(1);
        }

        solve(doorId, variantIndex);
        System.exit(0);
    }
}

