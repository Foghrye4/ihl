package ihl.utils;

public class IHLMathUtils 
{
	private final static int accuracy_level=65536;
	private static float[] sin_table=new float[accuracy_level];
	private static float[] atan_table=new float[accuracy_level];
	private final static float PI=(float)Math.PI;
	
	public static float sin(float angle)
	{
		float angle1 = angle % (2*PI);
		if(angle1<0)
		{
			angle1+=2*PI;
		}
		return sin_table[(int)(angle1*accuracy_level/2/PI)];
	}
	
	public static float atan(float tan_value)
	{
		if(tan_value<-32f)
		{
			return -1.54f;
		}
		else if(tan_value>32f)
		{
			return 1.54f;
		}
		else
		{
			return atan_table[(int)((tan_value+32f)*accuracy_level/64f)];
		}
	}

	
	static
	{
		for(int i=0;i<accuracy_level;i++)
		{
			sin_table[i]=(float) Math.sin(2d*Math.PI*i/accuracy_level);
		}
		for(int i=0;i<accuracy_level;i++)
		{
			atan_table[i]=(float) Math.atan(-32d+64d*i/accuracy_level);
		}
	}
}
