package cpen221.mp3.fsftbuffer;

import java.io.InvalidObjectException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public boolean put(T t) {
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
     * All timed out objects must be removed from the buffer.
     *
     * @return
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
     * @param t object to be removed from the buffer. Must be in the buffer, and be mapped to same
     *          String ID as its own id value.
     */
    private void removeObject(T t) {
        this.timeouts.remove(t);
        this.names.remove(t.id());
    }

    /**
     *
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
    public T get(String id) throws InvalidObjectException {
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
    public boolean touch(String id) {
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
    public boolean update(T t) {
        removeTimedOut();
        if (timeouts.get(t) == null) {
            return false;
        }
        timeouts.put(t, (int) (System.currentTimeMillis() / 1000 + timeout));
        return true;
    }

}
