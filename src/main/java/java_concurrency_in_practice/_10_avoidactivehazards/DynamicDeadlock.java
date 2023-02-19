package java_concurrency_in_practice._10_avoidactivehazards;

public class DynamicDeadlock {

    //注意：容易发生死锁
    public void transferMoney(Account fromAcct,
                              Account toAcct,
                              DollarAmount amount) throws InsufficientFundsException {
        synchronized (fromAcct) {
            synchronized (toAcct) {
                if(fromAcct.getBalance().compareTo(amount)<0) {
                    throw new InsufficientFundsException();
                } else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }
    }

    private static final Object tieLock = new Object();
    public void transferMoneyV2(Account fromAcct,
                              Account toAcct,
                              DollarAmount amount) throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                if(fromAcct.getBalance().compareTo(amount)<0) {
                    throw new InsufficientFundsException();
                } else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }
        int fromHash = System.identityHashCode(fromAcct);
        int toHash = System.identityHashCode(toAcct);

        if(fromHash<toHash) {
            synchronized (fromAcct) {
                synchronized (toAcct) {
                    new Helper().transfer();
                }
            }
        } else if(fromHash > toHash) {
            synchronized (toAcct) {
                synchronized (fromAcct) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAcct) {
                    synchronized (toAcct) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }
}
