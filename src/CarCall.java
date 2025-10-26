import java.util.Objects;

/**
 * Represents a passenger selecting a destination from inside the car.
 */
public final class CarCall implements Request {
    private final int floor;

    public CarCall(int floor) {
        if (floor < 1) {
            throw new IllegalArgumentException("Floor must be positive.");
        }
        this.floor = floor;
    }

    @Override
    public int getFloor() {
        return floor;
    }

    @Override
    public Type getType() {
        return Type.CAR;
    }

    @Override
    public Direction getDesiredDirection() {
        return Direction.IDLE;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CarCall call)) {
            return false;
        }
        return floor == call.floor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor);
    }

    @Override
    public String toString() {
        return "CarCall{floor=" + floor + '}';
    }
}
