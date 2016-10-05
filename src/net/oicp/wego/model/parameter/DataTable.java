package net.oicp.wego.model.parameter;

import java.util.Map;


/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.14
 * @Company        WEIGO
 * @Description    DataTable request parameters
*/

public class DataTable {
	// data
	private String data;
	// name
	private String name;
	// whether search
	private Boolean searchable;
	// whether order
	private Boolean orderable;
	// search content
	private Map<String, Object> search;
	
	public DataTable() {
		
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getSearchable() {
		return searchable;
	}
	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}
	public Boolean getOrderable() {
		return orderable;
	}
	public void setOrderable(Boolean orderable) {
		this.orderable = orderable;
	}
	public Map<String, Object> getSearch() {
		return search;
	}
	public void setSearch(Map<String, Object> search) {
		this.search = search;
	}
	
	@Override
	public String toString() {
		return "DataTables [data=" + data + ", name=" + name + ", searchable=" + searchable + ", orderable=" + orderable
				+ ", search=" + search + "]";
	}
}
