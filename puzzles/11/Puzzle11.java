import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.PriorityQueue;

public class Puzzle11 {

    private enum Element { POLONIUM, THULIUM, PROMETHIUM, RUTHENIUM, COBALT, HYDROGEN, LITHIUM, ELERIUM, DULITHIUM };
    private enum Type { GENERATOR, MICROCHIP };

    private static final int MOVE_WEIGHT = 10;
    private static final int DISTANCE_WEIGHT = 15;
    private static final int MAX_DISTANCE = 100;

    private enum Moveable {
        
        POGE(Element.POLONIUM,Type.GENERATOR),
        POMI(Element.POLONIUM,Type.MICROCHIP),
        THGE(Element.THULIUM,Type.GENERATOR),
        THMI(Element.THULIUM,Type.MICROCHIP),
        PRGE(Element.PROMETHIUM,Type.GENERATOR),
        PRMI(Element.PROMETHIUM,Type.MICROCHIP),
        RUGE(Element.RUTHENIUM,Type.GENERATOR),
        RUMI(Element.RUTHENIUM,Type.MICROCHIP),
        COGE(Element.COBALT,Type.GENERATOR),
        COMI(Element.COBALT,Type.MICROCHIP),
        HYGE(Element.HYDROGEN,Type.GENERATOR),
        HYMI(Element.HYDROGEN,Type.MICROCHIP),
        LIGE(Element.LITHIUM,Type.GENERATOR),
        LIMI(Element.LITHIUM,Type.MICROCHIP),
        ELGE(Element.ELERIUM,Type.GENERATOR),
        ELMI(Element.ELERIUM,Type.MICROCHIP),
        DUGE(Element.DULITHIUM,Type.GENERATOR),
        DUMI(Element.DULITHIUM,Type.MICROCHIP);

        public Element element;
        public Type type;

        private Moveable(Element element, Type type) {
            this.element = element;
            this.type = type;
        }

        public boolean equals(Moveable other) {
            return (this.element == other.element && this.type == other.type);
        }

        public boolean notFried(EnumSet<Moveable> floor) {
            return true;
        }

        public String toString() {
            return "<" + this.element + "," + this.type + ">";
        }
    }

    private static final EnumSet<Moveable> GENERATORS = EnumSet.of(Moveable.POGE, Moveable.THGE, Moveable.PRGE, Moveable.RUGE, Moveable.COGE, Moveable.HYGE, Moveable.LIGE, Moveable.LIMI, Moveable.ELGE, Moveable.DUGE);
    private static final EnumSet<Moveable> MICROCHIPS = EnumSet.of(Moveable.POMI, Moveable.THMI, Moveable.PRMI, Moveable.RUMI, Moveable.COMI, Moveable.HYMI, Moveable.LIMI, Moveable.LIMI, Moveable.ELMI, Moveable.DUMI);

    /**
    private static class Microchip extends Moveable {
        public Microchip (Element element) {
            super(element, Type.MICROCHIP);
        }

    }**/

    private static class MoveState {
        public LinkedList<EnumSet<Moveable>> floors;
        Boolean valid;
        Boolean success;
        int hash;
        int elevatorFloor;
        int moves;
        int floorHashes;
        int score;

        MoveState(int numFloors) {
            valid = null;
            success = null;
            hash = -1;
            elevatorFloor = 0;
            moves = 0;
            score = 0;
            floors = new LinkedList<EnumSet<Moveable>>();

            for (int i = 0; i < numFloors; i++) {
                EnumSet<Moveable> floor = EnumSet.noneOf(Moveable.class);
                floors.add(floor);
            }
        }

        MoveState(MoveState other) {
            valid = null;
            success = null;
            hash = -1;
            elevatorFloor = other.elevatorFloor;
            moves = other.moves;
            score = 10*moves;

            floors = new LinkedList<EnumSet<Moveable>>();
            for (int i = 0; i < other.floors.size(); i++) {
                EnumSet<Moveable> floor = EnumSet.noneOf(Moveable.class);
                floors.add(floor);
                EnumSet<Moveable> otherFloor = other.floors.get(i);
                for (Moveable item: otherFloor) {
                    floor.add(item);
                    int delta = other.floors.size() - 1 - 1;
                    score += DISTANCE_WEIGHT * delta;
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

        public void rescore() {
            score = moves;
            for (int i = 0; i < floors.size(); i++) {
                EnumSet<Moveable> floor = floors.get(i);
                int maxFloor = floors.size() - 1;
                score += floor.size() * (maxFloor - i);
            }
                
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
                EnumSet<Moveable> floor = floors.get(i);
                //System.out.println("--------" + i + "  " + floor.size() + "  " + maxFloor);
                /**
                EnumSet<Moveable> generators = EnumSet.copyOf(floor);
                generators.retainAll(GENERATORS);
                EnumSet<Moveable> microchips = EnumSet.copyOf(floor);
                microchips.retainAll(MICROCHIPS);
                valid = generators.isEmpty() || generators.containsAll(microchips);
                if (i != maxFloor && floor.size() != 0) {
                    success = false;
                }
                **/
                
                for (Moveable item: floor) {
                    if (i != maxFloor) {
                        success = false;
                    }
                    
                    if (item.type == Type.MICROCHIP) {
                            boolean powered = false;
                            boolean badGen = false;
                            for (Moveable iteml: floor) {
                                if (iteml.type == Type.GENERATOR && iteml.element == item.element) {
                                    powered = true;
                                    break;
                                } else if (iteml.type == Type.GENERATOR && iteml.element != item.element) {
                                    badGen = true;
                                }
                            }
                        boolean notFried = !badGen || powered;
                        //Microchip chip = (Microchip)item;
                        //boolean notFried = chip.notFried(floor);
                        //System.out.println(notFried + "  " + item + "  " + floor);
                        valid = valid && notFried;
                        
                    }
                    
                }
                
            }
            return valid;
        }

        public boolean move(EnumSet<Moveable> items, int floor, int dir) {
            if (floor + dir >= floors.size() || floor + dir < 0) {
                return false;
            }

            boolean success = true;
            EnumSet<Moveable> sourceFloor = floors.get(floor);
            EnumSet<Moveable> destFloor = floors.get(floor + dir);
            for (Moveable item: items) {
                if (!sourceFloor.remove(item)) {
                    success = false;
                    break;
                }
                destFloor.add(item);
                score += (-dir * DISTANCE_WEIGHT) + MOVE_WEIGHT;
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
            for (EnumSet<Moveable> floor: floors) {
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
        //LinkedList<MoveState> toCheck;
        PriorityQueue<MoveState> toCheck;

        public Solver() {
            shortest = MAX_DISTANCE;
            longest = 0;
            seen = new HashSet<MoveState>();
            toCheck = new PriorityQueue<MoveState>(10000, (MoveState o1, MoveState o2) -> o1.score - o2.score);
            //toCheck = new LinkedList<MoveState>();
        }
        
        public int solve(MoveState inputState) {
            toCheck.add(inputState);
            int count = 0;
            while (toCheck.size() > 0) {
                MoveState test = toCheck.poll();
                evalute(test);
                //evalute(toCheck.remove(0));
                count++;

                if (count % 2000000 == 0) {
                    System.out.println(count + "  " + test.score + "  " + test.moves + "  " + longest + "  " + seen.size() + "  " + toCheck.size());
                    System.out.println(test);
                }
            }

            for (MoveState move: seen) {
                //System.out.println(move);
                //System.out.println(move.hashCode());
            }
            //System.out.println(toCheck);
                    System.out.println(count + "  " +  longest + "  " + seen.size() + "  " + toCheck.size());

            return shortest;
        }

        public int evalute(MoveState inputState) {
            if (false) {
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
                    if (inputState.moves+1 > MAX_DISTANCE) {
                        return -1;
                    }
                    int currentFloor = inputState.elevatorFloor();
                    EnumSet<Moveable> floor = inputState.floors.get(currentFloor);
                    HashSet<EnumSet<Moveable>> nextMoves = new HashSet<EnumSet<Moveable>>();
                    for (Moveable left: floor) {
                        for (Moveable right: floor) {
                            if(left.type == right.type || left.element == right.element) {
                                nextMoves.add(EnumSet.of(left, right));
                            }
                        }
                        nextMoves.add(EnumSet.of(left));
                    }

                    for (EnumSet<Moveable> items: nextMoves) {
                        MoveState upState = new MoveState(inputState);
                        upState.move(items, currentFloor, 1);
                        if (upState.validate() && upState.score < MAX_DISTANCE*10 && !seen.contains(upState)) {
                            seen.add(upState);
                            toCheck.add(upState);
                            //System.out.println(upState);
                        }

                        MoveState downState = new MoveState(inputState);
                        downState.move(items, currentFloor, -1);
                        if (downState.validate() && downState.score < MAX_DISTANCE*10 && !seen.contains(downState)) {
                            toCheck.add(downState);
                            seen.add(downState);
                            //System.out.println(downState);
                        }
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
            input.floors.get(0).add(Moveable.THMI);
            input.floors.get(0).add(Moveable.RUMI);
            input.floors.get(0).add(Moveable.COMI);
            input.floors.get(0).add(Moveable.POGE);
            input.floors.get(0).add(Moveable.THGE);
            input.floors.get(0).add(Moveable.PRGE);
            input.floors.get(0).add(Moveable.RUGE);
            input.floors.get(0).add(Moveable.COGE);

            if (args[0].equals("part2")) {
                input.floors.get(0).add(Moveable.ELMI);
                input.floors.get(0).add(Moveable.DUMI);
                input.floors.get(0).add(Moveable.ELGE);
                input.floors.get(0).add(Moveable.DUGE);
            }

            input.floors.get(1).add(Moveable.POMI);
            input.floors.get(1).add(Moveable.PRMI);
            //input.elevator.add(new ElevatorMove(-1,0));

                Solver solver = new Solver();
                solver.solve(input);
        } else if (args[0].equals("test")) {
            MoveState input = new MoveState(4);
            input.floors.get(0).add(Moveable.HYMI);
            input.floors.get(1).add(Moveable.HYGE);
            input.floors.get(0).add(Moveable.LIMI);
            input.floors.get(2).add(Moveable.LIGE);
            //input.elevator.add(new ElevatorMove(-1,0));

            Solver solver = new Solver();
            solver.solve(input);
        }

        System.exit(0);
    }
}

