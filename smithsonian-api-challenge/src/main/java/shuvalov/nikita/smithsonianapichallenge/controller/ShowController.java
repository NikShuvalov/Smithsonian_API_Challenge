package shuvalov.nikita.smithsonianapichallenge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shuvalov.nikita.smithsonianapichallenge.Search;
import shuvalov.nikita.smithsonianapichallenge.database.ShowDbHelper;
import shuvalov.nikita.smithsonianapichallenge.entity.Show;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    //===================================== Shows ===================================================================

    /**
     *
     *
     * @param title Search for shows with text in Title
     * @param keyword Search for shows with Keyword attached
     * @param sortByTitle Use "asc" for ascending, "desc" for descending by title
     * @param sortById "desc" for descending by id. Defaults to ascending by nature
     * @param page Page of search Defaults to first page
     * @param pageQuantity results per page Defaults to 25 results per page
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getShowByParams(@RequestParam(value = "title", required =  false) String title,
                                             @RequestParam(value = "keyword", required = false) String keyword,
                                             @RequestParam(value = "sort_by_title", required = false) String sortByTitle,
                                             @RequestParam(value = "sort_by_id", required = false) String sortById,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                             @RequestParam(value = "results_per_page", required = false, defaultValue = "25") int pageQuantity){
        Search.Builder searchBuilder = new Search.Builder();

        attachPaginationParamsToSearchBuilder(searchBuilder, page, pageQuantity);
        if((title == null || title.isEmpty()) &&
                (keyword == null || keyword.isEmpty())) {
            attachOrderingParamsToSearchBuilder(searchBuilder,sortByTitle, sortById);
            List<Show> orderedShows = ShowDbHelper.getInstance().getShowsBySearch(searchBuilder.build());
            return orderedShows == null ?
                    new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR) :
                    new ResponseEntity<>(orderedShows, HttpStatus.OK);
        }

        if(title != null && !title.isEmpty()){
            searchBuilder.setSearchParam(Search.SearchParam.TITLE).setSearchValue(title);
            attachOrderingParamsToSearchBuilder(searchBuilder, sortByTitle, sortById);
        }else {//Otherwise Keyword isn't empty or null; we search by it
            searchBuilder.setSearchParam(Search.SearchParam.KEYWORD).setSearchValue(keyword);
            attachOrderingParamsToSearchBuilder(searchBuilder, sortByTitle, sortById);
        }

        Search search = searchBuilder.build();
        List<Show> searchResultList = getResultsFromSearch(search);

        return searchResultList == null ?
                new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR) :
                new ResponseEntity<>(searchResultList, HttpStatus.OK);

    }

    private void attachOrderingParamsToSearchBuilder(Search.Builder searchBuilder, String sortByTitle, String sortById){
        if(sortByTitle != null){
            searchBuilder.setOrderParam(Search.OrderParam.TITLE);
            if(sortByTitle.toLowerCase().equals("desc")){
                searchBuilder.setAscendingOrder(false);
            }else if(sortByTitle.toLowerCase().equals("asc")){
                searchBuilder.setAscendingOrder(true);
            }
        }else if (sortById != null){
            searchBuilder.setOrderParam(Search.OrderParam.ID);
            if(sortById.toLowerCase().equals("desc")){
                searchBuilder.setAscendingOrder(false);
            }else if(sortById.toLowerCase().equals("asc")){
                searchBuilder.setAscendingOrder(true);
            }
        }
    }

    private void attachPaginationParamsToSearchBuilder(Search.Builder sb, int page, int pageQuantity){
        sb.setPage(page);
        sb.setResultPerPage(pageQuantity);
    }

    private List<Show> getResultsFromSearch(Search search){
        return ShowDbHelper.getInstance().getShowsBySearch(search);
    }


    @RequestMapping(path = "/", method = RequestMethod.GET)
    public Collection<Show> getIndex(){
        return ShowDbHelper.getInstance().getAllShows(true);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getShowById(@PathVariable(value = "id") int id){
        Show show = ShowDbHelper.getInstance().getShowById(id);
        return show == null ?
                new ResponseEntity<>("Show does not exist", HttpStatus.NOT_FOUND) :
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
    public ResponseEntity<?> updateShowById(@PathVariable(value ="id") int id, @RequestBody Show updatedShow){
        if(id != updatedShow.getId()){
            return new ResponseEntity<>("ID in updated show JSON does not match ID in URL params",HttpStatus.BAD_REQUEST);
        }
        return ShowDbHelper.getInstance().updateShowById(id, updatedShow) ?
                new ResponseEntity(HttpStatus.ACCEPTED) :
                new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    //======================================= Keywords ==============================================================

    /**
     * Gets a list of all keywords associated with a show.
     * @param id
     * @param sortDirection use "sort=asc" for alphabetical order, "sort=desc" for reverse Alphabetical
     * @return Keyword List and OK response if successful, otherwise INTERNAL_SERVER_ERROR
     */
    @RequestMapping(path = "/{id}/keywords", method = RequestMethod.GET)
    public ResponseEntity<?> getKeywordsAssociatedWithShow(@PathVariable(value = "id") int id,
                                                           @RequestParam(value = "sort", required = false) String sortDirection){
        List<String> keywords = ShowDbHelper.getInstance().getAssociatedKeywords(id);
        if(sortDirection != null){
            if(sortDirection.toLowerCase().equals("desc")){
                keywords.sort(Comparator.reverseOrder());
            }else if (sortDirection.toLowerCase().equals("asc")){
                keywords.sort(Comparator.naturalOrder());
            }
        }
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
