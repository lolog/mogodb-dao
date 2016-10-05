package net.oicp.wego.controller;

import org.springframework.beans.factory.annotation.Autowired;

import net.oicp.wego.dao.CurrencyDao;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.07.08
 * @Company        WEIGO
 * @Description    manager base controller
*/

public class AdminBaseController extends BaseController {
	@Autowired
	protected CurrencyDao currencyDao;
	
	public AdminBaseController() {
	}
}
