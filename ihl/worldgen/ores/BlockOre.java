package ihl.worldgen.ores;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BlockOre extends Block
{
	private Type type;
	private static List<BlockOre> instances = new ArrayList<BlockOre>();
	private static Map<Type, IIcon> iconMap = new HashMap<Type, IIcon>();

	public BlockOre(Type type1) 
	{
		super(Material.rock);
		this.type=type1;
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setBlockName(type.unLocalizedName);
		instances.add(this);
		this.setHardness(2.0F).setResistance(4.0F);
	}
	
	public static void init()
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			new BlockOre(var1[i]);
		}
		Iterator<BlockOre> ii = instances.iterator();
		while(ii.hasNext())
		{
			BlockOre instance = ii.next();
			GameRegistry.registerBlock(instance,instance.type.unLocalizedName);
			OreDictionary.registerOre(instance.type.unLocalizedName, instance);
		}
	}
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
		super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, flag);
		if(this.type.fortuneAffectsDrops)
		{
			this.dropXpOnBlockBreak(world, x, y, z, MathHelper.getRandomIntegerInRange(world.rand, 1, 4));
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		int fortmod = world.rand.nextInt(fortune + 2) - 1;
		if (fortmod < 0 || !type.fortuneAffectsDrops)fortmod = 0;
		ItemStack drop = null;
		int amount = 1;
		amount = (type.amountFrom + (type.amountTo>0?world.rand.nextInt(type.amountTo):0)) * (fortmod + 1);
		drop = OreDictionary.getOres(type.oreDrop).get(0);
		if (drop != null && amount > 0)
		{
			drop.stackSize=amount;
			drops.add(drop);
		}
		return drops;
	}


	public enum Type
	{
		Bischofite("oreBischofite", "dustBischofite",1,2),
		Datolite("oreDatolite", "dustDatolite",1,2),
		Stibnite("oreStibnite"),
		Chromite("oreChromite"),
		Muscovite("oreMica"),
		Bauxite("oreBauxite"),
		Cinnabar("oreCinnabar"),
		RockSalt("oreRockSalt", "dustRockSalt",1,2),
		Limestone("oreLimestone", "dustCalcite",1,2),
		Gypsum("oreGypsum"),
		Gyubnera("oreGyubnera"),//(Mn,Fe)WO4 Mn:Fe 5/1
		OreTrona("oreTrona", "dustTrona",1,2),
		PotassiumFeldspar("orePotassiumFeldspar"),
		Apatite("oreApatite", "gemApatite",2,5),
		Saltpeter("oreSaltpeter", "dustSaltpeter",1,2);
		Type(String unlocalizedName1, String oreDrop1, int amountFrom1, int amountTo1 )
		{
			unLocalizedName=unlocalizedName1;
			oreDrop=oreDrop1;
			amountFrom=amountFrom1;
			amountTo=amountTo1;
		}
		Type(String unlocalizedName1)
		{
			unLocalizedName=unlocalizedName1;
			oreDrop=unlocalizedName1;
			amountFrom=1;
			amountTo=0;
			fortuneAffectsDrops=false;
		}
		String unLocalizedName;
		String oreDrop;
		int amountFrom;
		int amountTo;
		boolean fortuneAffectsDrops=true;
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) 
	{
		iconMap.put(this.type, register.registerIcon(IHLModInfo.MODID + ":"+this.type.unLocalizedName));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int j) 
	{
		return iconMap.get(type);
	}
}
