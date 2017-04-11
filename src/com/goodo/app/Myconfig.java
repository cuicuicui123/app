package com.goodo.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.goodo.app.javabean.SendAttachObject;

import android.graphics.Bitmap;

public class Myconfig {
	public static boolean login_flag;
	public static int x;
	public static int y;
	public static boolean select_flag;
	public static boolean select_flag1;
	public static boolean select_flag2;
	public static int select_flag3;
	public static boolean remind_flag;
	public static boolean ca_flag1;
	public static boolean ca_flag2;
	public static boolean ca_flag3;
	public static boolean click_flag1;
	public static boolean click_flag2;
	
	public static boolean richeng_flag;
	public static int width;
	public static int height;
	
	public static boolean notice_flag1;
	public static boolean notice_flag2;
	public static int notice_flag;
	public static String IP = null;
	public static String SessionID = null;
	public static String UserName = null;
	public static String User_ID = null;
	public static String Unit_ID = null;
	
	
	public static Bitmap local_pic = null; 
	public static final int SHOW_RESPONSE = 1;
	
	public static Set<String> set_AttachName;
	public static Set<String> set_Grapremind;
	public static Set<String> set_Dbswremind;
}
