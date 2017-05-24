package ihl.worldgen.ores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.items_blocks.IHLFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class IHLFluid extends Fluid {
	private IHLFluidType type;
	private static List<Fluid> fluidInstances = new ArrayList<Fluid>();
	private static Map<String, IHLFluidType> localFluidRegistry = new HashMap<String, IHLFluidType>();
	private static Map<String, Float> realDensityMap = new HashMap<String, Float>();
	private static Map<String, String> condensationMap = new HashMap<String, String>();
	private static Map<String, List<String>> solutionMap = new HashMap<String, List<String>>();
	public static final int maxGaseousStateVapoursDensity = 40;

	public IHLFluid(IHLFluidType type1) {
		super(type1.fluidRegistryName);
		type = type1;
		this.setTemperature(type.temperature);
		this.setDensity(Math.round(type.density));
		realDensityMap.put(type1.fluidRegistryName, type.density);
		this.setUnlocalizedName(type.fluidRegistryName.replaceFirst("fluid", ""));
		this.setGaseous(type.isGaseous);
		Fluid instance = this;
		if (!FluidRegistry.registerFluid(instance)) {
			instance = FluidRegistry.getFluid(type.fluidRegistryName);
		}
		if (instance.getBlock() == null) {
			instance.setBlock(new IHLFluidBlock(instance, type.blockMaterial, type.textureName,
					"fluid" + type.fluidName.replaceFirst("fluid", "")).setFlammable(type.flammable)
							.setBlockName("block" + type.fluidName).setCreativeTab(IHLCreativeTab.tab));
		}
		instance.setGaseous(type.isGaseous);
		if (type.haveBucket) {
			Item bucket = new ItemBucket(block).setTextureName(IHLModInfo.MODID + ":bucket_" + type.fluidName)
					.setUnlocalizedName("bucket_" + type.fluidName).setCreativeTab(IHLCreativeTab.tab);
			GameRegistry.registerItem(bucket, "bucket_" + type.fluidName);
			FluidContainerRegistry.registerFluidContainer(instance, new ItemStack(bucket),
					FluidContainerRegistry.EMPTY_BUCKET);
			bucket.setContainerItem(Items.bucket);
		}
		type.fluid = instance;
		localFluidRegistry.put(type1.fluidRegistryName, type);
		fluidInstances.add(this);
	}

	public static void init() {
		IHLFluidType[] var1 = IHLFluidType.values();
		for (int i = 0; i < var1.length; i++) {
			new IHLFluid(var1[i]);
		}
		condensationMap.put("steam", "ic2distilledwater");
		condensationMap.put("ic2steam", "ic2distilledwater");
		condensationMap.put("ic2superheatedsteam", "ic2distilledwater");
		condensationMap.put("vapour.sulfuricacid", "sulfuricacid");
		solutionMap.put("water", Arrays.asList(
				new String[] { "aquaregia", "sulfuricacid", "solution.nickelsulfate", "solution.bluevitriol" }));
		solutionMap.put("ic2distilledwater", Arrays.asList(
				new String[] { "aquaregia", "sulfuricacid", "solution.nickelsulfate", "solution.bluevitriol" }));
	}

	public static void registerIcons(TextureMap iconRegistry) {
		Iterator<Fluid> ii = fluidInstances.iterator();
		while (ii.hasNext()) {
			Fluid instance = ii.next();
			IHLFluidType instanceType = localFluidRegistry.get(instance.getName());
			instance.setIcons(iconRegistry.registerIcon(IHLModInfo.MODID + ":" + instanceType.textureName + "Still"),
					iconRegistry.registerIcon(IHLModInfo.MODID + ":" + instanceType.textureName + "Flowing"));
		}
	}

	public static ItemStack getCell(String fluidname1) {
		ItemStack filledCell = FluidContainerRegistry.fillFluidContainer(
				new FluidStack(FluidRegistry.getFluid(fluidname1), FluidContainerRegistry.BUCKET_VOLUME),
				Ic2Items.cell.copy());
		return filledCell;
	}

	public static Block getBlock(String fluidname) {
		return localFluidRegistry.get(fluidname).fluid.getBlock();
	}

	public static int getMeltingPoint(Fluid fluid) {
		if (localFluidRegistry.get(fluid.getName()) != null) {
			IHLFluidType type = localFluidRegistry.get(fluid.getName());
			return type.meltingPoint;
		} else {
			if (fluid.isGaseous()) {
				return fluid.getTemperature() > 100 ? fluid.getTemperature() - 100 : 0;
			} else {
				return fluid.getTemperature();
			}
		}
	}

	public static int getBoilingPoint(Fluid fluid) {
		if (localFluidRegistry.containsKey(fluid.getName())) {
			IHLFluidType type = localFluidRegistry.get(fluid.getName());
			return type.boilingPoint;
		} else {
			if (fluid.getName() == "steam" || fluid.getName() == "ic2steam"
					|| fluid.getName() == "ic2superheatedsteam") {
				return 373;
			} else if (fluid.isGaseous()) {
				return fluid.getTemperature();
			} else {
				return fluid.getTemperature() + 100;
			}
		}
	}

	public enum IHLFluidType {
		// Methane("Methane","fluidAcetylene",10047, 293, 249, 373, 1150,
		// "methane", Material.water, false, false, false, false),
		NitroGlyceryl("NitroGlycerin", 10052, 293, 291, 583, 1595, "nitroglycerin", Material.water, true, false, true),
		Ammonia("Ammonia", 10051, 273, 273 - 78, 273
				- 33, 0.772F, "ammonia", new MaterialLiquid(MapColor.airColor), true, false, true),
		Acetaldehyde("Acetaldehyde", 10050, 273, 273
				- 123, 294, 784, "acetaldehyde", Material.water, false, false, false),
		Formaldehyde("Formaldehyde", 10049, 254, 273
				- 118, 254, 815, "formaldehyde", Material.water, false, false, false),
		TarWater("TarWater", 10048, 293, 176, 338, 1032, "tarwater", Material.water, false, false, false),
		Methanol("Methanol", 10047, 293, 176, 338, 792, "methanol", Material.water, true, false, false),
		LithiumChloride("LithiumChlorideDissolvedInWater", 10046, 293, 249, 373, 1530, "solution.lithiumchloride", Material.water, false, false, false),
		CalciumChloride("CalciumChlorideDissolvedInWater", 10045, 293, 249, 373, 1630, "solution.calciumchloride", Material.water, false, false, false),
		ZeolitePulp("ZeolitePulp", "fluidPulpZeolite", 10044, 293, 249, 373, 1150, "pulp.sodiumzeolite", Material.water, false, false, false),
		MoltenTarPitch("MoltenTarPitch", "fluidOil", 10043, 373, 373, 800, 1250, "molten.tarpitch", Material.lava, false, false, false),
		CrackingGas("CrackingGas", "fluidAcetylene", 10042, 293, 135, 273, 2.27f, "crackinggas", new MaterialLiquid(
				MapColor.blackColor), false, false, false),
		Trichlorosilane("Trichlorosilane", "fluidHydrogenChloride", 10041, 293, 147, 305, 1342, "trichlorosilane", new MaterialLiquid(
				MapColor.greenColor), true, false, false),
		SiliconTetrachloride("SiliconTetrachloride", "fluidChlorine", 10040, 293, 204, 330, 584, "silicontetrachloride", new MaterialLiquid(
				MapColor.cyanColor), true, false, false),
		Chlorine("Chlorine", 10039, 293, 172, 238, 3.21F),
		BoricAcid("BoricAcid", 10038, 373, 249, 373, 1275, "boricacid", Material.water, false, false, false),
		MicaPulp("PulpMica", 10037, 293, 249, 373, 1150, "pulp.mica", Material.water, false, false, false),
		CellulosePulp("PulpCellulose", 10036, 293, 249, 373, 1075, "pulp.cellulose", Material.water, false, false, false),
		Fuel("Fuel", 10035, 293, 268, 633, 840, "fuel", Material.water, true, false, true),
		MineralOil("MineralOil", 10034, 293, 238, 704, 845, "mineraloil", Material.water, true, false, true),
		FuelOil("FuelOil", 10033, 293, 293, 693, 991, "fueloil", Material.water, true, false, true),
		Oil("Oil", "fluidOil", 10032, 293, 284, 773, 850f, "oil", Material.water, true, false, true),
		Mercury("Mercury", 10031, 293, 234, 630, 13546),
		VapourMercury("VapourMercury", 10030, 630, 234, 630, 9.229F, "vapour.mercury", new MaterialLiquid(
				MapColor.cyanColor), false, false, false),
		Hydrogen("Hydrogen", 10029, 293, 14, 20, 0.046F, "hydrogen", new MaterialLiquid(
				MapColor.blueColor), true, false, true),
		SaltWater("SaltWater", "fluidSaltWater", 10028, 293, 253, 373, 1360f, "saltwater", Material.water, true, false, false),
		HydrogenChloride("HydrogenChloride", 10027, 293, 159, 188, 1.477F, "hydrogenchloride", new MaterialLiquid(
				MapColor.yellowColor), true, false, true),
		NatriumTungstate("NatriumTungstateDissolvedInWater", 10026, 293, 249, 373, 1730, "solution.natriumtungstate", Material.water, true, false, false),
		Turpentine("Turpentine", 10025, 293, 217, 453, 1470, "turpentine", Material.water, true, false, true),
		Air("Air", 293, 55, 93, 1.292F, Ic2Items.airCell),
		Nitrogen("Nitrogen", 10024, 92, 56, 92, 1.251F, "nitrogen", new MaterialLiquid(
				MapColor.airColor), true, false, true),
		Acetylene("Acetylene", 10023, 293, 17, 190, 1.173F, "acetylene", Material.water, true, false, true),
		Oxygen("Oxygen", 10022, 93, 55, 93, 1.429F, "oxygen", new MaterialLiquid(MapColor.airColor), true, false, true),
		NatriumHydroxide("NatriumHydroxideDissolvedInWater", 10021, 293, 249, 373, 1525, "solution.natriumhydroxide", Material.water, true, false, false),
		LiquidGlass("LiquidGlass", 10020, 293, 253, 373, 2400),
		fluidRubberTreeSap("fluidRubberTreeSap", "fluidRubberTreeSap", 10019, 293, 273, 393, 1200f, "fluidrubbertreesap", Material.water, true, true, false),
		SpruceResin("SpruceResin","fluidSpruceResin", 10018, 293, 273, 533, 1080, "spruceresin", Material.water, true, true, true),
		CablingColophony("CablingColophony", 10017, 363, 363, 533, 1070, "cablingcolophony", Material.water, true, false, true),
		Glycerol("Glycerol", 10016, 293, 291, 583, 1261, "glycerol", Material.water, true, false, true),
		SeedOil("SeedOil", 10015, 293, 256, 583, 920, "seedoil", Material.water, true, false, true),
		AquaRegia("AquaRegia", 10014, 293, 231, 356, 1060),
		SodiumPeroxide("SodiumPeroxide", 10013, 950, 950, 2223, 1800),
		OsmiumTetroxide("OsmiumTetroxide", 10012, 313, 313, 403, 9),
		OleicAcid("OleicAcid", 10011, 293, 288, 633, 895, "oleicacid", Material.water, true, false, true),
		Limemilk("Limemilk", 10010, 293, 250, 373, 1020),
		NickelSulfateDissolvedInWater("NickelSulfateDissolvedInWater", 10009, 293, 253, 373, 1220, "solution.nickelsulfate", Material.water, true, false, false),
		BlueVitriolDissolvedInWater("BlueVitriolDissolvedInWater", 10008, 293, 253, 373, 1180, "solution.bluevitriol", Material.water, true, false, false),
		MoltenRubberWithSulfur("MoltenRubberWithSulfur", 10007, 600, 600, 1000, 1200, "molten.rubber", Material.lava, false, false, true),
		MoltenPotassium("MoltenPotassium", "fluidMolten", 10042, 336, 336, 1047, 856, "molten.potassium", Material.lava, false, false, false),
		MoltenLithium("MoltenLithium", "fluidMolten", 10042, 454, 454, 1613, 512, "molten.lithium", Material.lava, false, false, false),
		MoltenPotassiumChloride("MoltenPotassiumChloride", "fluidMolten", 10006, 776 + 273, 776 + 273, 1407
				+ 273, 1556, "molten.potassiumchloride", Material.lava, false, false, false),
		MoltenSodiumChloride("MoltenSodiumChloride", "fluidMolten", 10006, 1273, 1273, 1740, 1556, "molten.sodiumchloride", Material.lava, false, false, false),
		MoltenGlass("MoltenGlass", "fluidMolten", 10048, 600, 600, 1950, 2270, "molten.glass", Material.lava, false, false, false),
		MoltenMagnesium("MoltenMagnesium", "fluidMolten", 10006, 923, 923, 1623, 1584, "molten.magnesium", Material.lava, false, false, false),
		MoltenSteel("MoltenSteel", "fluidMolten", 10006, 1800, 1800, 3134, 6980, "molten.steel", Material.lava, false, false, false),
		MoltenCopper("MoltenCopper", "fluidMolten", 10048, 1356, 1356, 2840, 8920, "molten.copper", Material.lava, false, false, false),
		MoltenBronze("MoltenBronze", 10005, 940, 940, 2840, 8000, "molten.bronze", Material.lava, false, false, false),
		MoltenGold("MoltenGold", 10005, 1337, 1337, 3129, 17310, "molten.gold", Material.lava, false, false, false),
		SulfuricAcid("SulfuricAcid", 10002, 293, 283, 610, 1836),
		NitricAcid("NitricAcid", 10001, 293, 231, 356, 1100);

		IHLFluidType(String fluidName1, int celldamage, int temperature1, int meltingPoint1, int boilingPoint1,
				float density1) {
			fluidName = fluidName1;
			fluidRegistryName = fluidName.toLowerCase();
			textureName = "fluid" + fluidName.replaceFirst("fluid", "");
			temperature = temperature1;
			density = density1;
			cellName = "itemCell" + fluidName;
			haveBucket = false;
			isGaseous = density1 < maxGaseousStateVapoursDensity;
			boilingPoint = boilingPoint1;
			meltingPoint = meltingPoint1;
			damage = celldamage;
		}

		IHLFluidType(String fluidName1, int temperature1, int meltingPoint1, int boilingPoint1, float density1,
				ItemStack cell1) {
			fluidName = fluidName1;
			fluidRegistryName = fluidName.toLowerCase();
			textureName = "fluid" + fluidName.replaceFirst("fluid", "");
			temperature = temperature1;
			density = density1;
			cellName = "itemCell" + fluidName;
			haveBucket = false;
			isGaseous = density1 < maxGaseousStateVapoursDensity;
			boilingPoint = boilingPoint1;
			meltingPoint = meltingPoint1;
			damage = 99999;
			hasCell = false;
			cell = cell1;
		}

		IHLFluidType(String fluidName1, int celldamage, int temperature1, int meltingPoint1, int boilingPoint1,
				float density1, String fluidRegistryName1, Material blockMaterial1, boolean hasCell1,
				boolean haveBucket1, boolean flammable1) {
			fluidName = fluidName1;
			fluidRegistryName = fluidRegistryName1;
			textureName = "fluid" + fluidName.replaceFirst("fluid", "");
			temperature = temperature1;
			density = density1;
			cellName = "itemCell" + fluidName;
			haveBucket = haveBucket1;
			flammable = flammable1;
			isGaseous = density1 < maxGaseousStateVapoursDensity;
			blockMaterial = blockMaterial1;
			boilingPoint = boilingPoint1;
			meltingPoint = meltingPoint1;
			hasCell = hasCell1;
			damage = celldamage;
		}

		IHLFluidType(String fluidName1, String textureName1, int celldamage, int temperature1, int meltingPoint1,
				int boilingPoint1, float density1, String fluidRegistryName1, Material blockMaterial1, boolean hasCell1,
				boolean haveBucket1, boolean flammable1) {
			fluidName = fluidName1;
			fluidRegistryName = fluidRegistryName1;
			textureName = textureName1;
			temperature = temperature1;
			density = density1;
			cellName = "itemCell" + fluidName;
			haveBucket = haveBucket1;
			flammable = flammable1;
			isGaseous = density1 < maxGaseousStateVapoursDensity;
			blockMaterial = blockMaterial1;
			boilingPoint = boilingPoint1;
			meltingPoint = meltingPoint1;
			hasCell = hasCell1;
			damage = celldamage;
		}

		public String fluidName;
		public String fluidRegistryName;
		public String cellName;
		public String textureName;
		int temperature;
		float density;
		boolean isGaseous;
		boolean flammable = false;
		boolean haveBucket;
		Material blockMaterial = Material.water;
		int meltingPoint;
		int boilingPoint;
		public boolean hasCell = true;
		public ItemStack cell;
		public Fluid fluid;
		public final int damage;
	}

	@SuppressWarnings("deprecation")
	public static FluidStack getCondensationResult(FluidStack condensatedGas) {
		String fluidname = condensationMap.get(condensatedGas.getFluid().getName());
		Fluid fluid = FluidRegistry.getFluid(fluidname);
		if (fluid != null) {
			int quantity = Math.round(condensatedGas.amount * getCondensationConversionRate(condensatedGas.getFluid()));
			if (quantity > 0) {
				return new FluidStack(fluid.getID(), quantity);
			}
		}
		return null;
	}

	public static float getCondensationConversionRate(Fluid gas) {
		String fluidname = condensationMap.get(gas.getName());
		Fluid fluid = FluidRegistry.getFluid(fluidname);
		if (fluid != null) {
			float rate = (float) gas.getDensity() / (float) fluid.getDensity();
			if (gas.getDensity() < 0) {
				// IC2 steam density -800, 100 mb steam from 1 mb water
				rate = (-8000F / gas.getDensity()) / fluid.getDensity();
			}
			return rate;
		}
		return 0F;
	}

	public static boolean canBeDissolvedIn(FluidStack solvent, String impurity) {
		return solutionMap.get(solvent.getFluid().getName()).contains(impurity);
	}

	public static float getRealDensity(Fluid gas) {
		if (realDensityMap.containsKey(gas.getName())) {
			return realDensityMap.get(gas.getName());
		} else {
			float density = gas.getDensity();
			if (gas.getDensity() < 0) {
				// IC2 steam density -800, 100 mb steam from 1 mb water
				density = -8000F / gas.getDensity();
			}
			return density;
		}
	}

}
