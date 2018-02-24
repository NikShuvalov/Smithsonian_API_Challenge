package shuvalov.nikita.smithsonianapichallenge.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Show {

    private int id;

    private String title, description;
    private long duration; //Duration in millis?
    private long originalAirDate; //long makes life easier for sorting by age
    private float rating;
    private List<String> keywords;

    public Show(){
        //Required Empty Constructor
    }

    public Show(int id, String title, String description, long duration, long originalAirDate, float rating) {
        this(id, title, description,duration,originalAirDate,rating, new ArrayList<>());
    }

    public Show(int id, String title, String description, long duration, long originalAirDate, float rating, List<String> keywords) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.rating = rating;
        this.keywords = keywords;
        this.originalAirDate = originalAirDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getReadableAirDate() {
        return getDateFromMillis(originalAirDate);
    }

    public long getOriginalAirDate(){
        return originalAirDate;
    }

    public void setOriginalAirDate(long originalAirDateInMillis) {
        originalAirDate = originalAirDateInMillis;
    }

    public long getDuration() {
        return duration;
    }

    public String getReadableDuration(){
        return getDurationStringFromMillis(duration);
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
