import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankApp {
    // BankAccount class
    static class BankAccount {
        private double balance;
        private final Lock lock = new ReentrantLock();

        public BankAccount(double initialBalance) {
            this.balance = initialBalance;
        }

        public void deposit(double amount) {
            lock.lock(); // Acquire the lock
            try {
                if (amount > 0) {
                    balance += amount;
                    System.out.println("Deposited: " + amount + ", New Balance: " + balance);
                }
            } finally {
                lock.unlock(); // Always release the lock in a finally block
            }
        }

        public void withdraw(double amount) {
            lock.lock(); // Acquire the lock
            try {
                if (amount > 0 && balance >= amount) {
                    balance -= amount;
                    System.out.println("Withdrew: " + amount + ", New Balance: " + balance);
                } else if (amount > balance) {
                    System.out.println("Withdrawal of " + amount + " denied, insufficient funds.");
                }
            } finally {
                lock.unlock(); // Always release the lock in a finally block
            }
        }

        public double getBalance() {
            return balance;
        }
    }

    // BankTransaction class
    static class BankTransaction implements Runnable {
        private final BankAccount account;
        private final double amount;
        private final boolean isDeposit;

        public BankTransaction(BankAccount account, double amount, boolean isDeposit) {
            this.account = account;
            this.amount = amount;
            this.isDeposit = isDeposit;
        }

        @Override
        public void run() {
            if (isDeposit) {
                account.deposit(amount);
            } else {
                account.withdraw(amount);
            }
        }
    }

    public static void main(String[] args) {
        BankAccount account = new BankAccount(1000.00); // Initial balance of $1000

        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Create multiple deposit and withdrawal tasks
        for (int i = 0; i < 5; i++) {
            executor.submit(new BankTransaction(account, 100.00, true)); // Deposit $100
            executor.submit(new BankTransaction(account, 50.00, false)); // Withdraw $50
        }

        // Shutdown the executor
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all tasks to finish
        }

        System.out.println("Final Balance: " + account.getBalance());
    }
}
