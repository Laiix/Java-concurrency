package java_concurrency_in_practice._10_avoidactivehazards;

import java.util.Random;

public class DemonstrateDeallock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;

    public static void main(String[] args) {
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account();
        }

        class TransferThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
                    try {
                        transferMoney(accounts[fromAcct], accounts[toAcct], amount);
                    } catch (InsufficientFundsException e) {
                        e.printStackTrace();
                    }
                }
            }

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
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            new TransferThread().start();
        }
    }



}
