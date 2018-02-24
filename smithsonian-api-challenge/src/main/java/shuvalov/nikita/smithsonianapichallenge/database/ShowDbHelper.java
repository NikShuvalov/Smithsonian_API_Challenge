package shuvalov.nikita.smithsonianapichallenge.database;

import shuvalov.nikita.smithsonianapichallenge.entity.Show;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShowDbHelper {

    //=============================================== Constants ===============================================
    private static final String DATABASE_URI= "jdbc:h2:mem:dummydb";
    private static final String ADMIN_LOGIN = "sa";

    private static final String SHOW_TABLE = "SHOW_TABLE";
    private static final String KEYWORD_TABLE = "KEYWORD_TABLE";

    private static final String SHOW_ID_COLUMN = "SHOW_ID";
    private static final String TITLE_COLUMN = "TITLE";
    private static final String DESCRIP_COLUMN = "DESCRIPTION";
    private static final String DURATION_COLUMN ="DURATION";
    private static final String RATING_COLUMN = "RATING";
    private static final String AIRDATE_COLUMN = "ORIGINAL_AIRDATE";

    private static final String KEYWORD_TEXT_COLUMN = "KEYWORD_TEXT";


    private static final String CREATE_SHOW_TABLE_EXE = "CREATE TABLE " + SHOW_TABLE + " ("+
            SHOW_ID_COLUMN + " INTEGER PRIMARY KEY, " +
            TITLE_COLUMN + " TEXT, " +
            DESCRIP_COLUMN + " TEXT, " +
            DURATION_COLUMN + " BIGINT, " +
            AIRDATE_COLUMN + " BIGINT," +
            RATING_COLUMN + " FLOAT)"
            ;


    private static final String CREATE_KEYWORD_TABLE_EXE = "CREATE TABLE " + KEYWORD_TABLE + " (" +
            SHOW_ID_COLUMN + " INTEGER, " +
            KEYWORD_TEXT_COLUMN + " TEXT)";

    //========================================= Init =========================================
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
            insertShowIntoDatabase(statement, new Show(0, "The Big Bang Theory", "Nerds do physics while some hot girl annoys them", 60000 * 30, 0, 8.3f, genericKeywords));
            insertShowIntoDatabase(statement, new Show(1, "Aerial America", "Take a virtual tour of America as seen from an airplane", 60000 * 30, 0, 8.7f, genericKeywords));
            insertShowIntoDatabase(statement, new Show(2, "Filler", "A description is worth a 1000 words", 60000 * 30, 0, 8.7f, genericKeywords));
            insertShowIntoDatabase(statement, new Show(3, "Additional Filler", "Descriptive text 2: Electric boogaloo", 60000 * 30, 0, 8.7f, genericKeywords));
            System.out.println("Seeded database");
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

    //=================================== Util ==========================================================
    private Show getShowFromQueryResult(ResultSet cursor) throws SQLException{
        int id = cursor.getInt(cursor.findColumn(SHOW_ID_COLUMN));
        String title = cursor.getString(cursor.findColumn(TITLE_COLUMN));
        String descrip = cursor.getString(cursor.findColumn(DESCRIP_COLUMN));
        long duration = cursor.getLong(cursor.findColumn(DURATION_COLUMN));
        long airDate = cursor.getLong(cursor.findColumn(AIRDATE_COLUMN));
        float rating = cursor.getFloat(cursor.findColumn(RATING_COLUMN));

        Show show = new Show(id, title, descrip, duration, airDate, rating);
        List<String> keywordKeys = getAssociatedKeywords(id);
        show.setKeywords(keywordKeys);
        return show;
    }

    //=========================================== Get ========================================
    public List<Show> getAllShows(){
        System.out.println("DBHelper Index Called");
        List<Show> allShows = new ArrayList<>();
        try {
            if(mConnection == null){
                startConnection();
            }
            Statement statement = mConnection.createStatement();
            ResultSet cursor = statement.executeQuery("SELECT * FROM " + SHOW_TABLE);
            while(cursor.next()){
                allShows.add(getShowFromQueryResult(cursor));
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allShows;
    }

    public Show getShowById(int id){
        try {
            Statement statement = mConnection.createStatement();
            ResultSet cursor = statement.executeQuery(String.format("SELECT * FROM %s WHERE %s = %s", SHOW_TABLE, SHOW_ID_COLUMN, id));
            if(cursor.next()){
                return getShowFromQueryResult(cursor);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAssociatedKeywords(int showId){
        List<String> keywordKeys = new ArrayList<>();
        try {
            Statement statement = mConnection.createStatement();
            String executionString = String.format(" SELECT %s FROM  %s WHERE %s = %s", KEYWORD_TEXT_COLUMN, KEYWORD_TABLE, SHOW_ID_COLUMN, showId);
            ResultSet cursor = statement.executeQuery(executionString);
            while (cursor.next()) {
                keywordKeys.add(cursor.getString(cursor.findColumn(KEYWORD_TEXT_COLUMN)));
            }
            cursor.close();
            statement.close();
            return keywordKeys;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }



    public List<Show> getShowsByTitle(String title){
        List<Show> showsWithTitle = new ArrayList<>();
        try {
            Statement statement = mConnection.createStatement();
            String queryString = String.format("SELECT * FROM %s WHERE %s ILIKE '%s'", SHOW_TABLE, TITLE_COLUMN, "%" + title + "%");
            ResultSet cursor = statement.executeQuery(queryString);
            while(cursor.next()){
                showsWithTitle.add(getShowFromQueryResult(cursor));
            }
            cursor.close();
            statement.close();
            return showsWithTitle;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Show> getShowsByKeyword(String keyword){
        List<Show> showsWithKeyword = new ArrayList<>();
        try{
            Statement statement = mConnection.createStatement();
            String queryString = String.format("SELECT %s FROM %s WHERE %s ILIKE '%s'", SHOW_ID_COLUMN, KEYWORD_TABLE, KEYWORD_TEXT_COLUMN, "%" + keyword + "%");
            ResultSet cursor = statement.executeQuery(queryString);
            while(cursor.next()){
                showsWithKeyword.add(getShowById(cursor.getInt(cursor.findColumn(SHOW_ID_COLUMN))));
            }
            cursor.close();
            statement.close();
            return showsWithKeyword;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }



    //====================================== Add/Post ==================================================

    private void insertShowIntoDatabase(Statement statement, Show show) throws SQLException {
        String executionString = String.format("INSERT INTO %s VALUES (%s, '%s', '%s', %s, %s, %s)",
                SHOW_TABLE,
                show.getId(), show.getTitle(), show.getDescription(), show.getDuration(), show.getOriginalAirDate(), show.getRating()
        );
        statement.execute(executionString);
        insertKeywords(statement, show.getKeywords(), show.getId());
    }

    //ToDo: Allow user to add keywords to shows
    public void addKeywordAssociation(Statement statement, String keyword, int showId) throws SQLException{
        statement.execute(String.format("INSERT INTO %s VALUES (%s, '%s')", KEYWORD_TABLE, showId, keyword));
    }

    private void insertKeywords(Statement statement, List<String> keywords, int showId) throws SQLException {
        for(String keyword : keywords){
            addKeywordAssociation(statement, keyword, showId);
        }
    }

    public boolean addShow(Show show){
        try {
            Statement statement = mConnection.createStatement();
            insertShowIntoDatabase(statement, show);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }

    //============================================= Deletes ================================================

    public boolean removeShowById(int showId){
        try {
            Statement statement = mConnection.createStatement();
            deleteShow(statement, showId);
            deleteKeywordAssociations(statement, showId);
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void deleteShow(Statement statement, int showId) throws SQLException{
        String executionString = String.format("DELETE FROM %s WHERE %s = %s", SHOW_TABLE, SHOW_ID_COLUMN, showId);
        statement.execute(executionString);
    }

    private void deleteKeywordAssociations(Statement statement, int showId) throws SQLException{
        String executionString = String.format("DELETE FROM %s WHERE %s = %s", KEYWORD_TABLE, SHOW_ID_COLUMN, showId);
        statement.execute(executionString);
    }

    //ToDo: Allow user to remove keywords from shows
    private void removeKeywordAssociation(Statement statement, int showId, String keyword) throws SQLException{
        String executionString = String.format("DELETE FROM %s WHERE %s = %s AND %s = %s", KEYWORD_TABLE,
                SHOW_ID_COLUMN, showId,
                KEYWORD_TEXT_COLUMN, keyword);
        statement.execute(executionString);
    }

    //============================================ Update ========================================================

    public boolean updateShowById(int showId, Show updatedShow){
        try {
            Statement statement = mConnection.createStatement();
            String executionString = String.format("UPDATE %s SET %s = '%s', %s = '%s', %s = %s, %s = %s, %s = %s WHERE %s = %s", SHOW_TABLE,
                    TITLE_COLUMN, updatedShow.getTitle(),
                    DESCRIP_COLUMN, updatedShow.getDescription(),
                    DURATION_COLUMN, updatedShow.getDuration(),
                    AIRDATE_COLUMN, updatedShow.getOriginalAirDate(),
                    RATING_COLUMN, updatedShow.getRating(),
                    SHOW_ID_COLUMN, showId);
            statement.execute(executionString);
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //ToDo: Sync Keywords upon show update
}
