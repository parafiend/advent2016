import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
        int sslSupportedCount = 0;
        while (ipData.hasNextLine()) {
            HashSet<String> supernets = new HashSet<String>();
            HashSet<String> hypernets = new HashSet<String>();

            String ip = ipData.nextLine();
            boolean inBrackets = (ip.charAt(0) == '[' && ip.charAt(1) != ']') || (ip.charAt(1) == '[');
            boolean tlsEnabled = false;
            boolean tlsDisabled = false;
            char twoAgo = ip.charAt(0);
            char lastChar = ip.charAt(1);

            for (int i = 2; i < ip.length() && !tlsDisabled; i++) {
                char c = ip.charAt(i);
                boolean sslSupported = false;
                if (c == ']') {
                    inBrackets = false;
                } else if (c == '[') {
                    inBrackets = true;
                } else {
                    if(c == lastChar && lastChar != twoAgo && i + 1 < ip.length() && ip.charAt(i+1) == twoAgo) {
                        // System.out.println("" + twoAgo + lastChar + c + ip.charAt(i+1) + "  " + inBrackets);
                        if (inBrackets) {
                            tlsDisabled = true;
                            // break;
                        } else {
                            tlsEnabled = true;
                        }
                    }

                    if (c == twoAgo && lastChar != ']' && lastChar != '[' && !sslSupported) {
                        String sequence = "" + twoAgo + lastChar + c;
                        String inverse = "" + lastChar + c + lastChar;

                        // Hypernet sequence
                        if (inBrackets) {
                            if (supernets.contains(inverse)) {
                                sslSupportedCount++;
                                sslSupported = true;
                            } else {
                                hypernets.add(sequence);
                            }
                        // Supernet sequence
                        } else {
                            if (hypernets.contains(inverse)) {
                                sslSupportedCount++;
                                sslSupported = true;
                            } else {
                                supernets.add(sequence);
                            }
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
        System.out.println("SSL Enabled on " + sslSupportedCount + " addresses");

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

