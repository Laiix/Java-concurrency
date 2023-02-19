package java_concurrency_in_practice._10_avoidactivehazards;

import net.jcip.annotations.GuardedBy;

import java.util.HashSet;
import java.util.Set;

public class DispatcherSafe {
    @GuardedBy("this")
    private final Set<TaxiSafe> taxiSafes;
    @GuardedBy("this")
    private final Set<TaxiSafe> availableTaxiSafes;

    public DispatcherSafe() {
        this.taxiSafes = new HashSet<>();
        this.availableTaxiSafes = new HashSet<>();
    }

    public synchronized void notifyAvailable(TaxiSafe taxiSafe) {
        availableTaxiSafes.add(taxiSafe);
    }

    public Image getImage() {
       Set<TaxiSafe> copy;
       synchronized (this) {
           copy = new HashSet<>(taxiSafes);
       }
        Image image = new Image();
        for (TaxiSafe t : copy) {
            image.drawMarker(t.getLocation());
        }
        return image;
    }
}
