package java_concurrency_in_practice._11_performanceandscalability;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Set;

@ThreadSafe
public class BetterServerStatus {
    @GuardedBy("users")
    public final Set<String> users;
    @GuardedBy("queries")
    public final Set<String> queries;

    public BetterServerStatus(Set<String> users, Set<String> queries) {
        this.users = users;
        this.queries = queries;
    }

    public void addUser(String u) {
        synchronized (users) {
            users.add(u);
        }
    }
    public void addQuery(String q) {
        synchronized (queries) {
            queries.add(q);
        }
    }

    public void revoveUser(String u) {
        synchronized (users) {
            users.remove(u);
        }
    }
    public void removeQuery(String q) {
        synchronized (queries) {
            queries.remove(q);
        }
    }
}
