package shuvalov.nikita.smithsonianapichallenge.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Show {

    private int mId;

    private String mTitle, mDescription;
    private long mDuration; //Duration in millis?
    private long mOriginalAirDate; //long makes life easier for sorting by age
    private float mRating;
    private List<String> mKeywords;

    public Show(int id, String title, String description, long duration, long originalAirDate, float rating) {
        this(id, title, description,duration,originalAirDate,rating, new ArrayList<>());
    }

    public Show(int id, String title, String description, long durationInMillis, long originalAirDateInMillis, float rating, List<String> keywords) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mDuration = durationInMillis;
        mRating = rating;
        mKeywords = keywords;
        mOriginalAirDate = originalAirDateInMillis;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public List<String> getKeywords() {
        return mKeywords;
    }

    public void setKeywords(List<String> keywords) {
        mKeywords = keywords;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public String getReadableAirDate() {
        return getDateFromMillis(mOriginalAirDate);
    }

    public long getOriginalAirDate(){
        return mOriginalAirDate;
    }

    public void setOriginalAirDate(long originalAirDateInMillis) {
        mOriginalAirDate = originalAirDateInMillis;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getReadableDuration(){
        return getDurationStringFromMillis(mDuration);
    }

    private String getDateFromMillis(long timeInMillis){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        return String.format("%02d/%02d/%s", c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.YEAR));
    }

    private String getDurationStringFromMillis(long durationInMillis){
        long hour = (1000 * 60 *60);
        long hours = durationInMillis / hour;
        long minutes = (durationInMillis - (int)(hours *hour))/ (60 * 1000);
        return String.format("%s%s",
                hours > 0 ? hours+"hrs " : "",
                minutes > 0 ? minutes+"min" : "" );
    }
}
