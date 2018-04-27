package proj.dbms.grocerystore;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by rutvora (www.github.com/rutvora)
 */


public class CustomTextWatcher implements TextWatcher {

    private TextView textView;
    private float price;
    private Cart cartItem;

    CustomTextWatcher(Cart cartItem, TextView textView, float price) {
        this.textView = textView;
        this.price = price;
        this.cartItem = cartItem;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String s = editable.subSequence(0, editable.length()).toString();
        if (!s.equals("")) {
            textView.setText(String.valueOf((Float.parseFloat(s) * price)));
            cartItem.setQuantity(Long.parseLong(s));
        }
    }
}
