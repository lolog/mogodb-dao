package net.oicp.wego.controller.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.oicp.wego.controller.AdminBaseController;
import net.oicp.wego.model.Currency;
import net.oicp.wego.model.parameter.DataTable;
import net.oicp.wego.model.parameter.Order;
import net.oicp.wego.model.parameter.Search;
import net.oicp.wego.tools.Errors;
import net.oicp.wego.tools.JsonConvert;
import net.oicp.wego.tools.Tool;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.14
 * @Company        WEIGO
 * @Description    Currency Setting
*/
@Controller
@RequestMapping("/admin/currency/")
@SuppressWarnings({"unchecked", "unused"})
public class CurrencyController extends AdminBaseController {
	@RequestMapping(value="loadCurencyData")
	public ModelAndView loadCurrencyData (@RequestParam(value="data", required=false) String data) {
		
		// request parameters
		List<DataTable> dataTables = null;
		Search          search     = null;
		List<Order>     orders     = null;
		
		dataTables = (List<DataTable>) dealJsonData(data, DataTable.class, true, "columns");
		search     = (Search)          dealJsonData(data, Search.class,    false);
		orders     = (List<Order>)     dealJsonData(data, Order.class,     true,  "order");
		
		
		if(search == null
				|| search.getStart() < 0
				|| search.getLength() < 1) {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_PARAMETERS_ZH, Errors.ERROR_CODE_PARAMETER);
			return json();
		}
		
		// sort
		Map<String, Object> sort = new HashMap<String, Object>();
		String[] orderField = {"name", "rate", "add_time","is_forbidden"}; 
		if((orders != null 
			&& orders.size() > 0 
			&& orders.get(0).getColumn() > 1
			&& (orders.get(0).getColumn() < (orderField.length + 2))) == true) {
			Order order = orders.get(0);
			if (order.getDir() != null 
				&& "desc".equalsIgnoreCase(order.getDir().trim()) == true) {
				sort.put(orderField[order.getColumn() - 2], -1);
			}
			else {
				sort.put(orderField[order.getColumn() - 2], 1);
			}
		}
		
		Map<String, Object> filter = new HashMap<String, Object>();
		
		// {"is_del":{"$ne":true}}
		Object[][]          isDelFilter    = {{"$ne"},{true}};
		filter.put("is_del", Tool.arrayToMap(isDelFilter));
		
		if (search != null 
			&& search.getSearch() != null 
			&& search.getSearch().get("value") != null
			&& search.getSearch().get("value").toString().trim().length() > 0) {
			
			String  value = search.getSearch().get("value").toString();
			Double  rate  = Tool.stringToDouble(value);
			Boolean regex = (Boolean) search.getSearch().get("regex");
			
			// name
			Map<String, Object> regexMap  = null;
			
			if (rate == null) {
				// name
				Object[][] nameArray = {{"$regex"},{value}};
				filter.put("name", Tool.arrayToMap(nameArray));
			}
			else {
				Object[][] searchArray = {{"name","rate"},{"$regex","$eq"},{value,rate}};
				List<Object> serachList = Tool.arrayXMap(searchArray);
				
				filter.put("$or", serachList);
			}
		}
		
		// query fields
		String[] includes = {"currency_id","name","rate","add_time","is_forbidden"};		
		// query currency
		List<Document> currencys = currencyDao
				.filter(filter)
				.include(includes)
				.skip(search.getStart())
				.sort(sort)
				.limit(search.getLength())
				.queryDocument();
		
		Long totle = currencyDao.count();
		
		// set json data
		setJson(Errors.SUCCESS, Errors.SUCCESS_MESSAGE_LOAD_ZH, Errors.ERROR_CODE_NONE);
		
		jsons().put("draw", search.getDraw());
		jsons().put("data", currencys);
		jsons().put("recordsTotal", totle);
		jsons().put("recordsFiltered", totle);
		
		return json();
	}
	
	@RequestMapping("addCurrency")
	public ModelAndView addCurrency (@RequestParam(value="data", required=false) String data) {
		// get the parameters
		Currency currency = (Currency) JsonConvert.jsonToObject(Currency.class, data);
		if(currency == null 
				|| currency.getName() == null
				|| currency.getRate() == null) {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_PARAMETERS_ZH, Errors.ERROR_CODE_NONE);
			return json(jsons());
		}
		
		// currency id
		String currency_id = getIDS("currency_id");
		currency.setCurrency_id(currency_id);
		// add time
		Date add_time = new Date();
		currency.setAdd_time(add_time);
		
		// filter field
		String[] includeFields = {"currency_id","name","rate","add_time","is_forbidden"};
		
		Boolean flag = false;
		// save object
		try {
			flag = currencyDao.include(includeFields).data(currency).insert();
		} catch (Exception e) {
			
		}
		
		if (flag == true) {
			setJson(Errors.SUCCESS, Errors.SUCCESS_MESSAGE_ADD_ZH,Errors.ERROR_CODE_NONE);
		}
		else {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_ADD_ZH,Errors.ERROR_CODE_ADD);
		}
		
		return json();
	}
	
	@RequestMapping("editCurrency")
	public ModelAndView editCurrency (@RequestParam("data") String data) {
		// get the parameters
		Currency currency = (Currency) JsonConvert.jsonToObject(Currency.class, data);
		
		if (currency.getCurrency_id() == null
			|| currency.getName() == null 
			|| currency.getName().length() < 1
			|| currency.getRate() == null) {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_PARAMETERS_ZH, Errors.ERROR_CODE_PARAMETER);
			return json();
		}
		
		// forbidden setting
		if (currency.getIs_forbidden() == null) {
			currency.setIs_forbidden(false);
		}
		
		// condition
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("currency_id", currency.getCurrency_id());
		
		// query whether exists
		Currency currencyData = (Currency) currencyDao.filter(filter).queryOne();
		
		// data not exists
		if(currencyData == null) {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_DATA_NOT_EXISTS_ZH, Errors.ERROR_CODE_DATA_NOT_EXISTS);
			return json();
		}
		
		// update
		String[] include = {"name", "rate", "is_forbidden"};
		Boolean status = currencyDao.filter(filter).data(currency).include(include).update();
		
		if(status == true) {
			setJson(Errors.SUCCESS, Errors.ERROR_MESSAGE_UPDATE_ZH, Errors.ERROR_CODE_NONE);
		}
		else {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_UPDATE_ZH, Errors.ERROR_CODE_UPDATE);
		}
		
		return json();
	}
	
	@RequestMapping("deleteCurrency")
	public ModelAndView deleteCurrency (@RequestParam("data") String data) {
		Currency currency = (Currency) dealJsonData(data, Currency.class, false);
		if (currency.getCurrency_id() == null
			|| currency.getCurrency_id().length() < 1) {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_PARAMETERS_ZH, Errors.ERROR_CODE_PARAMETER);
			return json();
		}
		
		// condition
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("currency_id", currency.getCurrency_id());
		
		// query whether exists
		Currency currencyData = (Currency) currencyDao.filter(filter).queryOne();
		
		// data not exists
		if(currencyData == null) {
			setJson(Errors.ERROR, Errors.ERROR_MESSAGE_DATA_NOT_EXISTS_ZH, Errors.ERROR_CODE_DATA_NOT_EXISTS);
			return json();
		}
		
		String[] include = {"is_del"};
		// delete currency
		currency.setIs_del(true);
		
		Boolean status = currencyDao.filter(filter).include(include).data(currency).update();
		
		if (status == true) {
			setJson(Errors.SUCCESS, Errors.SUCCESS_MESSAGE_DELETE_ZH, Errors.ERROR_CODE_NONE);
		}
		else {
			setJson(Errors.SUCCESS, Errors.ERROR_MESSAGE_DELETE_ZH, Errors.ERROR_CODE_DELETE);
		}
		
		return json();
	}
}
