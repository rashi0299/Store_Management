package proj.dbms.grocerystore;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class CartFragment extends Fragment implements AdapterView.OnItemLongClickListener, View.OnClickListener {

    List<Cart> cartItems;
    String category;
    RelativeLayout catalogueLayout;
    ListView listView;
    TextView total;


    @Override
    public void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);
        cartItems = new DBConnection(getActivity()).getCart(Firebase.UID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.catalogue, container, false);
        catalogueLayout = rootView.findViewById(R.id.catalogueLayout);
        CircleImageView add = rootView.findViewById(R.id.addItem);
        if (!MainActivity.isAdmin) add.setImageResource(android.R.drawable.arrow_down_float);
        //add.setVisibility(View.GONE);
        add.setOnClickListener(this);
        total = rootView.findViewById(R.id.total);
        listView = rootView.findViewById(R.id.list);
        CartAdapter adapter = new CartAdapter(getActivity(), cartItems, this);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);
        setTotal(adapter.getTotal());
        return rootView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        new DBConnection(getActivity()).removeFromCart(cartItems.get(i).getId());
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addItem:
                DBConnection connection = new DBConnection(getActivity());
                int count = 0;
                for (int i = 0; i < cartItems.size(); i++) {
                    Cart cartItem = cartItems.get(i);
                    Item item = connection.getItem(cartItem.getItem());
                    if (item.getQuantity() - cartItem.getQuantity() > 0) {
                        connection.updateItem(connection.getCategoryName(cartItem.getCategory()), cartItem.getItem(), null, null, item.getQuantity() - cartItem.getQuantity());
                        connection.removeFromCart(cartItem.getId());
                        count++;
                    } else {
                        Toast.makeText(getActivity(), "Insufficient quantity of " + item.getName() + " in stock", Toast.LENGTH_SHORT).show();
                    }
                }
                if (count < cartItems.size())
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, new CartFragment()).commit();
                else {
                    Toast.makeText(getActivity(), "Purchase Successfull", Toast.LENGTH_SHORT).show();
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeScreen()).commit();
                }

        }
    }

    void setTotal(float total) {
        this.total.setText(String.valueOf(total));
    }
}
