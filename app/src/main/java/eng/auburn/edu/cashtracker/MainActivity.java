package eng.auburn.edu.cashtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by william on 12/1/15.
 */
public class MainActivity extends AppCompatActivity {

    private static final String ACCOUNTS_FRAGMENT = "accounts";
    private static final String BUDGETS_FRAGMENT = "budgets";
    private static final String GOALS_FRAGMENT = "goals";

    private static final int REQUEST_DELETE_USER = 13;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if (f == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, AccountListFragment.newInstance(), ACCOUNTS_FRAGMENT)
                    .commit();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment f = fm.findFragmentById(R.id.container);
                String tag = null;
                switch (item.getItemId()) {
                    case R.id.accounts:
                        if (f instanceof AccountListFragment) {
                            mDrawerLayout.closeDrawers();
                            return false;
                        }
                        f = fm.findFragmentByTag(ACCOUNTS_FRAGMENT);
                        tag = ACCOUNTS_FRAGMENT;
                        if (f == null) {
                            f = AccountListFragment.newInstance();
                        }
                        break;
                    case R.id.budgets:
                        if (f instanceof BudgetListFragment) {
                            mDrawerLayout.closeDrawers();
                            return false;
                        }
                        f = fm.findFragmentByTag(BUDGETS_FRAGMENT);
                        tag = BUDGETS_FRAGMENT;
                        if (f == null) {
                            f = BudgetListFragment.newInstance();
                        }
                        break;
                    case R.id.goals:
                        if (f instanceof GoalListFragment) {
                            mDrawerLayout.closeDrawers();
                            return false;
                        }
                        f = fm.findFragmentByTag(GOALS_FRAGMENT);
                        tag = GOALS_FRAGMENT;
                        if (f == null) {
                            f = GoalListFragment.newInstance();
                        }
                        break;
                    case R.id.action_settings:
                        Intent i = new Intent(MainActivity.this, ContainerActivity.class);
                        Bundle args = new Bundle();
                        args.putString("action", ContainerActivity.SETTINGS);
                        i.putExtras(args);
                        startActivityForResult(i, REQUEST_DELETE_USER);
                        return false;
                }
                fm.beginTransaction()
                        .replace(R.id.container, f, tag)
                        .commit();
                mDrawerLayout.closeDrawers();
                item.setChecked(true);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DELETE_USER && resultCode == RESULT_OK) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.closeDrawers();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
