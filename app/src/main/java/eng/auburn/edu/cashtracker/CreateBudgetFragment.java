package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Set;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by william on 12/1/15.
 */
public class CreateBudgetFragment extends Fragment {

    private Realm mRealm;

    @Bind(R.id.budget_name) EditText mBudgetName;
    @Bind(R.id.budget_goal) EditText mBudgetGoal;
    @Bind(R.id.create) Button mCreate;
    @Bind(R.id.category_spinner) Spinner mSpinner;

    public static CreateBudgetFragment newInstance() {
        Bundle args = new Bundle();
        CreateBudgetFragment fragment = new CreateBudgetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_budget, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final CategoryAdapter adapter = new CategoryAdapter();
        mSpinner.setAdapter(adapter);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = UserManager.getInstance().getUser();
                for (Budget b : u.getBudgets()) {
                    if (b.getName().equalsIgnoreCase(mBudgetName.getText().toString().trim())) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage(R.string.budget_already_exists)
                                .setPositiveButton(R.string.okay, null)
                                .show();
                        return;
                    }
                }

                String name = mBudgetName.getText().toString();
                int target = (int) (Double.parseDouble(mBudgetGoal.getText().toString()) * 100);

                mRealm.beginTransaction();
                Budget b = mRealm.createObject(Budget.class);
                b.setGoal(target);
                b.setName(name);
                b.setCategory(adapter.getItem(mSpinner.getSelectedItemPosition()));
                u.getBudgets().add(b);
                mRealm.commitTransaction();

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class CategoryAdapter extends ArrayAdapter<String> {
        public CategoryAdapter() {
            super(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
            SharedPreferences prefs = getActivity().getSharedPreferences("prefs",
                    Context.MODE_PRIVATE);
            Set<String> strings = new TreeSet<>();
            prefs.getStringSet("categories", strings);
            addAll(strings);
        }
    }
}
