package ihl.utils;

public class IHLMathUtils 
{
	private final static int accuracy_level=65536;
	private final static float[] sin_table=new float[accuracy_level];
	private final static float[] atan_table=new float[accuracy_level];
	private final static float[] sqrt_table=new float[accuracy_level];
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
	
	public static float sqrt(float value)
	{
		float value1 = value;
		int multiplier = 2;
		while(value1 >= 1.0f)
		{
			multiplier*=multiplier;
			value1 /=multiplier*multiplier;
		}
		return ((multiplier>2)?multiplier:1)*sqrt_table[(int)(value1*accuracy_level)];
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
	
	public static float[] vector_vector_multiply(float[] v1, float[] v2)
	{
		float c_x = v1[1]*v2[2] - v2[1]*v1[2];
		float c_y = v2[0]*v1[2] - v1[0]*v2[2];
		float c_z = v1[0]*v2[1] - v2[0]*v1[1];
		return new float[] {c_x,c_y,c_z};
	}

	public static void normalize_vector(float[] v1)
	{
		float d = (float)Math.sqrt(v1[0]*v1[0]+v1[1]*v1[1]+v1[2]*v1[2]);
		if(d == 0)
		{ // Nothing can we do. Create new vector towards up direction.
			v1[0]=0;
			v1[1]=1;
			v1[2]=0;
		}
		else
		{
			v1[0]/=d;
			v1[1]/=d;
			v1[2]/=d;
		}
	}

	public static void scale_vector_to_value(float[] v1, float v2)
	{
		float d = (float)Math.sqrt(v1[0]*v1[0]+v1[1]*v1[1]+v1[2]*v1[2]);
		if(d == 0)
		{ // Nothing can we do. Create new vector towards up direction.
			v1[0]=0;
			v1[1]=v2;
			v1[2]=0;
		}
		else
		{
			v1[0]=v1[0]*v2/d;
			v1[1]=v1[1]*v2/d;
			v1[2]=v1[2]*v2/d;
		}
	}
	
	public static void vector_add(float[] fs, float x, float y, float z) {
		fs[0]+=x;
		fs[1]+=y;
		fs[2]+=z;
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
		for(int i=0;i<accuracy_level;i++)
		{
			sqrt_table[i]=(float) Math.sqrt((double)i/accuracy_level);
		}
	}

	public static float[] vector_return_difference(double[] v1, double[] v2) {
		return new float[] {
    			(float)(v1[0]-v2[0]),
    			(float)(v1[1]-v2[1]),
    			(float)(v1[2]-v2[2])};
	}

	public static float[] vector_return_difference(float[] v1, double[] v2) {
		return new float[] {
    			(float)(v1[0]-v2[0]),
    			(float)(v1[1]-v2[1]),
    			(float)(v1[2]-v2[2])};
	}

	public static float[] vector_return_difference(int[] v1, double[] v2) {
		return new float[] {
    			(float)(v1[0]-v2[0]),
    			(float)(v1[1]-v2[1]),
    			(float)(v1[2]-v2[2])};
	}

	public static void multiply_vector_to_value(float[] v1, float v2) {
		v1[0]*=v2;
		v1[1]*=v2;
		v1[2]*=v2;
	}

	public static void vector_add(double[] v1, float[] v2) {
		v1[0]+=v2[0];
		v1[1]+=v2[1];
		v1[2]+=v2[2];
	}
	
	public static float[] get_triangle_normal(double[][] triangle1) {
		float[] v1 = vector_return_difference(triangle1[1],triangle1[0]);
		float[] v2 = vector_return_difference(triangle1[2],triangle1[0]);
		return vector_vector_multiply(v1,v2);
	}
	
}
