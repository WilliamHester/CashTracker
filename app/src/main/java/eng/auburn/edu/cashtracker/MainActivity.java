package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

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

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment f = fm.findFragmentById(R.id.container);
                String tag = null;
                switch (item.getItemId()) {
                    case R.id.accounts:
                        if (f instanceof AccountsFragment) {
                            drawerLayout.closeDrawers();
                            return false;
                        }
                        f = fm.findFragmentByTag(ACCOUNTS_FRAGMENT);
                        tag = ACCOUNTS_FRAGMENT;
                        if (f == null) {
                            f = AccountsFragment.newInstance();
                        }
                        break;
                    case R.id.budgets:
                        if (f instanceof BudgetsFragment) {
                            drawerLayout.closeDrawers();
                            return false;
                        }
                        f = fm.findFragmentByTag(BUDGETS_FRAGMENT);
                        tag = BUDGETS_FRAGMENT;
                        if (f == null) {
                            f = BudgetsFragment.newInstance();
                        }
                        break;
                    case R.id.goals:
                        if (f instanceof GoalsFragment) {
                            drawerLayout.closeDrawers();
                            return false;
                        }
                        f = fm.findFragmentByTag(GOALS_FRAGMENT);
                        tag = GOALS_FRAGMENT;
                        if (f == null) {
                            f = GoalsFragment.newInstance();
                        }
                        break;
                    case R.id.action_settings:
                        break;
                }
                fm.beginTransaction()
                        .replace(R.id.container, f, tag)
                        .commit();
                drawerLayout.closeDrawers();
                item.setChecked(true);
                return false;
            }
        });
    }
}
