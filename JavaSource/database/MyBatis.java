package database;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatis {

	private static SqlSessionFactory sqlMapper;
	private static Reader reader;
	
	static {
		
		try {
			
			// comment this function to dump debug output to console
			org.apache.ibatis.logging.LogFactory.useNoLogging();
			
			reader    = Resources.getResourceAsReader("database/MyBatisConfig.xml");
			sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
	}

	public static SqlSessionFactory getSession(){
		return sqlMapper;
	}
	

}
