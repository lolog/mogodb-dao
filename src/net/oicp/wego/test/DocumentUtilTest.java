package net.oicp.wego.test;

import net.oicp.wego.model.Currency;
import net.oicp.wego.util.impl.DocumentUtilImpl;

/**
 * @author lolog
 * @version V1.0
 * @Date 2016年7月10日
 * @Company CIMCSSC
 * @Description TODO(文件描述)
 */

public class DocumentUtilTest extends DocumentUtilImpl<Currency> {
	public static void main(String[] args) {
		System.out.println(new DocumentUtilTest().getIndexs(new Currency()));;
	}
}
