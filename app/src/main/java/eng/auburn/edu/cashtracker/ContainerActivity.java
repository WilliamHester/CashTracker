package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class ContainerActivity extends AppCompatActivity {

    public static final String CREATE_ACCOUNT = "createAccount";
    public static final String CREATE_BUDGET = "createBudget";
    public static final String CREATE_GOAL = "createGoal";
    public static final String VIEW_ACCOUNT = "viewAccount";
    public static final String VIEW_BUDGET = "viewBudget";
    public static final String VIEW_GOAL = "viewGoal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
            String action = getIntent().getExtras().getString("action");
            String name = getIntent().getExtras().getString("name");
            Fragment f = null;
            if (action == null) {
                return;
            }
            switch (action) {
                case CREATE_ACCOUNT:
                    f = CreateAccountFragment.newInstance();
                    break;
                case CREATE_BUDGET:
                    f = CreateBudgetFragment.newInstance();
                    break;
                case CREATE_GOAL:
                    f = CreateGoalFragment.newInstance();
                    break;
                case VIEW_ACCOUNT:
                    f = AccountFragment.newInstance(name);
                    break;
                case VIEW_BUDGET:
                    f = BudgetFragment.newInstance(name);
                    break;
                case VIEW_GOAL:
                    f = GoalFragment.newInstance(name);
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f, action)
                    .commit();
        }
    }
}
