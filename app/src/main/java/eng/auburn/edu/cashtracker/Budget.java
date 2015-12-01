package eng.auburn.edu.cashtracker;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by william on 11/30/15.
 */
public class Budget extends RealmObject {

    @Ignore private int currentAmount;

    private int goal;
    private String name;
    private String category;
    private RealmList<Transaction> transacitons;

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RealmList<Transaction> getTransacitons() {
        return transacitons;
    }

    public static int getCurrentAmount(Budget b) {
        int balance = b.goal;
        for (Transaction t : b.transacitons) {
            balance -= t.getAmount();
        }
        return balance;
    }
}
