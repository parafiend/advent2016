import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Puzzle21 {

    public static class Mangler {

        //All based on an 8 char string, values in left-rotations
        private static int[] REVERSE_BY_CHAR = {1, 1, 6, 2, 7, 3, 0, 4};
        public String base;
        public StringBuilder buffer;

        private Pattern swapPosP;
        private Pattern swapCharP;
        private Pattern rotateP;
        private Pattern rotateCharP;
        private Pattern reverseP;
        private Pattern moveP;

        public Mangler(String input) {
            base = input;
            buffer = new StringBuilder(input);

            swapPosP = Pattern.compile("swap position (\\d+) with position (\\d+)");
            swapCharP = Pattern.compile("swap letter (\\w) with letter (\\w)");
            rotateP = Pattern.compile("rotate (\\w+) (\\d+) steps?");
            rotateCharP = Pattern.compile("rotate based on position of letter (\\w)");
            reverseP = Pattern.compile("reverse positions (\\d+) through (\\d+)");
            moveP = Pattern.compile("move position (\\d+) to position (\\d+)");
        }

        public void swapPos(int x, int y) {
            char temp = buffer.charAt(x);
            buffer.setCharAt(x, buffer.charAt(y));
            buffer.setCharAt(y, temp);
        }

        public void swapChar(String x, String y) {
            int xPos = buffer.indexOf(x);
            int yPos = buffer.indexOf(y);
            swapPos(xPos, yPos);
        }

        public void rotate(int direction, int num) {
            int length = buffer.length();
            int actualRot = num % length;
            StringBuilder newBuffer = new StringBuilder();
            int startIndex = -1 * direction * actualRot;
            for (int i = startIndex; i < startIndex + length; i++) {
                // In Java, % is remainder, which could be negative
                // Force it to be positive
                int srcIndex = (((i % length) + length) % length);
                newBuffer.append(buffer.charAt(srcIndex));
            }
            System.out.println("  ROT" + direction + " " + actualRot + " " + buffer + "-> " + newBuffer);
            buffer = newBuffer;
        }

        public void rotateByChar(String x, boolean reverse) {
            int base = buffer.indexOf(x);
            if (!reverse) {
                if (base >= 4) {
                    base++;
                }
                base++;
                System.out.println("  x" + base);
                rotate(1, base);
            } else {
                int actual = REVERSE_BY_CHAR[base];
                System.out.println("  " + actual);
                rotate(-1, actual);
            }
        }

        public void reverse(int start, int end) {
            StringBuilder newSubstr = new StringBuilder(buffer.substring(start, end+1)).reverse();
            buffer.replace(start, end+1, newSubstr.toString());
        }

        public void move(int src, int dst) {
            char temp = buffer.charAt(src);
            buffer.deleteCharAt(src);
            buffer.insert(dst, temp);
        }

        public void parseAndExecute(String command, boolean reverse) {
            Matcher swapPosM = swapPosP.matcher(command);
            Matcher swapCharM = swapCharP.matcher(command);
            Matcher rotateM = rotateP.matcher(command);
            Matcher rotateCharM = rotateCharP.matcher(command);
            Matcher reverseM = reverseP.matcher(command);
            Matcher moveM = moveP.matcher(command);

            if (swapPosM.matches()) {
                swapPos(Integer.valueOf(swapPosM.group(1)), Integer.valueOf(swapPosM.group(2)));
            } else if (swapCharM.matches()) {
                swapChar(swapCharM.group(1), swapCharM.group(2));
            } else if (rotateM.matches()) {
                int direction = rotateM.group(1).equals("right") ? 1 : -1;
                direction = reverse ? -1 * direction : direction;
                rotate(direction, Integer.valueOf(rotateM.group(2)));
            } else if (rotateCharM.matches()) {
                rotateByChar(rotateCharM.group(1), reverse);
            } else if (reverseM.matches()) {
                reverse(Integer.valueOf(reverseM.group(1)), Integer.valueOf(reverseM.group(2)));
            } else if (moveM.matches()) {
                if (!reverse) {
                    move(Integer.valueOf(moveM.group(1)), Integer.valueOf(moveM.group(2)));
                } else {
                    move(Integer.valueOf(moveM.group(2)), Integer.valueOf(moveM.group(1)));
                }
            } else {
                System.out.println("WAAAAGGGGHHH");
            }
        
        }

        public String dump() {
            return buffer.toString();
        }
    }

    public static int solveFile(String inputFile, String input, boolean reverse){
        Scanner fileData = null;
        System.out.println("File is " + inputFile);
        try {
            fileData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        Mangler mangler = new Mangler(input);
        LinkedList<String> commands = new LinkedList<String>();
        while (fileData.hasNextLine()) {
            String command = fileData.nextLine();

            if (!reverse) {
                mangler.parseAndExecute(command, reverse);
                System.out.println(mangler.dump() + " -- " + command);
            } else {
                commands.addFirst(command);
            }
        }
        if (reverse) {
            Iterator<String> commandIter = commands.iterator();
            while (commandIter.hasNext()) {
                String command = commandIter.next();
                mangler.parseAndExecute(command, reverse);
                System.out.println(mangler.dump() + " -- " + command);
            }
        }
        System.out.println(mangler.dump());

        return 0;
    }



    public static void main(String[] args) {
        if (args.length != 1 && args.length != 2) {
            System.err.println("Usage: solve.py <input text file> [2]");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }

        String input = "abcdefgh";
        boolean reverse = false;
        if (args[0].equals("test.txt")) {
            input = "abcde";
            if (args.length > 1 && args[1].equals("2")) {
                input = "gbhafcde";
                reverse = true;
                args[0] = "input.txt";
            }
        } else if (args.length > 1 && args[1].equals("2")) {
            input = "fbgdceah";
            reverse = true;
        }

        solveFile(args[0], input, reverse);
        System.exit(0);
    }
}

