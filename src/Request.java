/**
 * Contract implemented by all elevator call types so that the scheduler can
 * process them uniformly.
 */
public interface Request {

    /**
     * Logical grouping of request origins.
     */
    enum Type {
        CAR,
        HALL
    }

    /**
     * @return the floor associated with the request.
     */
    int getFloor();

    /**
     * @return whether the call came from inside (CAR) or outside (HALL).
     */
    Type getType();

    /**
     * @return desired direction (IDLE for car calls).
     */
    Direction getDesiredDirection();
}
