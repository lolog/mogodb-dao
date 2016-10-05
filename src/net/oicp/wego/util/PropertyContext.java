package net.oicp.wego.util;

/** 
 * @author         lolog
 * @version        V1.0
 * @Date           2016.07.06
 * @Company        CIMCSSC
 * @Description    property configure
*/

public interface PropertyContext {
	// cookie timout
	public Integer getExpiry();
	
	// admin path
	public String getAdmin ();
	// home path
	public String getHome();
	
	// velocity default layout file name
	public String getLayoutDefault ();
	// velocity admin layout file path
	public String getAdminLayout ();
	
	// database name separator
	public String db_depr();
	
	// collection prefix
	public String db_prefix();
	
	// collection time field name contains string 
	public String regex_time();
	
	// file upload path
	public String uploadPath();
}
