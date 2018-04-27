package proj.dbms.grocerystore;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Boolean.TRUE;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class CatalogueFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    List<Item> items;
    String category;
    RelativeLayout catalogueLayout;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);
        category = this.getArguments().getString("Category");
        items = new DBConnection(getActivity()).getAllItems(category);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.catalogue, container, false);
        catalogueLayout = rootView.findViewById(R.id.catalogueLayout);
        CircleImageView add = rootView.findViewById(R.id.addItem);
        if (!MainActivity.isAdmin) {
            add.setVisibility(View.GONE);
        }
        listView = rootView.findViewById(R.id.list);
        CatalogueAdapter adapter = new CatalogueAdapter(getActivity(), items);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        add.setOnClickListener(this);

        TextView totalText = rootView.findViewById(R.id.totalText);
        TextView total = rootView.findViewById(R.id.total);

        totalText.setVisibility(View.GONE);
        total.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addItem:
                popup(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Item item = items.get(i);
        if (MainActivity.isAdmin) popup(item);
        else {
            if (Firebase.UID != null) {
                DBConnection connection = new DBConnection(getActivity());
                long categoryCode = connection.getCategoryCode(category);
                long res = connection.addToCart(categoryCode, item.getId());
                Toast.makeText(getActivity(), "Successfully added to cart", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void popup(@Nullable final Item item) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_item, null);
        final EditText name = dialogView.findViewById(R.id.editName);
        final EditText price = dialogView.findViewById(R.id.editPrice);
        final EditText quantity = dialogView.findViewById(R.id.editQuantity);
        if (item != null) {
            name.setText(item.getName());
            price.setText(String.valueOf(item.getPrice()));
            quantity.setText(String.valueOf(item.getQuantity()));
        }
        Button add = dialogView.findViewById(R.id.confirm);
        final Button cancel = dialogView.findViewById(R.id.dismiss);

        final PopupWindow popup = new PopupWindow(dialogView, 900, 900);
        popup.setBackgroundDrawable(new ColorDrawable(0xff808080));
        popup.setFocusable(TRUE);
        popup.showAtLocation(catalogueLayout, Gravity.CENTER, 0, 0);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().equals("") && !price.getText().toString().equals("") && !quantity.getText().toString().equals("")) {
                    DBConnection connection = new DBConnection(getActivity());
                    if (item == null) {
                        long newId = connection.insertItem(category,
                                name.getText().toString(),
                                Float.parseFloat(price.getText().toString()),
                                Integer.parseInt(quantity.getText().toString()), null);
                        Item item = new Item(newId,
                                name.getText().toString(),
                                Float.parseFloat(price.getText().toString()),
                                Integer.parseInt(quantity.getText().toString()), null);
                        items.add(item);
                    } else {
                        items.remove(item);
                        connection.updateItem(category, item.getId(),
                                name.getText().toString(),
                                Float.parseFloat(price.getText().toString()),
                                Long.parseLong(quantity.getText().toString()));
                        Item newItem = new Item(item.getId(),
                                name.getText().toString(),
                                Float.parseFloat(price.getText().toString()),
                                Integer.parseInt(quantity.getText().toString()), null);
                        if (newItem.getQuantity() != 0) items.add(newItem);
                    }

                    CatalogueAdapter adapter = new CatalogueAdapter(getActivity(), items);
                    listView.setAdapter(adapter);
                    popup.dismiss();
                } else {
                    Toast.makeText(getActivity(), "You forgot to enter something", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });
    }
}
