package eng.auburn.edu.cashtracker;

/**
 * Created by william on 11/30/15.
 */
public class Utils {

    private Utils() {

    }


    public static String getDollarString(int cents) {
        return "$" + (cents / 100) + "." + (cents % 100);
    }
}
