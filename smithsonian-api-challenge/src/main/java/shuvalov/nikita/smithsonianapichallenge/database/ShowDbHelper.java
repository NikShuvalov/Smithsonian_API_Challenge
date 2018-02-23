package shuvalov.nikita.smithsonianapichallenge.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ShowDbHelper {

    public static final String DATABASE_URI= "jdbc:h2:mem:dummydb";
    public static final String ADMIN_LOGIN = "sa";

    public static final String SHOW_TABLE = "SHOW";
    public static final String KEYWORD_TABLE = "KEYWORD";
    public static final String SHOW_KEY_JOIN = "SHOW_KEYWORD";

    public static final String SHOW_ID_COLUMN = "SHOW_ID";
    public static final String TITLE_COLUMN = "TITLE";
    public static final String DESCRIP_COLUMN = "DESCRIPTION";
    public static final String DURATION_COLUMN ="DURATION";
    public static final String RATING_COLUMN = "RATING";
    public static final String AIRDATE_COLUMN = "ORIGINAL_AIRDATE";

    public static final String KEYWORD_ID_COLUMN = "KEYWORD_ID";
    public static final String KEYWORD_TEXT_COLUMN = "KEYWORD_TEXT";


    private static final String CREATE_SHOW_TABLE_EXE = "CREATE TABLE " + SHOW_TABLE + " ("+
            SHOW_ID_COLUMN + " INTEGER PRIMARY KEY, " +
            TITLE_COLUMN + " TEXT, " +
            DESCRIP_COLUMN + " TEXT, " +
            DURATION_COLUMN + " BIGINT, " +
            RATING_COLUMN + " FLOAT, " +
            AIRDATE_COLUMN + " BIGINT)"
            ;


    private static final String CREATE_KEYWORD_TABLE_EXE = "CREATE TABLE " + KEYWORD_TABLE + " (" +
            KEYWORD_ID_COLUMN + " INTEGER PRIMARY KEY, " +
            KEYWORD_TEXT_COLUMN + " TEXT)";

    private static final String KEYWORD_SHOW_JOIN_EXE = "CREATE TABLE " + SHOW_KEY_JOIN + " (" +
            KEYWORD_ID_COLUMN + " INTEGER, " +
            SHOW_ID_COLUMN + " INTEGER)";

    private static ShowDbHelper sShowDbHelper;

    private Connection mConnection;

    private ShowDbHelper(){
        createTables();
//        seedDatabaseWithDummyData();
    }

    private void createTables(){
        try {
            if(mConnection == null) {
                startConnection();
            }
            Statement statement = mConnection.createStatement();
            statement.execute(CREATE_SHOW_TABLE_EXE);
            statement.execute(CREATE_KEYWORD_TABLE_EXE);
            statement.execute(KEYWORD_SHOW_JOIN_EXE);
            statement.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void startConnection() throws SQLException{
        mConnection = DriverManager.getConnection(DATABASE_URI, ADMIN_LOGIN, "");

    }

    public static ShowDbHelper getInstance() {
        if(sShowDbHelper == null){
            sShowDbHelper = new ShowDbHelper();
        }
        return sShowDbHelper;
    }

    private void seedDatabaseWithDummyData() {
//        List<Show>
//        try {
//            Connection connection = DriverManager.getConnection(DATABASE_URI, ADMIN_LOGIN, "");
//            Statement statement = connection.createStatement();
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
