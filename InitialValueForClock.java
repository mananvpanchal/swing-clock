import java.io.*;
class InitialValueForClock
{
	public static void main(String args[]) throws IOException
	{
		File f=new File("InitialValue");
		FileWriter fw=new FileWriter(f);
		long time=System.currentTimeMillis();
		String s=Long.toString(time);
		char ch[]=s.toCharArray();
		fw.write(ch);
		fw.flush();
		fw.close();
	}
}