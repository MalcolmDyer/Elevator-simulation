# The Elevator Simulation (Back-End Code Challenge)

## Overview
This project implements a discrete-time simulator for a single elevator cab serving a configurable building. Each tick advances the elevator's finite state machine (movement, doors, queues) while honoring a LOOK/SCAN scheduling policy so requests in the current direction are finished before reversing.

## How to Run
```bash
# Compile sources (Java 17)
javac -d out $(find src -name "*.java")

# Run the demo driver
java -cp out Main
```
To execute the included JUnit 5 tests, download the `junit-platform-console-standalone` jar and run:
```bash
javac -cp junit-platform-console-standalone-1.10.2.jar:out -d out/test $(find test -name "*.java")
java -jar junit-platform-console-standalone-1.10.2.jar --class-path out:out/test --scan-classpath
```

## Key Classes
- `Direction` / `DoorState`: enums that capture movement vectors and door phases.
- `Request`, `CarCall`, `HallCall`: abstractions for car and hall inputs, including hall direction intent.
- `Scheduler`: LOOK/SCAN scheduler backed by directional `TreeSet<Integer>` collections; handles deduplication and queue snapshots.
- `ElevatorMode`: enumerates IDLE, motion, and door-life-cycle states.
- `Elevator`: core simulation engine exposing `hallCall`, `carCall`, `tick`, and `getState`.
- `ElevatorState`: immutable snapshot returned by `getState()` for logging or diagnostics.
- `Main`: minimal console demonstration that runs 15 ticks and logs state.
- `ElevatorTest`: JUnit scenarios covering SCAN ordering, deduplication, and idle recovery.

## Assumptions
- Exactly one elevator car exists in the building.
- Floors are contiguous integers, 1..N, with N ≥ 1.
- Time is discrete; one tick equals one second of simulated time.
- Door open/close operations always succeed—no mechanical faults.
- Requests are processed deterministically via the SCAN policy (no randomness).

## Features Not Implemented
- Multi-elevator dispatching or peer coordination.
- Passenger capacity, weight, or load balancing logic.
- Real-time IO, networking, or event-driven APIs.
- Priority, emergency, or fire-service call handling.
- Continuous-time physics (acceleration, jerk, etc.).

## Test Scenarios
- **SCAN ordering**: verifies that once the elevator heads upward it completes the upward run before reversing to serve lower floors (`servesRequestsInScanOrder`).
- **Duplicate suppression**: ensures redundant car or hall requests do not produce multiple stops (`ignoresDuplicateRequests`).
- **Idle recovery**: confirms the cab returns to `IDLE` with closed doors and empty queues after satisfying outstanding requests (`returnsToIdleAfterCompletingWork`).

## Future Extensions
1. Multi-car coordination with a dispatcher that optimizes collective response time.
2. REST/gRPC API to feed requests and stream telemetry from the simulation.
3. GUI or web-based visualization showing shafts, passengers, and queue states.
4. Optimization hooks for predictive algorithms (traffic patterns, batching strategies).
