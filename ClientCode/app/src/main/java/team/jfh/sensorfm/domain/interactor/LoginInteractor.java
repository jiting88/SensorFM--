package team.jfh.sensorfm.domain.interactor;

import java.io.IOException;

import team.jfh.sensorfm.data.entity.LoginFormat;
import team.jfh.sensorfm.data.entity.UserInfo;
import team.jfh.sensorfm.data.util.ServerFormSubmit;

/**
 * Created by rootK on 2016/6/29.
 */
public class LoginInteractor {
    public String check(String username,String password){
        ServerFormSubmit formSubmit=new ServerFormSubmit();
        UserInfo userInfo=new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(password);
        LoginFormat result;
        try {
            // Simulate network access.
            result=formSubmit.access("/Login", userInfo, LoginFormat.class);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error in Connection";
        }
        if (result.getStatus().equals("Success"))
            return null;
        else
            return "Invalid username or password";
    }
}
