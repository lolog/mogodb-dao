package net.oicp.wego.test;


import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.07.31
 * @Company        CIMCSSC
 * @Description    Spring test
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:config/app.xml")
public class SpringTest {
	@Autowired
	private Date date;
	
	@Test
	public void springTest () {
		System.out.println(date);
	}
	
	public static void main(String[] args) {
		new SpringTest().test(null,null);
	}
	
	public void test (String...strings) {
		System.out.println(strings.length);
		System.out.println(strings[0]);
		System.out.println(strings[1]);
	}
}
