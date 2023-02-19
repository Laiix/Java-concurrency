package java_concurrency_in_practice._13_displaylock;

import java_concurrency_in_practice._10_avoidactivehazards.Account;
import java_concurrency_in_practice._10_avoidactivehazards.DollarAmount;
import java_concurrency_in_practice._10_avoidactivehazards.InsufficientFundsException;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class ReentrantLockTest {

    Random rnd = new Random();

    public boolean transferMoney(Account fromAcct, Account toAcct, DollarAmount amount, long timeout, TimeUnit unit) throws InsufficientFundsException, InterruptedException {
        long fixedDelay = getFixedDelayComponentNanos(timeout, unit);
        long randMod = getRandomDelayModulusNanos(timeout, unit);
        long stopTime = System.nanoTime() + unit.toNanos(timeout);

        while (true) {
            if(fromAcct.lock.tryLock()) {
                try {
                    if(toAcct.lock.tryLock()) {
                        try {
                            if(fromAcct.getBalance().compareTo(amount)<0) {
                                throw new InsufficientFundsException();
                            } else {
                                fromAcct.debit(amount);
                                toAcct.credit(amount);
                                return true;
                            }


                        } finally {
                            toAcct.lock.unlock();
                        }
                    }
                } finally {
                    fromAcct.lock.unlock();
                }
            }
            if(System.nanoTime()<stopTime) {
                return false;
            }
            NANOSECONDS.sleep(fixedDelay + rnd.nextLong() % randMod);
        }
    }


    private long estimatedNanosToSend(String message) {
        return 0;
    }


    private long getRandomDelayModulusNanos(long timeout, TimeUnit unit) {
        return timeout;
    }

    private long getFixedDelayComponentNanos(long timeout, TimeUnit unit) {
        return timeout;
    }
}
