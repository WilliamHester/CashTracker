package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by william on 11/30/15.
 */
public class GoalListFragment extends Fragment {

    private static final int CREATE_GOAL = 12;

    private final ArrayList<Goal> mGoals = new ArrayList<>();
    private GoalAdapter mAdapter;

    @Bind(R.id.list) ListView mListView;

    public static GoalListFragment newInstance() {
        Bundle args = new Bundle();
        GoalListFragment fragment = new GoalListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.goals);
        setHasOptionsMenu(true);
        if (mGoals.size() == 0) {
            mGoals.addAll(UserManager.getInstance().getUser().getGoals());
        }
        if (mAdapter == null) {
            mAdapter = new GoalAdapter();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_GOAL) {
            if (resultCode == Activity.RESULT_OK) {
                User u = UserManager.getInstance().getUser();
                mGoals.clear();
                mGoals.addAll(u.getGoals());
                mAdapter.notifyDataSetChanged();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_create_new, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_new) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            Bundle args = new Bundle();
            args.putString("action", ContainerActivity.CREATE_GOAL);
            i.putExtras(args);
            startActivityForResult(i, CREATE_GOAL);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GoalAdapter extends ArrayAdapter<Goal> {
        public GoalAdapter() {
            super(getActivity(), R.layout.list_item_goal, mGoals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater(null).inflate(R.layout.list_item_goal, parent,
                        false);
            }
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView goalSummary = (TextView) convertView.findViewById(R.id.goal_summary);
            TextView accountName = (TextView) convertView.findViewById(R.id.account_name);
            TextView value = (TextView) convertView.findViewById(R.id.value);

            Goal g = getItem(position);
            title.setText(g.getName());
            goalSummary.setText(String.format(getResources().getString(R.string.goals_summary),
                    Utils.getDollarString(g.getAmount()),
                    DateUtils.formatDateTime(getActivity(), g.getEndDate(),
                            DateUtils.FORMAT_NUMERIC_DATE)));
            accountName.setText(g.getAccount().getName());
            int accountTotal = Account.getBalance(g.getAccount());
            value.setText(String.format(getResources().getString(R.string.percent_of_goal),
                    accountTotal / (double) g.getAmount()));

            return convertView;
        }
    }
}
