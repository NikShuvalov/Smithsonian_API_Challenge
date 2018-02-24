package shuvalov.nikita.smithsonianapichallenge.controller;

import com.sun.istack.internal.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shuvalov.nikita.smithsonianapichallenge.database.ShowDbHelper;
import shuvalov.nikita.smithsonianapichallenge.entity.Show;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    //===================================== Shows ===================================================================
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getShowByParams(@RequestParam(value = "title", required =  false) String title,
                                                @RequestParam(value = "keyword", required = false) String keyword){
        if((title == null || title.isEmpty()) &&
                (keyword == null || keyword.isEmpty())) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        ShowDbHelper showDbHelper = ShowDbHelper.getInstance();
        if(title != null && !title.isEmpty()){
            List<Show> showsWithTitle = showDbHelper.getShowsByTitle(title);
            return showsWithTitle == null ?
                    new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR) :
                    new ResponseEntity<>(showsWithTitle, HttpStatus.OK);
        }else{
            List<Show> showsWithKeyword = showDbHelper.getShowsByKeyword(keyword);
            return showsWithKeyword == null ?
                    new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR) :
                    new ResponseEntity<>(showsWithKeyword, HttpStatus.OK);
        }
    }
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public Collection<Show> getIndex(){
        return ShowDbHelper.getInstance().getAllShows();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Show> getShowById(@PathVariable(value = "id") int id){
        Show show = ShowDbHelper.getInstance().getShowById(id);
        return show == null ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(show, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addNewShow(@RequestBody Show show){
        return ShowDbHelper.getInstance().addShow(show) ?
                new ResponseEntity(HttpStatus.ACCEPTED) :
                new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity removeShowById(@PathVariable(value = "id") int id){
        return ShowDbHelper.getInstance().removeShowById(id) ?
                new ResponseEntity(HttpStatus.ACCEPTED) :
                new ResponseEntity(HttpStatus.BAD_REQUEST)
                ;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateShowById(@PathVariable(value ="id") int id, @RequestBody Show updatedShow){
        return ShowDbHelper.getInstance().updateShowById(id, updatedShow) ?
                new ResponseEntity(HttpStatus.ACCEPTED) :
                new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    //======================================= Keywords ==============================================================

    /**
     * Gets a list of all keywords associated with a show.
     * @param id Show's id
     * @return Keyword List and OK response if successful otherwise INTERNAL_SERVER_ERROR
     */
    @RequestMapping(path = "/{id}/keywords", method = RequestMethod.GET)
    public ResponseEntity<?> getKeywordsAssociatedWithShow(@PathVariable(value = "id") int id){
        List<String> keywords = ShowDbHelper.getInstance().getAssociatedKeywords(id);
        return keywords == null ?
                new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR) :
                new ResponseEntity<>(keywords, HttpStatus.OK);
    }


    /**
     * This route accepts a JSONArray of strings to append to the show of the ID given. Currently does not handle duplicate Keywords.
     * @param id Show's id
     * @param keywords Keywords to be added
     * @return BAD_REQUEST if error occurs, otherwise ACCEPTED
     */
    @RequestMapping(path = "/{id}/keywords", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addKeywordsToShow(@PathVariable(value = "id") int id, @RequestBody List<String> keywords){
        ShowDbHelper showDbHelper = ShowDbHelper.getInstance();
        boolean error = false;
        for(String keyword : keywords){
            if(!showDbHelper.addKeywordAssociation(null, keyword, id)) {
                error = true;
            }
        }
        return error ? new ResponseEntity(HttpStatus.BAD_REQUEST) : new ResponseEntity(HttpStatus.ACCEPTED);
    }

    /**
     * This route accepts a JSONArray for strings of keywords to remove from the showid given.
     *
     * @param id
     * @param keywords
     * @return Accepted if success, otherwise InternalServerError
     */
    @RequestMapping(path = "/{id}/keywords", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeKeywordsFromShow(@PathVariable(value = "id") int id, @RequestBody List<String> keywords){
        return ShowDbHelper.getInstance().removeKeywordsFromShow(id, keywords) ?
                new ResponseEntity(HttpStatus.ACCEPTED) :
                new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
