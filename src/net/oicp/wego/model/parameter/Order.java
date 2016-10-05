package net.oicp.wego.model.parameter;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.19
 * @Company        WEIGO
 * @Description    Order 
*/

public class Order {
	private Integer column;
	private String dir;
	
	public Order() {
		
	}
	
	public Integer getColumn() {
		return column;
	}
	public void setColumn(Integer column) {
		this.column = column;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	@Override
	public String toString() {
		return "Order [column=" + column + ", dir=" + dir + "]";
	}
}
