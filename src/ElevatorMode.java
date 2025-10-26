/**
 * High-level elevator state machine phases used by the simulation.
 */
public enum ElevatorMode {
    IDLE,
    MOVING_UP,
    MOVING_DOWN,
    DOOR_OPENING,
    DOOR_OPEN,
    DOOR_CLOSING
}
