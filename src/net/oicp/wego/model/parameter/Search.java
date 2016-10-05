package net.oicp.wego.model.parameter;

import java.util.Map;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.19
 * @Company        WEIGO
 * @Description    search
*/

public class Search {
	private Integer draw;
	private Integer start;
	private Integer length;
	private Order[] order;
	private String csrf;
	private Map<String, Object> search;
	
	public Search() {
		
	}
	public Integer getDraw() {
		return draw;
	}
	public void setDraw(Integer draw) {
		this.draw = draw;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Order[] getOrder() {
		return order;
	}
	public void setOrder(Order[] order) {
		this.order = order;
	}
	public String getCsrf() {
		return csrf;
	}
	public void setCsrf(String csrf) {
		this.csrf = csrf;
	}
	public Map<String, Object> getSearch() {
		return search;
	}
	public void setSearch(Map<String, Object> search) {
		this.search = search;
	}
	
	@Override
	public String toString() {
		return "Search [draw=" + draw + ", start=" + start + ", length=" + length + ", order=" + order + ", csrf=" + csrf 
				+ ", search=" + search + "]";
	}
}
