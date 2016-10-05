package net.oicp.wego.tools;

/** 
 * @author         lolog
 * @version        V1.0  
 * @date           2016.08.18
 * @company        WEIGO
 * @description    error code
*/

public class Errors {
	public static Boolean SUCCESS                  = true; 
	public static Boolean ERROR                    = false;
	
	public static Integer ERROR_CODE_NONE          = 0;
	public static Integer ERROR_CODE_LOGIN_TIMEOUT = 1;
	public static Integer ERROR_CODE_ASK_TIMEOUT   = 2;
	public static Integer ERROR_CODE_ADD           = 3;
	public static Integer ERROR_CODE_DELETE        = 4;
	public static Integer ERROR_CODE_UPDATE        = 5;
	public static Integer ERROR_CODE_SELECT        = 6;
	public static Integer ERROR_CODE_PARAMETER     = 7;
	public static Integer ERROR_CODE_CSRF          = 8;
	
	public static Integer ERROR_CODE_DATA_EXISTS       = 9;
	public static Integer ERROR_CODE_DATA_NOT_EXISTS   = 10;
	
	public static Integer ERROR_CODE_FILE              = 11;
	
	public static String ERROR_MESSAGE_LOGIN_ZH        = "登录超时";
	public static String ERROR_MESSAGE_ASK_ZH          = "请求超时";
	public static String ERROR_MESSAGE_ADD_ZH          = "新增失败";
	public static String ERROR_MESSAGE_DELETE_ZH       = "删除失败";
	public static String ERROR_MESSAGE_UPDATE_ZH       = "更新失败";
	public static String ERROR_MESSAGE_SELECT_ZH       = "查询失败";
	public static String ERROR_MESSAGE_PARAMETERS_ZH   = "请求参数非法";
	public static String ERROR_MESSAGE_CSRF_ZH         = "非法提交";
	public static String ERROR_MESSAGE_REPEAT_ZH       = "重复提交";
	
	public static String ERROR_MESSAGE_FILE_ZH         = "文件为空";
	
	public static String ERROR_MESSAGE_DATA_EXISTS_ZH      = "已存在";
	public static String ERROR_MESSAGE_DATA_NOT_EXISTS_ZH  = "不存在";
	
	public static String SUCCESS_MESSAGE_LOAD_ZH         = "加载成功";
	public static String SUCCESS_MESSAGE_ADD_ZH          = "新增成功";
	public static String SUCCESS_MESSAGE_DELETE_ZH       = "删除成功";
	public static String SUCCESS_MESSAGE_UPDATE_ZH       = "更新成功";
	public static String SUCCESS_MESSAGE_SELECT_ZH       = "查询成功";
} 
