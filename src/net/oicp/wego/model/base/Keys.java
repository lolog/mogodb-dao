package net.oicp.wego.model.base;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.7.13
 * @Company        CIMCSSC
 * @Description    Index Key
*/

public class Keys {
	private String key;
	private Integer order;
	private String name;
	private String ns;
	private Boolean background;
	private Boolean sparse;
	
	public Keys() {
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNs() {
		return ns;
	}
	public void setNs(String ns) {
		this.ns = ns;
	}
	public Boolean getBackground() {
		return background;
	}
	public void setBackground(Boolean background) {
		this.background = background;
	}
	public Boolean getSparse() {
		return sparse;
	}
	public void setSparse(Boolean sparse) {
		this.sparse = sparse;
	}
	
	@Override
	public String toString() {
		return "Keys [key=" + key + ", order=" + order + ", name=" + name + ", ns=" + ns + ", background=" + background
				+ ", sparse=" + sparse + "]";
	}
}
