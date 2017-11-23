package org.zl.jf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		String strCmd=null;
		Scanner input =new Scanner(System.in);
		strCmd = input.nextLine();		
		String line="";
		StringBuffer sbCmd=new StringBuffer("");

		if(strCmd!=null) 
		{
			try
			{
				//out.println(strCmd);
				Process p=Runtime.getRuntime().exec("cmd /c "+strCmd);
				
//				BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream(),"UTF-8"));
				BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream(),"gb2312"));
				while((line=br.readLine())!=null)
				{
					sbCmd.append(line+"\r\n");		
				}    
				System.out.println(sbCmd);
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
		}
	}
	
}
