package ihl.handpump;

public class XYZ {
	public int x;
	public int y;
	public int z;
	
	public XYZ(int x1,int y1,int z1) 
	{
		this.x=x1;
		this.y=y1;
		this.z=z1;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof XYZ)
		{
			XYZ xyz2 = (XYZ) obj;
			return (this.x==xyz2.x && this.y==xyz2.y && this.z==xyz2.z);
		}
		return false;
	}

}
