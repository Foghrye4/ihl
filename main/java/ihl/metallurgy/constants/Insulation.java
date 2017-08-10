package ihl.metallurgy.constants;

public enum Insulation {
	TarPitch(10000,6000),
	RawRubber(10000,6000),
	PVC(18000,20000),
	Rubber(20000,10000),
	Plastic(40000,35000),//Polyethylene
	XPVC(30000,100000);
	Insulation(int maxVoltagePermm1, int maxVoltageCap1)
	{
		maxVoltagePermm=maxVoltagePermm1;//V/mm
		maxVoltageCap=maxVoltageCap1;
	}
	public final int maxVoltagePermm;
	public final int maxVoltageCap;
	public static int getMaxVoltagePermm(String name)
	{
		if(Insulation.valueOf(name)==null)
		{
			return 0;
		}
		else
		{
			return Insulation.valueOf(name).maxVoltagePermm;
		}
	}
	public static int getMaxVoltageCap(String name)
	{
		if(Insulation.valueOf(name)==null)
		{
			return 0;
		}
		else
		{
			return Insulation.valueOf(name).maxVoltageCap;
		}
	}

}
