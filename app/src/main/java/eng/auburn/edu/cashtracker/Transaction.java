package eng.auburn.edu.cashtracker;

import io.realm.RealmObject;

/**
 * Created by william on 11/30/15.
 */
public class Transaction extends RealmObject {

    private int amount;
    private long date;
    private String category;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
