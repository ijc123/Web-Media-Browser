package database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;

import utils.MapArgument;

import beans.user.LoginBean;

@Stateless
public class TypeTable {
	
	@Inject
	LoginBean loginBean;
	
	public boolean updateAllTypes(List<TypeItem> type) {
		
		if(type == null) return(false);
		
		List<TypeItem> oldType = getAllTypes();
		
		// delete old types not in new type
		// rename old types in new type
		for(int i = 0; i < oldType.size(); i++) {
			
			boolean delete = true;
			
			TypeItem o = oldType.get(i);
									
			for(int j = 0; j < type.size(); j++) {
				
				TypeItem t = type.get(j);
				
				if(o.getId() == t.getId()) {
					
					delete = false;
					
					renameType(o, t.getName());
					
				}
			}
			
			if(delete) {
			
				deleteType(o);
			}
		}
		
		// add new types
		SqlSession session = MyBatis.getSession().openSession();
		
		for(int i = 0; i < type.size(); i++) {
	
			TypeItem t = type.get(i);
		
			if(t.getId() == 0) {
				
				insertType(t);
			}
			
		}
		
		session.close();
		
		return(true);
	}
	
	private void insertType(TypeItem type) {
		
		if(type == null) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.insert("database.TypeMapper.insertType", type);
			
		session.close();
	}
	
	private void renameType(TypeItem type, String name) {
		
		if(type == null) return;
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("name", type.getName());
		map.put("newName", name);
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.insert("database.TypeMapper.modifyType", map);
	
		session.close();
	}
	
	public TypeItem getTypeByName(String name) {
		
		if(name == null) return(null);
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		TypeItem type  = (TypeItem) session.selectOne("database.TypeMapper.getTypeByName", name);
		
		session.close();
		
		return(type);
	}
	
	@SuppressWarnings("unchecked")
	public List<TypeItem> getAllTypes() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(		
				"accessTypes", accessTypes
				);
		
		List<TypeItem> type  = (List<TypeItem>) session.selectList("database.TypeMapper.getAllTypes", map);
		
		session.close();
		
		return(type);
		
	}

	private void deleteType(TypeItem type) {
		
		if(type == null) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
	
		session.delete("database.TypeMapper.deleteType", type);		
				
		session.close();
	}
}
