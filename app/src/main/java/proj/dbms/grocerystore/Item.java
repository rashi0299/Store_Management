package proj.dbms.grocerystore;

import java.io.Serializable;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class Item implements Serializable {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_PRICE = "Price";
    public static final String COLUMN_QUANTITY = "Quantity";
    public static String TABLE_NAME;


    private long id;
    private String name;
    private float price;
    private int quantity;

    public Item() {
    }

    public Item(long id, String name, float price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCreateTableStatement(String category) {
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + category + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_PRICE + " FLOAT,"
                        + COLUMN_QUANTITY + " INTEGER"
                        + ")";
        return CREATE_TABLE;
    }
}
