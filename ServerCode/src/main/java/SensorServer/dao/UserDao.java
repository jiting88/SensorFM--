package SensorServer.dao;

import SensorServer.entity.User;
import org.springframework.stereotype.Component;

/**
 * Created by jicl on 16/6/30.
 */

public interface UserDao {
    public boolean addUser(User user);
    public void deleteUser(String UserID);
    public void updateUser(User user);
    public User getUser(String username);
}
