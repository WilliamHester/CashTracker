package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class BudgetListFragment extends Fragment {

    private static final int CREATE_BUDGET = 11;

    private final ArrayList<Budget> mBudgets = new ArrayList<>();
    private BudgetsAdapter mBudgetsAdapter;

    @Bind(R.id.list) ListView mListView;

    public static BudgetListFragment newInstance() {
        Bundle args = new Bundle();
        BudgetListFragment fragment = new BudgetListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.budgets);
        setHasOptionsMenu(true);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_BUDGET) {
            if (resultCode == Activity.RESULT_OK) {
                User u = UserManager.getInstance().getUser();
                mBudgets.clear();
                mBudgets.addAll(u.getBudgets());
                mBudgetsAdapter.notifyDataSetChanged();
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
            args.putString("action", ContainerActivity.CREATE_BUDGET);
            i.putExtras(args);
            startActivityForResult(i, CREATE_BUDGET);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class BudgetsAdapter extends ArrayAdapter<Budget> {

        public BudgetsAdapter() {
            super(getActivity(), R.layout.list_item_budget, mBudgets);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater(null).inflate(R.layout.list_item_budget, parent,
                        false);
            }
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView category = (TextView) convertView.findViewById(R.id.category);
            TextView value = (TextView) convertView.findViewById(R.id.value);

            Budget b = getItem(position);
            title.setText(b.getName());
            category.setText(b.getCategory());
            value.setText(Utils.getDollarString(b.getGoal()));
            return convertView;
        }
    }
}
