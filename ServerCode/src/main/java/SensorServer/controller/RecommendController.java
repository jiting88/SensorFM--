package SensorServer.controller;

import SensorServer.entity.InputForRecommendation;
import SensorServer.entity.TrackInfoRemote;
import SensorServer.service.RecommendLogicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jicl on 16/9/10.
 */
@Controller
@RequestMapping("/Recommend")
public class RecommendController {
    @Autowired
    RecommendLogicController rlc;

    public void setRlc(RecommendLogicController rlc) {
        this.rlc = rlc;
    }

    @RequestMapping(method= RequestMethod.POST)
    public @ResponseBody
    TrackInfoRemote recommend(@ModelAttribute InputForRecommendation ifr){
        return rlc.recommend(ifr);
    }
}
