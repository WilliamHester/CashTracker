package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class BudgetsFragment extends Fragment {

    private final ArrayList<Budget> mBudgets = new ArrayList<>();
    private BudgetsAdapter mBudgetsAdapter;

    @Bind(R.id.list) ListView mListView;

    public static BudgetsFragment newInstance() {
        Bundle args = new Bundle();
        BudgetsFragment fragment = new BudgetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(R.string.budgets);
        }
        if (mBudgets.size() == 0) {
            mBudgets.addAll(UserManager.getInstance().getUser().getBudgets());
        }
        if (mBudgetsAdapter == null) {
            mBudgetsAdapter = new BudgetsAdapter();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budgets, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mBudgetsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class BudgetsAdapter extends ArrayAdapter<Budget> {

        public BudgetsAdapter() {
            super(getActivity(), R.layout.list_item_account, mBudgets);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView category = (TextView) v.findViewById(R.id.category);
            TextView value = (TextView) v.findViewById(R.id.value);

            Budget b = getItem(position);
            title.setText(b.getName());
            category.setText(b.getCategory());
            value.setText(Utils.getDollarString(b.getGoal()));
            return v;
        }
    }
}
