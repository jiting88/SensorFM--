package SensorServer.dao;

import com.mongodb.*;
import SensorServer.entity.User;
import SensorServer.utility.MongoDBJDBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jicl on 16/6/30.
 */

@Component
public class UserDaoImpl implements UserDao{
    @Autowired
    private MongoDBJDBC mongoDBJDBC;

    public MongoDBJDBC getMongoDBJDBC() {
        return mongoDBJDBC;
    }

    public void setMongoDBJDBC(MongoDBJDBC mongoDBJDBC) {
        this.mongoDBJDBC = mongoDBJDBC;
    }

    private DBCollection getUserInfo(){
        DB md=mongoDBJDBC.connectUserInfo();
        return md.getCollection("UserInfo");
    }
    public boolean addUser(User user){
        if(getUser(user.getUsername())==null){
            BasicDBObject doc=new BasicDBObject();
            doc.put("username",user.getUsername());
            doc.put("password",user.getPassword());
            doc.put("email",user.getEmail());
            getUserInfo().insert(doc);
            return true;
        }
        else
            return false;
    }
    public void deleteUser(String UserID){
        BasicDBObject query = new BasicDBObject();
        query.append("_id", UserID);
        getUserInfo().remove(query);
    }
    public void updateUser(User user){
        BasicDBObject query = new BasicDBObject();
        query.append("$set",new BasicDBObject().append("username",user.getUsername()));
        query.append("$set",new BasicDBObject().append("password",user.getPassword()));
        query.append("$set",new BasicDBObject().append("email",user.getEmail()));
        BasicDBObject searchQuery=new BasicDBObject();
        searchQuery.append("_id",user.getUserID());
        getUserInfo().update(searchQuery,query);
    }

    public User getUser(String username){
        DBCursor result=getUserInfo().find(new BasicDBObject("username",username));
        if(result.count()==0)
            return null;
        else{
            User user=new User();
            DBObject obj=result.next();
            user.setUserID(obj.get("_id").toString());
            user.setUsername(obj.get("username").toString());
            user.setPassword(obj.get("password").toString());
            user.setEmail(obj.get("email").toString());
            return user;
        }

    }
}
