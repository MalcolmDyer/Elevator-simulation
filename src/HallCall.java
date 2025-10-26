import java.util.Objects;

/**
 * Represents a hall button press that contains the passenger's intended travel direction.
 */
public final class HallCall implements Request {
    private final int floor;
    private final Direction direction;

    public HallCall(int floor, Direction direction) {
        if (floor < 1) {
            throw new IllegalArgumentException("Floor must be positive.");
        }
        this.direction = Objects.requireNonNull(direction, "direction");
        if (direction == Direction.IDLE) {
            throw new IllegalArgumentException("Hall calls must specify UP or DOWN.");
        }
        this.floor = floor;
    }

    @Override
    public int getFloor() {
        return floor;
    }

    @Override
    public Type getType() {
        return Type.HALL;
    }

    @Override
    public Direction getDesiredDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HallCall call)) {
            return false;
        }
        return floor == call.floor && direction == call.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor, direction);
    }

    @Override
    public String toString() {
        return "HallCall{floor=" + floor + ", direction=" + direction + '}';
    }
}
