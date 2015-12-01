package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationInterface {

    private static final String ACCOUNTS_FRAGMENT = "accounts";
    private static final String BUDGETS_FRAGMENT = "budgets";
    private static final String GOALS_FRAGMENT = "goals";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if (f == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, AccountsFragment.newInstance(), ACCOUNTS_FRAGMENT)
                    .commit();
        }

        Fragment nav = getSupportFragmentManager().findFragmentById(R.id.nav_container);
        if (nav == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nav_container, NavigationDrawerFragment.newInstance(), "nav")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccountsClicked() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag(ACCOUNTS_FRAGMENT);
        if (f == null) {
            f = AccountsFragment.newInstance();
        }
        fm.beginTransaction()
                .replace(R.id.container, f, ACCOUNTS_FRAGMENT)
                .commit();
    }

    @Override
    public void onBudgetsClicked() {

    }

    @Override
    public void onGoalsClicked() {

    }
}
