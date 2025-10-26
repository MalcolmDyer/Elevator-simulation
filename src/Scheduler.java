import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

/**
 * LOOK/SCAN scheduler that keeps separate TreeSets for upward and downward
 * travel segments. Requests are dynamically rebalanced based on the current floor.
 */
public final class Scheduler {
    private final NavigableSet<Integer> upRequests = new TreeSet<>();
    private final NavigableSet<Integer> downRequests = new TreeSet<>();

    /**
     * Adds a request into the correct directional queue while ignoring duplicates.
     *
     * @param request     call to add
     * @param currentFloor elevator's current floor for relative positioning
     * @return true when the request was accepted (not a duplicate)
     */
    public boolean submit(Request request, int currentFloor) {
        Objects.requireNonNull(request, "request");
        int floor = request.getFloor();
        if (contains(floor)) {
            return false;
        }
        Direction desired = request.getDesiredDirection();
        if (request.getType() == Request.Type.HALL && desired.isMoving()) {
            if (desired == Direction.UP && floor >= currentFloor) {
                return upRequests.add(floor);
            }
            if (desired == Direction.DOWN && floor <= currentFloor) {
                return downRequests.add(floor);
            }
        }
        if (floor >= currentFloor) {
            return upRequests.add(floor);
        }
        return downRequests.add(floor);
    }

    public boolean contains(int floor) {
        return upRequests.contains(floor) || downRequests.contains(floor);
    }

    public boolean consume(int floor) {
        return upRequests.remove(floor) || downRequests.remove(floor);
    }

    /**
     * Maintains the invariant that all floors above the cab live in the up queue
     * while floors below live in the down queue.
     */
    public void rebalance(int currentFloor) {
        NavigableSet<Integer> below = upRequests.headSet(currentFloor, false);
        if (!below.isEmpty()) {
            List<Integer> transfer = new ArrayList<>(below);
            below.clear();
            downRequests.addAll(transfer);
        }
        NavigableSet<Integer> above = downRequests.tailSet(currentFloor, true);
        if (!above.isEmpty()) {
            List<Integer> transfer = new ArrayList<>(above);
            above.clear();
            upRequests.addAll(transfer);
        }
    }

    public boolean hasUpRequestsAhead(int currentFloor) {
        return upRequests.higher(currentFloor) != null;
    }

    public boolean hasDownRequestsBelow(int currentFloor) {
        return downRequests.lower(currentFloor) != null;
    }

    /**
     * Chooses the next travel direction while honoring a preferred scan
     * direction whenever possible.
     */
    public Direction preferredDirection(int currentFloor, Direction preference) {
        if (preference == Direction.UP && hasUpRequestsAtOrAbove(currentFloor)) {
            return Direction.UP;
        }
        if (preference == Direction.DOWN && hasDownRequestsAtOrBelow(currentFloor)) {
            return Direction.DOWN;
        }
        if (hasUpRequestsAtOrAbove(currentFloor)) {
            return Direction.UP;
        }
        if (hasDownRequestsAtOrBelow(currentFloor)) {
            return Direction.DOWN;
        }
        return Direction.IDLE;
    }

    private boolean hasUpRequestsAtOrAbove(int currentFloor) {
        return upRequests.ceiling(currentFloor) != null;
    }

    private boolean hasDownRequestsAtOrBelow(int currentFloor) {
        return downRequests.floor(currentFloor) != null;
    }

    public List<Integer> snapshotUpQueue() {
        return Collections.unmodifiableList(new ArrayList<>(upRequests));
    }

    public List<Integer> snapshotDownQueue() {
        return Collections.unmodifiableList(new ArrayList<>(downRequests.descendingSet()));
    }
}
