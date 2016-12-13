import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;

public class Puzzle11 {

    private enum Element { POLONIUM, THULIUM, PROMETHIUM, RUTHENIUM, COBALT, HYDROGEN, LITHIUM, ELERIUM, DULITHIUM };
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

        public boolean notFried(HashSet<Moveable> floor) {
            return true;
        }

        public String toString() {
            return "<" + this.element + "," + this.type + ">";
        }

        public int hashCode() {
            return Objects.hash(this.floor, this.element, this.type);
        }
    }

    private static class Microchip extends Moveable {
        public Microchip (Element element) {
            super(element, Type.MICROCHIP);
        }

        public boolean notFried(HashSet<Moveable> floor) {
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
        public HashSet<Moveable> contains;

        public ElevatorMove(int start, int end) {
            this.startFloor = start;
            this.endFloor = end;
            contains = new HashSet<Moveable>();
        }

        public ElevatorMove(int start, int end, HashSet<Moveable> items) {
            this.startFloor = start;
            this.endFloor = end;
            contains = new HashSet<Moveable>();
            for (Moveable item: items) {
                contains.add(item);
            }
        }

        public String toString() {
            String result = "";
            result += startFloor + "->" + endFloor + contains;
            return result;
        }
    }

    private static class MoveState {
        public ArrayList<ElevatorMove> elevator;
        public ArrayList<HashSet<Moveable>> floors;
        Boolean valid;
        Boolean success;
        int hash;
        int elevatorFloor;
        int moves;
        int floorHashes;

        MoveState(int numFloors) {
            valid = null;
            success = null;
            hash = -1;
            elevatorFloor = 0;
            moves = 0;
            floors = new ArrayList<HashSet<Moveable>>();
            elevator = new ArrayList<ElevatorMove>();

            for (int i = 0; i < numFloors; i++) {
                HashSet<Moveable> floor = new HashSet<Moveable>();
                floors.add(floor);
            }
        }

        MoveState(MoveState other) {
            valid = null;
            success = null;
            hash = -1;
            elevatorFloor = other.elevatorFloor;
            moves = other.moves;
            elevator = new ArrayList<ElevatorMove>(other.elevator);

            floors = new ArrayList<HashSet<Moveable>>();
            for (int i = 0; i < other.floors.size(); i++) {
                HashSet<Moveable> floor = new HashSet<Moveable>();
                floors.add(floor);
                HashSet<Moveable> otherFloor = other.floors.get(i);
                for (Moveable item: otherFloor) {
                    floor.add(item);
                }
            }
        }

        public int elevatorFloor() {
            return this.elevatorFloor;
            /**int result = -1;
            if (elevator.size() > 0) {
                result = elevator.get(elevator.size() - 1).endFloor;
            }
            return result;**/
        }

        public int hashCode() {
            if (hash == -1) {
                hash =  Objects.hash(this.elevatorFloor, this.floors);
            }
            return hash;
        }

        public boolean equals(Object ob) {
            MoveState b = (MoveState)ob;
            return (this.elevatorFloor == b.elevatorFloor && this.floors.equals(b.floors));
        }

        public boolean validate() {
            valid = true;
            success = true;
            int maxFloor = floors.size() - 1;
            for (int i = 0; i < floors.size(); i++) {
                HashSet<Moveable> floor = floors.get(i);
                //System.out.println("--------" + i + "  " + floor.size() + "  " + maxFloor);
                for (Moveable item: floor) {
                    if (i != maxFloor) {
                        success = false;
                    }
                    if (item.type == Type.MICROCHIP) {
                        Microchip chip = (Microchip)item;
                        boolean notFried = chip.notFried(floor);
                        //System.out.println(notFried + "  " + item + "  " + floor);
                        valid = valid && notFried;
                        
                    }
                }
            }
            return valid;
        }

        public boolean move(HashSet<Moveable> items, int floor, int dir) {
            if (floor + dir >= floors.size() || floor + dir < 0) {
                return false;
            }

            boolean success = true;
            HashSet<Moveable> sourceFloor = floors.get(floor);
            HashSet<Moveable> destFloor = floors.get(floor + dir);
            for (Moveable item: items) {
                if (!sourceFloor.remove(item)) {
                    success = false;
                    break;
                }
                destFloor.add(item);
            }
            if (success) {
                //ElevatorMove move = new ElevatorMove(floor, floor + dir, items);
                //elevator.add(move);
                elevatorFloor += dir;
                moves += 1;
            }
            return success;
        }

        public String toString() {
            String result = "";
            for (HashSet<Moveable> floor: floors) {
                for (Moveable item: floor) {
                    result = result + item + ",";
                }
                result = result + "\n";
            }
            result += "-------------\n";
            return result;
        }
    }

    private static class Solver {
        int shortest;
        int longest;
        HashSet<MoveState> seen;
        ArrayList<MoveState> toCheck;

        public Solver() {
            shortest = 100;
            longest = 0;
            seen = new HashSet<MoveState>();
            toCheck = new ArrayList<MoveState>();
        }
        
        public int solve(MoveState inputState) {
            toCheck.add(inputState);
            int count = 0;
            while (toCheck.size() > 0) {
                evalute(toCheck.remove(0));
                count++;

                if (count % 10000 == 0) {
                    System.out.println(count + "  " + longest + "  " + seen.size() + "  " + toCheck.size());
                }
            }

            for (MoveState move: seen) {
                //System.out.println(move);
                //System.out.println(move.hashCode());
            }

            return shortest;
        }

        public int evalute(MoveState inputState) {
            if (seen.contains(inputState)) {
            } else {
                inputState.validate();
                seen.add(inputState);
                if (inputState.success) {
                    shortest = Math.min(inputState.moves, shortest);
                    System.out.println(inputState.moves);
                    System.out.println(shortest);
                    toCheck.clear();
                } else if (inputState.valid) {
                    longest = Math.max(longest, inputState.moves);
                    int currentFloor = inputState.elevatorFloor();
                    HashSet<Moveable> floor = inputState.floors.get(currentFloor);
                    HashSet<HashSet<Moveable>> nextMoves = new HashSet<HashSet<Moveable>>();
                    for (Moveable left: floor) {
                        for (Moveable right: floor) {
                            nextMoves.add(new HashSet<Moveable>(Arrays.asList(left, right)));
                        }
                        nextMoves.add(new HashSet<Moveable>(Arrays.asList(left)));
                    }

                    for (HashSet<Moveable> items: nextMoves) {
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
        if (args.length != 1 || (!args[0].equals("test") && !args[0].equals("part1") && !args[0].equals("part2"))) {
            System.err.println("Usage: solve.py [test|part1|part2]");
            for (int i = 0; i < args.length; i++) {
                System.err.println(args[i]);
            }
            System.exit(1);
        }

        if (args[0].equals("part1") || args[0].equals("part2")) {
            MoveState input = new MoveState(4);
            input.floors.get(0).add(new Microchip(Element.THULIUM));
            input.floors.get(0).add(new Microchip(Element.RUTHENIUM));
            input.floors.get(0).add(new Microchip(Element.COBALT));
            input.floors.get(0).add(new Moveable(Element.POLONIUM, Type.GENERATOR));
            input.floors.get(0).add(new Moveable(Element.THULIUM, Type.GENERATOR));
            input.floors.get(0).add(new Moveable(Element.PROMETHIUM, Type.GENERATOR));
            input.floors.get(0).add(new Moveable(Element.RUTHENIUM, Type.GENERATOR));
            input.floors.get(0).add(new Moveable(Element.COBALT, Type.GENERATOR));

            if (args[0].equals("part2")) {
                input.floors.get(0).add(new Microchip(Element.ELERIUM));
                input.floors.get(0).add(new Microchip(Element.DULITHIUM));
                input.floors.get(0).add(new Moveable(Element.ELERIUM, Type.GENERATOR));
                input.floors.get(0).add(new Moveable(Element.DULITHIUM, Type.GENERATOR));
            }

            input.floors.get(1).add(new Microchip(Element.POLONIUM));
            input.floors.get(1).add(new Microchip(Element.PROMETHIUM));
            //input.elevator.add(new ElevatorMove(-1,0));

                Solver solver = new Solver();
                solver.solve(input);
        } else if (args[0].equals("test")) {
            MoveState input = new MoveState(4);
            input.floors.get(0).add(new Microchip(Element.HYDROGEN));
            input.floors.get(1).add(new Moveable(Element.HYDROGEN, Type.GENERATOR));
            input.floors.get(0).add(new Microchip(Element.LITHIUM));
            input.floors.get(2).add(new Moveable(Element.LITHIUM, Type.GENERATOR));
            //input.elevator.add(new ElevatorMove(-1,0));

            Solver solver = new Solver();
            solver.solve(input);
        }

        System.exit(0);
    }
}

