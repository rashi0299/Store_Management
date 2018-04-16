package proj.dbms.grocerystore;

import android.content.Context;

/**
 * Created by rutvora (www.github.com/rutvora)
 */
class CategoryAdapter extends android.support.v13.app.FragmentPagerAdapter {

    private Context mContext;

    CategoryAdapter(Context context, android.app.FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    @Override
    public android.app.Fragment getItem(int position) {
        /*if (position == 0) {
            return new Category1();
        } else {
            return new Category2();
        }
*/
        //TODO
        return null;
    }


    @Override
    public int getCount() {
        //TODO
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*
        if (position == 0) {
            return mContext.getString(R.string.aroundMe);
        } else if (position == 1) {
            return mContext.getString(R.string.friends);
        } else {
            return mContext.getString(R.string.strangers);
        }
        */
        //TODO
        return null;
    }
}
