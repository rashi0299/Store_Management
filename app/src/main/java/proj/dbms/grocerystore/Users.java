package proj.dbms.grocerystore;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class Users {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_ISADMIN = "isAdmin";
    public static String TABLE_NAME = "Users";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT PRIMARY KEY,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_ISADMIN + " INTEGER"
                    + ")";

    private String id;
    private String name;
    private boolean isAdmin;

    public Users(String id, String name, int isAdmin) {
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin != 0;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
