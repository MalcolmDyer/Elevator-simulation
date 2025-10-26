import java.util.Objects;

/**
 * Core simulation engine for a single elevator cab. The class advances in
 * discrete ticks, processes calls, and manages door timing as a simple finite
 * state machine.
 */
public final class Elevator {
    private static final int DEFAULT_MIN_FLOOR = 1;
    private static final int DOOR_STAGE_TICKS = 1;
    private static final int DOOR_OPEN_HOLD_TICKS = 1;

    private final int minFloor;
    private final int maxFloor;
    private final Scheduler scheduler = new Scheduler();

    private int currentFloor;
    private ElevatorMode mode = ElevatorMode.IDLE;
    private Direction direction = Direction.IDLE;
    private Direction scanDirection = Direction.UP;
    private DoorState doorState = DoorState.CLOSED;
    private int doorTimer = 0;

    public Elevator() {
        this(10);
    }

    public Elevator(int totalFloors) {
        this(DEFAULT_MIN_FLOOR, totalFloors);
    }

    public Elevator(int minFloor, int maxFloor) {
        if (maxFloor < minFloor) {
            throw new IllegalArgumentException("maxFloor must be >= minFloor.");
        }
        if (minFloor < 1) {
            throw new IllegalArgumentException("Floors must be positive.");
        }
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.currentFloor = minFloor;
    }

    public void hallCall(int floor, Direction callDirection) {
        Objects.requireNonNull(callDirection, "callDirection");
        if (callDirection == Direction.IDLE) {
            throw new IllegalArgumentException("Hall calls must specify UP or DOWN.");
        }
        enqueue(new HallCall(floor, callDirection));
    }

    public void carCall(int floor) {
        enqueue(new CarCall(floor));
    }

    private void enqueue(Request request) {
        validateFloor(request.getFloor());
        boolean added = scheduler.submit(request, currentFloor);
        if (added) {
            tryStartMovement();
        }
    }

    public ElevatorState getState() {
        return new ElevatorState(
                currentFloor,
                direction,
                doorState,
                mode,
                scheduler.snapshotUpQueue(),
                scheduler.snapshotDownQueue());
    }

    /**
     * Advances the simulation by one discrete tick.
     */
    public void tick() {
        scheduler.rebalance(currentFloor);
        switch (mode) {
            case IDLE -> handleIdle();
            case MOVING_UP -> moveUp();
            case MOVING_DOWN -> moveDown();
            case DOOR_OPENING, DOOR_OPEN, DOOR_CLOSING -> advanceDoorCycle();
            default -> throw new IllegalStateException("Unhandled mode " + mode);
        }
    }

    private void handleIdle() {
        if (scheduler.consume(currentFloor)) {
            beginDoorCycle();
            return;
        }
        Direction next = scheduler.preferredDirection(currentFloor, scanDirection);
        if (next != Direction.IDLE) {
            switchTo(next);
        }
    }

    private void moveUp() {
        if (scheduler.consume(currentFloor)) {
            beginDoorCycle();
            return;
        }
        if (currentFloor >= maxFloor) {
            switchTo(Direction.DOWN);
            return;
        }
        currentFloor += 1;
        if (scheduler.consume(currentFloor)) {
            beginDoorCycle();
            return;
        }
        if (!scheduler.hasUpRequestsAhead(currentFloor)) {
            Direction next = scheduler.preferredDirection(currentFloor, Direction.DOWN);
            switchTo(next);
        }
    }

    private void moveDown() {
        if (scheduler.consume(currentFloor)) {
            beginDoorCycle();
            return;
        }
        if (currentFloor <= minFloor) {
            switchTo(Direction.UP);
            return;
        }
        currentFloor -= 1;
        if (scheduler.consume(currentFloor)) {
            beginDoorCycle();
            return;
        }
        if (!scheduler.hasDownRequestsBelow(currentFloor)) {
            Direction next = scheduler.preferredDirection(currentFloor, Direction.UP);
            switchTo(next);
        }
    }

    private void advanceDoorCycle() {
        if (doorTimer > 0) {
            doorTimer--;
        }
        if (doorTimer > 0) {
            return;
        }
        switch (mode) {
            case DOOR_OPENING -> {
                doorState = DoorState.OPEN;
                mode = ElevatorMode.DOOR_OPEN;
                doorTimer = DOOR_OPEN_HOLD_TICKS;
            }
            case DOOR_OPEN -> {
                doorState = DoorState.CLOSING;
                mode = ElevatorMode.DOOR_CLOSING;
                doorTimer = DOOR_STAGE_TICKS;
            }
            case DOOR_CLOSING -> {
                doorState = DoorState.CLOSED;
                Direction next = scheduler.preferredDirection(currentFloor, scanDirection);
                switchTo(next);
            }
            default -> throw new IllegalStateException("Door cycle reached mode " + mode);
        }
    }

    private void beginDoorCycle() {
        doorState = DoorState.OPENING;
        mode = ElevatorMode.DOOR_OPENING;
        doorTimer = DOOR_STAGE_TICKS;
        direction = Direction.IDLE;
    }

    private void tryStartMovement() {
        if (mode != ElevatorMode.IDLE || doorState != DoorState.CLOSED) {
            return;
        }
        Direction next = scheduler.preferredDirection(currentFloor, scanDirection);
        if (next != Direction.IDLE) {
            switchTo(next);
        }
    }

    private void switchTo(Direction next) {
        if (next == Direction.IDLE) {
            direction = Direction.IDLE;
            mode = ElevatorMode.IDLE;
            return;
        }
        direction = next;
        scanDirection = next;
        mode = next == Direction.UP ? ElevatorMode.MOVING_UP : ElevatorMode.MOVING_DOWN;
    }

    private void validateFloor(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException("Floor " + floor + " is outside [" + minFloor + ", " + maxFloor + "]");
        }
    }
}
