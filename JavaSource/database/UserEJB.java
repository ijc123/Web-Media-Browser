package database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.ibatis.session.SqlSession;

import utils.MapArgument;

@Stateless
public class UserEJB {

	public List<UserItem> getAllUsers() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		@SuppressWarnings("unchecked")
		List<UserItem> users = (List<UserItem>)session.selectList("database.UserMapper.getAllUsers");
	
		session.close();
		
		return(users);
	}
	
	public UserItem getUser(String userName, String password) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("name", userName);
		map.put("password", password);
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		UserItem user = (UserItem)session.selectOne("database.UserMapper.getUser", map);
	
		session.close();
		
		return(user);
	}
	
	public boolean updateAllUsers(List<UserItem> user) {
		
		if(user == null) return(false);
		
		List<UserItem> oldUser = getAllUsers();
		
		// delete old types not in new type
		// rename old types in new type
		for(int i = 0; i < oldUser.size(); i++) {
			
			boolean delete = true;
			
			UserItem o = oldUser.get(i);
									
			for(int j = 0; j < user.size(); j++) {
				
				UserItem t = user.get(j);
				
				if(o.getId() == t.getId()) {
					
					delete = false;
					
					updateUser(t);
					deleteUserTypes(t);
					insertUserTypes(t);
					
				}
			}
			
			if(delete) {
			
				deleteUser(o);
			}
		}
		
		// add new types
		SqlSession session = MyBatis.getSession().openSession();
		
		for(int i = 0; i < user.size(); i++) {
	
			UserItem t = user.get(i);
		
			if(t.getId() == 0) {
				
				insertUser(t);
			}
			
		}
		
		session.close();
		
		return(true);
	}
	
	private void insertUser(UserItem user) {
		
		if(user == null) return;
						
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.insert("database.UserMapper.insertUser", user);
			
		session.close();
	}
	
	private void updateUser(UserItem user) {
		
		if(user == null) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.insert("database.UserMapper.modifyUser", user);
	
		session.close();
	}
	
	private void deleteUser(UserItem user) {
		
		if(user == null) return;
			
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.insert("database.UserMapper.deleteUser", user);
	
		session.close();
	}
	
	private void insertUserTypes(UserItem user) {
		
		if(user == null) return;
		
		Map<String, Object> map = MapArgument.create(
				"name", user.getName(),
				"accessTypes", user.getAccessTypes()
				);
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.insert("database.UserMapper.insertUserTypes", map);
	
		session.close();
	}
	
	private void deleteUserTypes(UserItem user) {
		
		if(user == null) return;
			
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.insert("database.UserMapper.deleteUserTypes", user);
	
		session.close();
	}

}
