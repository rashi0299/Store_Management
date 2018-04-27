package proj.dbms.grocerystore;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

/**
 * Created by rutvora (www.github.com/rutvora)
 */
class CategoryAdapter extends android.support.v13.app.FragmentPagerAdapter {

    private static DBConnection conn;
    List<String> categories;
    private Context mContext;

    CategoryAdapter(Context context, android.app.FragmentManager fm) {
        super(fm);
        mContext = context;
        conn = new DBConnection(context);
        this.categories = conn.getCategories();
    }


    @Override
    public android.app.Fragment getItem(int position) {

        CatalogueFragment fragment = new CatalogueFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Category", categories.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public int getCount() {

        return categories.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return categories.get(position);
    }
}
