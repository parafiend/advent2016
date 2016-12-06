import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle3 {

    public static boolean checkTriangle(int[] lengths) {
        boolean isATriangle = true;

        for (int i = 0; i < lengths.length; i++) {
            int next = (i+1) % lengths.length;
            int last = (i+2) % lengths.length;

            if ((lengths[i] + lengths[next]) <= lengths[last]) {
                System.out.println((lengths[i] + lengths[next]) + " < " + lengths[last]);
                System.out.println("------------------");
                isATriangle = false;
                break;
            } else {
                System.out.println((lengths[i] + lengths[next]) + " > " + lengths[last]);
            }
        }

        if (isATriangle) {
            System.out.println(lengths[0] + ", " + lengths[1] + ", " + lengths[2]);
            System.out.println("------------------");
        }

        return isATriangle;
    }

    public static int solve(String inputFile){
        Scanner triData = null;
        System.out.println("File is " + inputFile);
        try {
            triData = new Scanner(new File(inputFile)).useDelimiter("\\s*,\\s*");
        }
        catch (IOException e) {
            System.err.println("Unable to open " + inputFile);
            e.printStackTrace();
            return 1;
        }

        int numReal = 0;
        int numReal2 = 0;
        int vertTriangles[][] = new int[3][3];
        int vertCount = 0;
        while (triData.hasNextLine()) {
            String triangle = triData.nextLine();
            String[] strLengths = triangle.split(" +");
            int[] lengths = new int[3];
            int foundLengths = 0;

            for (int i = 0; i < strLengths.length; i++) {
                if (!strLengths[i].equals("")) {
                    int length = Integer.parseInt(strLengths[i]);
                    vertTriangles[foundLengths][vertCount] = length;
                    lengths[foundLengths++] = length;
                    // System.err.println(lengths[foundLengths-1]);
                }
            }
            
            vertCount++;
            if (vertCount >= 3) {
                for (int i = 0; i < 3; i++) {
                    if (checkTriangle(vertTriangles[i])) {
                        numReal2++;
                    }
                }
                vertCount = 0;
            }

            if (checkTriangle(lengths)) {
                numReal++;
            }
        }

        System.out.println("Num valid triangles: " + numReal);
        System.out.println("Num vert triangles: " + numReal2);

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

