package proj.dbms.grocerystore;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class Cart {
    static final String COLUMN_ID = "ID";
    static final String COLUMN_USER = "UserID";
    static final String COLUMN_CATEGORY = "CategoryCode";
    static final String COLUMN_ITEM = "ItemCode";
    static final String COLUMN_QUANTITY = "Quantity";
    static final String TABLE_NAME = "Cart";
    // Create table SQL query
    static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER + " TEXT,"
                    + COLUMN_CATEGORY + " INTEGER,"
                    + COLUMN_ITEM + " INTEGER,"
                    + COLUMN_QUANTITY + " INTEGER,"
                    + " FOREIGN KEY (" + COLUMN_USER + ") REFERENCES " + Users.TABLE_NAME + "(" + Users.COLUMN_ID + "),"
                    + " FOREIGN KEY (" + COLUMN_CATEGORY + ") REFERENCES " + Category.TABLE_NAME + "(" + Category.COLUMN_ID + ")"
                    + ")";
    private long id, category, item, quantity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public long getItem() {
        return item;
    }

    public void setItem(long item) {
        this.item = item;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
