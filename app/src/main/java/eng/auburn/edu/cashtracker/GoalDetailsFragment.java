package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by william on 11/30/15.
 */
public class GoalDetailsFragment extends Fragment {

    private Goal mGoal;

    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.value) TextView mValue;
    @Bind(R.id.percent) TextView mPercent;

    public static GoalDetailsFragment newInstance(String goalName) {
        Bundle args = new Bundle();
        args.putString("goalName", goalName);
        GoalDetailsFragment fragment = new GoalDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User u = UserManager.getInstance().getUser();
        String goalName = getArguments().getString("goalName");
        for (Goal g : u.getGoals()) {
            if (g.getName().equals(goalName)) {
                mGoal = g;
                break;
            }
        }

        getActivity().setTitle(mGoal.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mTitle.setText(mGoal.getName());
        mValue.setText(Utils.getDollarString(mGoal.getAmount()));
        mPercent.setText(String.format(getResources().getString(R.string.goals_summary),
                Utils.getDollarString(mGoal.getAmount()),
                DateUtils.formatDateTime(getActivity(), mGoal.getEndDate(),
                        DateUtils.FORMAT_NUMERIC_DATE)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
