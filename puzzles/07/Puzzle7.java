import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.SortedSet;
import java.util.Scanner;

public class Puzzle7 {
    public static int solve(String inputFile) {
        System.out.println("File is " + inputFile);
        Scanner ipData = null;
        try {
            ipData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        int tlsEnabledCount = 0;
        while (ipData.hasNextLine()) {
            String ip = ipData.nextLine();
            boolean inBrackets = (ip.charAt(0) == '[' && ip.charAt(1) != ']') || (ip.charAt(1) == '[');
            boolean tlsEnabled = false;
            boolean tlsDisabled = false;
            char twoAgo = ip.charAt(0);
            char lastChar = ip.charAt(1);

            for (int i = 2; i < ip.length() && !tlsDisabled; i++) {
                char c = ip.charAt(i);
                if (c == ']') {
                    inBrackets = false;
                } else if (c == '[') {
                    inBrackets = true;
                } else if (c == lastChar && lastChar != twoAgo) {
                    if (i + 1 < ip.length() && ip.charAt(i+1) == twoAgo) {
                        // System.out.println("" + twoAgo + lastChar + c + ip.charAt(i+1) + "  " + inBrackets);
                        if (inBrackets) {
                            tlsDisabled = true;
                            break;
                        } else {
                            tlsEnabled = true;
                        }
                    }
                }
                twoAgo = lastChar;
                lastChar = c;
            }

            boolean realEnabled = tlsEnabled && !tlsDisabled;
            if (realEnabled) {
                tlsEnabledCount++;
                System.out.println(realEnabled + "  " + tlsEnabled + "  " + tlsDisabled + "  " + ip);
            }
            // System.out.println(realEnabled + "  " + tlsEnabled + "  " + tlsDisabled + "  " + ip);
        }
        System.out.println("TLS Enabled on " + tlsEnabledCount + " addresses");

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

