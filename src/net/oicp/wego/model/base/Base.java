package net.oicp.wego.model.base;

import java.util.Date;
import java.util.List;

import net.oicp.wego.annotations.EKey;
import net.oicp.wego.annotations.Index;
import net.oicp.wego.model.append.Editor;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.7.13
 * @Company        CIMCSSC
 * @Description    Base Model
*/

public class Base {
	@Index(id=true)
	private String id;
	// manager id of add 
	private String addy_id;
	// manager name of add
	private String addy_name;
	// add time
	private Date add_time;
	// edit manager
	@EKey("$push")
	List<Editor> editors;
	// delete label
	@Index(key=false)
	private Boolean is_del;
	// forbidden label
	@Index(key=false)
	private Boolean is_forbidden;
	
	public Base() {
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddy_id() {
		return addy_id;
	}
	public void setAddy_id(String addy_id) {
		this.addy_id = addy_id;
	}
	public String getAddy_name() {
		return addy_name;
	}
	public void setAddy_name(String addy_name) {
		this.addy_name = addy_name;
	}
	public Date getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}
	public List<Editor> getEditors() {
		return editors;
	}
	public void setEditors(List<Editor> editors) {
		this.editors = editors;
	}
	public Boolean getIs_del() {
		return is_del;
	}
	public void setIs_del(Boolean is_del) {
		this.is_del = is_del;
	}
	public Boolean getIs_forbidden() {
		return is_forbidden;
	}
	public void setIs_forbidden(Boolean is_forbidden) {
		this.is_forbidden = is_forbidden;
	}
	
	@Override
	public String toString() {
		return "Base [id=" + id + ", addy_id=" + addy_id + ", addy_name=" + addy_name + ", add_time=" + add_time
				+ ", editors=" + editors + ", is_del=" + is_del + ", is_forbidden=" + is_forbidden + "]";
	}
	
}
