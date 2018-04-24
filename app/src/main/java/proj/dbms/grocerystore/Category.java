package proj.dbms.grocerystore;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class Category {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "Name";
    public static final String TABLE_NAME = "Categories";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT"
                    + ")";
}
