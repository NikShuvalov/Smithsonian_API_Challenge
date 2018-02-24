package shuvalov.nikita.smithsonianapichallenge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shuvalov.nikita.smithsonianapichallenge.database.ShowDbHelper;
import shuvalov.nikita.smithsonianapichallenge.entity.Show;

import java.util.Collection;

@RestController
@RequestMapping("/shows")
public class ShowController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public Collection<Show> getIndex(){
        return ShowDbHelper.getInstance().getAllShows();
    }

    //ToDo: do "?id={id} instead, maybe perhaps?
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

    @RequestMapping(method = RequestMethod.GET)
    public void redirectToIndex(){
        //ToDo: Redirect
    }

}
