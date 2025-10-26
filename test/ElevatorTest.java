import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests that verify the most important scheduling scenarios.
 */
public class ElevatorTest {

    @Test
    void servesRequestsInScanOrder() {
        Elevator elevator = new Elevator(12);
        elevator.carCall(8);

        // Allow the cab to travel upward for a few ticks before adding a down request.
        for (int i = 0; i < 3; i++) {
            elevator.tick();
        }
        elevator.hallCall(2, Direction.DOWN);

        List<Integer> stops = collectStops(elevator, 30);
        assertEquals(List.of(8, 2), stops, "Up run completes before reversing downward.");
    }

    @Test
    void ignoresDuplicateRequests() {
        Elevator elevator = new Elevator(6);
        elevator.carCall(4);
        elevator.carCall(4); // duplicate car call
        elevator.hallCall(4, Direction.UP); // duplicate hall call

        List<Integer> stops = collectStops(elevator, 15);
        long uniqueStopsAtFour = stops.stream().filter(floor -> floor == 4).count();
        assertEquals(1, uniqueStopsAtFour, "Duplicate calls must be deduplicated.");
    }

    @Test
    void returnsToIdleAfterCompletingWork() {
        Elevator elevator = new Elevator(5);
        elevator.carCall(5);

        collectStops(elevator, 20);
        ElevatorState state = elevator.getState();

        assertEquals(5, state.getCurrentFloor());
        assertEquals(ElevatorMode.IDLE, state.getActivity());
        assertEquals(DoorState.CLOSED, state.getDoorState());
        assertTrue(state.getUpQueue().isEmpty());
        assertTrue(state.getDownQueue().isEmpty());
    }

    private List<Integer> collectStops(Elevator elevator, int ticks) {
        List<Integer> stops = new ArrayList<>();
        for (int i = 0; i < ticks; i++) {
            elevator.tick();
            ElevatorState state = elevator.getState();
            if (state.getDoorState() == DoorState.OPEN) {
                stops.add(state.getCurrentFloor());
            }
        }
        return stops;
    }
}
