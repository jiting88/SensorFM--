package SensorServer.service;

import SensorServer.entity.User;

/**
 * Created by jicl on 16/6/30.
 */
public interface LoginService {
    public User check(User user);
}
