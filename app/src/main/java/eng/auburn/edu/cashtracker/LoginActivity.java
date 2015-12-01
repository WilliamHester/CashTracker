package eng.auburn.edu.cashtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by william on 11/30/15.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if (f == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, LoginFragment.newInstance(), "LoginFragment")
                    .commit();
        }
    }

    public void showCreateAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, CreateUserFragment.newInstance(), "createUser")
                .addToBackStack("createUser")
                .commit();
    }

    public void onLoggedIn() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
