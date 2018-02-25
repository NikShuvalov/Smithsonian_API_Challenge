package shuvalov.nikita.smithsonianapichallenge.database;

import shuvalov.nikita.smithsonianapichallenge.Search;

import java.sql.SQLException;

public class QueryBuilder{
    private StringBuilder mStringBuilder;

    public QueryBuilder(){
        mStringBuilder = new StringBuilder();
    }

    public QueryBuilder appendSelectClause(String tableName, String... columns) throws SQLException {
        if(columns.length == 0){
            throw new SQLException("Column Selection params missing");
        }
        String columnsJoined;
        if(columns.length == 1){
            columnsJoined = columns[0];
        }else{
            columnsJoined = String.join(", ", columns);
            columnsJoined = columnsJoined.substring(0, columnsJoined.length() -1);
        }
        mStringBuilder.append(String.format("SELECT %s FROM %s ", columnsJoined, tableName));
        return this;
    }

    public QueryBuilder appendDeleteClause(String tableName){
        mStringBuilder.append(String.format("DELETE FROM %s ", tableName));
        return this;
    }

    public QueryBuilder appendOrderByClause(Search search){
        mStringBuilder.append(String.format("ORDER BY %s %s ",
                search.getOrderParam().getColumnName(),
                search.isAscendingOrder() ?
                        "ASC " : "DESC "));
        return this;
    }

    public QueryBuilder appendPaginationClause(Search search){
        int resultsPerPage = search.getResultsPerPage();
        mStringBuilder.append(String.format("LIMIT %s ", resultsPerPage));
        int offset = resultsPerPage * search.getPage();
        if(offset > 0){
            mStringBuilder.append(String.format("OFFSET %s ", offset));
        }
        return this;
    }


    /**
     * Make sure to add Quotes if value is a stored as String in db
     */
    public QueryBuilder appendWhereClause( String column, String value){
        mStringBuilder.append(String.format("WHERE %s = %s ", column, value));
        return this;
    }

    public QueryBuilder appendWhereApproximateLikeClause( String column, String value){
        mStringBuilder.append(String.format("WHERE %s ILIKE %s ", column, value));
        return this;
    }

    public QueryBuilder appendWhereLikeClause( String column, String value){
        mStringBuilder.append(String.format("WHERE %s LIKE %s ", column, value));
        return this;
    }

    public StringBuilder getStringBuilder() {
        return mStringBuilder;
    }

    public String toString(){
        return mStringBuilder.toString();
    }

}
