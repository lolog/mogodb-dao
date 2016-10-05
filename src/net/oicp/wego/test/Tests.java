package net.oicp.wego.test;

import net.oicp.wego.tools.Tool;

/** 
 * @author         00013052
 * @version        V1.0  
 * @date           2016年8月19日
 * @company        CIMCSSC
 * @description    TODO(文件描述) 
*/

public class Tests {
	public static void main(String[] args) {
		// filter
		Object[][] filterArray = {{"$eq"}, {true}};
		System.out.println(Tool.arrayToMap(filterArray));
		filterArray[0][0] = "$neq";
		filterArray[1][0] = "";
	}
}
