package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by william on 11/30/15.
 */
public class AccountListFragment extends Fragment {

    private static final int CREATE_ACCOUNT = 10;

    private final ArrayList<Account> mAccounts = new ArrayList<>();

    private AccountsAdapter mAdapter;

    @Bind(R.id.list) ListView mListView;
    @Bind(R.id.accounts_total) TextView mAccountsTotal;

    public static AccountListFragment newInstance() {
        Bundle args = new Bundle();
        AccountListFragment fragment = new AccountListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.accounts);
        setHasOptionsMenu(true);
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ContainerActivity.class);
                Bundle args = new Bundle();
                args.putString("action", ContainerActivity.VIEW_ACCOUNT);
                args.putString("name", mAdapter.getItem(position).getName());
                i.putExtras(args);
                startActivity(i);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.delete_account)
                        .setMessage(R.string.delete_account_message)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Realm realm = Realm.getInstance(getActivity());
                                Account a = mAdapter.getItem(position);
                                RealmResults<Goal> goals = realm.where(Goal.class).findAll();
                                ArrayList<Goal> toRemoveGoals = new ArrayList<>();
                                for (Goal g : goals) {
                                    if (g.getAccount().getName().equals(a.getName())) {
                                        toRemoveGoals.add(g);
                                    }
                                }
                                realm.beginTransaction();
                                for (Goal g : toRemoveGoals) {
                                    g.removeFromRealm();
                                }
                                for (Transaction t : a.getTransactions()) {
                                    t.removeFromRealm();
                                }
                                a.removeFromRealm();
                                realm.commitTransaction();

                                updateAccounts(UserManager.getInstance().getUser().getAccounts());
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        updateAccounts(UserManager.getInstance().getUser().getAccounts());
    }

    private void updateAccounts(RealmList<Account> accounts) {
        mAccounts.clear();
        mAccounts.addAll(accounts);
        mAdapter.notifyDataSetChanged();
        User u = UserManager.getInstance().getUser();
        mAccountsTotal.setText(Utils.getDollarString(User.getAccountsTotal(u)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                User u = UserManager.getInstance().getUser();
                updateAccounts(u.getAccounts());
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

        private LayoutInflater mLayoutInflater;

        public AccountsAdapter() {
            super(getActivity(), R.layout.list_item_account, mAccounts);
            mLayoutInflater = getLayoutInflater(null);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_item_account, parent, false);
            }
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView interestRate = (TextView) convertView.findViewById(R.id.interest_rate);
            TextView balance = (TextView) convertView.findViewById(R.id.balance);

            Account a = getItem(position);
            title.setText(a.getName());
            interestRate.setText(String.format(getResources().getString(R.string.interest_rate_format),
                    a.getInterestRate()));
            balance.setText(String.format(getResources().getString(R.string.balance_format),
                    Utils.getDollarString(Account.getBalance(a))));

            return convertView;
        }
    }
}
