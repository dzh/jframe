/**
 * 
 */
package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzh
 * @date Jul 24, 2015 4:53:39 PM
 * @since 1.0
 */
public class TestService {

	@Test
	@Ignore
	public void testConfig() {
		String mybatisId = "run run_ro1";
		String[] ids = mybatisId.split("\\s+");
		for (String id : ids) {
			System.out.println(id);
		}
	}
	
	@Test
	public void testutf8mb4(){
	    try {  
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mry_app", "root", "");
            Statement stat = conn.createStatement();
            stat.execute("UPDATE `mry_app`.`u_usr` SET `name`='😊' WHERE `id`='1';");
            conn.commit();
            conn.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
	

}
