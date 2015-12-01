package eng.auburn.edu.cashtracker;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by william on 11/30/15.
 */
public class Account extends RealmObject {

    private String name;
    private double interestRate;
    private int interestRateFrequency;
    private int startingBalance;

    private RealmList<Transaction> transactions;

    public RealmList<Transaction> getTransactions() {
        return transactions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(int balance) {
        this.startingBalance = balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getInterestRateFrequency() {
        return interestRateFrequency;
    }

    public void setInterestRateFrequency(int interestRateFrequency) {
        this.interestRateFrequency = interestRateFrequency;
    }

    public static int getBalance(Account a) {
        int balance = a.startingBalance;
        for (Transaction t : a.transactions) {
            balance += t.getAmount();
        }
        return balance;
    }

}
