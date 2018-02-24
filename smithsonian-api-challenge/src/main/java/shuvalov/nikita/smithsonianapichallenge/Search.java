package shuvalov.nikita.smithsonianapichallenge;

import shuvalov.nikita.smithsonianapichallenge.database.ShowDbHelper;


//ToDo: Probably make this an internal class for the DBHelper
public class Search {
    private SearchParam mSearchParam;
    private OrderParam mOrderParam;
    private boolean mAscendingOrder;

    private String mSearchValue;

    private Search(SearchParam searchParam, OrderParam orderParam, boolean ascendingOrder, String searchValue) {
        mSearchParam = searchParam;
        mOrderParam = orderParam;
        mAscendingOrder = ascendingOrder;
        mSearchValue = searchValue;
    }

    public SearchParam getSearchParam() {
        return mSearchParam;
    }

    public OrderParam getOrderParam() {
        return mOrderParam;
    }

    public boolean isAscendingOrder() {
        return mAscendingOrder;
    }

    public void setSearchParam(SearchParam searchParam) {
        mSearchParam = searchParam;
    }

    public void setOrderParam(OrderParam orderParam) {
        mOrderParam = orderParam;
    }

    public void setAscendingOrder(boolean ascendingOrder) {
        mAscendingOrder = ascendingOrder;
    }

    public String getSearchValue() {
        return mSearchValue;
    }

    public void setSearchValue(String searchValue) {
        mSearchValue = searchValue;
    }

    public enum SearchParam{
        TITLE(ShowDbHelper.TITLE_COLUMN),
        KEYWORD(ShowDbHelper.KEYWORD_TEXT_COLUMN);

        String columnName;
        int value;

        SearchParam(String columnName){
            this.columnName =columnName;
        }

        public String getColumnName() {
            return columnName;
        }

    }

    public enum OrderParam{
        ID(ShowDbHelper.SHOW_ID_COLUMN),
        TITLE(ShowDbHelper.TITLE_COLUMN);

        String columnName;
        int value;

        OrderParam(String columnName){
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }

    }

    public static class Builder{
        private SearchParam mSearchParam;
        private OrderParam mOrderParam;
        private boolean mAscendingOrder;
        private String mSearchValue;

        //By Default order by Ascending ID since this is how it will be returned from Database normally
        public Builder() {
            mOrderParam = OrderParam.ID;
             mAscendingOrder= true;
        }

        public Builder setSearchParam(SearchParam searchParam) {
            mSearchParam = searchParam;
            return this;
        }

        public Builder setOrderParam(OrderParam orderParam) {
            mOrderParam = orderParam;
            return this;
        }

        public Builder setAscendingOrder(boolean ascendingOrder) {
            mAscendingOrder = ascendingOrder;
            return this;
        }

        public Builder setSearchValue(String searchValue){
            mSearchValue = searchValue;
            return this;
        }

        public Search build(){
            return new Search(mSearchParam, mOrderParam, mAscendingOrder, mSearchValue);
        }
    }
}
