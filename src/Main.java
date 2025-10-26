/**
 * Minimal demo runner that exercises the elevator simulation for a fixed number of ticks.
 */
public final class Main {
    public static void main(String[] args) {
        Elevator elevator = new Elevator(10);
        elevator.hallCall(3, Direction.UP);
        elevator.carCall(7);
        elevator.hallCall(2, Direction.DOWN);

        final String rowFormat = "%4s | %5s | %11s | %8s | %13s | %-12s | %-12s%n";
        System.out.printf(rowFormat, "Tick", "Floor", "Direction", "Door", "Activity", "UpQueue", "DownQueue");
        for (int tick = 1; tick <= 15; tick++) {
            elevator.tick();
            ElevatorState state = elevator.getState();
            System.out.printf(
                    rowFormat,
                    tick,
                    state.getCurrentFloor(),
                    state.getDirection(),
                    state.getDoorState(),
                    state.getActivity(),
                    state.getUpQueue(),
                    state.getDownQueue());
        }
    }
}
