package java_concurrency_in_practice._04_compositionofobjects;

import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class DelegatingVehicleTracker {

    private final Map<String, Point> locations;
    private final Map<String, Point> unmodifiableMap;
    public DelegatingVehicleTracker(Map<String, Point> points) {
        locations = new ConcurrentHashMap<String, Point>(points);
        unmodifiableMap = Collections.unmodifiableMap(locations);
    }

    public Map<String, Point> getLocations() {
        return unmodifiableMap;
    }

    public Point getLocation(String id) {
        return locations.get(id);
    }

    public synchronized void setLocation(String id, int x, int y) {
        if(locations.replace(id, new Point(x, y))==null)
            throw new IllegalArgumentException("invalid vehicle name:" + id);
    }

}
