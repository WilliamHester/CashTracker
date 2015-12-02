package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by william on 11/30/15.
 */
public class BudgetDetailsFragment extends Fragment {

    private final ArrayList<Transaction> mTransactions = new ArrayList<>();

    private TransactionAdapter mAdapter;
    private Budget mBudget;

    @Bind(R.id.list) ListView mListView;

    public static BudgetDetailsFragment newInstance(String budgetName) {
        Bundle args = new Bundle();
        args.putString("budgetName", budgetName);
        BudgetDetailsFragment fragment = new BudgetDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String budgetName = getArguments().getString("budgetName");
        for (Budget b : UserManager.getInstance().getUser().getBudgets()) {
            if (b.getName().equals(budgetName)) {
                mBudget = b;
                break;
            }
        }
        findRelevantTransactions();
        getActivity().setTitle(budgetName);

        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(getActivity(), mTransactions,
                    getLayoutInflater(null));
        }
    }

    private void findRelevantTransactions() {
        User u = UserManager.getInstance().getUser();
        for (Account a : u.getAccounts()) {
            for (Transaction t : a.getTransactions()) {
                if (t.getCategory() != null && t.getCategory().equals(mBudget.getCategory())) {
                    mTransactions.add(t);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
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
}
