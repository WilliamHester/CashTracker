package eng.auburn.edu.cashtracker;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by william on 11/30/15.
 */
public class User extends RealmObject {

    private String username;
    private String password;
    private String displayName;
    private RealmList<Account> accounts;
    private RealmList<Budget> budgets;
    private RealmList<Goal> goals;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public RealmList<Account> getAccounts() {
        return accounts;
    }

    public RealmList<Goal> getGoals() {
        return goals;
    }

    public RealmList<Budget> getBudgets() {
        return budgets;
    }

    public void setAccounts(RealmList<Account> accounts) {
        this.accounts = accounts;
    }

    public void setBudgets(RealmList<Budget> budgets) {
        this.budgets = budgets;
    }

    public void setGoals(RealmList<Goal> goals) {
        this.goals = goals;
    }

    public static int getAccountsTotal(User u) {
        int balance = 0;
        for (Account a : u.getAccounts()) {
            balance += Account.getBalance(a);
        }
        return balance;
    }
}
