package java_concurrency_in_practice._10_avoidactivehazards;

import net.jcip.annotations.GuardedBy;

import java.util.HashSet;
import java.util.Set;

public class Dispatcher {
    @GuardedBy("this")
    private final Set<Taxi> taxis;
    @GuardedBy("this")
    private final Set<Taxi> availableTaxis;

    public Dispatcher() {
        this.taxis = new HashSet<>();
        this.availableTaxis = new HashSet<>();
    }

    public synchronized void notifyAvailable(Taxi taxi) {
        availableTaxis.add(taxi);
    }

    public synchronized Image getImage() {
        Image image = new Image();
        for (Taxi t : taxis) {
            image.drawMarker(t.getLocation());

        }
        return image;
    }
}
