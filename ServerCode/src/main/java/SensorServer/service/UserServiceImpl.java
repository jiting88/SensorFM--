package SensorServer.service;

import SensorServer.dao.UserDao;
import SensorServer.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jicl on 16/7/1.
 */
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao ud;

    public UserDao getUd() {
        return ud;
    }

    public void setUd(UserDao ud) {
        this.ud = ud;
    }

    public boolean addUser(User user){
        return ud.addUser(user);
    }
}
