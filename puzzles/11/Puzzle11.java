import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;

public class Puzzle11 {

    private enum Element { POLONIUM, THULUNIUM, PROMETHIUM, RUTHENIUM, COBALT, HYDROGEN, LITHIUM };
    private enum Type { GENERATOR, MICROCHIP };

    private static class Moveable {
        public int floor;
        public Element element;
        public Type type;

        public Moveable(Element element, Type type) {
            this.element = element;
            this.type = type;
        }

        public boolean equals(Moveable other) {
            return (this.element == other.element && this.type == other.type && this.floor == other.floor);
        }

        public static boolean notFried(ArrayList<Moveable> floor) {
            return true;
        }

        public String toString() {
            return "<" + this.element + "," + this.type + ">";
        }
    }

    private static class Microchip extends Moveable {
        public Microchip (Element element) {
            super(element, Type.MICROCHIP);
        }

        public boolean notFriend(ArrayList<Moveable> floor) {
            boolean powered = false;
            boolean badGen = false;
            for (Moveable item: floor) {
                if (item.type == Type.GENERATOR && item.element == this.element) {
                    powered = true;
                } else if (item.type == Type.GENERATOR && item.element != this.element) {
                    badGen = true;
                }
            }
            return !badGen || powered;
        }
    }

    private static class ElevatorMove {
        public int startFloor;
        public int endFloor;
        public ArrayList<Moveable> contains;

        public ElevatorMove(int start, int end) {
            this.startFloor = start;
            this.endFloor = end;
            contains = new ArrayList<Moveable>();
        }

        public ElevatorMove(int start, int end, ArrayList<Moveable> items) {
            this.startFloor = start;
            this.endFloor = end;
            contains = new ArrayList<Moveable>();
            for (Moveable item: items) {
                contains.add(item);
            }
        }


    }

    private static class MoveState {
        public ArrayList<ElevatorMove> elevator;
        public ArrayList<ArrayList<Moveable>> floors;
        Boolean valid;
        Boolean success;

        MoveState(int numFloors) {
            valid = null;
            success = null;
            floors = new ArrayList<ArrayList<Moveable>>();
            elevator = new ArrayList<ElevatorMove>();

            for (int i = 0; i < numFloors; i++) {
                ArrayList<Moveable> floor = new ArrayList<Moveable>();
                floors.add(floor);
            }
        }

        MoveState(MoveState other) {
            valid = null;
            success = null;
            elevator = new ArrayList<ElevatorMove>(other.elevator);

            floors = new ArrayList<ArrayList<Moveable>>();
            for (int i = 0; i < other.floors.size(); i++) {
                ArrayList<Moveable> floor = new ArrayList<Moveable>();
                floors.add(floor);
                ArrayList<Moveable> otherFloor = other.floors.get(i);
                for (int j = 0; j < otherFloor.size(); j++) {
                    floor.add(otherFloor.get(j));
                }
            }
        }

        public int elevatorFloor() {
            int result = -1;
            if (elevator.size() > 0) {
                result = elevator.get(elevator.size() - 1).endFloor;
            }
            return result;
        }

        public int hashCode() {
            return Objects.hash(this.elevatorFloor(), floors);
        }

        public boolean equals(MoveState b) {
            return (this.elevatorFloor() == b.elevatorFloor() && this.floors.equals(b.floors));
        }

        public boolean validate() {
            valid = true;
            success = true;
            int maxFloor = floors.size() - 1;
            for (int i = 0; i < floors.size(); i++) {
                ArrayList<Moveable> floor = floors.get(i);
                for (Moveable item: floor) {
                    if (i != maxFloor) {
                        success = false;
                    }
                    if (item.type == Type.MICROCHIP) {
                        boolean notFried = item.notFried(floor);
                        // System.out.println(notFried + "  " + item + "  " + floor);
                        valid = valid && notFried;
                        
                    }
                }
            }
            return valid;
        }

        public boolean move(ArrayList<Moveable> items, int floor, int dir) {
            if (floor + dir >= floors.size() || floor + dir < 0) {
                return false;
            }

            boolean success = true;
            ArrayList<Moveable> sourceFloor = floors.get(floor);
            ArrayList<Moveable> destFloor = floors.get(floor + dir);
            for (Moveable item: items) {
                if (!sourceFloor.remove(item)) {
                    success = false;
                    break;
                }
                destFloor.add(item);
            }
            if (success) {
                ElevatorMove move = new ElevatorMove(floor, floor + dir, items);
                elevator.add(move);
            }
            return success;
        }
    }

    private static class Solver {
        int shortest;
        int longest;
        HashSet<MoveState> seen;
        ArrayList<MoveState> toCheck;

        public Solver() {
            shortest = -1;
            longest = 0;
            seen = new HashSet<MoveState>();
            toCheck = new ArrayList<MoveState>();
        }
        
        public int solve(MoveState inputState) {
            toCheck.add(inputState);
            int count = 0;
            while (shortest < 0 && count < 1000000) {
                evalute(toCheck.remove(0));
                count++;

                if (count % 10000 == 0) {
                    System.out.println(count + "  " + longest + "  " + seen.size() + "  " + toCheck.size());
                }
            }
            return shortest;
        }

        public int evalute(MoveState inputState) {
            if (seen.contains(inputState)) {
            } else {
                inputState.validate();
                seen.add(inputState);
                if (inputState.success) {
                    shortest = Math.min(inputState.elevator.size(), shortest);
                    System.out.println(inputState.elevator);
                    System.out.println(shortest);
                } else if (inputState.valid) {
                    longest = Math.max(longest, inputState.elevator.size());
                    int currentFloor = inputState.elevatorFloor();
                    ArrayList<Moveable> floor = inputState.floors.get(currentFloor);
                    for (Moveable item: floor) {
                        for (int i = floor.indexOf(item)+1; i < floor.size(); i++) {
                            ArrayList<Moveable> items = new ArrayList<Moveable>();
                            items.add(item);
                            items.add(floor.get(i));
                            MoveState upState = new MoveState(inputState);
                            upState.move(items, currentFloor, 1);
                            toCheck.add(upState);

                            MoveState downState = new MoveState(inputState);
                            downState.move(items, currentFloor, -1);
                            toCheck.add(downState);
                        }
                        ArrayList<Moveable> items = new ArrayList<Moveable>();
                        items.add(item);
                        MoveState upState = new MoveState(inputState);
                        upState.move(items, currentFloor, 1);
                        toCheck.add(upState);

                        MoveState downState = new MoveState(inputState);
                        downState.move(items, currentFloor, -1);
                        toCheck.add(downState);
                    }
                }
            }
                    
            return -1;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1 || (!args[0].equals("test") && !args[0].equals("part1"))) {
            System.err.println("Usage: solve.py [test|part1]");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }

        if (args[0].equals("test")) {
            MoveState input = new MoveState(4);
            input.floors.get(0).add(new Microchip(Element.HYDROGEN));
            input.floors.get(1).add(new Moveable(Element.HYDROGEN, Type.GENERATOR));
            input.floors.get(0).add(new Microchip(Element.LITHIUM));
            input.floors.get(2).add(new Moveable(Element.LITHIUM, Type.GENERATOR));
            input.elevator.add(new ElevatorMove(-1,0));

            Solver solver = new Solver();
            solver.solve(input);
        } 

        System.exit(0);
    }
}

