package team.jfh.sensorfm.data.repository.datasource;

import java.io.IOException;

import team.jfh.sensorfm.data.entity.LoginFormat;
import team.jfh.sensorfm.data.entity.UserInfo;
import team.jfh.sensorfm.data.util.ServerApiProvider;

/**
 * Created by rootK on 2016/7/19.
 */
public class CloudLoginFormat {
    private final ServerApiProvider provider=new ServerApiProvider();
    public LoginFormat getLoginFormat(UserInfo userInfo){
        LoginFormat result=new LoginFormat();
        try {
            provider.access("/Login", userInfo, result);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
