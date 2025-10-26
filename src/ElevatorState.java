import java.util.List;
import java.util.Objects;

/**
 * Immutable snapshot describing the elevator position, motion, and queued work.
 */
public final class ElevatorState {
    private final int currentFloor;
    private final Direction direction;
    private final DoorState doorState;
    private final ElevatorMode activity;
    private final List<Integer> upQueue;
    private final List<Integer> downQueue;

    public ElevatorState(
            int currentFloor,
            Direction direction,
            DoorState doorState,
            ElevatorMode activity,
            List<Integer> upQueue,
            List<Integer> downQueue) {
        this.currentFloor = currentFloor;
        this.direction = Objects.requireNonNull(direction, "direction");
        this.doorState = Objects.requireNonNull(doorState, "doorState");
        this.activity = Objects.requireNonNull(activity, "activity");
        this.upQueue = List.copyOf(upQueue);
        this.downQueue = List.copyOf(downQueue);
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public DoorState getDoorState() {
        return doorState;
    }

    public ElevatorMode getActivity() {
        return activity;
    }

    public List<Integer> getUpQueue() {
        return upQueue;
    }

    public List<Integer> getDownQueue() {
        return downQueue;
    }

    @Override
    public String toString() {
        return "ElevatorState{" +
                "floor=" + currentFloor +
                ", direction=" + direction +
                ", door=" + doorState +
                ", activity=" + activity +
                ", upQueue=" + upQueue +
                ", downQueue=" + downQueue +
                '}';
    }
}
