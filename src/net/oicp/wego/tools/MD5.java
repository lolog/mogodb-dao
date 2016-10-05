package net.oicp.wego.tools;

import java.security.MessageDigest;

/**
 * @author      lolog
 * @version     v1.0
 * @date        2016.08.18
 * @company     WEIGO
 * @description MD5
 */

public class MD5
{
	private String[] strDigits =
	{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public MD5()
	{
	}

	/**
	 * @Title:        byteToString
	 * @author        lolog 
	 * @Description:  convert byte to string
	 * @param bByte   need to converted object 
	 * @return:       get the result of being converted
	 * @Date          2016.08.18 09:58:23
	 */
	private String byteToString(byte bByte)
	{
		int iRet = bByte;
		if (iRet < 0)
		{
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	/**
	 * @Title:        byteToString
	 * @author        lolog 
	 * @Description:  convert byte array to string
	 * @param bByte   need to be converted object 
	 * @return:       get the result of being converted 
	 * @Date          2016.08.18 10:02:22
	 */
	private String byteToString(byte[] bByte)
	{
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++)
		{
			sBuffer.append(byteToString(bByte[i]));
		}
		return sBuffer.toString();
	}

	/**
	 * @Title:        getMD5Code
	 * @author        lolog 
	 * @Description:  encrypted code by md5 
	 * @param code    need to be encrypted object
	 * @return:       get the result    
	 * @Date          2016.08.18 10:03:55
	 */
	public String getMD5Code (String code)
	{
		String resultString = null;
		try
		{
			resultString = new String(code);
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.digest() result of return that store hash array
			resultString = byteToString(md.digest(code.getBytes()));
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		return resultString;
	}
}
