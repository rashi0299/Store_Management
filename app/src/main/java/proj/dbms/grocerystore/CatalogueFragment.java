package proj.dbms.grocerystore;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Boolean.TRUE;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class CatalogueFragment extends Fragment implements View.OnClickListener {

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
        listView.setAdapter(adapter);
        add.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addItem:
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_item, null);
                final EditText name = dialogView.findViewById(R.id.editName);
                final EditText price = dialogView.findViewById(R.id.editPrice);
                final EditText quantity = dialogView.findViewById(R.id.editQuantity);
                Button add = dialogView.findViewById(R.id.confirm);
                Button cancel = dialogView.findViewById(R.id.dismiss);

                final PopupWindow popup = new PopupWindow(dialogView, 900, 900);
                popup.setBackgroundDrawable(new ColorDrawable(0x80000000));
                popup.setFocusable(TRUE);
                popup.showAtLocation(catalogueLayout, Gravity.CENTER, 0, 0);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!name.getText().toString().equals("") && !price.getText().toString().equals("") && !quantity.getText().toString().equals("")) {
                            DBConnection connection = new DBConnection(getActivity());
                            long id = connection.insertItem(category,
                                    name.getText().toString(),
                                    Float.parseFloat(price.getText().toString()),
                                    Integer.parseInt(quantity.getText().toString()));
                            Item item = new Item(id,
                                    name.getText().toString(),
                                    Float.parseFloat(price.getText().toString()),
                                    Integer.parseInt(quantity.getText().toString()));
                            items.add(item);
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
}
