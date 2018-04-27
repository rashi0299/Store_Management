package proj.dbms.grocerystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class CartAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    CartFragment fragment;
    private Context context;
    private List<Cart> cartItems;
    private List<Item> items = new ArrayList<>();
    private float total = 0;

    CartAdapter(Context receivedContext, List<Cart> cartItems, CartFragment fragment) {

        context = receivedContext;
        this.cartItems = cartItems;
        this.fragment = fragment;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DBConnection connection = new DBConnection(context);
        for (int i = 0; i < cartItems.size(); i++) {
            Item item = connection.getItem(cartItems.get(i).getItem());
            items.add(item);
            total += item.getPrice();
        }


    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cartItems.get(position).getItem();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inflater.inflate(R.layout.cart_row, null); //TODO: Create proper rowView for this
        TextView name = rowView.findViewById(R.id.itemName);
        EditText quantity = rowView.findViewById(R.id.itemDemand);
        TextView price = rowView.findViewById(R.id.itemPrice);
        name.setText(this.items.get(position).getName());
        quantity.setText(cartItems.get(position).getQuantity() + "");
        CustomTextWatcher customTextWatcher = new CustomTextWatcher(this, this.cartItems.get(position), price, this.items.get(position).getPrice());
        quantity.addTextChangedListener(customTextWatcher);
        price.setText(String.valueOf(this.items.get(position).getPrice()));
        return rowView;
    }

    float getTotal() {
        return total;
    }

    void setTotal(float total) {
        this.total = total;
        fragment.setTotal(this.total);
    }
}