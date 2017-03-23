package SensorServer.controller;

import SensorServer.entity.User;
import SensorServer.service.UserService;
import SensorServer.utility.SignupFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jicl on 16/7/1.
 */
@Controller
@RequestMapping("/Signup")
public class SignupController {
@Autowired
    private UserService us;
    private User user;

    public UserService getUs() {
        return us;
    }

    public void setUs(UserService us) {
        this.us = us;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @RequestMapping(method= RequestMethod.POST)
    public @ResponseBody
    SignupFormat Login(@RequestParam(value="username")String username, @RequestParam(value="password")String password, @RequestParam(value="email")String email) {
        user=new User();
        SignupFormat sf=new SignupFormat();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        if(us.addUser(user))
            sf.setStatus("Success");
        else
            sf.setStatus("Fail");
        return sf;
    }
}
