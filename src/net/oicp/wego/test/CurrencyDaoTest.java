package net.oicp.wego.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.oicp.wego.dao.CurrencyDao;
import net.oicp.wego.model.Currency;
import net.oicp.wego.model.append.Editor;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.17
 * @Company        WEIGO
 * @Description    currency test 
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:config/app.xml")
public class CurrencyDaoTest {
	@Resource
	private CurrencyDao currencyDao;
	
	@Test
	public void insertTest () {
		Currency currency = new Currency();
		
		currency.setName("港币");
		currency.setRate(.857);
		currency.setCurrency_id("current_id0");
		
		Editor editor = new Editor();
		editor.setEdit_id("xxx");
		editor.setEdit_name("lolog");
		editor.setEdit_time(new Date());
		
		List<Editor> editors = new ArrayList<Editor>();
		editors.add(editor);
		
		currency.setEditors(editors);
		
		currencyDao.insert(currency);
	}
	
	@Test
	public void queryTest () {
		Map<String, Object> filter = new HashMap<String, Object>();
		// filter.put("name", "人民币");
		filter.put("editors.$.edit_name", new HashMap<>().put("$eq", "lolog"));
		System.out.println(currencyDao.filter(filter).query());;
	}
	
	@Test
	public void queryIndexTest () {
		System.out.println(currencyDao.queryIndex());;
	}
}
