package shuvalov.nikita.smithsonianapichallenge.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
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
    public Show test(@PathVariable int id){
        return ShowDbHelper.getInstance().getShowById(id);
    }


    @RequestMapping(method = RequestMethod.GET)
    public void redirectToIndex(){
        //ToDo: Redirect
    }

}
