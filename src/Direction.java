/**
 * Represents the direction of travel for the elevator, including an IDLE value
 * for moments when no movement is scheduled.
 */
public enum Direction {
    UP(1),
    DOWN(-1),
    IDLE(0);

    private final int delta;

    Direction(int delta) {
        this.delta = delta;
    }

    /**
     * Signed magnitude associated with the direction.
     *
     * @return -1, 0, or 1 depending on the direction.
     */
    public int delta() {
        return delta;
    }

    /**
     * @return the opposite direction; IDLE maps to itself.
     */
    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            default -> IDLE;
        };
    }

    /**
     * @return true if this direction implies motion.
     */
    public boolean isMoving() {
        return this != IDLE;
    }
}
