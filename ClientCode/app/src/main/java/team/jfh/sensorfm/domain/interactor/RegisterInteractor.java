package team.jfh.sensorfm.domain.interactor;

import java.io.IOException;

import team.jfh.sensorfm.data.entity.LoginFormat;
import team.jfh.sensorfm.data.entity.UserInfo;
import team.jfh.sensorfm.data.util.ServerFormSubmit;

/**
 * Created by rootK on 2016/9/12.
 */
public class RegisterInteractor {
    public String register(String username,String password,String email){
        UserInfo userInfo=new UserInfo();
        userInfo.setEmail(email);
        userInfo.setPassword(password);
        userInfo.setUsername(username);
        ServerFormSubmit formSubmit=new ServerFormSubmit();
        LoginFormat result;
        try {
            // Simulate network access.
            result=formSubmit.access("/Signup", userInfo, LoginFormat.class);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error in Connection";
        }
        if (result.getStatus().equals("Success"))
            return null;
        else
            return "User already exists";
    }
}
