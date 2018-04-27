package proj.dbms.grocerystore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class DBConnection extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Database";


    DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create users table
        db.execSQL(Users.CREATE_TABLE);

        // create categories table
        db.execSQL(Category.CREATE_TABLE);

        // create cart table
        db.execSQL(Cart.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Testing phase, hence no tasks on upgrade
    }

    //Cart Functions

    long addToCart(long category, long id) {
        List<Cart> list = getCart(Firebase.UID);
        boolean flag = false;
        int i;
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).getCategory() == category && list.get(i).getItem() == id) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Cart.COLUMN_CATEGORY, category);
            values.put(Cart.COLUMN_ITEM, id);
            values.put(Cart.COLUMN_USER, Firebase.UID);
            values.put(Cart.COLUMN_QUANTITY, 1);

            long result = db.insert(Cart.TABLE_NAME, null, values);
            db.close();
            return result;
        } else {
            return 0;
        }
    }

    void removeFromCart(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Cart.TABLE_NAME, Cart.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();

    }

    List<Cart> getCart(String UID) {
        List<Cart> cartItems = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Cart.TABLE_NAME,
                new String[]{Cart.COLUMN_ID, Cart.COLUMN_CATEGORY, Cart.COLUMN_ITEM, Cart.COLUMN_QUANTITY},
                Cart.COLUMN_USER + "=?",
                new String[]{"" + UID + ""}, null, null, Cart.COLUMN_CATEGORY, null);


        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Cart cartItem = new Cart();
                cartItem.setId(cursor.getInt(cursor.getColumnIndex(Cart.COLUMN_ID)));
                cartItem.setCategory(cursor.getLong(1));
                cartItem.setItem(cursor.getLong(2));
                cartItem.setQuantity(cursor.getLong(cursor.getColumnIndex(Cart.COLUMN_QUANTITY)));
                cartItems.add(cartItem);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        // return items list
        return cartItems;
    }

    //Category Functions

    long addCategory(String category) {
        Item.TABLE_NAME = category;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(new Item().getCreateTableStatement(category));
        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME, category);

        long result = db.insert(Category.TABLE_NAME, null, values);
        db.close();
        return result;
    }

    long getCategoryCode(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Category.TABLE_NAME,
                new String[]{Category.COLUMN_ID},
                Category.COLUMN_NAME + "=?",
                new String[]{"" + category + ""}, null, null, null, null);


        int result = 0;
        if (cursor != null)
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex(Category.COLUMN_ID));

            } else {
                Log.w("Error", "Cursor has no content, " + category);
            }

        // close the db connection
        cursor.close();
        db.close();
        return result;
    }

    String getCategoryName(long category) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Category.TABLE_NAME,
                new String[]{Category.COLUMN_NAME},
                Category.COLUMN_ID + "=?",
                new String[]{String.valueOf(category)}, null, null, null, null);


        String result = "";
        if (cursor != null)
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(Category.COLUMN_NAME));

            } else {
                Log.w("Error", "Cursor has no content, " + category);
            }

        // close the db connection
        cursor.close();
        db.close();
        return result;
    }

    List<String> getCategories() {

        List<String> categories = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Category.TABLE_NAME + " ORDER BY " +
                Category.COLUMN_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndex(Category.COLUMN_NAME));

                categories.add(category);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return items list
        return categories;
    }

    //Item Functions

    long insertItem(String category, String item, float price, int quantity, String imageResource) {
        Item.TABLE_NAME = category;
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Item.COLUMN_NAME, item);
        values.put(Item.COLUMN_PRICE, price);
        values.put(Item.COLUMN_QUANTITY, quantity);
        values.put(Item.COLUMN_IMAGE, imageResource);

        // insert row
        long rowID = db.insert(Item.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return rowID;
    }

    Item getItem(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Item.TABLE_NAME,
                new String[]{Item.COLUMN_ID, Item.COLUMN_NAME, Item.COLUMN_PRICE, Item.COLUMN_QUANTITY, Item.COLUMN_IMAGE},
                Item.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare item object
        Item item = new Item(
                cursor.getInt(cursor.getColumnIndex(Item.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Item.COLUMN_NAME)),
                cursor.getFloat(cursor.getColumnIndex(Item.COLUMN_PRICE)),
                cursor.getInt(cursor.getColumnIndex(Item.COLUMN_QUANTITY)),
                cursor.getString(cursor.getColumnIndex(Item.COLUMN_IMAGE)));

        // close the db connection
        cursor.close();
        db.close();

        return item;
    }

    List<Item> getAllItems(String category) {
        Item.TABLE_NAME = category;
        List<Item> items = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Item.TABLE_NAME + " ORDER BY " +
                Item.COLUMN_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(Item.COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(Item.COLUMN_NAME)));
                item.setPrice(cursor.getFloat(cursor.getColumnIndex(Item.COLUMN_PRICE)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndex(Item.COLUMN_QUANTITY)));

                if (item.getQuantity() != 0) items.add(item);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        // return items list
        return items;
    }

    /*
    public int getItemsCount(String category) {
        Item.TABLE_NAME = category;
        String countQuery = "SELECT  * FROM " + Item.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();


        // return count
        return count;
    } */

    long updateItem(String category, long id, String name, Float price, Long quantity) {
        Item.TABLE_NAME = category;
        Item item = getItem(id);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (name != null) values.put(Item.COLUMN_NAME, name);
        else values.put(Item.COLUMN_NAME, item.getName());

        if (price != null) values.put(Item.COLUMN_PRICE, price);
        else values.put(Item.COLUMN_PRICE, item.getPrice());

        if (quantity != null) values.put(Item.COLUMN_QUANTITY, quantity);
        else values.put(Item.COLUMN_QUANTITY, item.getQuantity());

        // updating row
        if (quantity != null) {
            long result = db.update(Item.TABLE_NAME, values, Item.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            db.close();
            return result;
        } else {
            db.close();
            deleteItem(category, id);
            return 0;
        }
    }

    private void deleteItem(String category, long id) {
        Item.TABLE_NAME = category;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Item.TABLE_NAME, Item.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    //User Functions

    void addUser(String id, String name, boolean isAdmin) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Users.COLUMN_NAME, name);
        values.put(Users.COLUMN_ISADMIN, isAdmin);
        values.put(Users.COLUMN_ID, id);

        // insert row
        db.insert(Users.TABLE_NAME, null, values);

        // close db connection
        db.close();

    }

    Users getUser(String id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Users.TABLE_NAME,
                new String[]{Users.COLUMN_ID, Users.COLUMN_NAME, Users.COLUMN_ISADMIN},
                Item.COLUMN_ID + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare item object
        Users user = new Users(
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Users.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(Users.COLUMN_ISADMIN)));

        // close the db connection
        cursor.close();
        db.close();

        return user;
    }

    /*
    public List<Users> getAllUsers() {

        List<Users> users = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Users.TABLE_NAME + " ORDER BY " +
                Users.COLUMN_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Users user = new Users(cursor.getString(cursor.getColumnIndex(Users.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Users.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndex(Users.COLUMN_ISADMIN)));

                users.add(user);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return items list
        return users;
    }

    public int makeAdmin(String id, boolean isAdmin) {
        Users user = getUser(id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Users.COLUMN_ISADMIN, isAdmin);

        return db.update(Users.TABLE_NAME, values, Item.COLUMN_ID + " = ?",
                new String[]{id});
    }



    public void deleteUser(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Users.TABLE_NAME, Item.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
    */
}
