package shuvalov.nikita.smithsonianapichallenge.database;

import shuvalov.nikita.smithsonianapichallenge.entity.Show;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShowDbHelper {

    public static final String DATABASE_URI= "jdbc:h2:mem:dummydb";
    public static final String ADMIN_LOGIN = "sa";

    public static final String SHOW_TABLE = "SHOW_TABLE";
    public static final String KEYWORD_TABLE = "KEYWORD_TABLE";
    public static final String SHOW_KEY_JOIN = "SHOW_KEYWORD_JOIN";

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
            AIRDATE_COLUMN + " BIGINT," +
            RATING_COLUMN + " FLOAT)"
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
        seedDatabaseWithDummyData();
    }

    private void createTables(){
        try {
            if(mConnection == null){
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

    private void seedDatabaseWithDummyData(){
        try {
            if(mConnection == null){
                startConnection();
            }

            Statement statement = mConnection.createStatement();
            List<String> genericKeywords = new ArrayList<>();
            genericKeywords.add("Quirky");
            genericKeywords.add("Cerebral");
            genericKeywords.add("Funny");
            genericKeywords.add("Provocative");
            System.out.println("INSERTION RUNS");
            insertShowIntoDatabase(statement, new Show(0, "The Big Bang Theory", "Nerds do physics while some hot girl annoys them", 60000 * 30, 0, 8.3f, genericKeywords));
            insertShowIntoDatabase(statement, new Show(1, "Aerial America", "Take a virtual tour of America as seen from an airplane", 60000 * 30, 0, 8.7f, genericKeywords));
            insertShowIntoDatabase(statement, new Show(2, "Filler", "A description is worth a 1000 words", 60000 * 30, 0, 8.7f, genericKeywords));
            insertShowIntoDatabase(statement, new Show(3, "Additional Filler", "Descriptive text 2: Electric boogaloo", 60000 * 30, 0, 8.7f, genericKeywords));

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void startConnection() throws SQLException{
        mConnection =  DriverManager.getConnection(DATABASE_URI, ADMIN_LOGIN, "");
        System.out.println("Database connection created");
    }

    public static ShowDbHelper getInstance() {
        instantiate();
        System.out.println("Database begun");
        return sShowDbHelper;
    }

    public static void instantiate(){
        if(sShowDbHelper == null){
            sShowDbHelper = new ShowDbHelper();
        }
    }

    private Show getShowFromQueryResults(ResultSet cursor) throws SQLException{
        int id = cursor.getInt(cursor.findColumn(SHOW_ID_COLUMN));
        String title = cursor.getString(cursor.findColumn(TITLE_COLUMN));
        String descrip = cursor.getString(cursor.findColumn(DESCRIP_COLUMN));
        long duration = cursor.getLong(cursor.findColumn(DURATION_COLUMN));
        long airDate = cursor.getLong(cursor.findColumn(AIRDATE_COLUMN));
        float rating = cursor.getFloat(cursor.findColumn(RATING_COLUMN));

        Show show = new Show(id, title, descrip, duration, airDate, rating);
        //ToDo: Attach KeyWords to Show;
        return show;
    }

    private void insertShowIntoDatabase(Statement statement, Show show) throws SQLException {
        String executionString = String.format("INSERT INTO %s VALUES (%s, '%s', '%s', %s, %s, %s)",
                SHOW_TABLE,
                show.getId(), show.getTitle(), show.getDescription(), show.getDuration(), show.getOriginalAirDate(), show.getRating()
        );
        //ToDo: Handle Keywords
        statement.execute(executionString);
    }


    public Collection<Show> getAllShows(){
        System.out.println("DBHelper AllShows Called");
        Collection<Show> allShows = new ArrayList<>();
        try {
            if(mConnection == null){
                startConnection();
            }
            Statement statement = mConnection.createStatement();
            ResultSet cursor = statement.executeQuery("SELECT * FROM " + SHOW_TABLE);
            while(cursor.next()){
                allShows.add(getShowFromQueryResults(cursor));
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allShows;
    }
}
