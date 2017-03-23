package SensorServer.controller;

import SensorServer.service.TrackService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by rootK on 2016/9/7.
 */
@Controller
@RequestMapping("/Track")
public class TrackController {
    @Autowired
    TrackService trackService;

    public void setTrackService(TrackService trackService) {
        this.trackService = trackService;
    }

    @RequestMapping(path="/{id}",method = RequestMethod.GET)
    public @ResponseBody
    Document getTrackInfo(@PathVariable Integer id)
    {return trackService.getTrackInfoById(id);
    }
}
