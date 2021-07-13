import java.io.*;
class SetTime_12
{
	public static void main(String args[]) throws IOException
	{
		FileWriter fw=new FileWriter("d:/java/Clock1/Initialvalue");
		double time=System.currentTimeMillis()-12*60*60*1000;
		String s=Double.toString(time);
		char ch[]=s.toCharArray();
		fw.write(ch);
		fw.flush();
		fw.close();
	}
}