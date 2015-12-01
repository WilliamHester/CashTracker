package eng.auburn.edu.cashtracker;

/**
 * Created by william on 11/30/15.
 */
public class UserManager {

    private User mUser;

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {}

    public void setUser(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

}
