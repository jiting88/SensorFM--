package SensorServer.service;

import SensorServer.dao.UserDao;
import SensorServer.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jicl on 16/6/30.
 */
@Component
public class LoginServiceImpl implements LoginService{
    @Autowired
    private UserDao ud;

    public UserDao getUd() {
        return ud;
    }

    public void setUd(UserDao ud) {
        this.ud = ud;
    }

    public User check(User user){
        User tmp=ud.getUser(user.getUsername());
        if(tmp.getPassword().equals(user.getPassword()))
            return tmp;
        else
            return null;
    }
}
