package SensorServer.controller;

/**
 * Created by jicl on 16/6/30.
 */
import java.util.concurrent.atomic.AtomicLong;

import SensorServer.entity.User;
import SensorServer.service.LoginService;
import SensorServer.utility.LoginFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/Login")
public class LoginController {
    private User user;
    @Autowired
    private LoginService ls;

    public LoginService getLs() {
        return ls;
    }

    public void setLs(LoginService ls) {
        this.ls = ls;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @RequestMapping(method=RequestMethod.POST)
    public @ResponseBody LoginFormat Login(@RequestParam(value="username")String username, @RequestParam(value="password")String password) {
        user=new User();
        LoginFormat lf=new LoginFormat();
        user.setUsername(username);
        user.setPassword(password);
        if(ls.check(user)==null){
            lf.setStatus("Fail");
        }
        else{
            lf.setStatus("Success");
            lf.setUsername(username);
        }
        return lf;
    }
}
