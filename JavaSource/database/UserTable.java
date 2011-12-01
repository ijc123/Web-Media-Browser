package database;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.ibatis.session.SqlSession;

@Stateless
public class UserTable {

	public UserItem getUser(String userName, String password) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("name", userName);
		map.put("password", password);
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		UserItem user = (UserItem)session.selectOne("database.UserMapper.getUser", map);
	
		session.close();
		
		return(user);
	}

}
