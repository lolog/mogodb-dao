package net.oicp.wego.model.append;

import java.util.Date;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.17
 * @Company        WEIGO
 * @Description    editor 
*/

public class Editor {
	private String edit_id;
	private String edit_name;
	private Date edit_time;
	
	public Editor() {
		
	}
	
	public String getEdit_id() {
		return edit_id;
	}
	public void setEdit_id(String edit_id) {
		this.edit_id = edit_id;
	}
	public String getEdit_name() {
		return edit_name;
	}
	public void setEdit_name(String edit_name) {
		this.edit_name = edit_name;
	}
	public Date getEdit_time() {
		return edit_time;
	}
	public void setEdit_time(Date edit_time) {
		this.edit_time = edit_time;
	}
	
	@Override
	public String toString() {
		return "Editor [edit_id=" + edit_id + ", edit_name=" + edit_name + ", edit_time=" + edit_time + "]";
	}
}
