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


    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create users table
        db.execSQL(Users.CREATE_TABLE);

        // create categories table
        db.execSQL(Category.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Testing phase, hence no tasks on upgrade
    }

    public long addCategory(String category) {
        Item.TABLE_NAME = category;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(new Item().getCreateTableStatement(category));
        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME, category);

        return db.insert(Category.TABLE_NAME, null, values);
    }

    public long insertItem(String category, String item, float price, int quantity) {
        Item.TABLE_NAME = category;
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Item.COLUMN_NAME, item);
        values.put(Item.COLUMN_PRICE, price);
        values.put(Item.COLUMN_QUANTITY, quantity);

        // insert row
        long rowID = db.insert(Item.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return rowID;
    }

    public void addUser(String id, String name, boolean isAdmin) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Users.COLUMN_NAME, name);
        values.put(Users.COLUMN_ISADMIN, isAdmin);
        values.put(Users.COLUMN_ID, id);

        // insert row
        long rowID = db.insert(Users.TABLE_NAME, null, values);

        // close db connection
        db.close();

    }

    public Item getItem(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Item.TABLE_NAME,
                new String[]{Item.COLUMN_ID, Item.COLUMN_NAME, Item.COLUMN_PRICE, Item.COLUMN_QUANTITY},
                Item.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare item object
        Item item = new Item(
                cursor.getInt(cursor.getColumnIndex(Item.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Item.COLUMN_NAME)),
                cursor.getFloat(cursor.getColumnIndex(Item.COLUMN_PRICE)),
                cursor.getInt(cursor.getColumnIndex(Item.COLUMN_QUANTITY)));

        // close the db connection
        cursor.close();

        return item;
    }

    public Users getUser(String id) {
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

        return user;
    }

    public List<Item> getAllItems(String category) {
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
        db.close();

        // return items list
        return items;
    }

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

    public List<String> getCategories() {

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
                Log.w("Added", category);
                categories.add(category);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return items list
        return categories;
    }

    public int getItemsCount(String category) {
        Item.TABLE_NAME = category;
        String countQuery = "SELECT  * FROM " + Item.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateItem(String category, long id, String name, Float price, Integer quantity) {
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
        return db.update(Item.TABLE_NAME, values, Item.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int makeAdmin(String id, boolean isAdmin) {
        Users user = getUser(id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Users.COLUMN_ISADMIN, isAdmin);

        return db.update(Users.TABLE_NAME, values, Item.COLUMN_ID + " = ?",
                new String[]{id});
    }

    public void deleteItem(String category, long id) {
        Item.TABLE_NAME = category;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Item.TABLE_NAME, Item.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteUser(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Users.TABLE_NAME, Item.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}
