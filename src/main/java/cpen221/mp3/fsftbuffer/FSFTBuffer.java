package cpen221.mp3.fsftbuffer;

import java.io.InvalidObjectException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Finite space finite time buffer.
 *
 * Abstraction Function:
 * capacity represents the maximum number of objects that can be stored in the FSFT buffer.
 * timeout represents the number of seconds that an object can stay in the
 * buffer without being interacted with before being timed out and removed.
 * timeouts is a map that represents all of the objects in the buffer and maps the time in which
 * they will timeout.
 * names is a map that represents all of the ids of the objects mapped to the objects themselves.
 *
 * Representation Invariant: timeouts.size() <= capacity && names.size() == timeouts.size().
 * Every key in timeouts must appear as a value in names exactly once.
 * There must not exist a value of less than 0 in timeouts.
 * Every key.equals(value.id()) in names.
 * capacity > 0 && timeout > 0.
 *
 *
 * @param <T>
 */
public class FSFTBuffer<T extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    private int capacity;
    private int timeout;
    Map<T, Integer> timeouts = new ConcurrentHashMap<>();
    Map<String, T> names = new ConcurrentHashMap<>();

    /**
     * Create a buffer with a fixed capacity and a timeout value.
     * Objects in the buffer that have not been refreshed within the
     * timeout period are removed from the cache.
     *
     * @param capacity the number of objects the buffer can hold
     * @param timeout  the duration, in seconds, an object should
     *                 be in the buffer before it times out
     */
    public FSFTBuffer(int capacity, int timeout) {
        this.capacity = capacity;
        this.timeout = timeout;
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DSIZE, DTIMEOUT);
    }

    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     */
    public synchronized boolean put(T t) {
        removeTimedOut();
        if (this.timeouts.size() >= capacity) {
            T stalest = findStalest();
            String id = stalest.id();
            timeouts.remove(stalest);
            names.remove(id);
        }
        String name = t.id();
        this.timeouts.put(t, (int) (System.currentTimeMillis() / 1000 + timeout));
        this.names.put(name, t);
        return true;
    }

    /**
     * Helper method to find the stalest object in the buffer.
     *
     * Requires: All timed out objects must be removed from the buffer.
     *
     * @return the object that has the smallest difference between its timeout time and the current
     * time.
     */
    private T findStalest() {
        T stalestObj = null;
        int minTimeout = Integer.MAX_VALUE;
        for (T t : timeouts.keySet()) {
            int currTime = (int) (timeouts.get(t) - System.currentTimeMillis() / 1000);
            if (currTime < minTimeout) {
                minTimeout = currTime;
                stalestObj = t;
            }
        }
        return stalestObj;
    }

    /**
     * Helper method that removes object t from both the names map and the timeouts map.
     * @param t object to be removed from the buffer. Must be in the buffer, and be mapped to same
     *          String ID as its own id value.
     */
    private void removeObject(T t) {
        this.timeouts.remove(t);
        this.names.remove(t.id());
    }

    /**
     * Helper method that removes all objects from the buffer that have timed out.
     * Timing out occurs when an object's timeout time is lesser than the current time.
     */
    private void removeTimedOut() {
        for (T t : timeouts.keySet()) {
            if (timeouts.get(t) <= System.currentTimeMillis()/1000) {
                removeObject(t);
            }
        }
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public synchronized T get(String id) throws InvalidObjectException {
        removeTimedOut();
        if (names.get(id) == null) {
            throw new InvalidObjectException("Object not found in FSFT Buffer.");
        }
        if (!touch(id)) {
            throw new InvalidObjectException("Error with object timeout.");
        }
        return names.get(id);
    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its
     * timeout is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    public synchronized boolean touch(String id) {
        removeTimedOut();
        T t = names.get(id);
        if (t == null) {
            return false;
        }
        if (!update(t)) {
            return false;
        }
        return true;
    }

    /**
     * Update an object in the buffer.
     * This method updates an object and acts like a "touch" to
     * renew the object in the cache.
     *
     * @param t the object to update
     * @return true if successful and false otherwise
     */
    public synchronized boolean update(T t) {
        removeTimedOut();
        if (timeouts.get(t) == null) {
            return false;
        }
        timeouts.put(t, (int) (System.currentTimeMillis() / 1000 + timeout));
        return true;
    }

}