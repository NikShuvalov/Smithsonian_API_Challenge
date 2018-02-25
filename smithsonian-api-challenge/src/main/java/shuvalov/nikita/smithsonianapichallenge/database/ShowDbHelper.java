package shuvalov.nikita.smithsonianapichallenge.database;

import com.sun.istack.internal.Nullable;
import shuvalov.nikita.smithsonianapichallenge.Search;
import shuvalov.nikita.smithsonianapichallenge.entity.Show;

import java.sql.*;
import java.util.*;

public class ShowDbHelper {

    //=============================================== Constants ===============================================
    private static final String DATABASE_URI= "jdbc:h2:mem:dummydb";
    private static final String ADMIN_LOGIN = "sa";

    private static final String SHOW_TABLE = "SHOW_TABLE";
    private static final String KEYWORD_TABLE = "KEYWORD_TABLE";

    public static final String SHOW_ID_COLUMN = "SHOW_ID";
    public static final String TITLE_COLUMN = "TITLE";
    public static final String DESCRIP_COLUMN = "DESCRIPTION";
    public static final String DURATION_COLUMN ="DURATION";
    public static final String RATING_COLUMN = "RATING";
    public static final String AIRDATE_COLUMN = "ORIGINAL_AIRDATE";

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


    private static final String LOREM_IPSUM_FULL = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    private static final String LOREM_ISPUM_SHORT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
    private void seedDatabaseWithDummyData(){
        try {
            if(mConnection == null){
                startConnection();
            }

            Statement statement = mConnection.createStatement();
            List<String> genericKeywords = new ArrayList<String>(){{
                add("quirky");
                add("cerebral");
                add("funny");
                add("provocative");
                add("drama");
                add("gripping");
                add("nerdy");
                add("crime");
                add("thriller");
                add("informative");
                add("family");
                add("inticing");
                add("history");
            }};

            List<String> shows = new ArrayList<String>(){{
                add("The Big Bang Theory");
                add("Survivor");
                add("NCIS");
                add("Aerial America");
                add("Elementary");
                add("Sound Revolutin");
                add("Terror in the skies");
                add("Unabashed pandering");
                add("The Real Story");
            }};

            long fakeAirDate = Calendar.getInstance().getTimeInMillis();
            for(int i = 0; i < 100; i++){
                List<String> randomKeywords = new ArrayList<String>(){
                    {
                        add(genericKeywords.get(new Random().nextInt(genericKeywords.size()-1)));
                        add(genericKeywords.get(new Random().nextInt(genericKeywords.size()-1)));
                        add(genericKeywords.get(new Random().nextInt(genericKeywords.size()-1)));
                        add(genericKeywords.get(new Random().nextInt(genericKeywords.size()-1)));
                    }
                };
                insertShowIntoDatabase(statement,
                        new Show(i,
                                i >= shows.size() ? String.format("Filler Show #%s", (i - shows.size()) + 1) : shows.get(i),
                                LOREM_ISPUM_SHORT,
                                60000 * 30,
                                fakeAirDate,
                                8.3f,
                                randomKeywords)
                );

            }
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
        return sShowDbHelper;
    }

    public static void instantiate(){
        if(sShowDbHelper == null){
            System.out.println("Database begun");
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


    public List<Show> getAllShows(boolean ascendingOrder){
        System.out.println("DBHelper Index Called");
        List<Show> allShows = new ArrayList<>();
        try {
            if(mConnection == null){
                startConnection();
            }
            Statement statement = mConnection.createStatement();
            ResultSet cursor = statement.executeQuery(String.format("SELECT * FROM %s %s", SHOW_TABLE, ascendingOrder ? "" : String.format("ORDER BY %s DESC", SHOW_ID_COLUMN)));
            while(cursor.next()){
                allShows.add(getShowFromQueryResult(cursor));
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allShows;
    }

    public List<Show> getAllShows(Search search){
        System.out.println("DBHelper Index Called");
        List<Show> allShows = new ArrayList<>();
        try {
            if(mConnection == null){
                startConnection();
            }
            Statement statement = mConnection.createStatement();
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.appendSelectClause(SHOW_TABLE, "*")
                    .appendOrderByClause(search)
                    .appendPaginationClause(search);
            ResultSet cursor = statement.executeQuery(queryBuilder.toString());
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
            Show show = null;
            if(cursor.next()){
                show =getShowFromQueryResult(cursor);
                if(!cursor.isLast()){
                    throw new SQLException("Multiple Show Entries with same Id");
                }
            }
            cursor.close();
            statement.close();
            return show;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAssociatedKeywords(int showId){
        List<String> keywordKeys = new ArrayList<>();
        try {
            Statement statement = mConnection.createStatement();
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.appendSelectClause(KEYWORD_TABLE, KEYWORD_TEXT_COLUMN)
                    .appendWhereClause(SHOW_ID_COLUMN, String.valueOf(showId));
            ResultSet cursor = statement.executeQuery(queryBuilder.toString());
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

    public List<Show> getShowsBySearch(Search search) {
        List<Show> showResultList = new ArrayList<>();
        try {
            Statement statement = mConnection.createStatement();
            QueryBuilder queryBuilder = new QueryBuilder();
            switch(search.getSearchParam()){
                case TITLE:
                    queryBuilder.appendSelectClause(SHOW_TABLE, "*")
                            .appendWhereInsenstiveLike(search.getSearchParam().getColumnName(),"'%" + search.getSearchValue() + "%'")
                            .appendOrderByClause(search)
                            .appendPaginationClause(search);
                    break;
                case KEYWORD:
                    List<Show> showList = getShowsAssociatedWithKeyword(search);
                    sortShowList(showList, search);
                    statement.close();
                    return showList;
                default:
                    throw new Exception(String.format("Unhandled SearchParam ('%s') when assigning Table to search", search.getSearchParam().toString()));

            }
            ResultSet cursor = statement.executeQuery(queryBuilder.toString());
            while(cursor.next()){
                showResultList.add(getShowFromQueryResult(cursor));
            }
            cursor.close();
            statement.close();
            return showResultList;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void sortShowList(List<Show> showList, Search search){
        switch(search.getOrderParam()){
            case ID:
                if(!search.isAscendingOrder()){
                    showList.sort(new Show.IdComparator(true));
                }
                break; //By default the result list should be ascending by ID, nothing needs to be done
            case TITLE:
                showList.sort(new Show.TitleComparator(search.isAscendingOrder()));
                break;
        }
    }

    private List<Show> getShowsAssociatedWithKeyword(Search search){
        List<Show> showList = new ArrayList<>();
        Set<Integer> showIdList = new HashSet<>(); //FixMe: Hotfix for multiple instances of a show being returned when querying for a keyword when show has duplicate keywords.
        try{
            Statement statement = mConnection.createStatement();
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.appendSelectClause(KEYWORD_TABLE, SHOW_ID_COLUMN)
                    .appendWhereInsenstiveLike(KEYWORD_TEXT_COLUMN, "'%" + search.getSearchValue() + "%'")
                    .appendPaginationClause(search);
            ResultSet cursor = statement.executeQuery(queryBuilder.toString());
            while(cursor.next()){
                showIdList.add(cursor.getInt(cursor.findColumn(SHOW_ID_COLUMN)));
            }
            for(int id: showIdList){ //FixMe: Hotfix for multiple show instances...(See above)
                showList.add(getShowById(id));
            }
            cursor.close();
            statement.close();
            return showList;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    //====================================== Add/Post ==================================================

    //ToDo: Refactor with QueryBuilder
    private void insertShowIntoDatabase(Statement statement, Show show) throws SQLException {
        String executionString = String.format("INSERT INTO %s VALUES (%s, '%s', '%s', %s, %s, %s)",
                SHOW_TABLE,
                show.getId(), show.getTitle(), show.getDescription(), show.getDuration(), show.getOriginalAirDate(), show.getRating()
        );
        statement.execute(executionString);
        insertKeywords(statement, show.getKeywords(), show.getId());
    }

    //FixMe: Does not handle duplicate entries
    //ToDo:Refactor with Query Builder
    public boolean addKeywordAssociation(@Nullable Statement statement, String keyword, int showId){
        boolean rememberToClose = (statement == null);
        try {
            if (rememberToClose) statement = mConnection.createStatement();

            statement.execute(String.format("INSERT INTO %s VALUES (%s, '%s')", KEYWORD_TABLE, showId, keyword));

            if (rememberToClose) statement.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private void insertKeywords(Statement statement, List<String> keywords, int showId){
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
        QueryBuilder queryBuilder = new QueryBuilder();
//        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.appendDeleteClause(SHOW_TABLE)
                .appendWhereClause(SHOW_ID_COLUMN, String.valueOf(showId));
        statement.execute(queryBuilder.toString());
    }

    /**
     * Removes ALL keyword associations linked to a show in the Keyword table.
     * It's called when a show is deleted.
     *
     * @param statement
     * @param showId
     * @throws SQLException
     */
    private void deleteKeywordAssociations(Statement statement, int showId) throws SQLException{
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.appendDeleteClause(KEYWORD_TABLE)
                .appendWhereClause(SHOW_ID_COLUMN, String.valueOf(showId));
        statement.execute(queryBuilder.toString());
    }

    /**
     * Removes ONE keyword that's associated with a show.
     *
     * @param statement
     * @param showId
     * @param keyword
     * @throws SQLException
     */
    private void removeKeywordAssociation(Statement statement, int showId, String keyword) throws SQLException{
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.appendDeleteClause(KEYWORD_TABLE)
                .appendWhereClause(SHOW_ID_COLUMN, String.valueOf(showId));
        queryBuilder.getStringBuilder().append(String.format("AND %s = '%s'",KEYWORD_TEXT_COLUMN, keyword));
        statement.execute(queryBuilder.toString());
    }

    /**
     * Removes the passed keywords from the show.
     *
     * @param showId
     * @param keywords
     * @return true if all keywords removed, false if error
     */
    public boolean removeKeywordsFromShow(int showId, List<String> keywords){
        try {
            Statement statement = mConnection.createStatement();
            for(String keyword: keywords){
                removeKeywordAssociation(statement, showId, keyword);
            }
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //============================================ Update ========================================================


    //ToDo: Refactor using QueryBuilder
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

}
