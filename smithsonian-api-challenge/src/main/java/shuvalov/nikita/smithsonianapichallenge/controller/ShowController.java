package shuvalov.nikita.smithsonianapichallenge.controller;

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

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public String test(){
        //ToDo: Get Show By Id
        return "You can do it";
    }


    @RequestMapping(method = RequestMethod.GET)
    public void redirectToIndex(){
        //ToDo: Redirect
    }


}
