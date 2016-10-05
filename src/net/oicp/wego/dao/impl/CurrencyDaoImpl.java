package net.oicp.wego.dao.impl;

import org.springframework.stereotype.Service;

import net.oicp.wego.dao.CurrencyDao;
import net.oicp.wego.model.Currency;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.17
 * @Company        WEIGO
 * @Description    currency data dao impl 
*/

@Service("currencyDao")
public class CurrencyDaoImpl extends MongoDaoImpl<Currency> implements CurrencyDao {

}
