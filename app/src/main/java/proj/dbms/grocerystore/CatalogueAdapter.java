package proj.dbms.grocerystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class CatalogueAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    List<Item> items;

    CatalogueAdapter(Context receivedContext, List<Item> items) {

        context = receivedContext;
        this.items = items;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inflater.inflate(R.layout.item_row, null); //TODO: Create proper rowView for this
        TextView name = rowView.findViewById(R.id.itemName);
        TextView quantity = rowView.findViewById(R.id.itemQuantity);
        TextView price = rowView.findViewById(R.id.itemPrice);
        name.setText(this.items.get(position).getName());
        quantity.setText(String.valueOf(this.items.get(position).getQuantity()));
        price.setText(String.valueOf(this.items.get(position).getPrice()));
        return rowView;
    }
}