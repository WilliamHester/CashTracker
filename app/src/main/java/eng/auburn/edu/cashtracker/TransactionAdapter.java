package eng.auburn.edu.cashtracker;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by william on 11/30/15.
 */
public class TransactionAdapter extends ArrayAdapter<Transaction> {

    public TransactionAdapter(Context c, ArrayList<Transaction> transactions) {
        super(c, R.layout.list_item_transaction, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        TextView category = (TextView) v.findViewById(R.id.category);
        TextView date = (TextView) v.findViewById(R.id.date);
        TextView amount = (TextView) v.findViewById(R.id.amount);

        Transaction t = getItem(position);
        category.setText(t.getCategory());
        date.setText(DateUtils.formatDateTime(getContext(), t.getDate(), DateUtils.FORMAT_NUMERIC_DATE));
        amount.setText(Utils.getDollarString(t.getAmount()));

        return v;
    }
}
