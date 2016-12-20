import java.io.File;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle12 {

    private static final int regFlag = 0x1000000;

    private enum Instr {CPY, INC, DEC, JNZ};

    private static class Command {
        // Instructions should be subclasses but I'm buzzed and lazy
        private Instr instruction;
        private int arg1;
        private boolean arg1Reg;
        private int arg2;

        public Command(Instr instruct, String arg1, String arg2) {
            this.instruction = instruct; 
            try {
                this.arg1 = Integer.valueOf(arg1);
                arg1Reg = false;
            } catch (NumberFormatException e) {
                this.arg1 = (arg1.charAt(0) - 'a');
                arg1Reg = true;
            }

            try {
                this.arg2 = Integer.valueOf(arg2);
            } catch (NumberFormatException e) {
                this.arg2 = (arg2.charAt(0) - 'a');
            }
        }

        public Command(Instr instruct, String arg1) {
            this.instruction = instruct;
            this.arg1 = (arg1.charAt(0) - 'a');
        }


        public int execute(int[] registers) {
            int move = 1;
            //System.out.println(instruction + "  " + arg1 + "  " + arg2);
            switch (this.instruction) {
                case CPY:
                    int source = arg1;
                    if (arg1Reg) {
                        source = registers[arg1];
                    }
                    registers[arg2] = source;
                    break;
                case INC:
                    registers[this.arg1]++;
                    break;
                case DEC:
                    registers[this.arg1]--;
                    break;
                case JNZ:
                    int gate = registers[arg1];
                    if (gate != 0) {
                        move = arg2;
                    }
                    break;
                default:
                    System.out.println("Qu'est que le fuck??");

            }

            return move;
        }
    }

    public static int solveFile(String inputFile, boolean isPart2){
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

        ArrayList<Command> commands = new ArrayList<Command>();
        while (fileData.hasNextLine()) {
            String fullCommand = fileData.nextLine();
            String[] strCommand = fullCommand.split(" ");
            Command command = null;
            switch(strCommand[0]) { 
                case "cpy":
                    command = new Command(Instr.CPY, strCommand[1], strCommand[2]);
                    break;
                case "inc":
                    command = new Command(Instr.INC, strCommand[1]);
                    break;
                case "dec":
                    command = new Command(Instr.DEC, strCommand[1]);
                    break;
                case "jnz":
                    command = new Command(Instr.JNZ, strCommand[1], strCommand[2]);
                    break;
                default:
                    System.out.println("Qu'est que le fuck??");
            }

            if (command != null) {
                commands.add(command);
            }
        }

        int[] registers = new int[4];
        if (isPart2) {
            registers[2] = 1;
        }
        int pc = 0;
        int lastD = registers[3];
        while (pc < commands.size()) {
            int incr = commands.get(pc).execute(registers);
            pc += incr;
            if (registers[3] != lastD) {
                lastD = registers[3];
                for (int i = 0; i < 4; i++) {
                    System.out.print(registers[i] + ",");
                }
                System.out.println();
            }
        }

        for (int i = 0; i < 4; i++) {
            System.out.print(registers[i] + ",");
        }
        System.out.println();

        return 0;
    }

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 2) {
            System.err.println("Usage: solve.py <input text file> [y]");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }
        boolean isPart2 = args.length == 2;
        solveFile(args[0], isPart2);
        System.exit(0);
    }
}

