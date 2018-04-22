package proj.dbms.grocerystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class CatalogueAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    ArrayList<String> name;
    Context context;
    ArrayList<Float> price;
    ArrayList<Integer> quantity;
    ArrayList<Integer> id;

    CatalogueAdapter(Context receivedContext, ArrayList<Integer> id, ArrayList<String> name, ArrayList<Float> price, ArrayList<Integer> quantity) {

        this.name = name;
        context = receivedContext;
        this.quantity = quantity;
        this.price = price;
        this.id = id;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return id.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inflater.inflate(R.layout.item_row, null); //TODO: Create proper rowView for this
        TextView name = rowView.findViewById(R.id.itemName);
        TextView quantity = rowView.findViewById(R.id.itemQuantity);
        TextView price = rowView.findViewById(R.id.itemPrice);
        name.setText(this.name.get(position));
        quantity.setText(this.quantity.get(position));
        price.setText(String.valueOf(this.price.get(position)));
        return rowView;
    }

}