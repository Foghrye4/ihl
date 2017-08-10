package ihl.metallurgy.constants;

public enum ElectricConductor {
	Silver(158,1000),
	Copper(175,100),
	Gold(220,100),
	Aluminum(270,100),
	Magnesium(440,100),
	Iridium(474,1000),
	Molybdenum(540,1000),
	Tungsten(550,5000),
	Zinc(600,100),
	Nickel(730,5000),
	Bronze(990,100),
	Iron(1000,5000),
	Platinum(1080,5000),
	Tin(1130,100),
	Steel(1300,5000),
	Lead(2080,100),
	Antimony(3400,5000),
	Manganese(4300,5000),
	Constantan(5000,5000),
	Titan(6000,5000),
	Mercury(9540,-10),
	Castiron(10000,5000),
	Nichrome(11200,6000),
	Bismuth(11800,5000),
	Graphite(80000,10000),
	Coal(400000,10000),
	Uran(29000000,5000);
	ElectricConductor(int resistivity1, int meltingPoint1)//Ohm*m *10^10
	{
		resistivity=resistivity1;
		meltingPoint=meltingPoint1;
	}
	public final long resistivity;
	public final int meltingPoint;
	public static long getResistivity(String name)
	{
		if(ElectricConductor.valueOf(name)==null)
		{
			return 0;
		}
		else
		{
			return ElectricConductor.valueOf(name).resistivity;
		}
	}
}
