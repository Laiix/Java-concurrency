package java_concurrency_in_practice._10_avoidactivehazards;

import java_concurrency_in_practice._04_compositionofobjects.Point;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class TaxiSafe {
    @GuardedBy("this")
    private Point location, destination;
    private final DispatcherSafe dispatcher;

    public TaxiSafe(DispatcherSafe dispatcher) {
        this.dispatcher = dispatcher;
    }

    public synchronized Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        boolean reachedDestination;
        synchronized (this) {
            this.location = location;
            reachedDestination = location.equals(destination);
        }
        if(reachedDestination) {
            dispatcher.notifyAvailable(this);
        }
    }
}
