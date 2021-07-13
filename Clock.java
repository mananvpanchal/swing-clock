import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
class Clock
{
	public static void main(String args[])
	{
		try
		{
			ClockPanel cp=new ClockPanel();
			ClockFrame cf=new ClockFrame(cp);
			AnalogClock ac=new AnalogClock(cf);
			MilliSecondStorageFile f=new MilliSecondStorageFile();
			DigitalClock dc=new DigitalClock(f);
			cp.references(dc,ac);
			ac.references(dc);
			dc.setTime();
			SetTimeFrame stf=new SetTimeFrame(dc,f);
			stf.arrange();
			cf.references(stf);
			cp.drawing=true;
			while(true)
			{
				ac.machine();
				cp.repaint();
				while((System.currentTimeMillis()-dc.getCurrentTime())<1000)
				{
					Thread.sleep(50);
				}
				dc.setTime();
				stf.setTime();
			}
		}
		catch(InterruptedException ex)
		{							}
	}
}
class SetTimeFrame extends JFrame implements ActionListener
{
	JButton stop,set,cancel;
	JSpinner second,minute,hour;
	Panel button,spinner;
	SpinnerNumberModel sec,min,hr;
	DigitalClock d;
	Object s,m,h;
	MilliSecondStorageFile f;
	boolean settime;
	public SetTimeFrame(DigitalClock dc,MilliSecondStorageFile a)
	{
		settime=false;
		setTitle("Set Time");
		setSize(250,200);
		setLocation(600,500);
		setResizable(false);
		d=dc;
		f=a;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		set=new JButton("Set");
		stop=new JButton("Stop");
		cancel=new JButton("Cancel");
		sec=new SpinnerNumberModel((int)d.getSecond(),0,59,1);
		min=new SpinnerNumberModel((int)d.getMinute(),0,59,1);
		hr=new SpinnerNumberModel((int)d.getHour(),0,11,1);
		second=new JSpinner(sec);
		minute=new JSpinner(min);
		hour=new JSpinner(hr);
		button=new Panel();
		spinner=new Panel();
		button.setLocation(600,500);
		spinner.setLocation(600,500);
		set.addActionListener(this);
		stop.addActionListener(this);
		cancel.addActionListener(this);
	}
	public void arrange()
	{
		setLayout(new BorderLayout());
		button.add(set);
		button.add(stop);
		button.add(cancel);
		add(button,BorderLayout.SOUTH);
		spinner.add(hour);
		spinner.add(minute);
		spinner.add(second);
		add(spinner,BorderLayout.CENTER);
	}
	public void setTime()
	{
		if(!settime)
		{
			second.setValue((int)d.getSecond());
			minute.setValue((int)d.getMinute());
			hour.setValue((int)d.getHour());
		}
	}
	public void makeSetDisable()
	{
		set.setEnabled(false);
	}
	public void makeStopEnable()
	{
		stop.setEnabled(true);
	}
	public void makeSetTimeDisable()
	{
		settime=false;
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==set)
		{
			d.setSecond(Long.parseLong(String.valueOf(second.getValue())));
			d.setMinute(Long.parseLong(String.valueOf(minute.getValue())));
			d.setHour(Long.parseLong(String.valueOf(hour.getValue())));
			d.setFileTime(d.getCurrentTime()-(d.getSecond()*1000+d.getMinute()*60*1000+d.getHour()*60*60*1000));
			f.writeFile(d.getFileTime());
			dispose();
		}
		else if(e.getSource()==stop)
		{
			set.setEnabled(true);
			stop.setEnabled(false);
			settime=true;
		}
		else if(e.getSource()==cancel)
		{
			dispose();
		}
	}
}
class ClockPanel extends Component
{
	boolean drawing=false;
	DigitalClock d;
	AnalogClock a;
	public ClockPanel()
	{
		setBackground(Color.white);
	}
	public void references(DigitalClock dc,AnalogClock ac)
	{
		d=dc;
		a=ac;
	}
	public void paint(Graphics g)
	{
		if(drawing)
		{
			d.draw(g);
			a.draw(g);
		}
	}
}
class ClockFrame extends JFrame implements ActionListener
{
	int width,height;
	JButton set;
	Panel setp;
	ClockPanel cp;
	SetTimeFrame stf;
	public ClockFrame(ClockPanel p)
	{
		cp=p;
		width=500;
		height=400;
		setTitle("Clock");
		setSize(width,height);
		setLocation(200,200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		set=new JButton("Set");
		setp=new Panel();
		setLayout(new BorderLayout());
		set.addActionListener(this);
		setp.add(set);
		add(setp,BorderLayout.SOUTH);
		add(cp,BorderLayout.CENTER);
		setResizable(false);
		setVisible(true);
	}
	public void references(SetTimeFrame s)
	{
		stf=s;
	}
	public void actionPerformed(ActionEvent e)
	{
		stf.setVisible(true);
		stf.makeStopEnable();
		stf.makeSetDisable();
		stf.makeSetTimeDisable();
	}
}
class MilliSecondStorageFile
{
	protected String filename;
	public MilliSecondStorageFile()
	{
		filename=new String("InitialValue");
	}
	public void writeFile(double a)
	{
		try
		{
			FileWriter fw=new FileWriter(filename);
			String s=Double.toString(a);
			char ch[]=s.toCharArray();
			fw.write(ch);
			fw.flush();
			fw.close();
		}
		catch(IOException ex)
		{	System.out.println("Exception caught");				}
	}
	public double readFile()
	{
		String s=new String("");
		try
		{
			FileReader fr=new FileReader(filename);
			char ch[]=new char[22];
			fr.read(ch);
			fr.close();
			s=String.valueOf(ch);
		}
		catch(IOException ex)
		{	System.out.println("Exception caught");				}
		finally
		{
			return Double.valueOf(s);
		}
	}
}
class DigitalClock
{
	protected long millisecond,second,minute,hour;
	protected double filetime,currenttime;
	protected Color digit_colour;
	protected MilliSecondStorageFile f;
	boolean initial=true;
	public DigitalClock(MilliSecondStorageFile a)
	{
		f=a;
		digit_colour=Color.blue;
		filetime=f.readFile();
	}
	public void setSecond(long a)
	{
		second=a;
	}
	public void setMinute(long a)
	{
		minute=a;
	}
	public void setHour(long a)
	{
		hour=a;
	}
	public long getSecond()
	{
		return second;
	}
	public long getMinute()
	{
		return minute;
	}
	public long getHour()
	{
		return hour;
	}
	public double getFileTime()
	{
		return filetime;
	}
	public double getCurrentTime()
	{
		return currenttime;
	}
	public void setFileTime(double a)
	{
		filetime=a;
	}
	public void setTime()
	{
		long i,j,k;
		currenttime=System.currentTimeMillis();
		millisecond=(long)(currenttime-filetime);
		for(i=1;millisecond>=i*43200000;i++);
		millisecond=millisecond-(i-1)*43200000;
		second=(long)millisecond/1000;
		for(j=1;j*60<=second;j++);
		minute=j-1;
		second=second-minute*60;
		for(k=1;k*60<=minute;k++);
		hour=k-1;
		minute=minute-hour*60;
		if(initial)
		{
			initial=false;
			filetime=filetime+(i-1)*43200000;
			f.writeFile(filetime);
		}
	}
	public void draw(Graphics g)
	{
		g.clearRect(0,0,60,20);
		g.setColor(digit_colour);
		g.drawString((int)hour+":"+(int)minute+":"+(int)second,5,15);
	}
}
class AnalogClock
{
	Dial d;
	Arrow s,m,h;
	public AnalogClock(ClockFrame cf)
	{
		d=new Dial(cf);
		s=new SecondArrow(d);
		m=new MinuteArrow(d);
		h=new HourArrow(d);
	}
	public void references(DigitalClock dc)
	{
		s.references(dc);
		m.references(dc);
		h.references(dc);
	}
	public void machine()
	{
		s.setArrow();
		m.setArrow();
		h.setArrow();
	}
	public void draw(Graphics g)
	{
		d.draw(g);
		h.draw(g);
		m.draw(g);
		s.draw(g);
	}
}
class Dial
{
	int midx,midy;
	int hx,tx,hy,ty;
	int x,y;
	double in_radius;
	double out_radius;
	double angle;
	double reference_distance;
	Color out_colour;
	Color in_colour;
	public Dial(ClockFrame cf)
	{
		midx=cf.width/2;
		midy=cf.height/2-30;
		in_radius=(int)(cf.height/3.2);
		out_radius=(int)(cf.height/2.8);
		reference_distance=in_radius*9/10;
		out_colour=Color.cyan;
		in_colour=Color.white;
		angle=0;
	}
	public void draw(Graphics g)
	{
		g.setColor(out_colour);
		g.fillOval((int)(midx-out_radius),(int)(midy-out_radius),(int)(2*out_radius),(int)(2*out_radius));
		g.setColor(in_colour);
		g.fillOval((int)(midx-in_radius),(int)(midy-in_radius),(int)(2*in_radius),(int)(2*in_radius));
		g.setColor(Color.black);
		for(int i=1;i<=60;i++)
		{
			angle=angle+Math.PI/30;
			if(i%5==0)
			{
				hx=(int)(midx+(reference_distance+2)*Math.cos(angle));
				hy=(int)(midy+(reference_distance+2)*Math.sin(angle));
				tx=(int)(midx+(reference_distance-2)*Math.cos(angle));
				ty=(int)(midy+(reference_distance-2)*Math.sin(angle));
				g.drawLine(hx,hy,tx,ty);
			}
			else
			{
				x=(int)(midx+reference_distance*Math.cos(angle));
				y=(int)(midy+reference_distance*Math.sin(angle));
				g.drawLine(x,y,x,y);
			}
		}
	}
}
abstract class Arrow
{
	double radian;
	int hx,hy,tx,ty;
	double mid_to_head,mid_to_tail;
	Color arrow_colour;
	Dial d;
	DigitalClock dc;
	abstract public void setArrow();
	abstract public void draw(Graphics g);
	public void references(DigitalClock dgc)
	{
		dc=dgc;
	}
}
class SecondArrow extends Arrow
{
	public SecondArrow(Dial dl)
	{
		d=dl;
		mid_to_head=d.in_radius*8/10;
		mid_to_tail=mid_to_head/4;
		arrow_colour=Color.red;
	}
	public void setArrow()
	{
		radian=((int)dc.getSecond())*Math.PI/30;
		hx=(int)(d.midx+mid_to_head*Math.cos(radian-Math.PI/2));
		hy=(int)(d.midy+mid_to_head*Math.sin(radian-Math.PI/2));
		tx=(int)(d.midx+mid_to_tail*Math.cos(radian-Math.PI*3/2));
		ty=(int)(d.midy+mid_to_tail*Math.sin(radian-Math.PI*3/2));
	}
	public void draw(Graphics g)
	{
		g.setColor(arrow_colour);
		g.drawLine(hx,hy,tx,ty);
		g.fillOval(d.midx-2,d.midy-2,4,4);
	}
}
class MinuteArrow extends Arrow
{
	int lx,ly,rx,ry;
	int x[],y[];
	double mid_to_left,mid_to_right;
	public MinuteArrow(Dial dl)
	{
		d=dl;
		mid_to_head=d.in_radius*9/10;
		mid_to_tail=mid_to_head/4;
		mid_to_left=mid_to_head/25;
		mid_to_right=mid_to_head/25;
		arrow_colour=Color.lightGray;
		x=new int[4];
		y=new int[4];
	}
	public void setArrow()
	{
		radian=dc.getMinute()*Math.PI/30+dc.getSecond()*Math.PI/1800;
		hx=(int)(d.midx+mid_to_head*Math.cos(radian-Math.PI/2));
		hy=(int)(d.midy+mid_to_head*Math.sin(radian-Math.PI/2));
		tx=(int)(d.midx+mid_to_tail*Math.cos(radian-Math.PI*3/2));
		ty=(int)(d.midy+mid_to_tail*Math.sin(radian-Math.PI*3/2));
		lx=(int)(d.midx+mid_to_left*Math.cos(radian-Math.PI));
		ly=(int)(d.midy+mid_to_left*Math.sin(radian-Math.PI));
		rx=(int)(d.midx+mid_to_right*Math.cos(radian));
		ry=(int)(d.midy+mid_to_right*Math.sin(radian));
	}
	public void draw(Graphics g)
	{
		x[0]=hx;x[1]=lx;x[2]=tx;x[3]=rx;
		y[0]=hy;y[1]=ly;y[2]=ty;y[3]=ry;
		g.setColor(arrow_colour);
		g.fillPolygon(x,y,4);
		g.drawLine(hx,hy,tx,ty);
		g.drawLine(lx,ly,tx,ty);
	}
}
class HourArrow extends Arrow
{
	int lx,ly,rx,ry;
	int x[],y[];
	double mid_to_left,mid_to_right;
	public HourArrow(Dial dl)
	{
		d=dl;
		mid_to_head=d.in_radius*7/10;
		mid_to_tail=mid_to_head/4;
		mid_to_left=mid_to_head/15;
		mid_to_right=mid_to_head/15;
		arrow_colour=Color.gray;
		x=new int[4];
		y=new int[4];
	}
	public void setArrow()
	{
		radian=dc.getHour()*Math.PI/6+dc.getMinute()*Math.PI/360+dc.getSecond()*Math.PI/216000;
		hx=(int)(d.midx+mid_to_head*Math.cos(radian-Math.PI/2));
		hy=(int)(d.midy+mid_to_head*Math.sin(radian-Math.PI/2));
		tx=(int)(d.midx+mid_to_tail*Math.cos(radian-Math.PI*3/2));
		ty=(int)(d.midy+mid_to_tail*Math.sin(radian-Math.PI*3/2));
		lx=(int)(d.midx+mid_to_left*Math.cos(radian-Math.PI));
		ly=(int)(d.midy+mid_to_left*Math.sin(radian-Math.PI));
		rx=(int)(d.midx+mid_to_right*Math.cos(radian));
		ry=(int)(d.midy+mid_to_right*Math.sin(radian));
	}
	public void draw(Graphics g)
	{
		x[0]=hx;x[1]=lx;x[2]=tx;x[3]=rx;
		y[0]=hy;y[1]=ly;y[2]=ty;y[3]=ry;
		g.setColor(arrow_colour);
		g.fillPolygon(x,y,4);
		g.drawLine(hx,hy,tx,ty);
		g.drawLine(lx,ly,tx,ty);
	}
}