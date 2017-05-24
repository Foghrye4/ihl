package ihl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.Logger;

import codechicken.nei.NEIModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import gregapi.data.IL;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.oredict.OreDictManager;
import gregapi.recipes.GT_ModHandler;
import gregapi.recipes.Recipe;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputFluidContainer;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import ic2.core.Ic2Items;
import ic2.core.util.StackUtil;
import ihl.crop_harvestors.RubberTreeBlock;
import ihl.crop_harvestors.SackBlock;
import ihl.crop_harvestors.SackTileEntity;
import ihl.enviroment.LaserHitMirrorEventHandler;
import ihl.enviroment.MirrorBlock;
import ihl.explosion.ChunkAndWorldLoadEventHandler;
import ihl.explosion.ExplosionVectorBlockV2;
import ihl.explosion.ExplosiveBlock;
import ihl.explosion.GroundRemoverItem;
import ihl.explosion.IHLEntityFallingPile;
import ihl.explosion.PileBlock;
import ihl.flexible_cable.AnchorBlock;
import ihl.flexible_cable.AnchorTileEntity;
import ihl.flexible_cable.IHLENet;
import ihl.flexible_cable.IronWorkbenchTileEntity;
import ihl.flexible_cable.NodeEntity;
import ihl.flexible_cable.PowerCableNodeEntity;
import ihl.handpump.AdvancedHandPump;
import ihl.handpump.IHLHandPump;
import ihl.items_blocks.FiberItem;
import ihl.items_blocks.FlexibleCableItem;
import ihl.items_blocks.FlexiblePipeItem;
import ihl.items_blocks.IHLBucketHandler;
import ihl.items_blocks.IHLTool;
import ihl.items_blocks.ItemSubstance;
import ihl.items_blocks.MachineBaseBlock;
import ihl.items_blocks.RecipeInputs;
import ihl.metallurgy.constants.ElectricConductor;
import ihl.nei_integration.NEIIHLConfig;
import ihl.processing.chemistry.BatteryItem;
import ihl.processing.chemistry.ChemicalReactorTileEntity;
import ihl.processing.chemistry.CryogenicDistillerTileEntity;
import ihl.processing.chemistry.ElectricEvaporatorBlock;
import ihl.processing.chemistry.ElectricEvaporatorTileEntity;
import ihl.processing.chemistry.ElectrolysisBathTileEntity;
import ihl.processing.chemistry.EvaporatorBlock;
import ihl.processing.chemistry.EvaporatorTileEntity;
import ihl.processing.chemistry.FluidizedBedReactorTileEntity;
import ihl.processing.chemistry.FractionatorBottomTileEntity;
import ihl.processing.chemistry.LabElectrolyzerTileEntity;
import ihl.processing.chemistry.LeadOvenTileEntity;
import ihl.processing.chemistry.LoomTileEntity;
import ihl.processing.chemistry.PaperMachineTileEntity;
import ihl.processing.metallurgy.AchesonFurnanceTileEntity;
import ihl.processing.metallurgy.Crucible;
import ihl.processing.metallurgy.DetonationSprayingMachineTileEntity;
import ihl.processing.metallurgy.ElectricEngineItem;
import ihl.processing.metallurgy.ExtruderTileEntity;
import ihl.processing.metallurgy.GasWeldingStationTileEntity;
import ihl.processing.metallurgy.ImpregnatingMachineTileEntity;
import ihl.processing.metallurgy.InjectionMoldBlock;
import ihl.processing.metallurgy.MuffleFurnanceTileEntity;
import ihl.processing.metallurgy.RollingMachinePart1TileEntity;
import ihl.processing.metallurgy.VulcanizationExtrudingMoldTileEntity;
import ihl.processing.metallurgy.WireMillTileEntity;
import ihl.processing.metallurgy.WoodenRollingMachinePart1TileEntity;
import ihl.recipes.IronWorkbenchRecipe;
import ihl.recipes.RecipeInputDetonator;
import ihl.recipes.RecipeInputDie;
import ihl.recipes.RecipeInputOreDictionaryList;
import ihl.recipes.RecipeInputWire;
import ihl.recipes.RecipeOutputItemStack;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import ihl.servitor.BoneBlock;
import ihl.servitor.LostHeadEntity;
import ihl.trans_dimensional_item_teleporter.TDITBlock;
import ihl.trans_dimensional_item_teleporter.TDITFrequencyTransmitter;
import ihl.trans_dimensional_item_teleporter.TDITTileEntity;
import ihl.utils.EntityDropEventHandler;
import ihl.utils.FluidDictionary;
import ihl.utils.IHLUtils;
import ihl.worldgen.IHLWorldGenerator;
import ihl.worldgen.ores.BlockOre;
import ihl.worldgen.ores.DebugScannerBlock;
import ihl.worldgen.ores.DebugScannerTileEntity;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = IHLModInfo.MODID, name = IHLModInfo.MODNAME, version = IHLModInfo.MODVERSION, dependencies = "required-after:IC2@[2.2.767-experimental,)")
public class IHLMod implements IFuelHandler {

	@SidedProxy(clientSide = "ihl.ClientProxy", serverSide = "ihl.ServerProxy")
	public static ServerProxy proxy;
	public static IHLModConfig config;
	public static IHLENet enet;
	// This used to determine if GregTech mod presented on server and load
	// GregTech recipes.
	public static boolean isGregTechModLoaded = false;
	public static boolean isGT_API_Version_5 = false;
	public static Block cableAnchorBlock;
	public static Block sackBlock;
	public static Block rubberTreeBlock;
	public static Block spruceTreeBlock;
	public static Block evaporatorBlock;
	public static Block electricEvaporatorBlock;
	public static Block boneBlock = (new BoneBlock(Material.coral)).setBlockName("boneBlock").setHardness(2.0F)
			.setResistance(2.0F);
	public static Item ic2_handpump;
	public static Item ic2_advanced_handpump;
	public static Item ihlSkull = (new Item()).setUnlocalizedName("skull").setFull3D()
			.setCreativeTab(CreativeTabs.tabMisc).setTextureName(IHLModInfo.MODID + ":skull");
	public static Block ic2Leaves;
	public static Block ic2Wood;
	public static Block tditBlock = (new TDITBlock(Material.glass)).setBlockName("tditBlock")
			.setBlockTextureName(IHLModInfo.MODID + ":tditRight").setHardness(0.5F).setResistance(0.5F);
	public static Item tditft;
	public static Block ds = new DebugScannerBlock(Material.ice).setBlockName("debugScanner")
			.setBlockTextureName(IHLModInfo.MODID + ":tditTop");
	public static Item crucible;
	public static Logger log;
	private ItemStack pfaalimestone;
	private ItemStack pfaacobblelimestone;
	public static FluidDictionary fluidDictionary;
	public static Map<String, Integer> moltenAmounts = new HashMap<String, Integer>();
	public static ExplosionVectorBlockV2 explosionHandler;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) throws IOException, ParserConfigurationException {
		fluidDictionary = new FluidDictionary();
		log = evt.getModLog();
		IHLMod.config = new IHLModConfig(evt);
		rubberTreeBlock = (new RubberTreeBlock(RubberTreeBlock.TreeType.RUBBERTREE)).setBlockName("rubberTreeBlock")
				.setBlockTextureName(IHLModInfo.MODID + ":blockRubWoodFront").setHardness(2.0F).setResistance(5.0F);
		spruceTreeBlock = (new RubberTreeBlock(RubberTreeBlock.TreeType.SPRUCE)).setBlockName("spruceBlock")
				.setBlockTextureName(IHLModInfo.MODID + ":blockSpruceFront").setHardness(2.0F).setResistance(5.0F)
				.setCreativeTab(IHLCreativeTab.tab);
		GameRegistry.registerFuelHandler(this);
		FiberItem.init();
		FlexiblePipeItem.init();
		InjectionMoldBlock.init();
		ElectricEngineItem.init();
		BlockOre.init();
		IHLFluid.init();
		GameRegistry.registerWorldGenerator(new IHLWorldGenerator(), 0);
		ItemSubstance.init();
		BatteryItem.init();
		IHLTool.init();
		MirrorBlock.init();
		MachineBaseBlock.init();// must be first
		IHLMod.enet = new IHLENet();
		MinecraftForge.EVENT_BUS.register(new EntityDropEventHandler());
		MinecraftForge.EVENT_BUS.register(new LaserHitMirrorEventHandler());
		MinecraftForge.EVENT_BUS.register(new IHLBucketHandler());
		MinecraftForge.EVENT_BUS.register(proxy);
		FMLCommonHandler.instance().bus().register(proxy);
		GameRegistry.registerBlock(ds, "debugScanner");
		GameRegistry.registerTileEntity(DebugScannerTileEntity.class, "DebugScanner");
		cableAnchorBlock = new AnchorBlock("cableAnchor");
		List<String> info1 = new ArrayList<String>();
		info1.add("non vulcanized rubber insulated");
		GroundRemoverItem.init();
		FlexibleCableItem.init();
		ExplosiveBlock.init();
		PileBlock.init();
		GameRegistry.registerTileEntity(AnchorTileEntity.class, "anchorTileEntity");

		crucible = new Crucible();

		ic2_handpump = new IHLHandPump().setUnlocalizedName("handpump");
		ic2_advanced_handpump = new AdvancedHandPump().setUnlocalizedName("advanced_handpump");
		tditft = new TDITFrequencyTransmitter().setUnlocalizedName("tditFrequencyTransmitter");

		evaporatorBlock = (new EvaporatorBlock(Material.iron)).setBlockName("evaporatorBlock")
				.setBlockTextureName(IHLModInfo.MODID + ":solidFuelEvaporatorFrontActive").setHardness(5.0F)
				.setResistance(5.0F);
		electricEvaporatorBlock = (new ElectricEvaporatorBlock(Material.iron)).setBlockName("electricEvaporatorBlock")
				.setBlockTextureName(IHLModInfo.MODID + ":electricEvaporatorFrontActive").setHardness(5.0F)
				.setResistance(5.0F);
		sackBlock = (new SackBlock(Material.iron)).setBlockName("sackBlock")
				.setBlockTextureName(IHLModInfo.MODID + ":sackItem").setHardness(0.5F).setResistance(0.5F);

		GameRegistry.registerBlock(boneBlock, "boneBlock");

		GameRegistry.registerItem(ic2_handpump, "Handpump");
		GameRegistry.registerItem(ic2_advanced_handpump, ic2_advanced_handpump.getUnlocalizedName());
		GameRegistry.registerItem(ihlSkull, "skull");

		GameRegistry.registerBlock(rubberTreeBlock, "rubberTreeBlock");
		GameRegistry.registerBlock(spruceTreeBlock, "spruceTreeBlock");
		GameRegistry.registerBlock(sackBlock, "sackBlock");
		GameRegistry.registerTileEntity(SackTileEntity.class, "sackTileEntity");
		GameRegistry.registerBlock(evaporatorBlock, "evaporatorBlock");
		GameRegistry.registerBlock(electricEvaporatorBlock, "electricEvaporatorBlock");
		GameRegistry.registerTileEntity(EvaporatorTileEntity.class, "evaporatorTileEntity");
		GameRegistry.registerTileEntity(ElectricEvaporatorTileEntity.class, "electricEvaporatorTileEntity");

		GameRegistry.registerBlock(tditBlock, "tditBlock");
		GameRegistry.registerTileEntity(TDITTileEntity.class, "tditTileEntity");
		GameRegistry.registerItem(tditft, "tditftItem");
		MinecraftForge.EVENT_BUS.register(new ChunkAndWorldLoadEventHandler());
		IHLMod.log.info("IHL precalculating explosion.");
		IHLMod.explosionHandler = new ExplosionVectorBlockV2();
		IHLMod.log.info("Explosion calculated.");
		proxy.load();
		registerEntities();
		OreDictionary.registerOre("ingotBrick", Items.brick);
		OreDictionary.registerOre("dustGunpowder", Items.gunpowder);
		OreDictionary.registerOre("toolLighter", Items.flint_and_steel);
		OreDictionary.registerOre("charcoal", new ItemStack(Items.coal, 1, 1));
		OreDictionary.registerOre("blockDirt", new ItemStack(Blocks.dirt, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("blockDirt", new ItemStack(Blocks.grass, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("platePaper", new ItemStack(Items.paper, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("dustGunpowder", new ItemStack(Items.gunpowder, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("blockExplosive", IHLUtils.getThisModItemStack("ihlExplosive"));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) throws IOException {
		ic2Leaves = StackUtil.getBlock(IC2Items.getItem("rubberLeaves"));
		ic2Wood = StackUtil.getBlock(IC2Items.getItem("rubberWood"));
		if (!IHLMod.config.skipRecipeLoad) {
			if (IHLMod.config.enableRubberTreeSack) {
				GameRegistry.addRecipe(new ItemStack(sackBlock, 1),
						new Object[] { "STS", "L L", "LLL", 'S', Items.string, 'T', Items.stick, 'L', Items.leather });
				Recipes.advRecipes.addRecipe(new ItemStack(evaporatorBlock, 1),
						new Object[] { " B ", " I ", "   ", Character.valueOf('B'), Items.bucket,
								Character.valueOf('I'), IC2Items.getItem("ironFurnace") });
				EvaporatorTileEntity.init();
			}
			if (IHLMod.config.enableTDIT) {
				Recipes.advRecipes.addRecipe(new ItemStack(tditBlock, 1),
						new Object[] { "AAA", "CMC", "ATA", Character.valueOf('A'), IC2Items.getItem("advancedAlloy"),
								Character.valueOf('C'), new ItemStack(Blocks.chest, 1), Character.valueOf('M'),
								IC2Items.getItem("advancedMachine"), Character.valueOf('T'),
								IC2Items.getItem("teleporter") });
				Recipes.advRecipes.addRecipe(new ItemStack(tditft, 1),
						new Object[] { " CC", "IAI", "   ", Character.valueOf('I'), IC2Items.getItem("casingiron"),
								Character.valueOf('C'), IC2Items.getItem("copperCableItem"), Character.valueOf('A'),
								IC2Items.getItem("advancedCircuit") });
			}
			GameRegistry.addRecipe(new ItemStack(boneBlock, 1),
					new Object[] { "XXX", "XYX", "XXX", 'X', Items.bone, 'Y', Items.iron_ingot });
			GameRegistry.addRecipe(new ItemStack(boneBlock, 1),
					new Object[] { "XZX", "XYX", "XZX", 'X', Items.bone, 'Y', Items.iron_ingot, 'Z', ihlSkull });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 15), new Object[] { ihlSkull });
			FurnaceRecipes.smelting().func_151394_a(IHLUtils.getThisModItemStack("bucket_SpruceResin"),
					IHLUtils.getThisModItemStack("bucket_tarPitch"), 20F);
			ItemStack nuggetTarPitch = OreDictionary.getOres("nuggetTarPitch").get(0).copy();
			GameRegistry.addShapelessRecipe(OreDictionary.getOres("ingotTarPitch").get(0).copy(),
					new Object[] { nuggetTarPitch, nuggetTarPitch, nuggetTarPitch, nuggetTarPitch, nuggetTarPitch,
							nuggetTarPitch, nuggetTarPitch, nuggetTarPitch, nuggetTarPitch });
			if (IHLUtils.hasOreDictionaryEntry("brickPeat")
					&& GameRegistry.findItem("Forestry", "bituminousPeat") != null)
				GameRegistry.addRecipe(new ItemStack(GameRegistry.findItem("Forestry", "bituminousPeat"), 5),
						new Object[] { "BBB", "BPB", "BBB", 'B', IHLUtils.getOreDictItemStack("brickPeat"), 'P',
								OreDictionary.getOres("ingotTarPitch").get(0) });
			pfaalimestone = IHLUtils.getOtherModItemStackWithDamage("PFAAGeologica", "mediumStone", 0, 1);
			pfaacobblelimestone = IHLUtils.getOtherModItemStackWithDamage("PFAAGeologica", "mediumCobble", 0, 1);
			isGregTechModLoaded = Loader.isModLoaded("gregtech");
			if (isGregTechModLoaded) {
				try {
					Class.forName("gregapi.GT_API");
				} catch (ClassNotFoundException e) {
					isGT_API_Version_5 = true;
				}
				if (isGT_API_Version_5) {
					this.loadGT5Recipes();
				} else {
					this.loadGT6Recipes();
				}
			} else {
				this.loadIC2Recipes();
			}
			IHLUtils.addIC2RollingRecipe("ingotTungsten", IHLUtils.getOreDictItemStack("plateTungsten"));
			IHLUtils.addIC2CentrifugeRecipe("crushedGyubnera", IHLUtils.getThisModItemStack("crushedPurifiedGyubnera"),
					IHLUtils.getThisModItemStack("dustPotassiumFeldspar"));
			IHLUtils.addIC2CentrifugeRecipe("crushedCinnabar", IHLUtils.getThisModItemStack("crushedPurifiedCinnabar"),
					IHLUtils.getThisModItemStack("dustPotassiumFeldspar"));
			IHLUtils.addIC2CentrifugeRecipe("crushedBauxite", IHLUtils.getThisModItemStack("crushedPurifiedBauxite"),
					IHLUtils.getOreDictItemStack("dustClay"));
			IHLUtils.addIC2CentrifugeRecipe("crushedStibnite", IHLUtils.getThisModItemStack("crushedPurifiedStibnite"),
					IHLUtils.getOreDictItemStack("dustTinyGold"));
			IHLUtils.addIC2MaceratorRecipe("logWood", IHLUtils.getThisModItemStack("dustWood"));
			IHLUtils.addIC2MaceratorRecipe("ingotPotassium", IHLUtils.getThisModItemStack("dustPotassiumOxide"));
			if (IHLUtils.hasOreDictionaryEntry("blockLimestone"))
				IHLUtils.addIC2MaceratorRecipe("blockLimestone",
						IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4));
			if (pfaalimestone != null)
				IHLUtils.addIC2MaceratorRecipe(pfaalimestone, IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4));
			if (pfaacobblelimestone != null)
				IHLUtils.addIC2MaceratorRecipe(pfaacobblelimestone,
						IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4));
			IHLUtils.addIC2MaceratorRecipe("oreGypsum", IHLUtils.getOreDictItemStack("dustGypsum"));
			IHLUtils.addIC2MaceratorRecipe("orePotassiumFeldspar",
					IHLUtils.getOreDictItemStack("dustPotassiumFeldspar"));
			IHLUtils.addIC2MaceratorRecipe("oreLimestone", IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4));
			IHLUtils.addIC2MaceratorRecipe("oreSaltpeter", IHLUtils.getOreDictItemStackWithSize("dustSaltpeter", 4));
			IHLUtils.addIC2MaceratorRecipe("oreGyubnera", IHLUtils.getThisModItemStackWithSize("crushedGyubnera", 2));
			IHLUtils.addIC2MaceratorRecipe("oreCinnabar", IHLUtils.getThisModItemStackWithSize("crushedCinnabar", 2));
			IHLUtils.addIC2MaceratorRecipe("oreBauxite", IHLUtils.getThisModItemStackWithSize("crushedBauxite", 2));
			IHLUtils.addIC2MaceratorRecipe("crushedPurifiedBauxite", IHLUtils.getThisModItemStack("dustBauxite"));
			IHLUtils.addIC2MaceratorRecipe("oreStibnite", IHLUtils.getThisModItemStackWithSize("crushedStibnite", 2));
			IHLUtils.addIC2MaceratorRecipe("crushedPurifiedStibnite", IHLUtils.getThisModItemStack("dustStibnite"));
			IHLUtils.addIC2MaceratorRecipe("blockGlass", IHLUtils.getOreDictItemStackWithSize("dustGlass", 1));
			IHLUtils.addIC2MaceratorRecipe("stickGraphite", 2, IHLUtils.getThisModItemStack("dustGraphite"));
			IHLUtils.addIC2MaceratorRecipe("oreMica", IHLUtils.getThisModItemStackWithSize("dustMica", 4));
			IHLUtils.addIC2MaceratorRecipe("oreTrona", IHLUtils.getThisModItemStackWithSize("dustTrona", 4));
			IHLUtils.addIC2MaceratorRecipe(Ic2Items.iridiumOre, IHLUtils.getThisModItemStack("dustIridium"));
			IHLUtils.addIC2MaceratorRecipe("oreDatolite", IHLUtils.getThisModItemStackWithSize("dustDatolite", 4));
			IHLUtils.addIC2MaceratorRecipe("oreBischofite", IHLUtils.getThisModItemStackWithSize("dustBischofite", 4));
			IHLUtils.addIC2MaceratorRecipe("ingotBrick", IHLUtils.getOreDictItemStackWithSize("dustBrick", 1));
			Recipes.advRecipes.addShapelessRecipe(IHLUtils.getThisModItemStackWithSize("dustIrongraphite", 2),
					new Object[] { new RecipeInputOreDict("dustIron"), new RecipeInputOreDict("dustGraphite") });
			Recipes.advRecipes.addShapelessRecipe(IHLUtils.getOreDictItemStackWithSize("dustPorcelain", 4),
					new Object[] { new RecipeInputOreDict("dustClay"), new RecipeInputOreDict("dustClay"),
							new RecipeInputOreDict("dustSiliconDioxide"),
							new RecipeInputOreDict("dustPotassiumFeldspar") });
			Recipes.advRecipes.addShapelessRecipe(IHLUtils.getThisModItemStack("chisel"), new Object[] {
					IHLUtils.getThisModItemStack("grindstone"), IHLUtils.getThisModItemStack("blankChiselSteel") });
			Recipes.advRecipes.addShapelessRecipe(IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze"),
					new Object[] { IHLUtils.getThisModItemStack("grindstone"),
							IHLUtils.getThisModItemStack("carvingKnifeBronze") });
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStack("hammer"),
					new Object[] { "H  ", "SC ", "   ", Character.valueOf('H'),
							IHLUtils.getThisModItemStack("toolHeadHammerSmallSteel"), Character.valueOf('S'),
							new ItemStack(Items.stick), Character.valueOf('C'),
							new RecipeInputOreDict("craftingToolKnife") });
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStackWithSize("grindstone", 2),
					new Object[] { " H ", " Q ", "   ", Character.valueOf('H'),
							new RecipeInputOreDict("craftingToolForgeHammer"), Character.valueOf('Q'),
							new RecipeInputOreDict("blockQuartz") });
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStackWithSize("grindstone", 2),
					new Object[] { " H ", " Q ", "   ", Character.valueOf('H'),
							new RecipeInputOreDict("craftingToolHardHammer"), Character.valueOf('Q'),
							new RecipeInputOreDict("blockQuartz") });
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStackWithSize("blankNeedleFileSteel", 64),
					new Object[] { " H ", " C ", " P ", Character.valueOf('H'),
							new RecipeInputOreDict("craftingToolForgeHammer"), Character.valueOf('C'),
							new RecipeInputOreDict("craftingToolChisel"), Character.valueOf('P'),
							IHLUtils.getThisModItemStack("plateHotSteel") });
			Recipes.metalformerExtruding.addRecipe(new RecipeInputOreDict("dustIrongraphite"), null,
					IHLUtils.getThisModItemStackWithSize("linerIronGraphite", 4));
			Recipes.compressor.addRecipe(new RecipeInputOreDict("foilMica", 4), null,
					IHLUtils.getThisModItemStack("plateMica"));
			Recipes.metalformerExtruding.addRecipe(new RecipeInputOreDict("dustBoronCarbide"), null,
					IHLUtils.getThisModItemStackWithSize("stickBoronCarbide", 2));
			Recipes.metalformerRolling.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("dustCoalElectrodePremix")), null,
					IHLUtils.getThisModItemStack("plateRawCoal"));
			Recipes.metalformerExtruding.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("foilRubberWithSulfur")), null,
					IHLUtils.getThisModItemStack("batteryCellsEbonite"));
			GameRegistry.addShapelessRecipe(IHLUtils.getThisModItemStack("dustAntimony"), new Object[] {
					IHLUtils.getThisModItemStack("dustTinyAntimony"), IHLUtils.getThisModItemStack("dustTinyAntimony"),
					IHLUtils.getThisModItemStack("dustTinyAntimony"), IHLUtils.getThisModItemStack("dustTinyAntimony"),
					IHLUtils.getThisModItemStack("dustTinyAntimony"), IHLUtils.getThisModItemStack("dustTinyAntimony"),
					IHLUtils.getThisModItemStack("dustTinyAntimony"), IHLUtils.getThisModItemStack("dustTinyAntimony"),
					IHLUtils.getThisModItemStack("dustTinyAntimony") });
			GameRegistry.addShapelessRecipe(IHLUtils.getThisModItemStackWithSize("dustTinyAntimony", 9),
					new Object[] { IHLUtils.getThisModItemStack("dustAntimony") });
			GameRegistry.addShapelessRecipe(IHLUtils.getThisModItemStack("dustSilicon"), new Object[] {
					IHLUtils.getThisModItemStack("dustTinySilicon"), IHLUtils.getThisModItemStack("dustTinySilicon"),
					IHLUtils.getThisModItemStack("dustTinySilicon"), IHLUtils.getThisModItemStack("dustTinySilicon"),
					IHLUtils.getThisModItemStack("dustTinySilicon"), IHLUtils.getThisModItemStack("dustTinySilicon"),
					IHLUtils.getThisModItemStack("dustTinySilicon"), IHLUtils.getThisModItemStack("dustTinySilicon"),
					IHLUtils.getThisModItemStack("dustTinySilicon") });
			GameRegistry.addShapelessRecipe(IHLUtils.getThisModItemStackWithSize("dustTinySilicon", 9),
					new Object[] { IHLUtils.getThisModItemStack("dustSilicon") });
			GameRegistry.addShapelessRecipe(IHLUtils.getThisModItemStack("dustSolderingAlloy"), new Object[] {
					IHLUtils.getOreDictItemStack("dustTin"), IHLUtils.getThisModItemStack("dustTinyAntimony") });
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStack("achesonFurnance"),
					new Object[] { "BPB", "S S", "B B", Character.valueOf('B'), new RecipeInputOreDict("ingotBrick"),
							Character.valueOf('P'), new RecipeInputOreDict("plateSteel"), Character.valueOf('S'),
							new RecipeInputOreDict("stickCoal") });
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStack("muffleFurnance"),
					new Object[] { "BCB", "SDP", "BSB", Character.valueOf('B'), new RecipeInputOreDict("ingotBrick"),
							Character.valueOf('P'), new RecipeInputOreDict("plateSteel"), Character.valueOf('C'),
							IHLUtils.getThisModItemStack("crucibleMixture"), Character.valueOf('S'),
							new RecipeInputOreDict("stickGraphite"), Character.valueOf('D'),
							new RecipeInputOreDict("dustCarborundum") });

			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 64),
					new Object[] { "VFS", "DR ", "WT ", Character.valueOf('V'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("viseSteel")), Character.valueOf('F'),
							new RecipeInputOreDict("craftingToolFile"), Character.valueOf('S'),
							new RecipeInputOreDict("stickSteel"), Character.valueOf('D'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("handDrillBronze")),
							Character.valueOf('R'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("drillSteelHardened")),
							Character.valueOf('W'), new RecipeInputOreDict("craftingToolSaw"), Character.valueOf('T'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("tapM10x1SteelHardened")) });

			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 8),
					new Object[] { "VFS", "DR ", "W  ", Character.valueOf('V'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("viseSteel")), Character.valueOf('F'),
							new RecipeInputOreDict("craftingToolFile"), Character.valueOf('S'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("barD10Steel")),
							Character.valueOf('D'), new RecipeInputOreDict("craftingToolHardHammer"),
							Character.valueOf('R'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("diceM10x1SteelHardened")),
							Character.valueOf('W'), new RecipeInputOreDict("craftingToolSaw") });

			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStackWithSize("drillSteel", 2),
					new Object[] { "VFS", "DR ", "   ", Character.valueOf('V'),
							new RecipeInputOreDict("craftingToolHardHammer"), Character.valueOf('F'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("viseSteel")), Character.valueOf('S'),
							new RecipeInputOreDict("craftingToolFile"), Character.valueOf('D'),
							new RecipeInputOreDict("craftingToolChisel"), Character.valueOf('R'),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("barD10SteelHot")) });

			ItemStack crystal = IC2Items.getItem("lapotronCrystal").copy();
			ItemStack advBattery = IC2Items.getItem("advBattery").copy();
			ItemStack chargedReBattery = IC2Items.getItem("chargedReBattery").copy();
			crystal.setItemDamage(OreDictionary.WILDCARD_VALUE);
			advBattery.setItemDamage(OreDictionary.WILDCARD_VALUE);
			chargedReBattery.setItemDamage(OreDictionary.WILDCARD_VALUE);

			if (IHLMod.config.enableHandpump) {
				Recipes.advRecipes.addRecipe(((IHLHandPump) ic2_handpump).getItemStack(0),
						new Object[] { "T  ", " C ", "  P", Character.valueOf('T'), IC2Items.getItem("treetap"),
								Character.valueOf('C'), IC2Items.getItem("cell"), Character.valueOf('P'),
								IC2Items.getItem("powerunitsmall") });
				Recipes.advRecipes.addRecipe(((AdvancedHandPump) ic2_advanced_handpump).getItemStack(0),
						new Object[] { "TK ", "KCA", " RP", Character.valueOf('T'), IC2Items.getItem("treetap"),
								Character.valueOf('K'), IC2Items.getItem("advancedAlloy"), Character.valueOf('C'),
								IC2Items.getItem("cell"), Character.valueOf('A'), IC2Items.getItem("advancedCircuit"),
								Character.valueOf('R'), IC2Items.getItem("energyCrystal"), Character.valueOf('P'),
								IC2Items.getItem("powerunitsmall") });
			}
			ItemStack forestryWaxCapsule = IHLUtils.getOtherModItemStackWithDamage("Forestry", "waxCapsule", 0, 1);
			ItemStack forestryWaxCast = IHLUtils.getOtherModItemStackWithDamage("Forestry", "waxCast", 0, 1);
			ItemStack forestryCandle = IHLUtils.getOtherModItemStackWithDamage("Forestry", "candle", 0, 1);
			ItemStack forestryBeesWax = IHLUtils.getOtherModItemStackWithDamage("Forestry", "beeswax", 0, 1);
			if (forestryWaxCapsule != null) {
				Recipes.advRecipes.addRecipe(forestryWaxCapsule,
						new Object[] { "SBS", "   ", "   ", Character.valueOf('S'),
								IHLUtils.getThisModItemStack("ingotStearin"), Character.valueOf('B'),
								forestryBeesWax });
			}
			if (forestryCandle != null) {
				Recipes.advRecipes.addRecipe(forestryCandle,
						new Object[] { " S ", " I ", "   ", Character.valueOf('S'), new ItemStack(Items.string),
								Character.valueOf('I'), IHLUtils.getThisModItemStack("ingotStearin") });
			}
			if (forestryWaxCast != null) {
				Recipes.advRecipes.addRecipe(forestryWaxCast,
						new Object[] { "BSS", "S S", "SSB", Character.valueOf('S'),
								IHLUtils.getThisModItemStack("ingotStearin"), Character.valueOf('B'),
								forestryBeesWax });
			}
			Iterator<ItemStack> odi = OreDictionary.getOres("dustGypsum").iterator();
			while (odi.hasNext()) {
				ItemStack stack = odi.next().copy();
				stack.stackSize = 2;
				FurnaceRecipes.smelting().func_151394_a(stack, IHLUtils.getThisModItemStack("dustDehydratedGypsum"),
						20F);
			}
			Recipes.FluidHeatGenerator.addFluid("oleicacid", 10, 16);
			Recipes.FluidHeatGenerator.addFluid("mineraloil", 10, 16);
			Recipes.FluidHeatGenerator.addFluid("acetylene", 10, 64);
			Recipes.FluidHeatGenerator.addFluid("glycerol", 10, 16);
			Recipes.FluidHeatGenerator.addFluid("turpentine", 10, 16);
			Recipes.FluidHeatGenerator.addFluid("fueloil", 10, 32);
			Recipes.FluidHeatGenerator.addFluid("crackinggas", 10, 32);
			Recipes.semiFluidGenerator.addFluid("hydrogen", 10, 32);
			Recipes.semiFluidGenerator.addFluid("crackinggas", 10, 32);
			Recipes.advRecipes.addShapelessRecipe(IHLUtils.getThisModItemStackWithSize("breadWithLard", 4),
					new Object[] { new ItemStack(Items.bread), IHLUtils.getThisModItemStack("muttonLard") });
			Recipes.advRecipes.addShapelessRecipe(IHLUtils.getThisModItemStack("dustCoalElectrodePremix"),
					new Object[] { Ic2Items.coalDust, IHLUtils.getOreDictItemStack("nuggetTarPitch") });
			Recipes.metalformerExtruding.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("dustCoalElectrodePremix")), null,
					IHLUtils.getThisModItemStack("stickCoalElectrodePremix"));
			FurnaceRecipes.smelting().func_151394_a(IHLUtils.getThisModItemStack("stickCoalElectrodePremix"),
					IHLUtils.getOreDictItemStack("stickCoal"), 20F);
			FurnaceRecipes.smelting().func_151394_a(IHLUtils.getThisModItemStack("plateRawCoal"),
					IHLUtils.getOreDictItemStack("plateCoal"), 20F);
			FurnaceRecipes.smelting().func_151394_a(IHLUtils.getOreDictItemStackWithSize("dustCalcite", 3),
					IHLUtils.getOreDictItemStack("dustQuicklime"), 2F);
			FurnaceRecipes.smelting().func_151394_a(IHLUtils.getThisModItemStack("catalystRawIronOxide"),
					IHLUtils.getThisModItemStack("catalystIronOxide"), 2F);
			ItemSubstance.postInit();
			ItemStack lathingTool = Ic2Items.LathingTool.copy();
			lathingTool.setItemDamage(OreDictionary.WILDCARD_VALUE);
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new IRecipeInput[] { new RecipeInputDie("setOfDies1_5sqmm", 240) }),
					Arrays.asList(new ItemStack[] { Ic2Items.copperCableItem }),
					Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 240) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new IRecipeInput[] { new RecipeInputDie("setOfDies1_5sqmm", 240) }),
					Arrays.asList(new ItemStack[] { Ic2Items.ironCableItem }),
					Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 240) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, Arrays.asList(
					new ItemStack[] { IHLUtils.getThisModItemStack("bucket_tarPitch"), new ItemStack(Items.stick) }),
					Arrays.asList(new ItemStack[] { new ItemStack(Items.bucket),
							IHLUtils.getOreDictItemStackWithSize("nuggetTarPitch", 4), new ItemStack(Blocks.torch) })));
			List<IRecipeInput> materialList3 = new ArrayList<IRecipeInput>();
			materialList3.add(new RecipeInputItemStack(new ItemStack(Blocks.sand, 1, OreDictionary.WILDCARD_VALUE)));
			materialList3.add(new RecipeInputOreDict("dustClay"));
			materialList3.add(new RecipeInputFluidContainer(FluidRegistry.WATER, 1));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays
					.asList(new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("ingot") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays
					.asList(new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("tub") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(
					new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("blankChisel") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(
					new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("carvingKnife") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(new ItemStack[] {
							InjectionMoldBlock.instance.getSandInjectionMoldForResult("toolHeadHammerSmall") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays
					.asList(new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("barD10") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays
					.asList(new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("stick") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(new ItemStack[] {
							InjectionMoldBlock.instance.getSandInjectionMoldForResult("setOfPartsForLVElemotor") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(new ItemStack[] {
							InjectionMoldBlock.instance.getSandInjectionMoldForResult("pipelineAccessories") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(new ItemStack[] {
							InjectionMoldBlock.instance.getSandInjectionMoldForResult("highPressureVessel") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays
					.asList(new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("gasJet") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(new ItemStack[] {
							InjectionMoldBlock.instance.getSandInjectionMoldForResult("handDrillSetOfMoldedParts") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3,
					Arrays.asList(new ItemStack[] { InjectionMoldBlock.instance
							.getSandInjectionMoldForResult("detonationSprayingMachineSetOfMoldedParts") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays.asList(new ItemStack[] {
							InjectionMoldBlock.instance.getSandInjectionMoldForResult("viseSetOfMoldedParts") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null, materialList3, Arrays
					.asList(new ItemStack[] { InjectionMoldBlock.instance.getSandInjectionMoldForResult("nozzle") })));
			// Sand molds recipe section end
			List<IRecipeInput> materialsForPrecisionMold = Arrays
					.asList(new IRecipeInput[] { new RecipeInputItemStack(IHLUtils.getThisModItemStack("ingotStearin")),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("liquidglass"), 1),
							new RecipeInputOreDict("dustSiliconDioxide"),
							new RecipeInputItemStack(new ItemStack(Blocks.sand)) });
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					materialsForPrecisionMold, Arrays.asList(new ItemStack[] { InjectionMoldBlock.instance
							.getGypsumInjectionMoldForResult("rollingMachineSetOfMoldedParts") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					materialsForPrecisionMold, Arrays.asList(new ItemStack[] {
							InjectionMoldBlock.instance.getGypsumInjectionMoldForResult("polishingPucks") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					materialsForPrecisionMold, Arrays.asList(new ItemStack[] { InjectionMoldBlock.instance
							.getGypsumInjectionMoldForResult("turboCompressorSetOfMoldedParts") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					materialsForPrecisionMold, Arrays.asList(new ItemStack[] { InjectionMoldBlock.instance
							.getGypsumInjectionMoldForResult("extruderSetOfMoldedParts") })));
			// Precision molds recipe section end
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("hammer"),
							IHLUtils.getThisModItemStack("chisel"), IHLUtils.getThisModItemStack("needleFile") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("barD10SteelHot") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("sawBladeSteel", 2) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(
							new ItemStack[] { IHLUtils.getThisModItemStack("hammer"),
									IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze"),
									IHLUtils.getThisModItemStack("needleFile") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("barD10Steel"),
							IHLUtils.getThisModItemStack("sawBladeSteelHardened"), new ItemStack(Items.stick) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("hackSawSteel") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("needleFile"),
							IHLUtils.getThisModItemStack("hackSawSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("plateSteel") }), Arrays.asList(
							new ItemStack[] { IHLUtils.getThisModItemStackWithSize("blankSetOfFilesSteel", 4) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("viseSetOfMoldedPartsSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("viseSteel") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("viseSteel"), IHLUtils.getThisModItemStack("setOfFilesSteel"),
					IHLUtils.getThisModItemStack("handDrillBronze"), IHLUtils.getThisModItemStack("hackSawSteel"),
					IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("tapM10x1SteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("stickSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("diceM10x1Steel", 16) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("viseSteel"),
							IHLUtils.getThisModItemStack("setOfFilesSteel"), IHLUtils.getThisModItemStack("needleFile"),
							IHLUtils.getThisModItemStack("hackSawSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("barD10Steel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("tapM10x1Steel", 2) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new IRecipeInput[] {new RecipeInputOreDict("craftingToolSaw") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tubBronze") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("solarEvaporator") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("handDrillBronze"), IHLUtils.getThisModItemStack("hackSawSteel"),
					IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("diceM10x1SteelHardened"),
					IHLUtils.getThisModItemStack("tapM10x1SteelHardened"), IHLUtils.getThisModItemStack("viseSteel"),
					IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 16),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 12),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 16),
							IHLUtils.getOreDictItemStackWithSize("plateSteel", 4),
							IHLUtils.getOreDictItemStack("stickSteel"), new ItemStack(Items.leather),
							new ItemStack(Items.string) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("lathePart1"),
							IHLUtils.getThisModItemStack("lathePart2") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("handDrillBronze"), IHLUtils.getThisModItemStack("hackSawSteel"),
					IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("tapM10x1SteelHardened"), IHLUtils.getThisModItemStack("viseSteel"),
					IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 16),
							IHLUtils.getOreDictItemStack("plateSteel"),
							IHLUtils.getThisModItemStack("extruderSetOfMoldedPartsSteel"),
							IHLUtils.getThisModItemStack("meshGlass"), IHLUtils.getThisModItemStack("dustCarborundum"),
							new ItemStack(Items.leather), new ItemStack(Items.string) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("extruder"),
							IHLUtils.getThisModItemStack("vulcanizationExtrudingMold") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(
					new ItemStack[] { IHLUtils.getThisModItemStack("chisel"), IHLUtils.getThisModItemStack("hammer") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("barD10SteelHot") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("nailSteel", 8) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("hammer"), IHLUtils.getThisModItemStack("handDrillBronze"),
					IHLUtils.getThisModItemStack("drillSteelHardened"), IHLUtils.getThisModItemStack("hackSawSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("barD10Steel"),
							IHLUtils.getThisModItemStackWithSize("nailSteel", 16), new ItemStack(Blocks.planks),
							new ItemStack(Items.leather), new ItemStack(Items.string),
							IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 8),
							IHLUtils.getOreDictItemStack("plateSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("coiler") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("viseSteel"),
							IHLUtils.getThisModItemStack("setOfFilesSteel"), IHLUtils.getThisModItemStack("hammer"),
							IHLUtils.getThisModItemStack("diceM10x1SteelHardened"),
							IHLUtils.getThisModItemStack("hackSawSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("barD10Steel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 8) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("viseSteel"),
							IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("handDrillSetOfMoldedPartsBronze"),
							new ItemStack(Items.stick) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("handDrillBronze") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new RecipeInputOreDict[] { new RecipeInputOreDict("craftingToolFile") }),
					Arrays.asList(new IRecipeInput[] {
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("boltM10x1Steel"), 16),
							new RecipeInputOreDict("dustCarborundum"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("crucibleMixture")),
							new RecipeInputOreDict("stickGraphite"), new RecipeInputOreDict("plateSteel"),
							new RecipeInputOreDict("stickSteel", 4),
							new RecipeInputItemStack(
									IHLUtils.getThisModItemStackWithSize("linerIronGraphiteGreased", 16)) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("wireMill") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("lathePart1") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfPartsForLVElemotorSteel"),
							IHLUtils.getThisModItemStack("ingotStearin"), IHLUtils.getUninsulatedWire("Copper", 4, 15),
							new ItemStack(Items.paper), new ItemStack(Blocks.planks),
							IHLUtils.getThisModItemStackWithSize("linerIronGraphiteGreased", 2) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("electricMotorLVLEDC") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new IRecipeInput[] {
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("setOfPartsForLVElemotorSteel")),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("spruceresin"), 1),
							new RecipeInputWire("Copper", 4, 15), new RecipeInputItemStack(new ItemStack(Items.paper)),
							new RecipeInputItemStack(new ItemStack(Blocks.planks, 1, OreDictionary.WILDCARD_VALUE)),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("linerIronGraphiteGreased"), 2) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("electricMotorLVLEDC") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteHot"),
							IHLUtils.getThisModItemStack("muttonLard") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteGreased") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(
							new IRecipeInput[] { new RecipeInputOreDict("dustBrick", 7),
									new RecipeInputOreDict("dustClay", 3), new RecipeInputOreDict("dustGraphite", 2),
									new RecipeInputFluidContainer(FluidRegistry.WATER, 1) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("crucibleMixture", 12) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("crucibleMixture") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("crucible") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("viseSteel"), IHLUtils.getThisModItemStack("setOfFilesSteel"),
					IHLUtils.getThisModItemStack("handDrillBronze"), IHLUtils.getThisModItemStack("hackSawSteel"),
					IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("tapM10x1SteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("stickSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 64) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("hammer"), IHLUtils.getThisModItemStack("handDrillBronze"),
					IHLUtils.getThisModItemStack("hackSawSteel"), IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("diceM10x1SteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("barD10Steel", 4),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 8), new ItemStack(Items.leather),
							new ItemStack(Items.stick, 8), IHLUtils.getThisModItemStackWithSize("nailSteel", 8) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("loom") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("hammer") }),
					Arrays.asList(new ItemStack[] {
							IHLUtils.getThisModItemStack("detonationSprayingMachineSetOfMoldedPartsBronze"),
							new ItemStack(Blocks.planks), IHLUtils.getThisModItemStackWithSize("nailSteel", 8) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("cannonBronze") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("hammer"), lathingTool,
					IHLUtils.getThisModItemStack("setOfFilesSteel"), IHLUtils.getThisModItemStack("tinSnipsSteel"),
					IHLUtils.getThisModItemStack("handDrillBronze"), IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("diceM10x1SteelHardened"),
					IHLUtils.getThisModItemStack("tapM10x1SteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("ingotHotSteel"),
							IHLUtils.getThisModItemStack("foilRubber"), IHLUtils.getThisModItemStack("springSteel"),
							IHLUtils.getThisModItemStack("barD10Steel"),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 6),
							IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 6) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasReducerSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("lathePart1") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("hammer"), lathingTool }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("ingotHotSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("pistonCylinderSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("lathePart1") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 240),
							new ItemStack(Items.stick) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("springSteel", 4) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfFilesSteel"),
							IHLUtils.getThisModItemStack("hammer"), IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("barD10SteelHot", 2),
							IHLUtils.getThisModItemStack("springSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("hammer"), IHLUtils.getThisModItemStack("tinSnipsSteel"),
					IHLUtils.getThisModItemStack("handDrillBronze"), IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("diceM10x1SteelHardened"),
					IHLUtils.getThisModItemStack("tapM10x1SteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("foilRubber"),
							IHLUtils.getThisModItemStackWithSize("gasReducerSteel", 2),
							IHLUtils.getThisModItemStack("gasJetSteel"),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 10),
							IHLUtils.getThisModItemStackWithSize("highPressureVesselSteel", 2),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 32),
							IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 32),
							IHLUtils.getThisModItemStack("pipeVulcanizedRubber") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfFilesSteel"),
							IHLUtils.getThisModItemStack("tinSnipsSteel"), IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened"), lathingTool }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("pistonCylinderSteel"),
							IHLUtils.getOreDictItemStackWithSize("plateSteel", 2),
							IHLUtils.getThisModItemStack("pipelineAccessoriesSteel"),
							IHLUtils.getThisModItemStack("foilRubber"),
							IHLUtils.getThisModItemStackWithSize("stickSteel", 2),
							IHLUtils.getThisModItemStackWithSize("linerIronGraphiteGreased", 4),
							IHLUtils.getThisModItemStackWithSize("highPressureVesselSteel", 2) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("cryogenicDistiller") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("lathePart1") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfFilesSteel"),
							IHLUtils.getThisModItemStack("tinSnipsSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("meshGlass"),
							IHLUtils.getThisModItemStack("dustCarborundum"),
							IHLUtils.getThisModItemStack("pipelineAccessoriesSteel"),
							IHLUtils.getThisModItemStack("foilRubber"),
							IHLUtils.getThisModItemStack("highPressureVesselSteel"),
							IHLUtils.getOreDictItemStack("stickSteel"), IHLUtils.getOreDictItemStack("plateSteel"),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("chemicalReactor") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(
					new IronWorkbenchRecipe(
							Arrays.asList(
									new IRecipeInput[] {
											new RecipeInputItemStack(IHLUtils.getThisModItemStack("handDrillBronze")),
											new RecipeInputItemStack(
													IHLUtils.getThisModItemStack("drillSteelHardened")) }),
							Arrays.asList(new IRecipeInput[] {
									new RecipeInputItemStack(IHLUtils.getThisModItemStack("electricMotorLVLEDC")),
									new RecipeInputItemStack(IHLUtils.getThisModItemStack("pipelineAccessoriesSteel")),
									new RecipeInputOreDict("foilRubber"),
									new RecipeInputItemStack(IHLUtils.getThisModItemStack("highPressureVesselSteel")),
									new RecipeInputItemStack(IHLUtils.getThisModItemStack("pistonCylinderSteel")),
									new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 8)),
									new RecipeInputItemStack(
											IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 8)) }),
							Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("dosingPump") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(new ItemStack[] {
					IHLUtils.getThisModItemStack("hammer"), IHLUtils.getThisModItemStack("handDrillBronze"),
					IHLUtils.getThisModItemStack("hackSawSteel"), IHLUtils.getThisModItemStack("drillSteelHardened"),
					IHLUtils.getThisModItemStack("diceM10x1SteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("barD10SteelHot", 7),
							IHLUtils.getOreDictItemStack("plateSteel"), new ItemStack(Items.leather),
							IHLUtils.getThisModItemStackWithSize("nailSteel", 8), new ItemStack(Blocks.log) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("woodenRollingMachinePart1"),
							IHLUtils.getThisModItemStack("woodenRollingMachinePart2") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("plateSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("fiberGlassDieSteel") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("drillSteelHardened"),
							IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("plateSteel") }),
					Arrays.asList(new ItemStack[] {
							IHLUtils.getItemStackWithTag("setOfDies1_5sqmm", "transverseSection", 240) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("drillSteelHardened"),
							IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("plateSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfDies1_5sqmm") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("drillSteelHardened"),
							IHLUtils.getThisModItemStack("tapM10x1SteelHardened"),
							IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 8),
							IHLUtils.getOreDictItemStackWithSize("plateSteel", 4),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4),
							IHLUtils.getThisModItemStack("rollingMachineSetOfMoldedPartsSteel"),
							IHLUtils.getThisModItemStackWithSize("linerIronGraphiteGreased", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("rollingMachinePart1") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("drillSteelHardened"),
							IHLUtils.getThisModItemStack("tapM10x1SteelHardened"),
							IHLUtils.getThisModItemStack("setOfFilesSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 8),
							IHLUtils.getOreDictItemStackWithSize("plateSteel", 4),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("rollingMachinePart2") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputFluidContainer(FluidRegistry.WATER, 1),
							new RecipeInputOreDict("dustPorcelain") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("electrolysisBathRawPorcelain") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputFluidContainer(FluidRegistry.WATER, 1),
							new RecipeInputOreDict("dustPorcelain") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("ovenRawPorcelain") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputFluidContainer(FluidRegistry.WATER, 1),
							new RecipeInputOreDict("dustPorcelain") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gaedesPumpBarrelRawPorcelain") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputFluidContainer(FluidRegistry.WATER, 1),
							new RecipeInputOreDict("dustPorcelain") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("ringRawPorcelain", 4) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputFluidContainer(FluidRegistry.WATER, 1),
							new RecipeInputOreDict("dustPorcelain") }),
					Arrays.asList(
							new ItemStack[] { IHLUtils.getThisModItemStackWithSize("lampHolderRawPorcelain", 4) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputFluidContainer(FluidRegistry.WATER, 1),
							new RecipeInputOreDict("dustPorcelain") }),
					Arrays.asList(
							new ItemStack[] { IHLUtils.getThisModItemStackWithSize("insulatorRawPorcelain", 4) })));
			// Raw porcelain recipes section end
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfFilesSteel"),
							IHLUtils.getThisModItemStack("tinSnipsSteel"), IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("foilSteel", 4),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4),
							IHLUtils.getThisModItemStack("pipelineAccessoriesSteel"),
							IHLUtils.getThisModItemStack("foilRubber"), IHLUtils.getOreDictItemStack("plateSteel"),
							IHLUtils.getThisModItemStack("turboCompressorSetOfMoldedPartsBronze"),
							IHLUtils.getThisModItemStack("highPressureVesselSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("fluidizedBedReactor") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("setOfFilesSteel"),
							IHLUtils.getThisModItemStack("tinSnipsSteel"), IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new IRecipeInput[] {
							new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("pistonCylinderSteel", 2)),
							new RecipeInputOreDict("foilSteel"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("pipelineAccessoriesSteel")),
							new RecipeInputOreDict("foilRubber"), new RecipeInputOreDict("plateSteel"),
							new RecipeInputItemStack(
									IHLUtils.getThisModItemStackWithSize("highPressureVesselSteel", 2)),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("electrolysisBathPorcelain")),
							new RecipeInputOreDict("stickGraphite", 2) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("labElectrolyzer") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("glassBlowingTubeSteel") }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("foilSteel"),
							new RecipeInputOreDict("foilTungsten"), new RecipeInputOreDict("dustQuicklime"),
							new RecipeInputOreDict("dustGlass"), new RecipeInputOreDict("stickGraphite"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("barD10Steel")) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("gu-81m", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation"),
							IHLUtils.getThisModItemStack("gaedesMercuryRotaryPump") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("foilSteel"),
							IHLUtils.getThisModItemStack("barD10Steel"), new ItemStack(Items.stick) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("glassBlowingTubeSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("foilSteel"),
							new RecipeInputOreDict("plateSteel"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("barD10Steel"), 4),
							new RecipeInputOreDict("foilRubber"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("gaedesPumpBarrelPorcelain")),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("mercury"), 1) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gaedesMercuryRotaryPump") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(
							Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") }),
							Arrays.asList(new IRecipeInput[] {
									new RecipeInputFluidContainer(FluidRegistry.getFluid("mineraloil"), 1),
									new RecipeInputOreDict("foilMica"), new RecipeInputOreDict("foilCopper"),
									new RecipeInputOreDict("foilSteel") }),
							Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("highVoltageCapacitor") })));
			IronWorkbenchTileEntity
					.addRecipe(new IronWorkbenchRecipe(
							Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") }),
							Arrays.asList(new IRecipeInput[] {
									new RecipeInputFluidContainer(FluidRegistry.getFluid("mineraloil"), 1),
									new RecipeInputOreDict("foilMica"), new RecipeInputOreDict("foilGold"),
									new RecipeInputOreDict("foilSteel") }),
							Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("highVoltageCapacitor") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("ringPorcelain", 64),
							IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 6),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 6),
							IHLUtils.getOreDictItemStack("foilSteel"),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("fractionatorSection") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("stickSteel", 4),
							IHLUtils.getOreDictItemStackWithSize("plateSteel", 2),
							IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 6),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 6),
							IHLUtils.getOreDictItemStack("foilSteel"),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("fractionatorBottom") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 6),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 6),
							IHLUtils.getOreDictItemStack("foilSteel"),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("fractionatorCover") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("handDrillBronze"),
							IHLUtils.getThisModItemStack("drillSteelHardened") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("boltM10x1Steel", 6),
							IHLUtils.getThisModItemStackWithSize("nutM10x1Steel", 6),
							IHLUtils.getOreDictItemStack("foilSteel"),
							IHLUtils.getThisModItemStackWithSize("barD10Steel", 4) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("refluxCondenser") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("dustFerrite"),
							IHLUtils.getThisModItemStack("ingotStearin") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("ringRawFerrite", 4) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("glassBlowingTubeSteel") }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("foilSteel"),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("mercury"), 1),
							new RecipeInputOreDict("stickBoronCarbide"), new RecipeInputOreDict("dustGlass"),
							new RecipeInputOreDict("stickGraphite"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("barD10Steel")) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("ignitron", 6) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation"),
							IHLUtils.getThisModItemStack("gaedesMercuryRotaryPump") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("glassBlowingTubeSteel") }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("foilSteel"),
							new RecipeInputOreDict("foilTungsten"), new RecipeInputOreDict("dustQuicklime"),
							new RecipeInputOreDict("dustGlass"), new RecipeInputOreDict("stickGraphite"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("barD10Steel")) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("valveTube1C21P", 6) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation"),
							IHLUtils.getThisModItemStack("gaedesMercuryRotaryPump") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel"),
							IHLUtils.getThisModItemStack("glassBlowingTubeSteel") }),
					Arrays.asList(
							new IRecipeInput[] { new RecipeInputOreDict("foilGold"), new RecipeInputOreDict("foilMica"),
									new RecipeInputFluidContainer(FluidRegistry.getFluid("cablingcolophony"), 1),
									new RecipeInputOreDict("dustGlass"), new RecipeInputOreDict("foilSteel"),
									new RecipeInputItemStack(IHLUtils.getThisModItemStack("springSteel")) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("vacuumSwitch", 16) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation"),
							IHLUtils.getThisModItemStack("gaedesMercuryRotaryPump") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") }),
					Arrays.asList(new IRecipeInput[] {
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("insulatorPorcelain"), 8),
							new RecipeInputOreDict("plateMica", 2),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("highVoltageCapacitor"), 3),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("cablingcolophony"), 1),
							new RecipeInputOreDict("dustSolderingAlloy"), new RecipeInputOreDict("foilMica"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("gu-81m"), 4),
							new RecipeInputOreDict("foilSteel", 8),
							new RecipeInputWire(IHLUtils.getUninsulatedWire("Copper", 288, 15)),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("mineraloil"), 1000),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("ignitron"), 6),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("valveTube1C21P"), 6) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("rectifierTransformerUnit") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("foilCopper"),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("cablingcolophony"), 1),
							new RecipeInputOreDict("dustSolderingAlloy"), new RecipeInputOreDict("ringFerrite", 8),
							new RecipeInputOreDict("foilMica") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("hallSensor", 8) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("insulatorPorcelain", 4),
							IHLUtils.getUninsulatedWire("Copper", 4, 15), IHLUtils.getThisModItemStack("foilMica"),
							IHLUtils.getThisModItemStack("leadAcidBattery"),
							IHLUtils.getThisModItemStack("dustSolderingAlloy"),
							IHLUtils.getThisModItemStack("vacuumSwitch"),
							IHLUtils.getThisModItemStackWithSize("hallSensor", 2),
							IHLUtils.getThisModItemStackWithSize("valveTube1C21P", 2) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("batterySwitchUnit") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("foilGold"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("foilRubberWithSulfur")),
							new RecipeInputOreDict("foilLead"),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("sulfuricacid"), 1),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("ic2distilledwater"), 1),
							new RecipeInputOreDict("dustSolderingAlloy"), new RecipeInputOreDict("dustLeadPlumbate"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("batteryCellsEbonite"), 8) }),
					Arrays.asList(
							new ItemStack[] { BatteryItem.getFullyChargedItemStackWithSize("leadAcidBattery", 8) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("tinSnipsSteel") }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("foilSteel"),
							new RecipeInputOreDict("plateGraphite", 5),
							new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("barD10Steel", 4)),
							new RecipeInputOreDict("stickGraphite"),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("dustCoalElectrodePremix")),
							new RecipeInputOreDict("foilRubber") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("electrolysisBath") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("dustTin"),
							new RecipeInputItemStack(new ItemStack(Blocks.glass_pane), 16),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("mercury"), 1),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("turpentine"), 1),
							new RecipeInputItemStack(IHLUtils.getThisModItemStack("fabric")) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("mirror", 16) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("dustTin"),
							new RecipeInputItemStack(new ItemStack(Blocks.glass_pane), 16),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("mercury"), 1),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("turpentine"), 1),
							new RecipeInputItemStack(new ItemStack(Blocks.wool)) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("mirror", 16), })));
			if (FluidRegistry.isFluidRegistered("ethanol")) {
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("dustTin"),
								new RecipeInputItemStack(new ItemStack(Blocks.glass_pane), 16),
								new RecipeInputFluidContainer(FluidRegistry.getFluid("mercury"), 1),
								new RecipeInputFluidContainer(FluidRegistry.getFluid("ethanol"), 1),
								new RecipeInputItemStack(IHLUtils.getThisModItemStack("fabric")) }),
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("mirror", 16) })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("dustTin"),
								new RecipeInputItemStack(new ItemStack(Blocks.glass_pane), 16),
								new RecipeInputFluidContainer(FluidRegistry.getFluid("mercury"), 1),
								new RecipeInputFluidContainer(FluidRegistry.getFluid("ethanol"), 1),
								new RecipeInputItemStack(new ItemStack(Blocks.wool)) }),
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("mirror", 16) })));
			}
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(Arrays.asList(
					new IRecipeInput[] { RecipeInputs.cutter, RecipeInputs.saw, RecipeInputs.vise, RecipeInputs.file }),
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("plateSteel", 2),
							new RecipeInputOreDict("foilSteel", 2), RecipeInputs.get("boltM10x1Steel", 8),
							RecipeInputs.get("nutM10x1Steel", 8), RecipeInputs.get("barD10Steel", 16),
							RecipeInputs.get("rollingMachineSetOfMoldedPartsSteel"),
							RecipeInputs.get("linerIronGraphiteGreased", 4),
							RecipeInputs.get("turboCompressorSetOfMoldedPartsBronze"),
							RecipeInputs.get("foilRubber") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("paperMachine") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("gasWeldingStation") })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("dustIronOxide", 8),
							new RecipeInputOreDict("dustBauxite"), new RecipeInputOreDict("dustPotassiumOxide"), }),
					Arrays.asList(
							new ItemStack[] { IHLUtils.getThisModItemStackWithSize("catalystRawIronOxide", 10) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("hackSawSteel"),
							IHLUtils.getThisModItemStack("viseSteel"), IHLUtils.getThisModItemStack("setOfFilesSteel"),
							IHLUtils.getThisModItemStack("drillSteelHardened"),
							IHLUtils.getThisModItemStack("handDrillBronze") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getOreDictItemStack("plateSteel") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("incisorSteel", 4) })));
			if (IHLMod.isGregTechModLoaded && IHLMod.isGT_API_Version_5) {
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 15),
								IHLUtils.getOreDictItemStack("dustRawRubber") }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 15, "RawRubber", 100) })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 240),
								IHLUtils.getOreDictItemStack("dustRawRubber") }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 240, "RawRubber", 100) })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 15),
								IHLUtils.getOreDictItemStack("dustRawRubber") }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 15, "RawRubber", 100) })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 240),
								IHLUtils.getOreDictItemStack("dustRawRubber") }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 240, "RawRubber", 100) })));

				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 15, "RawRubber", 100) }),
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 15),
								IHLUtils.getOreDictItemStack("dustRawRubber") })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 240, "RawRubber", 100) }),
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 240),
								IHLUtils.getOreDictItemStack("dustRawRubber") })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 15, "RawRubber", 100) }),
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 15),
								IHLUtils.getOreDictItemStack("dustRawRubber") })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 240, "RawRubber", 100) }),
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 240),
								IHLUtils.getOreDictItemStack("dustRawRubber") })));
			} else {
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(
								new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 15), Ic2Items.rubber }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 15, "RawRubber", 100) })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(
								new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 240), Ic2Items.rubber }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 240, "RawRubber", 100) })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 15), Ic2Items.rubber }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 15, "RawRubber", 100) })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
						Arrays.asList(
								new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 240), Ic2Items.rubber }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 240, "RawRubber", 100) })));

				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 15, "RawRubber", 100) }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 15), Ic2Items.rubber })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 240, "RawRubber", 100) }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 240), Ic2Items.rubber })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 15, "RawRubber", 100) }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 15), Ic2Items.rubber })));
				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
						Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
						Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 240, "RawRubber", 100) }),
						Arrays.asList(
								new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 240), Ic2Items.rubber })));
			}
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 15, "Rubber", 100) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 15) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Copper", 1, 240, "Rubber", 100) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Copper", 1, 240) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 15, "Rubber", 100) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 15) })));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(
					Arrays.asList(new ItemStack[] { IHLUtils.getThisModItemStack("sharpenedCarvingKnifeBronze") }),
					Arrays.asList(new ItemStack[] { IHLUtils.getInsulatedWire("Steel", 1, 240, "Rubber", 100) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getUninsulatedWire("Steel", 1, 240) })));
			ItemStack detonator = IHLUtils.getItemStackWithTag("detonator", "detonator_delay", 5);
			detonator.stackSize = 16;
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("platePaper", 2),
							new RecipeInputItemStack(new ItemStack(Items.string)),
							new RecipeInputOreDict("dustGunpowder", 1),
							new RecipeInputOreDictionaryList(
									new String[] { "dustPentaerythritolTetranitrate", "dustMercuryFulminate" }, 1),
							new RecipeInputOreDict("ingotTarPitch", 1) }),
					Arrays.asList(new ItemStack[] { detonator }), null));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("platePaper", 2),
							new RecipeInputOreDict("dustPentaerythritolTetranitrate", 4),
							new RecipeInputOreDict("ingotTarPitch", 1), new RecipeInputDetonator(detonator) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getItemStackWithTag("ihlExplosive", "explosionPower",
							15 * config.explosionPowerBase) }),
					null));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("platePaper", 2),
							new RecipeInputOreDict("dustWood", 4),
							new RecipeInputFluidContainer(FluidRegistry.getFluid("nitroglycerin"), 1),
							new RecipeInputOreDict("ingotTarPitch", 1), new RecipeInputDetonator(detonator) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getItemStackWithTag("ihlExplosive", "explosionPower",
							10 * config.explosionPowerBase) }),
					null));
			IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(null,
					Arrays.asList(new IRecipeInput[] { new RecipeInputOreDict("platePaper", 2),
							new RecipeInputOreDict("dustGunpowder", 8), new RecipeInputOreDict("ingotTarPitch", 1),
							new RecipeInputDetonator(detonator) }),
					Arrays.asList(new ItemStack[] { IHLUtils.getItemStackWithTag("ihlExplosive", "explosionPower",
							3 * config.explosionPowerBase) }),
					null));

			AchesonFurnanceTileEntity.addRecipe(new RecipeInputOreDict("dustSiliconDioxide", 2),
					new RecipeInputOreDict("dustCoal", 4), "dustCarborundum");
			AchesonFurnanceTileEntity.addRecipe(new RecipeInputOreDict("dustQuicklime"),
					new RecipeInputOreDict("dustCoal", 4), "dustCalciumCarbide");
			AchesonFurnanceTileEntity.addRecipe(new RecipeInputOreDict("stickCoal"), new RecipeInputOreDict("dustCoal"),
					"stickGraphite");
			AchesonFurnanceTileEntity.addRecipe(new RecipeInputOreDict("dustTungsten"),
					new RecipeInputOreDict("dustCoal"), "ingotTungsten");
			AchesonFurnanceTileEntity.addRecipe(new RecipeInputOreDict("dustBoricAcid", 4),
					new RecipeInputOreDict("dustCoal", 8), "dustBoronCarbide");
			AchesonFurnanceTileEntity.addRecipe(new RecipeInputOreDict("plateCoal"),
					new RecipeInputOreDict("dustCoal", 1), "plateGraphite");
			LeadOvenTileEntity.addRecipe(
					new UniversalRecipeInput(new FluidStack[] { IHLUtils.getFluidStackWithSize("water", 400) },
							new IRecipeInput[] { new RecipeInputOreDict("dustSulfur"),
									new RecipeInputOreDict("dustSaltpeter") }),
					IHLUtils.getFluidStackWithSize("sulfuricacid", 100));
			LeadOvenTileEntity.addRecipe(
					new UniversalRecipeInput(new FluidStack[] { IHLUtils.getFluidStackWithSize("water", 100) },
							new IRecipeInput[] { new RecipeInputOreDict("crushedPurifiedCinnabar") }),
					IHLUtils.getFluidStackWithSize("mercury", 36));
			LeadOvenTileEntity.addRecipe(new RecipeInputOreDict("dustStibnite", 1),
					IHLUtils.getThisModItemStack("dustAntimonyOxide"));
			LeadOvenTileEntity.addRecipe(new RecipeInputOreDict("dustSodiumHydrogenSulfate", 2),
					IHLUtils.getFluidStackWithSize("sulfuricacid", 20),
					IHLUtils.getOreDictItemStack("dustSodiumSulfate"));
			LeadOvenTileEntity.addRecipe(new RecipeInputOreDict("dustSodiumZeoliteCoked", 2),
					IHLUtils.getThisModItemStack("dustSodiumZeolite"));
			ImpregnatingMachineTileEntity.addChemicalRecipe(
					new UniversalRecipeInput((new FluidStack[] { new FluidStack(FluidRegistry.WATER, 1500) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustQuicklime", 1) })),
					new UniversalRecipeOutput((new FluidStack[] { IHLUtils.getFluidStackWithSize("limemilk", 2500) }),
							null, 20));
			ImpregnatingMachineTileEntity.addChemicalRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { new FluidStack(FluidRegistry.WATER, 130),
									IHLUtils.getFluidStackWithSize("limemilk", 142) }),
							(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("muttonLard", 6) })),
					new UniversalRecipeOutput((new FluidStack[] { IHLUtils.getFluidStackWithSize("glycerol", 130) }),
							(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("ingotCalciumSoap", 5) }), 200));
			ImpregnatingMachineTileEntity.addChemicalRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { new FluidStack(FluidRegistry.WATER, 600),
									IHLUtils.getFluidStackWithSize("sulfuricacid", 284) }),
							(new ItemStack[] { IHLUtils.getThisModItemStackWithSize("ingotCalciumSoap", 6) })),
					new UniversalRecipeOutput((new FluidStack[] { IHLUtils.getFluidStackWithSize("oleicacid", 280) }),
							(new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getThisModItemStackWithSize("ingotStearin", 4)),
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustGypsum"), 0.48f) }),
							200));
			ImpregnatingMachineTileEntity.addChemicalRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("limemilk", 1000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustTrona") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("solution.natriumhydroxide", 1000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustCalcite") }), 200));
			DetonationSprayingMachineTileEntity.addRecipe(IHLUtils.getThisModItemStack("blankNeedleFileSteel"),
					IHLUtils.getThisModItemStack("needleFile"));
			DetonationSprayingMachineTileEntity.addRecipe(IHLUtils.getThisModItemStack("blankSetOfFilesSteel"),
					IHLUtils.getThisModItemStack("setOfFilesSteel"));
			DetonationSprayingMachineTileEntity.addRecipe(IHLUtils.getThisModItemStack("incisorSteel"),
					IHLUtils.getThisModItemStack("incisorSteelDiamondCoated"));
			ItemStack drill = Ic2Items.miningDrill.copy();
			drill.setItemDamage(OreDictionary.WILDCARD_VALUE);
			DetonationSprayingMachineTileEntity.addRecipe(drill, Ic2Items.diamondDrill.copy());
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputOreDict("plateSteel", 1),
					IHLUtils.getThisModItemStack("plateHotSteel"));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputOreDict("ingotSteel", 1),
					IHLUtils.getOreDictItemStack("ingotHotSteel"));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputOreDict("plateTungsten", 1),
					IHLUtils.getThisModItemStack("plateHotTungsten"));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("gaedesPumpBarrelRawPorcelain"), 1),
					IHLUtils.getThisModItemStack("gaedesPumpBarrelPorcelain"));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(new ItemStack(IHLMod.crucible, 1, OreDictionary.WILDCARD_VALUE), 1),
					new ItemStack(IHLMod.crucible, 1, 1));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("injectionMold", 1), 1),
					IHLUtils.getThisModItemStackWithSize("injectionMold", 1));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("sawBladeSteel"), 1),
					IHLUtils.getThisModItemStack("sawBladeSteelHot"));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputItemStack(IHLUtils.getThisModItemStack("barD10Steel"), 1),
					IHLUtils.getThisModItemStack("barD10SteelHot"));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("linerIronGraphite"), 1),
					IHLUtils.getThisModItemStack("linerIronGraphiteHot"));
			MuffleFurnanceTileEntity.addRecipe(
					new UniversalRecipeInput(null,
							new IRecipeInput[] { new RecipeInputOreDict("dustIronOxide", 1),
									new RecipeInputOreDict("dustLithiumOxide", 1) }),
					new UniversalRecipeOutput(null,
							new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStackWithSize("dustFerrite", 2)) },
							200));
			MuffleFurnanceTileEntity.addRecipe(
					new UniversalRecipeInput(null,
							new IRecipeInput[] { new RecipeInputOreDict("dustBauxite"),
									new RecipeInputOreDict("dustSodiumHydroxide") }),
					new UniversalRecipeOutput(null,
							new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustSodiumAluminate")) },
							200));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputItemStack(IHLUtils.getThisModItemStack("drillSteel")),
					IHLUtils.getThisModItemStack("drillSteelHot"));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputItemStack(IHLUtils.getThisModItemStack("tapM10x1Steel")),
					IHLUtils.getThisModItemStack("tapM10x1SteelHot"));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputItemStack(IHLUtils.getThisModItemStack("diceM10x1Steel")),
					IHLUtils.getThisModItemStack("diceM10x1SteelHot"));
			LoomTileEntity.addRecipe(IHLUtils.getThisModWireItemStackWithLength("fiberGlass", 1024),
					IHLUtils.getThisModItemStack("meshGlass"));
			LoomTileEntity.addRecipe(new ItemStack(Items.string, 4), IHLUtils.getThisModItemStack("fabric"));
			RollingMachinePart1TileEntity.addRecipe(IHLUtils.getThisModItemStack("plateHotSteel"),
					IHLUtils.getOreDictItemStackWithSize("foilSteel", 4));
			RollingMachinePart1TileEntity.addRecipe(IHLUtils.getThisModItemStack("plateHotTungsten"),
					IHLUtils.getOreDictItemStackWithSize("foilTungsten", 4));
			RollingMachinePart1TileEntity.addRecipe(IHLUtils.getOreDictItemStack("plateGold"),
					IHLUtils.getOreDictItemStackWithSize("foilGold", 4));
			RollingMachinePart1TileEntity.addRecipe(IHLUtils.getOreDictItemStack("plateCopper"),
					IHLUtils.getOreDictItemStackWithSize("foilCopper", 4));
			RollingMachinePart1TileEntity.addRecipe(IHLUtils.getOreDictItemStack("plateLead"),
					IHLUtils.getOreDictItemStackWithSize("foilLead", 4));
			ImpregnatingMachineTileEntity.addQuenchingRecipe("diceM10x1SteelHot", "diceM10x1SteelHardened");
			ImpregnatingMachineTileEntity.addQuenchingRecipe("tapM10x1SteelHot", "tapM10x1SteelHardened");
			ImpregnatingMachineTileEntity.addQuenchingRecipe("drillSteelHot", "drillSteelHardened");
			ImpregnatingMachineTileEntity.addQuenchingRecipe("sawBladeSteelHot", "sawBladeSteelHardened");
			ImpregnatingMachineTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("oleicacid", 1) }),
							(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteHot") })),
					new UniversalRecipeOutput(null,
							(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteGreased") }), 2));
			ImpregnatingMachineTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("seedoil", 1) }),
							(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteHot") })),
					new UniversalRecipeOutput(null,
							(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteGreased") }), 2));
			ImpregnatingMachineTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("mineraloil", 1) }),
							(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteHot") })),
					new UniversalRecipeOutput(null,
							(new ItemStack[] { IHLUtils.getThisModItemStack("linerIronGraphiteGreased") }), 2));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("foilRubberWithSulfur")),
					IHLUtils.getThisModItemStack("foilRubber"));
			MuffleFurnanceTileEntity
					.addRecipe(
							new UniversalRecipeInput(null,
									(new IRecipeInput[] { new RecipeInputItemStack(
											IHLUtils.getThisModItemStack("pipeRubberWithSulfur")) })),
							new UniversalRecipeOutput(null,
									(new ItemStack[] { IHLUtils.getThisModItemStack("pipeVulcanizedRubber") }), 20));
			GasWeldingStationTileEntity.addGasRecipe(IHLUtils.getOreDictItemStack("dustCalciumCarbide"),
					IHLUtils.getFluidStackWithSize("water", 666), IHLUtils.getFluidStackWithSize("acetylene", 1333),
					IHLUtils.getFluidStackWithSize("limemilk", 333));
			CryogenicDistillerTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("air", 1000),
					IHLUtils.getFluidStackWithSize("nitrogen", 781), IHLUtils.getFluidStackWithSize("oxygen", 209),
					false);
			ChemicalReactorTileEntity
					.addRecipe(
							new UniversalRecipeInput(
									(new FluidStack[] {
											IHLUtils.getFluidStackWithSize("solution.natriumhydroxide", 500) }),
									(new ItemStack[] { IHLUtils.getOreDictItemStack("dustSiliconDioxide") })),
							new UniversalRecipeOutput(
									(new FluidStack[] { IHLUtils.getFluidStackWithSize("liquidglass", 500) }), null,
									200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("spruceresin", 1000),
							IHLUtils.getFluidStackWithSize("glycerol", 50) }), null),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("cablingcolophony", 600) }), null, 200,
							true));
			CryogenicDistillerTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("spruceresin", 1000),
					IHLUtils.getFluidStackWithSize("ic2distilledwater", 150),
					IHLUtils.getFluidStackWithSize("turpentine", 300), true);
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("fueloil", 2000) }),
							null),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("molten.tarpitch", 144) }), null, 200,
							true));
			CryogenicDistillerTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("fueloil", 2000),
					IHLUtils.getFluidStackWithSize("mineraloil", 1000), null, true);
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("solution.natriumhydroxide", 4000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("crushedPurifiedGyubnera") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("solution.natriumtungstate", 4000) }),
							(new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustTinyManganeseOxide"),
											3.75f),
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustTinyIronOxide"),
											9f / 12f) }),
							200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("sulfuricacid", 1750) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustRockSalt", 1) })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("hydrogenchloride", 1000) }),
							(new RecipeOutputItemStack[] { new RecipeOutputItemStack(
									IHLUtils.getOreDictItemStack("dustPotassiumSulphate"), 1.75f) }),
							200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("sulfuricacid", 3500) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustSalt") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("hydrogenchloride", 1000) }),
							(new RecipeOutputItemStack[] { new RecipeOutputItemStack(
									IHLUtils.getOreDictItemStack("dustSodiumHydrogenSulfate"), 3.5f) }),
							200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("sulfuricacid", 7000 / 9),
									new FluidStack(FluidRegistry.WATER, 6000 / 9 + 1000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustDatolite") })),
					new UniversalRecipeOutput((new FluidStack[] { IHLUtils.getFluidStackWithSize("boricacid", 1777) }),
							(new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustGypsum"), 12f / 9f),
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustTinySiliconDioxide"),
											3f) }),
							200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("limemilk", 1667),
							IHLUtils.getFluidStackWithSize("hydrogenchloride", 1334) }), null),
					new UniversalRecipeOutput(null,
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustCalciumChloride") }), 200));
			ChemicalReactorTileEntity
					.addRecipe(
							new UniversalRecipeInput(
									(new FluidStack[] {
											IHLUtils.getFluidStackWithSize("solution.natriumtungstate", 6667) }),
									(new ItemStack[] { IHLUtils.getOreDictItemStack("dustCalciumChloride") })),
							new UniversalRecipeOutput(
									(new FluidStack[] { IHLUtils.getFluidStackWithSize("saltwater", 6000) }),
									(new ItemStack[] { IHLUtils.getOreDictItemStack("dustCalciumTungstate") }), 200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("solution.natriumhydroxide", 2000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustWood"),
									IHLUtils.getOreDictItemStack("dustSodiumSulfide") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("pulp.cellulose", 1500),
									IHLUtils.getFluidStackWithSize("ic2biomass", 500) }),
							null, 200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("cablingcolophony", 50),
									new FluidStack(FluidRegistry.WATER, 2000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustMica") })),
					new UniversalRecipeOutput((new FluidStack[] { IHLUtils.getFluidStackWithSize("pulp.mica", 2000) }),
							null, 200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("hydrogenchloride", 500),
									new FluidStack(FluidRegistry.WATER, 7500) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustMagnesiumOxide") })),
					new UniversalRecipeOutput(null,
							(new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustBischofite"), 8.75f) }),
							200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("liquidglass", 1000),
									new FluidStack(FluidRegistry.WATER, 2000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustSodiumAluminate") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("pulp.sodiumzeolite", 4000) }), null,
							200));
			EvaporatorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("saltwater", 4000),
					IHLUtils.getOreDictItemStack("dustSalt"));
			EvaporatorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("boricacid", 2288),
					IHLUtils.getOreDictItemStack("dustBoricAcid"));
			EvaporatorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("solution.natriumhydroxide", 2000),
					IHLUtils.getOreDictItemStack("dustSodiumHydroxide"));
			EvaporatorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("pulp.sodiumzeolite", 4000),
					IHLUtils.getOreDictItemStack("dustSodiumZeolite"));
			EvaporatorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("solution.lithiumchloride", 4000),
					IHLUtils.getOreDictItemStack("dustLithiumChloride"));
			EvaporatorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("solution.calciumchloride", 4000),
					IHLUtils.getOreDictItemStack("dustCalciumChloride"));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("water", 2000),
									IHLUtils.getFluidStackWithSize("hydrogenchloride", 667) }),
							(new IRecipeInput[] { new RecipeInputOreDict("dustCalciumTungstate") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("solution.calciumchloride", 2000) }),
							(new RecipeOutputItemStack[] { new RecipeOutputItemStack(
									IHLUtils.getOreDictItemStack("dustTungsticAcid"), 1.16f) }),
							200));
			if (IHLUtils.hasOreDictionaryEntry("dustTungstate"))
				ChemicalReactorTileEntity.addRecipe(
						new UniversalRecipeInput(
								(new FluidStack[] { IHLUtils.getFluidStackWithSize("water", 1000),
										IHLUtils.getFluidStackWithSize("hydrogenchloride", 140) }),
								(new IRecipeInput[] { new RecipeInputOreDict("dustTungstate") })),
						new UniversalRecipeOutput(
								(new FluidStack[] { IHLUtils.getFluidStackWithSize("solution.lithiumchloride", 560) }),
								(new RecipeOutputItemStack[] { new RecipeOutputItemStack(
										IHLUtils.getOreDictItemStack("dustTungsticAcid"), 1.0f) }),
								200));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputOreDict("dustTungsticAcid", 2),
					IHLUtils.getOreDictItemStack("dustTungstenOxide"), 1.14f);
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("electrolysisBathRawPorcelain")),
					IHLUtils.getThisModItemStack("electrolysisBathPorcelain"));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("ovenRawPorcelain")),
					IHLUtils.getThisModItemStack("leadOven"));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("ringRawPorcelain")),
					IHLUtils.getThisModItemStack("ringPorcelain"));
			MuffleFurnanceTileEntity.addRecipe(new RecipeInputItemStack(IHLUtils.getThisModItemStack("ringRawFerrite")),
					IHLUtils.getThisModItemStack("ringFerrite"));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("insulatorRawPorcelain")),
					IHLUtils.getThisModItemStack("insulatorPorcelain"));
			MuffleFurnanceTileEntity.addRecipe(
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("lampHolderRawPorcelain")),
					IHLUtils.getThisModItemStack("lampHolderPorcelain"));
			LabElectrolyzerTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("water", 300),
					IHLUtils.getFluidStackWithSize("hydrogen", 200), IHLUtils.getFluidStackWithSize("oxygen", 100),
					null);
			LabElectrolyzerTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("saltwater", 1000),
					IHLUtils.getFluidStackWithSize("solution.natriumhydroxide", 875),
					IHLUtils.getFluidStackWithSize("chlorine", 100), null);
			if (IHLUtils.hasOreDictionaryEntry("dustSilicon"))
				FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("hydrogen", 1333),
						IHLUtils.getOreDictItemStackWithSize("dustSiliconDioxide", 1),
						IHLUtils.getFluidStackWithSize("water", 2000),
						IHLUtils.getOreDictItemStackWithSize("dustTinySilicon", 3));
			if (IHLUtils.hasOreDictionaryEntry("dustManganese"))
				FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("hydrogen", 2000),
						IHLUtils.getOreDictItemStackWithSize("dustManganeseOxide", 2),
						IHLUtils.getFluidStackWithSize("water", 3000),
						IHLUtils.getOreDictItemStack("dustManganese"));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("hydrogen", 1200),
					IHLUtils.getOreDictItemStack("dustIronOxide"),
					IHLUtils.getFluidStackWithSize("water", 1800),
					new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustTinyIron"), 3.6f));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("hydrogen", 1500),
					IHLUtils.getOreDictItemStack("dustTungstenOxide"),
					IHLUtils.getFluidStackWithSize("water", 2250),
					new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustTinyTungsten"), 2.25f));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("hydrogen", 4000),
					IHLUtils.getOreDictItemStackWithSize("dustSodiumSulfate", 3),
					IHLUtils.getFluidStackWithSize("water", 6000),
					IHLUtils.getOreDictItemStack("dustSodiumSulfide"));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("hydrogen", 1200),
					IHLUtils.getOreDictItemStack("dustAntimonyOxide"),
					IHLUtils.getFluidStackWithSize("water", 1800),
					new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustTinyAntimony"), 3.6f));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("oxygen", 1500),
					IHLUtils.getOreDictItemStack("dustIron"), null,
					new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustIronOxide"), 2.5f));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("oxygen", 500),
					IHLUtils.getOreDictItemStack("dustLithium"), null,
					new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustLithiumOxide"), 1.5f));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("oxygen", 1333),
					IHLUtils.getOreDictItemStack("dustLead"), null,
					new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustLeadPlumbate"), 2.333f));
			FluidizedBedReactorTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("chlorine", 2000),
					IHLUtils.getOreDictItemStack("dustCarborundum"),
					IHLUtils.getFluidStackWithSize("silicontetrachloride", 2500),
					new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustCoal"), 0.5f));
			FluidizedBedReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("hydrogen", 445),
									IHLUtils.getFluidStackWithSize("silicontetrachloride", 1667) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustIridium", 0),
									IHLUtils.getOreDictItemStack("dustTinySilicon") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("trichlorosilane", 2112) }), null, 200));
			FluidizedBedReactorTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("fueloil", 6000) }),
							(new ItemStack[] { IHLUtils.getOreDictItemStack("dustSodiumZeolite") })),
					new UniversalRecipeOutput(
							(new FluidStack[] { IHLUtils.getFluidStackWithSize("fuel", 4500),
									IHLUtils.getFluidStackWithSize("crackinggas", 500) }),
							(new RecipeOutputItemStack[] { new RecipeOutputItemStack(
									IHLUtils.getOreDictItemStack("dustSodiumZeoliteCoked"), 1.9f) }),
							2000));
			FluidizedBedReactorTileEntity.addRecipe(new UniversalRecipeInput(
					null, (new IRecipeInput[] { new RecipeInputOreDict("dustWood") })), new UniversalRecipeOutput(
							(new FluidStack[] {
									IHLUtils.getFluidStackWithSize("molten.tarpitch", 8), IHLUtils
											.getFluidStackWithSize("tarwater", 39),
									IHLUtils.getFluidStackWithSize("crackinggas", 31) }),
							(new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustCoal"), 0.25f) }),
							2000));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput((new FluidStack[] { IHLUtils.getFluidStackWithSize("tarwater", 50) }),
							(new IRecipeInput[] {
									new RecipeInputOreDict("dustQuicklime") })),
					new UniversalRecipeOutput((new FluidStack[] { IHLUtils.getFluidStackWithSize("water", 10) }),
							(new RecipeOutputItemStack[] { new RecipeOutputItemStack(
									IHLUtils.getOreDictItemStack("dustCalciumAcetate"), 1f) }),
							200, true));
			CryogenicDistillerTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("tarwater", 50),
					IHLUtils.getFluidStackWithSize("ic2distilledwater", 30),
					IHLUtils.getFluidStackWithSize("methanol", 10), true);
			FluidizedBedReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							new FluidStack[] { IHLUtils.getFluidStackWithSize("methanol", 200),
									IHLUtils.getFluidStackWithSize("oxygen", 100) },
							new IRecipeInput[] { new RecipeInputOreDict("dustSilver", 0) }),
					new UniversalRecipeOutput(new FluidStack[] { IHLUtils.getFluidStackWithSize("formaldehyde", 300) },
							null, 200));
			FluidizedBedReactorTileEntity.addRecipe(
					new UniversalRecipeInput(new FluidStack[] { IHLUtils.getFluidStackWithSize("chlorine", 110),
							IHLUtils.getFluidStackWithSize("mercury", 50) }, null),
					new UniversalRecipeOutput(null, new RecipeOutputItemStack[] {
							new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustMercuryChloride"), 1f) }, 200));
			FluidizedBedReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							new FluidStack[] { IHLUtils.getFluidStackWithSize("acetylene", 100),
									IHLUtils.getFluidStackWithSize("ic2distilledwater", 100),
									IHLUtils.getFluidStackWithSize("hydrogenchloride", 2) },
							new IRecipeInput[] { new RecipeInputOreDict("dustMercuryChloride", 0) }),
					new UniversalRecipeOutput(new FluidStack[] { IHLUtils.getFluidStackWithSize("acetaldehyde", 200) },
							null, 200));
			ChemicalReactorTileEntity
					.addRecipe(new UniversalRecipeInput(
							new FluidStack[] { IHLUtils.getFluidStackWithSize("acetaldehyde", 100),
									IHLUtils.getFluidStackWithSize("formaldehyde",
											400),
									IHLUtils.getFluidStackWithSize("solution.natriumhydroxide", 100) },
							null),
							new UniversalRecipeOutput(null, new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustSodiumFormate"), 1f),
									new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustPentaerythritol"),
											1f) },
									200, false));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(new FluidStack[] { IHLUtils.getFluidStackWithSize("sulfuricacid", 288) },
							new IRecipeInput[] {
									new RecipeInputOreDict("dustSaltpeter") }),
					new UniversalRecipeOutput(null, new RecipeOutputItemStack[] {
							new RecipeOutputItemStack(IHLUtils.getOreDictItemStack("dustPotassiumSulphate"), 2f) }, 200,
							true));
			CryogenicDistillerTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("sulfuricacid", 288),
					IHLUtils.getFluidStackWithSize("nitricacid", 120), null, true);
			FluidizedBedReactorTileEntity
					.addRecipe(
							new UniversalRecipeInput(
									new FluidStack[] { IHLUtils.getFluidStackWithSize("hydrogen", 90) },
									new IRecipeInput[] { new RecipeInputItemStack(
											IHLUtils.getThisModItemStack("catalystIronOxide")) }),
							new UniversalRecipeOutput(null, new RecipeOutputItemStack[] {
									new RecipeOutputItemStack(IHLUtils.getThisModItemStack("catalystIron"), 1f) },
									200));
			FluidizedBedReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							new FluidStack[] { IHLUtils.getFluidStackWithSize("nitrogen", 100),
									IHLUtils.getFluidStackWithSize("hydrogen", 300) },
							new IRecipeInput[] { new RecipeInputItemStack(
									IHLUtils.getThisModItemStackWithSize("catalystIron", 0), 0) }),
					new UniversalRecipeOutput(new FluidStack[] { IHLUtils.getFluidStackWithSize("ammonia", 400) }, null,
							200));
			FluidizedBedReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							new FluidStack[] { IHLUtils.getFluidStackWithSize("ammonia", 100),
									IHLUtils.getFluidStackWithSize("oxygen", 150) },
							new IRecipeInput[] { new RecipeInputOreDict("dustIridium", 0) }),
					new UniversalRecipeOutput(new FluidStack[] { IHLUtils.getFluidStackWithSize("nitricacid", 250) },
							null, 200));
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(
							new FluidStack[] { IHLUtils.getFluidStackWithSize("nitricacid", 50),
									IHLUtils.getFluidStackWithSize("sulfuricacid", 50) },
							new IRecipeInput[] { new RecipeInputOreDict("dustPentaerythritol"),
									new RecipeInputItemStack(new ItemStack(Items.snowball, 8)) }),
					new UniversalRecipeOutput(null,
							new RecipeOutputItemStack[] { new RecipeOutputItemStack(
									IHLUtils.getOreDictItemStack("dustPentaerythritolTetranitrate"), 1f) },
							200, false));
			if (FluidRegistry.isFluidRegistered("bioethanol")) {
				ChemicalReactorTileEntity.addRecipe(
						new UniversalRecipeInput(new FluidStack[] { IHLUtils.getFluidStackWithSize("bioethanol", 450),
								IHLUtils.getFluidStackWithSize("mercury", 144),
								IHLUtils.getFluidStackWithSize("nitricacid", 300) }, null),
						new UniversalRecipeOutput(
								new FluidStack[] { IHLUtils.getFluidStackWithSize("acetaldehyde", 300) },
								new RecipeOutputItemStack[] { new RecipeOutputItemStack(
										IHLUtils.getOreDictItemStack("dustMercuryFulminate"), 1f) },
								200));
			}
			ChemicalReactorTileEntity.addRecipe(
					new UniversalRecipeInput(new FluidStack[] { IHLUtils.getFluidStackWithSize("glycerol", 100),
							IHLUtils.getFluidStackWithSize("nitricacid", 300) }, null),
					new UniversalRecipeOutput(new FluidStack[] { IHLUtils.getFluidStackWithSize("nitroglycerin", 400) },
							null, 200));

			Crucible.addRecipe("ingotSteel", IHLUtils.getFluidStackWithSize("molten.steel", 144));
			Crucible.addRecipe("ingotBronze", IHLUtils.getFluidStackWithSize("molten.bronze", 144));
			Crucible.addRecipe("ingotGold", IHLUtils.getFluidStackWithSize("molten.gold", 144));
			Crucible.addRecipe("ingotMagnesium", IHLUtils.getFluidStackWithSize("molten.magnesium", 144));
			Crucible.addRecipe("dustGold", IHLUtils.getFluidStackWithSize("molten.gold", 144));
			Crucible.addRecipe("dustSalt", IHLUtils.getFluidStackWithSize("molten.sodiumchloride", 144));
			Crucible.addRecipe("dustGlass", IHLUtils.getFluidStackWithSize("molten.glass", 144));
			Crucible.addRecipe("dustRockSalt", IHLUtils.getFluidStackWithSize("molten.potassiumchloride", 144));
			PaperMachineTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("pulp.cellulose", 1500),
					new ItemStack(Items.paper));
			PaperMachineTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("pulp.mica", 500),
					IHLUtils.getThisModItemStack("foilMica"));
			FractionatorBottomTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("oil", 100),
					IHLUtils.getFluidStackWithSize("fueloil", 60), IHLUtils.getFluidStackWithSize("fuel", 40));
			if (FluidRegistry.isFluidRegistered("liquid_light_oil"))
				FractionatorBottomTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("liquid_light_oil", 100),
						IHLUtils.getFluidStackWithSize("fueloil", 40), IHLUtils.getFluidStackWithSize("fuel", 60));
			if (FluidRegistry.isFluidRegistered("liquid_medium_oil"))
				FractionatorBottomTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("liquid_medium_oil", 100),
						IHLUtils.getFluidStackWithSize("fueloil", 60), IHLUtils.getFluidStackWithSize("fuel", 40));
			if (FluidRegistry.isFluidRegistered("liquid_heavy_oil"))
				FractionatorBottomTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("liquid_heavy_oil", 100),
						IHLUtils.getFluidStackWithSize("fueloil", 70), IHLUtils.getFluidStackWithSize("fuel", 30));
			if (FluidRegistry.isFluidRegistered("liquid_extra_heavy_oil"))
				FractionatorBottomTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("liquid_extra_heavy_oil", 100),
						IHLUtils.getFluidStackWithSize("fueloil", 80), IHLUtils.getFluidStackWithSize("fuel", 20));
			ElectrolysisBathTileEntity
					.addRecipe(
							new UniversalRecipeInput(
									(new FluidStack[] {
											IHLUtils.getFluidStackWithSize("molten.sodiumchloride", 1000) }),
									(new ItemStack[] { IHLUtils.getOreDictItemStack("dustBischofite") })),
							new UniversalRecipeOutput(
									(new FluidStack[] { IHLUtils.getFluidStackWithSize("molten.magnesium", 6) }), null,
									200));
			ElectrolysisBathTileEntity
					.addRecipe(
							new UniversalRecipeInput(
									(new FluidStack[] {
											IHLUtils.getFluidStackWithSize("molten.sodiumchloride", 1000) }),
									(new ItemStack[] { IHLUtils.getOreDictItemStack("dustLithiumChloride") })),
							new UniversalRecipeOutput(
									(new FluidStack[] { IHLUtils.getFluidStackWithSize("molten.lithium", 72) }), null,
									200));
			ElectrolysisBathTileEntity
					.addRecipe(
							new UniversalRecipeInput(
									(new FluidStack[] {
											IHLUtils.getFluidStackWithSize("molten.potassiumchloride", 1000) }),
									(new ItemStack[] { IHLUtils.getOreDictItemStack("dustRockSalt") })),
							new UniversalRecipeOutput(
									(new FluidStack[] { IHLUtils.getFluidStackWithSize("molten.potassium", 72) }), null,
									200));
			// 1 mb of molten metal per 1 m of cable with transverse section
			// equals to 1.5 sq. mm.
			fluidDictionary.registerFluidStack("water", IHLUtils.getFluidStackWithSize("water", 1000));
			fluidDictionary.registerFluidStack("water", IHLUtils.getFluidStackWithSize("ic2distilledwater", 1000));
			fluidDictionary.registerFluidStack("lubricant", IHLUtils.getFluidStackWithSize("seedoil", 1000));
			fluidDictionary.registerFluidStack("lubricant", IHLUtils.getFluidStackWithSize("oleicacid", 1000));
			fluidDictionary.registerFluidStack("lubricant", IHLUtils.getFluidStackWithSize("mineraloil", 1000));
			if (FluidRegistry.isFluidRegistered("lubricant"))
				fluidDictionary.registerFluidStack("lubricant", IHLUtils.getFluidStackWithSize("lubricant", 1000));
			WireMillTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("molten.glass", 1),
					new RecipeInputItemStack(IHLUtils.getThisModItemStack("fiberGlassDieSteel"), 0),
					IHLUtils.getThisModWireItemStackWithLength("fiberGlass", 64));
			WireMillTileEntity.addRecipe("dustGlass", IHLUtils.getFluidStackWithSize("molten.glass", 144));
			for (ElectricConductor ec : ElectricConductor.values()) {
				String fluidName = "molten." + ec.toString().toLowerCase();
				if (FluidRegistry.isFluidRegistered(fluidName)) {
					WireMillTileEntity.addRecipe(IHLUtils.getFluidStackWithSize(fluidName, 1),
							new RecipeInputDie("setOfDies1_5sqmm", 15),
							IHLUtils.getUninsulatedWire(ec.toString(), 1, 15));
					log.debug("added recipe for " + fluidName);
					if (ec.meltingPoint < 500) {
						WireMillTileEntity.addRecipe("ingot" + ec.toString(),
								IHLUtils.getFluidStackWithSize(fluidName, 144));
					}
				}
			}
			VulcanizationExtrudingMoldTileEntity.addRecipe(IHLUtils.getFluidStackWithSize("molten.rubber", 32),
					IHLUtils.getUninsulatedWire("Copper", 1, 15),
					IHLUtils.getInsulatedWire("Copper", 1, 15, "Rubber", 100));
			IHLMod.config.loadRecipeModificators();
		}
		if (Loader.isModLoaded("NotEnoughItems")) {
			NEIModContainer.plugins.add(new NEIIHLConfig());
		}
		IHLMod.proxy.initBlockRenderer();
		IHLMod.log.info("IHL loaded.");
	}

	private void registerEntities() {
		EntityRegistry.registerModEntity(PowerCableNodeEntity.class, "PowerCableNodeEntity", 2, this, 80, 3, true);
		EntityRegistry.registerModEntity(NodeEntity.class, "NodeEntity", 3, this, 80, 3, true);
		EntityRegistry.registerModEntity(IHLEntityFallingPile.class, "IHLEntityFallingPile", 4, this, 80, 3, true);
		EntityRegistry.registerGlobalEntityID(LostHeadEntity.class, "LostHead",
				EntityRegistry.findGlobalUniqueEntityId(), 0x0033FF, 0x00CCFF);
		if (IHLMod.config.enableWailers) {
			BiomeGenBase[] biomes = BiomeDictionary.getBiomesForType(Type.END);
			for (int i = 0; i < biomes.length; i++) {
				if (biomes[i].biomeName.equals("Sky")) {
					EntityRegistry.addSpawn(LostHeadEntity.class, 50, 1, 10, EnumCreatureType.monster, biomes[i]);
				}
			}
		}
	}

	@Override
	public int getBurnTime(ItemStack stack) {
		if (IHLUtils.getFirstOreDictName(stack) == "ingotTarPitch") {
			return 2000;
		}
		if (IHLUtils.getFirstOreDictName(stack) == "dustSodiumZeoliteCoked") {
			return 500;
		} else if (IHLUtils.getFirstOreDictName(stack) == "nuggetTarPitch") {
			return 222;
		} else if (IHLUtils.getFirstOreDictName(stack) == "dustSulfur") {
			return 160;
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	private void loadGT5Recipes() {
		ItemStack battery_mv_c = ItemList.Battery_RE_MV_Cadmium.get(1L, new Object[] { null });
		ItemStack battery_mv_l = ItemList.Battery_RE_MV_Lithium.get(1L, new Object[] { null });
		ItemStack battery_mv_s = ItemList.Battery_RE_MV_Sodium.get(1L, new Object[] { null });
		ItemStack energy_crystal = gregtech.api.util.GT_ModHandler.getIC2Item("energyCrystal", 1L, 32767);
		ItemStack pump = ItemList.Electric_Pump_MV.get(1L, new Object[] { null });
		ItemStack pump_hv = ItemList.Electric_Pump_HV.get(1L, new Object[] { null });
		int[] gypsumOreList1;
		int[] oreListIterator1;

		if (config.enableRubberTreeSack) {
			gypsumOreList1 = new int[] { 10000, 2000, 2000, 1000, 100, 0 };
			oreListIterator1 = new int[] { 10000, 10000, 10000, 5000, 500, 10000 };
			GregTech_API.sRecipeAdder.addCentrifugeRecipe((ItemStack) null, (ItemStack) null,
					new FluidStack(FluidRegistry.getFluid("fluidrubbertreesap"), 200),
					new FluidStack(FluidRegistry.WATER, 120), ItemList.IC2_Resin.get(1L, new Object[] { null }),
					IHLUtils.getOreDictItemStack("dustRawRubber"), new ItemStack(Items.sugar, 1),
					GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Wood, 2L), (ItemStack) null,
					(ItemStack) null, gypsumOreList1, 500, 5);
			GregTech_API.sRecipeAdder.addCentrifugeRecipe(IHLFluid.getCell("fluidrubbertreesap"), (ItemStack) null,
					new FluidStack(FluidRegistry.getFluid("fluidrubbertreesap"), 200),
					new FluidStack(FluidRegistry.WATER, 600), ItemList.IC2_Resin.get(5L, new Object[] { null }),
					IHLUtils.getOreDictItemStack("dustRawRubber"), new ItemStack(Items.sugar, 1),
					GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Wood, 1L),
					ItemList.Cell_Empty.get(1L, new Object[] { null }), (ItemStack) null, oreListIterator1, 2500, 5);
		}

		if (config.enableHandpump) {
			gregtech.api.util.GT_ModHandler.addCraftingRecipe(((IHLHandPump) ic2_handpump).getItemStack(0),
					new Object[] { "T  ", " P ", "  B", 'T', OrePrefixes.pipeSmall.get(Materials.Steel), 'P', pump, 'B',
							battery_mv_c });
			gregtech.api.util.GT_ModHandler.addCraftingRecipe(((IHLHandPump) ic2_handpump).getItemStack(0),
					new Object[] { "T  ", " P ", "  B", 'T', OrePrefixes.pipeSmall.get(Materials.Steel), 'P', pump, 'B',
							battery_mv_l });
			gregtech.api.util.GT_ModHandler.addCraftingRecipe(((IHLHandPump) ic2_handpump).getItemStack(0),
					new Object[] { "T  ", " P ", "  B", 'T', OrePrefixes.pipeSmall.get(Materials.Steel), 'P', pump, 'B',
							battery_mv_s });
			gregtech.api.util.GT_ModHandler.addCraftingRecipe(
					((AdvancedHandPump) ic2_advanced_handpump).getItemStack(0), new Object[] { "T  ", " P ", "  B", 'T',
							OrePrefixes.pipeSmall.get(Materials.TungstenSteel), 'P', pump_hv, 'B', energy_crystal });
		}

		if (config.enableFlexibleCablesCrafting) {
			gregtech.api.util.GT_ModHandler.addCraftingRecipe(IHLUtils.getThisModItemStack("ironWorkbench"),
					new Object[] { "PPS", "RRh", "RRd", 'P', OrePrefixes.plate.get(Materials.Iron), 'S',
							OrePrefixes.screw.get(Materials.Iron), 'R', OrePrefixes.stick.get(Materials.Iron) });
			gregtech.api.util.GT_ModHandler.addCraftingRecipe(IHLUtils.getThisModItemStack("setOfDies1_5sqmm"),
					new Object[] { "   ", "fPs", "   ", 'P', OrePrefixes.plate.get(Materials.Steel) });
			gregtech.api.util.GT_ModHandler.addCraftingRecipe(
					IHLUtils.getItemStackWithTag("setOfDies1_5sqmm", "transverseSection", 240),
					new Object[] { " f ", " P ", " s ", 'P', OrePrefixes.plate.get(Materials.Steel) });
		}
		if (pfaalimestone != null) {
			GregTech_API.sRecipeAdder.addPulveriserRecipe(pfaalimestone,
					new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4) }, new int[] { 10000 },
					600, 30);
		}

		if (pfaacobblelimestone != null) {
			GregTech_API.sRecipeAdder.addPulveriserRecipe(pfaacobblelimestone,
					new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4) }, new int[] { 10000 },
					600, 30);
		}
		List<ItemStack> gypsumOreList3 = IHLUtils.getEntryListForOre("oreGypsum");
		Iterator<ItemStack> oreListIterator3 = gypsumOreList3.iterator();
		while (oreListIterator3.hasNext()) {
			GregTech_API.sRecipeAdder.addPulveriserRecipe((oreListIterator3.next()),
					new ItemStack[] { IHLUtils.getOreDictItemStack("dustGypsum") }, new int[] { 10000 }, 600, 30);
		}
		GregTech_API.sRecipeAdder.addPulveriserRecipe(IHLUtils.getThisModItemStack("orePotassiumFeldspar"),
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustPotassiumFeldspar") }, new int[] { 10000 }, 600,
				30);
		GregTech_API.sRecipeAdder.addPulveriserRecipe(IHLUtils.getThisModItemStack("oreLimestone"),
				new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4) }, new int[] { 10000 }, 600,
				30);
		GregTech_API.sRecipeAdder.addPulveriserRecipe(IHLUtils.getThisModItemStack("oreSaltpeter"),
				new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustSaltpeter", 4) }, new int[] { 10000 }, 600,
				30);
		GregTech_API.sRecipeAdder.addPulveriserRecipe(IHLUtils.getThisModItemStackWithSize("stickGraphite", 2),
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustGraphite") }, new int[] { 10000 }, 600, 30);
		GregTech_API.sRecipeAdder.addPulveriserRecipe(new ItemStack(Items.brick),
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustBrick") }, new int[] { 10000 }, 600, 30);
		GregTech_API.sRecipeAdder.addChemicalRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 1L),
				GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Empty, 1L),
				new FluidStack(FluidRegistry.WATER, 1500),
				FluidRegistry.getFluidStack("solution.natriumhydroxide", 1000),
				GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Hydrogen, 1L), 950);
		GregTech_API.sRecipeAdder.addChemicalRecipe(
				GT_OreDictUnificator.get(OrePrefixes.dust, Materials.SiliconDioxide, 1L),
				GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Empty, 2L),
				FluidRegistry.getFluidStack("solution.natriumhydroxide", 1000), (FluidStack) null,
				GT_OreDictUnificator.get(OrePrefixes.cell, "LiquidGlass", 2L), 950);
		gregtech.api.util.GT_ModHandler.addShapelessCraftingRecipe(
				IHLUtils.getThisModItemStack("dustCoalElectrodePremix"),
				new Object[] { GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1L),
						IHLUtils.getOreDictItemStack("nuggetTarPitch") });
		gregtech.api.util.GT_ModHandler.addShapelessCraftingRecipe(
				IHLUtils.getThisModItemStackWithSize("dustIrongraphite", 2),
				new Object[] { GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1L),
						GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Graphite, 1L) });
		gregtech.api.util.GT_ModHandler.addSmeltingRecipe(IHLUtils.getThisModItemStack("stickCoalElectrodePremix"),
				GT_OreDictUnificator.get(OrePrefixes.stick, Materials.Coal, 1L));
		gregtech.api.util.GT_ModHandler.addSmeltingRecipe(
				GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Calcite, 1L),
				GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Quicklime, 1L));
		GregTech_API.sRecipeAdder.addFormingPressRecipe(IHLUtils.getThisModItemStack("dustIrongraphite"),
				ItemList.Shape_Mold_Cylinder.get(0L, new Object[0]),
				IHLUtils.getThisModItemStackWithSize("linerIronGraphite", 4), 950, 30);
		GregTech_API.sRecipeAdder.addFormingPressRecipe(IHLUtils.getThisModItemStack("dustCoalElectrodePremix"),
				ItemList.Shape_Extruder_Rod.get(0L, new Object[0]),
				IHLUtils.getThisModItemStack("stickCoalElectrodePremix"), 950, 30);
		GregTech_API.sRecipeAdder.addDistilleryRecipe(
				ItemList.Circuit_Integrated.getWithDamage(0, 0, new Object[] { null }),
				IHLUtils.getFluidStackWithSize("spruceresin", 1000), IHLUtils.getFluidStackWithSize("turpentine", 50),
				3000, 30, false);
		ExtruderTileEntity.addRecipe(IHLUtils.getOreDictItemStackWithSize("dustRawRubber", 5),
				IHLUtils.getOreDictItemStackWithSize("dustTinySulfur", 1),
				IHLUtils.getFluidStackWithSize("molten.rubber", 144 * 5));
		ExtruderTileEntity.addRecipe(IHLUtils.getOreDictItemStackWithSize("dustRawRubber", 20),
				IHLUtils.getOreDictItemStackWithSize("dustSulfur", 1),
				IHLUtils.getFluidStackWithSize("molten.rubber", 144 * 20));
		WoodenRollingMachinePart1TileEntity.addRecipe(new RecipeInputOreDict("dustRawRubber", 5),
				new RecipeInputOreDict("dustTinySulfur", 1),
				IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 20));
		WoodenRollingMachinePart1TileEntity.addRecipe(new RecipeInputOreDict("dustRawRubber", 20),
				new RecipeInputOreDict("dustSulfur", 1),
				IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 80));
		WoodenRollingMachinePart1TileEntity.addRecipe(
				new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 16)),
				new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("fabric", 8)),
				new RecipeInputOreDict("dustGraphite"),
				IHLUtils.getThisModItemStack("pipeRubberWithSulfur"));
	}

	@SuppressWarnings("deprecation")
	private void loadGT6Recipes() {
		ItemStack battery_mv_c = IL.Battery_RE_MV_Cadmium.get(1);
		ItemStack battery_mv_l = IL.Battery_RE_MV_Lithium.get(1);
		ItemStack battery_mv_s = IL.Battery_RE_MV_Sodium.get(1);
		ItemStack energy_crystal = IC2Items.getItem("energyCrystal");
		ItemStack pump = IL.Electric_Pump_MV.get(1, (Object) null);
		ItemStack pump_hv = IL.Electric_Pump_HV.get(1, (Object) null);

		OreDictionary.registerOre("toolLighter", IL.Tool_Lighter_Invar_Full.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_Lighter_Invar_Used.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_Lighter_Plastic_Full.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_Lighter_Plastic_Used.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_Lighter_Platinum_Full.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_Lighter_Platinum_Used.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_MatchBox_Full.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_MatchBox_Used.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_Matches.get(1));
		OreDictionary.registerOre("toolLighter", IL.Tool_Fire_Starter.get(1));

		gregapi.util.UT.Crafting.shaped(
				IHLUtils.getThisModItemStack(MachineBaseBlock.MachineType.ChemicalReactor.unlocalizedName),
				new Object[] { " R ", "PVP", " Mw", Character.valueOf('R'),
						gregapi.data.OP.stick.dat(gregapi.data.MT.StainlessSteel), Character.valueOf('V'),
						gregapi.data.OP.rotor.dat(gregapi.data.MT.StainlessSteel), Character.valueOf('P'),
						gregapi.data.OP.plateCurved.dat(gregapi.data.MT.StainlessSteel), Character.valueOf('M'),
						gregapi.data.OP.casingMachine.dat(gregapi.data.MT.Steel) });

		gregapi.util.UT.Crafting.shaped(
				IHLUtils.getThisModItemStack(MachineBaseBlock.MachineType.CryogenicDistiller.unlocalizedName),
				new Object[] { " R ", "P P", " Mw", Character.valueOf('R'),
						gregapi.data.OP.pipeTiny.dat(gregapi.data.MT.Copper), Character.valueOf('P'),
						gregapi.data.OP.plateCurved.dat(gregapi.data.MT.Copper), Character.valueOf('M'),
						gregapi.data.OP.casingMachine.dat(gregapi.data.MT.Steel) });

		gregapi.util.UT.Crafting.shaped(
				IHLUtils.getThisModItemStack(MachineBaseBlock.MachineType.FluidizedBedReactor.unlocalizedName),
				new Object[] { "   ", "RPR", "VMw", Character.valueOf('R'),
						gregapi.data.OP.pipeTiny.dat(gregapi.data.MT.Titanium), Character.valueOf('P'),
						gregapi.data.OP.pipeHuge.dat(gregapi.data.MT.Titanium), Character.valueOf('V'),
						gregapi.data.OP.rotor.dat(gregapi.data.MT.Titanium), Character.valueOf('M'),
						gregapi.data.OP.casingMachine.dat(gregapi.data.MT.Steel) });

		IRecipeInput[] dsmInputs1 = new IRecipeInput[3];
		ItemStack gregtechfile = IHLUtils.getOtherModItemStackWithDamage("gregtech", "gt.metatool.01", 18, 1);
		dsmInputs1[0] = new RecipeInputItemStack(gregtechfile);
		dsmInputs1[1] = new RecipeInputOreDict("dustDiamond");
		dsmInputs1[2] = new RecipeInputOreDict("dustGunpowder");
		DetonationSprayingMachineTileEntity.addRecipe((new UniversalRecipeInput(null, dsmInputs1)),
				new UniversalRecipeOutput(null, new ItemStack[] { gregtechfile.copy() }, 2));

		if (IHLMod.config.enableRubberTreeSack) {
			long[] chances = { 10000, 2000, 2000, 1000, 100, 0 };
			Recipe.RecipeMap.sCoagulatorRecipes.addRecipe(true, new ItemStack[] {},
					new ItemStack[] { OreDictManager.INSTANCE.getStack(OP.nugget, MT.Rubber, 1) }, null, chances,
					new FluidStack[] { new FluidStack(FluidRegistry.getFluid("fluidrubbertreesap"), 500) },
					new FluidStack[] {}, 64, 0, 0);
		}

		if (IHLMod.config.enableHandpump) {
			gregapi.util.UT.Crafting.shaped(((IHLHandPump) ic2_handpump).getItemStack(0),
					new Object[] { "T  ", " P ", "  B", Character.valueOf('T'), OP.pipeSmall.get(MT.Steel),
							Character.valueOf('P'), pump, Character.valueOf('B'), battery_mv_c });
			gregapi.util.UT.Crafting.shaped(((IHLHandPump) ic2_handpump).getItemStack(0),
					new Object[] { "T  ", " P ", "  B", Character.valueOf('T'), OP.pipeSmall.get(MT.Steel),
							Character.valueOf('P'), pump, Character.valueOf('B'), battery_mv_l });
			gregapi.util.UT.Crafting.shaped(((IHLHandPump) ic2_handpump).getItemStack(0),
					new Object[] { "T  ", " P ", "  B", Character.valueOf('T'), OP.pipeSmall.get(MT.Steel),
							Character.valueOf('P'), pump, Character.valueOf('B'), battery_mv_s });
			gregapi.util.UT.Crafting.shaped(((AdvancedHandPump) ic2_advanced_handpump).getItemStack(0),
					new Object[] { "T  ", " P ", "  B", Character.valueOf('T'), OP.pipeSmall.get(MT.TungstenSteel),
							Character.valueOf('P'), pump_hv, Character.valueOf('B'), energy_crystal });
		}
		if (IHLMod.config.enableFlexibleCablesCrafting) {
			gregapi.util.UT.Crafting.shaped(IHLUtils.getThisModItemStack("ironWorkbench"),
					new Object[] { "PPS", "RRh", "RRd", Character.valueOf('P'), OP.plate.get(MT.Iron),
							Character.valueOf('S'), OP.screw.get(MT.Iron), Character.valueOf('R'),
							OP.stick.get(MT.Iron) });
			gregapi.util.UT.Crafting.shaped(IHLUtils.getThisModItemStack("setOfDies1_5sqmm"),
					new Object[] { "   ", "fPs", "   ", Character.valueOf('P'), OP.plate.get(MT.Steel) });
			gregapi.util.UT.Crafting.shaped(IHLUtils.getItemStackWithTag("setOfDies1_5sqmm", "transverseSection", 240),
					new Object[] { " f ", " P ", " s ", Character.valueOf('P'), OP.plate.get(MT.Steel) });
		}
		if (pfaalimestone != null) {
			Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true, new ItemStack[] { pfaalimestone },
					new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4) }, null,
					new long[] { 10000 }, new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		}
		if (pfaacobblelimestone != null) {
			Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true, new ItemStack[] { pfaacobblelimestone },
					new ItemStack[] { IHLUtils.getOreDictItemStackWithSize("dustCalcite", 4) }, null,
					new long[] { 10000 }, new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		}
		List<ItemStack> gypsumOreList = IHLUtils.getEntryListForOre("oreGypsum");
		Iterator<ItemStack> oreListIterator = gypsumOreList.iterator();
		while (oreListIterator.hasNext()) {
			Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true, new ItemStack[] { oreListIterator.next().copy() },
					new ItemStack[] { IHLUtils.getOreDictItemStack("dustGypsum") }, null, new long[] { 10000 },
					new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		}

		Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getThisModItemStack("orePotassiumFeldspar") },
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustPotassiumFeldspar") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);

		Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getThisModItemStack("oreLimestone") },
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustCalcite") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getThisModItemStack("oreSaltpeter") },
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustSaltpeter") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true, new ItemStack[] { new ItemStack(Items.brick) },
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustBrick") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		Recipe.RecipeMap.sShredderRecipes.addRecipe(true, new ItemStack[] { new ItemStack(Items.brick) },
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustBrick") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getOreDictItemStack("stickGraphite") },
				new ItemStack[] { IHLUtils.getOreDictItemStack("dustSmallGraphite") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		GT_ModHandler.addShapelessCraftingRecipe(IHLUtils.getThisModItemStack("dustCoalElectrodePremix"),
				new Object[] { OreDictManager.INSTANCE.getStack(OP.dust, MT.Coal, 1),
						IHLUtils.getOreDictItemStack("nuggetTarPitch") });
		GT_ModHandler.addShapelessCraftingRecipe(IHLUtils.getThisModItemStackWithSize("dustIrongraphite", 2),
				new Object[] { OreDictManager.INSTANCE.getStack(OP.dust, MT.Iron, 1),
						OreDictManager.INSTANCE.getStack(OP.dust, MT.Graphite, 1) });
		GT_ModHandler.addSmeltingRecipe(IHLUtils.getThisModItemStack("stickCoalElectrodePremix"),
				OreDictManager.INSTANCE.getStack(OP.stick, MT.Coal, 1));
		GT_ModHandler.addSmeltingRecipe(OreDictManager.INSTANCE.getStack(OP.dust, MT.Calcite, 1),
				OreDictManager.INSTANCE.getStack("dustQuicklime", 1));
		Recipe.RecipeMap.sPressRecipes.addRecipe(true,
				new ItemStack[] { OreDictManager.INSTANCE.getStack(OP.dust, MT.Iron, 1),
						OreDictManager.INSTANCE.getStack(OP.dust, MT.Graphite, 1) },
				new ItemStack[] { IHLUtils.getThisModItemStackWithSize("linerIronGraphite", 4) }, null,
				new long[] { 10000 }, new FluidStack[] {}, new FluidStack[] {}, 950, 30, 0);
		Recipe.RecipeMap.sPressRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getThisModItemStack("dustCoalElectrodePremix"),
						IL.Shape_Extruder_Rod.get(0) },
				new ItemStack[] { IHLUtils.getThisModItemStack("stickCoalElectrodePremix") }, null,
				new long[] { 10000 }, new FluidStack[] {}, new FluidStack[] {}, 950, 30, 0);
		Recipe.RecipeMap.sPressRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getThisModItemStack("foilRubberWithSulfur"),
						IL.Shape_Extruder_Casing.get(0) },
				new ItemStack[] { IHLUtils.getThisModItemStack("batteryCellsEbonite") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 950, 30, 0);
		Recipe.RecipeMap.sCompressorRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getThisModItemStackWithSize("foilMica", 4) },
				new ItemStack[] { IHLUtils.getThisModItemStack("plateMica") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 950, 30, 0);
		Recipe.RecipeMap.sMaceratorRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getOreDictItemStack("oreGyubnera") },
				new ItemStack[] { IHLUtils.getOreDictItemStack("crushedGyubnera") }, null, new long[] { 10000 },
				new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		Recipe.RecipeMap.sCentrifugeRecipes.addRecipe(true,
				new ItemStack[] { IHLUtils.getOreDictItemStack("crushedGyubnera") },
				new ItemStack[] { IHLUtils.getOreDictItemStack("crushedPurifiedGyubnera"),
						IHLUtils.getOreDictItemStack("dustPotassiumFeldspar") },
				null, new long[] { 10000 }, new FluidStack[] {}, new FluidStack[] {}, 600, 30, 0);
		Recipe.RecipeMap.sCentrifugeRecipes.addRecipe(true, new ItemStack[] {},
				new ItemStack[] { Ic2Items.resin.copy(), IHLUtils.getOreDictItemStack("dustTinyWood") }, null,
				new long[] { 5000, 10000 },
				new FluidStack[] { new FluidStack(FluidRegistry.getFluid("fluidrubbertreesap"), 569) },
				new FluidStack[] { new FluidStack(FluidRegistry.getFluid("molten.latex"), 140),
						new FluidStack(FluidRegistry.getFluid("glue"), 240),
						new FluidStack(FluidRegistry.getFluid("water"), 100) },
				64, 32, 0);

		ExtruderTileEntity.addRecipe(IHLUtils.getOreDictItemStackWithSize("dustRubber", 5),
				IHLUtils.getOreDictItemStackWithSize("dustTinySulfur", 1),
				IHLUtils.getFluidStackWithSize("molten.rubber", 144 * 5));
		ExtruderTileEntity.addRecipe(IHLUtils.getOreDictItemStackWithSize("dustRubber", 20),
				IHLUtils.getOreDictItemStackWithSize("dustSulfur", 1),
				IHLUtils.getFluidStackWithSize("molten.rubber", 144 * 20));
		WoodenRollingMachinePart1TileEntity.addRecipe(new RecipeInputOreDict("dustRubber", 5),
				new RecipeInputOreDict("dustTinySulfur", 1),
				IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 20));
		WoodenRollingMachinePart1TileEntity.addRecipe(new RecipeInputOreDict("dustRubber", 20),
				new RecipeInputOreDict("dustSulfur", 1),
				IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 80));
		WoodenRollingMachinePart1TileEntity.addRecipe(
				new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 16)),
				new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("fabric", 8)),
				new RecipeInputOreDict("dustGraphite"),
				IHLUtils.getThisModItemStack("pipeRubberWithSulfur"));
	}

	private void loadIC2Recipes() {
		ItemStack crystal = IC2Items.getItem("lapotronCrystal").copy();
		ItemStack advBattery = IC2Items.getItem("advBattery").copy();
		ItemStack chargedReBattery = IC2Items.getItem("chargedReBattery").copy();
		crystal.setItemDamage(OreDictionary.WILDCARD_VALUE);
		advBattery.setItemDamage(OreDictionary.WILDCARD_VALUE);
		chargedReBattery.setItemDamage(OreDictionary.WILDCARD_VALUE);
		Recipes.advRecipes.addRecipe(new ItemStack(electricEvaporatorBlock, 1),
				new Object[] { "CCC", "CCC", " F ", Character.valueOf('C'), IC2Items.getItem("platecopper"),
						Character.valueOf('F'), IC2Items.getItem("electroFurnace") });
		Recipes.advRecipes.addRecipe(new ItemStack(electricEvaporatorBlock, 1),
				new Object[] { "   ", " C ", " F ", Character.valueOf('C'), Ic2Items.electronicCircuit.copy(),
						Character.valueOf('F'), new ItemStack(evaporatorBlock, 1) });
		if (IHLMod.config.enableFlexibleCablesCrafting) {
			ItemStack cutter = Ic2Items.cutter.copy();
			cutter.setItemDamage(OreDictionary.WILDCARD_VALUE);
			ItemStack forgeHammer = Ic2Items.ForgeHammer.copy();
			forgeHammer.setItemDamage(OreDictionary.WILDCARD_VALUE);
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStack("setOfDies1_5sqmm"), new Object[] { "   ", " PC",
					"   ", Character.valueOf('P'), Ic2Items.plateadviron, Character.valueOf('C'), cutter });
			Recipes.advRecipes.addRecipe(IHLUtils.getItemStackWithTag("setOfDies1_5sqmm", "transverseSection", 240),
					new Object[] { " C ", " P ", "   ", Character.valueOf('P'), Ic2Items.plateadviron,
							Character.valueOf('C'), cutter });
			Recipes.advRecipes.addRecipe(IHLUtils.getThisModItemStack("ironWorkbench"), new Object[] { "P  ", "P  ",
					"P H", Character.valueOf('P'), Ic2Items.plateadviron, Character.valueOf('H'), forgeHammer });
		}
		ExtruderTileEntity.addRecipe(IHLUtils.getOreDictItemStackWithSize("itemRubber", 5),
				IHLUtils.getOreDictItemStackWithSize("dustTinySulfur", 1),
				IHLUtils.getFluidStackWithSize("molten.rubber", 144 * 5));
		ExtruderTileEntity.addRecipe(IHLUtils.getOreDictItemStackWithSize("itemRubber", 20),
				IHLUtils.getOreDictItemStackWithSize("dustSulfur", 1),
				IHLUtils.getFluidStackWithSize("molten.rubber", 144 * 20));
		WoodenRollingMachinePart1TileEntity.addRecipe(new RecipeInputOreDict("itemRubber", 5),
				new RecipeInputOreDict("dustTinySulfur", 1),
				IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 20));
		WoodenRollingMachinePart1TileEntity.addRecipe(new RecipeInputOreDict("itemRubber", 20),
				new RecipeInputOreDict("dustSulfur", 1),
				IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 80));
		WoodenRollingMachinePart1TileEntity.addRecipe(
				new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("foilRubberWithSulfur", 16)),
				new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize("fabric", 8)),
				new RecipeInputOreDict("dustGraphite"),
				IHLUtils.getThisModItemStack("pipeRubberWithSulfur"));
	}
}
