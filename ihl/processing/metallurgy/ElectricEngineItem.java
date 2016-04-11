package ihl.processing.metallurgy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;

public class ElectricEngineItem extends Item{
	
	public Type type;
	public static List<ElectricEngineItem> instances = new ArrayList();
	private static Map<Type, IIcon> iconMap = new HashMap();


	public ElectricEngineItem(Type type1) 
	{
		super();
		this.type=type1;
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setUnlocalizedName(type.unLocalizedName);
		this.setMaxStackSize(1);
		instances.add(this);
	}
	
	public static void init()
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			new ElectricEngineItem(var1[i]);
		}
		Iterator<ElectricEngineItem> ii = instances.iterator();
		while(ii.hasNext())
		{
			ElectricEngineItem instance = ii.next();
			GameRegistry.registerItem(instance,instance.type.unLocalizedName);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		iconMap.put(this.type, register.registerIcon(IHLModInfo.MODID + ":"+this.type.unLocalizedName));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int i) 
	{
		return iconMap.get(type);
	}
	
	
	public enum Type
	{
		LVLEElectricEngine("electricMotorLVLEDC", 600,400,0.2F,1500);
		Type(String unlocalizedName1, int maxVoltage1, int generatedVoltage1, float efficiency1,int rpm1)
		{
			unLocalizedName=unlocalizedName1;
			maxVoltage=maxVoltage1;
			generatedVoltage=generatedVoltage1;
			efficiency=efficiency1;
			rpm=rpm1;
		}

		public String unLocalizedName;
		public int maxVoltage=600;
		public int generatedVoltage=400;
		public float efficiency = 0.2F;
		public int rpm=1500;//at max voltage
	}

}
