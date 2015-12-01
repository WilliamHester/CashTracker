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
public class AccountsFragment extends Fragment {

    private static final int CREATE_ACCOUNT = 10;

    private final ArrayList<Account> mAccounts = new ArrayList<>();

    private AccountsAdapter mAdapter;

    @Bind(R.id.list) ListView mListView;
    @Bind(R.id.accounts_total) TextView mAccountsTotal;

    public static AccountsFragment newInstance() {
        Bundle args = new Bundle();
        AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(R.string.accounts);
            setHasOptionsMenu(true);
        }

        if (mAccounts.size() == 0) {
            mAccounts.addAll(UserManager.getInstance().getUser().getAccounts());
        }
        if (mAdapter == null) {
            mAdapter = new AccountsAdapter();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mAdapter);

        User u = UserManager.getInstance().getUser();
        mAccountsTotal.setText(Utils.getDollarString(User.getAccountsTotal(u)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                User u = UserManager.getInstance().getUser();
                mAccounts.clear();
                mAccounts.addAll(u.getAccounts());
                mAdapter.notifyDataSetChanged();
                if (mAccountsTotal != null) {
                    mAccountsTotal.setText(Utils.getDollarString(User.getAccountsTotal(u)));
                }
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
            args.putString("action", ContainerActivity.CREATE_ACCOUNT);
            i.putExtras(args);
            startActivityForResult(i, CREATE_ACCOUNT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    private class AccountsAdapter extends ArrayAdapter<Account> {

        public AccountsAdapter() {
            super(getActivity(), R.layout.list_item_account, mAccounts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView interestRate = (TextView) v.findViewById(R.id.interest_rate);
            TextView balance = (TextView) v.findViewById(R.id.balance);

            Account a = getItem(position);
            title.setText(a.getName());
            interestRate.setText(String.format(getResources().getString(R.string.interest_rate_format),
                    a.getInterestRate()));
            balance.setText(String.format(getResources().getString(R.string.balance_format),
                    Account.getBalance(a)));

            return v;
        }
    }
}
