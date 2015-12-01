package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
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
public class GoalsFragment extends Fragment {

    private final ArrayList<Goal> mGoals = new ArrayList<>();
    private GoalAdapter mAdapter;

    @Bind(R.id.list) ListView mListView;

    public static GoalsFragment newInstance() {
        Bundle args = new Bundle();
        GoalsFragment fragment = new GoalsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(R.string.goals);
        }
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

    private class GoalAdapter extends ArrayAdapter<Goal> {
        public GoalAdapter() {
            super(getActivity(), R.layout.list_item_goal, mGoals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView goalSummary = (TextView) v.findViewById(R.id.goal_summary);
            TextView accountName = (TextView) v.findViewById(R.id.account_name);
            TextView value = (TextView) v.findViewById(R.id.value);

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

            return v;
        }
    }
}
