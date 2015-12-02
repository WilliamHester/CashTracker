package eng.auburn.edu.cashtracker;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by william on 11/30/15.
 */
public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private LayoutInflater mLayoutInflater;

    public TransactionAdapter(Context c, ArrayList<Transaction> transactions,
                              LayoutInflater inflater) {
        super(c, R.layout.list_item_transaction, transactions);
        mLayoutInflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_transaction, parent, false);
        }
        TextView category = (TextView) convertView.findViewById(R.id.category);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView amount = (TextView) convertView.findViewById(R.id.amount);

        Transaction t = getItem(position);
        category.setText(t.getCategory());
        date.setText(DateUtils.formatDateTime(getContext(), t.getDate(), DateUtils.FORMAT_NUMERIC_DATE));
        amount.setText(Utils.getDollarString(t.getAmount()));

        return convertView;
    }
}
