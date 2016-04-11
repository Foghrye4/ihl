package ihl.items_blocks;

import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid.IHLFluidType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSubstance extends Item {
	
	private static Map<Integer, IIcon> iconMap = new HashMap();
	private static Map<Integer, String> nameMap = new HashMap();
	private static Map<Integer, String> descriptionMap = new HashMap();
	public static ItemSubstance instance;
	
	public ItemSubstance() 
	{
		super();
		this.setMaxDamage(0);
		this.setNoRepair();
		this.setHasSubtypes(true);
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setUnlocalizedName("ihlSimpleItem");
		instance=this;
	}
	
	public static void init()
	{
		Item breadWithLard = (new ItemFood(4, 0.1F, false)).setUnlocalizedName("breadWithLard").setTextureName(IHLModInfo.MODID+":breadWithLard").setCreativeTab(IHLCreativeTab.tab);
		GameRegistry.registerItem(breadWithLard,"food");
		IHLUtils.registerLocally("breadWithLard", new ItemStack(breadWithLard,1,0));
		ItemSubstance item = new ItemSubstance();
		GameRegistry.registerItem(item,item.getUnlocalizedName());
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			nameMap.put(var1[i].damage,var1[i].unLocalizedName);
			IHLUtils.registerLocally(var1[i].unLocalizedName, new ItemStack(item,1,var1[i].damage));
			if(var1[i].registerInOreDictionary)
			{
				OreDictionary.registerOre(var1[i].oreRegistryName, new ItemStack(item,1,var1[i].damage));
			}
			if(var1[i].description!=null)
			{
				descriptionMap.put(var1[i].damage,var1[i].description);
			}
		}
    	ItemStack emptyCell = IC2Items.getItem("cell");
		IHLFluidType[] var2 = IHLFluidType.values();
		for(int i=0;i<var2.length;i++)
		{
			IHLFluidType type = var2[i];
			if(type.cell!=null)
			{
				OreDictionary.registerOre("cell"+type.fluidName.replaceFirst("fluid", ""), type.cell);
				FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(type.fluid, FluidContainerRegistry.BUCKET_VOLUME), type.cell, emptyCell.copy()));
			}
			else
			{
				ItemStack filledCell = FluidContainerRegistry.fillFluidContainer(new FluidStack(type.fluid, FluidContainerRegistry.BUCKET_VOLUME),emptyCell.copy());
				if(filledCell==null && type.hasCell)
				{
					ItemStack stack = new ItemStack(item,1,type.damage);
					nameMap.put(type.damage,type.cellName);
					IHLUtils.registerLocally(type.cellName, stack);
					type.cell = stack;
					OreDictionary.registerOre("cell"+type.fluidName.replaceFirst("fluid", ""), type.cell);
					FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(type.fluid, FluidContainerRegistry.BUCKET_VOLUME), stack, emptyCell.copy()));
				}
				else
				{
					type.cell=filledCell;
				}
			}
		}
		OreDictionary.registerOre("itemSalt",IHLUtils.getThisModItemStack("dustSalt"));
	}
	
	public static void postInit()
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			if(var1[i].registerInOreDictionary)
			{
				if(var1[i].oreRegistryName.contains("dustTiny"))
				{
					RecipeInputOreDict stack = new RecipeInputOreDict(var1[i].oreRegistryName);;
					Recipes.advRecipes.addShapelessRecipe(IHLUtils.getOreDictItemStackWithSize(var1[i].oreRegistryName,9),new RecipeInputOreDict(var1[i].oreRegistryName.replace("dustTiny", "dust")));
					Recipes.advRecipes.addShapelessRecipe(IHLUtils.getOreDictItemStack(var1[i].oreRegistryName.replace("dustTiny", "dust")),stack,stack,stack,stack,stack,stack,stack,stack,stack);
				}
				if(var1[i].oreRegistryName.contains("ingot") && IHLUtils.hasOreDictionaryEntry(var1[i].oreRegistryName.replace("ingot", "dust")))
				{
					IHLUtils.addIC2MaceratorRecipe(var1[i].oreRegistryName, IHLUtils.getOreDictItemStack(var1[i].oreRegistryName.replace("ingot", "dust")));
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList)
    {
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			ItemStack stack = IHLUtils.getThisModItemStack(var1[i].unLocalizedName);
	        itemList.add(stack);
		}
		IHLFluidType[] var2 = IHLFluidType.values();
		for(int i=0;i<var2.length;i++)
		{
			if(var2[i].hasCell && var2[i].cell!=null)
			{
				itemList.add(var2[i].cell);
			}
		}
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			iconMap.put(var1[i].damage, register.registerIcon(IHLModInfo.MODID + ":"+var1[i].textureName));
		}
		IHLFluidType[] var2 = IHLFluidType.values();
		for(int i=0;i<var2.length;i++)
		{
			IHLFluidType type = var2[i];
			if(var2[i].hasCell)
			{
				iconMap.put(type.damage, register.registerIcon(IHLModInfo.MODID + ":"+type.cellName));
			}
		}
			
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int i) 
	{
		return iconMap.get(i);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
		return nameMap.get(stack.getItemDamage());
	}
	
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
        if(ItemSubstance.descriptionMap.containsKey(itemStack.getItemDamage()))
        {
        	info.add(ItemSubstance.descriptionMap.get(itemStack.getItemDamage()));
        }
    }
	
	public enum Type
	{
		TinySiliconDioxide(182, "dustTinySiliconDioxide",true,"SiO\u2082"),
		TinyIronOxide(181, "dustTinyIronOxide",true,"Fe\u2082O\u2083"),
		TinyManganeseOxide(180, "dustTinyManganeseOxide",true,"Mn\u2083O\u2084"),
		TinyGypsum(179, "dustTinyGypsum",true,"CaSO\u2084\u00B72H\u2082O"),
		SodiumZeoliteCoked(178,"dustSodiumZeoliteCoked",true,"Na[AlSi\u2083O\u2088]+C"),
		SodiumZeolite(177,"dustSodiumZeolite",true,"Na[AlSi\u2083O\u2088]"),
		SodiumAluminate(176,"dustSodiumAluminate",true,"NaAlO\u2082"),
		SodiumHydroxide(175,"dustSodiumHydroxide",true,"NaOH"),
		DustTinyTungsten(174,"dustTinyTungsten",true,"W"),
		BauxiteDust(173, "dustBauxite",true,"Al\u2082O\u2083\u00B7H\u2082O"),
		LithiumIngot(172,"ingotLithium",true,"Li"),
		DustLithiumChloride(171,"dustLithiumChloride",true,"LiCl"),
		CoalPlate(170, "plateCoal", true, "C (amorphic)"),
		GraphitePlate(156, "plateGraphite", true, "C (graphite)"),
		CoalRawPlate(155, "plateRawCoal"),
		//RedstoneSensor(169,"redstoneSensor"),
		//RedstoneEmitter(168,"redstoneEmitter"),
		IncisorSteelDiamondCoated(167,"incisorSteelDiamondCoated"),
		IncisorSteel(166,"incisorSteel"),
		LampHolderPorcelain(165,"lampHolderPorcelain"),
		LampHolderRawPorcelain(164,"lampHolderRawPorcelain"),
		DustIridium(163,"dustIridium",true,"Ir"),
		SiliconTiny(162, "dustTinySilicon",true,"Si"),
		DustMagnesiumOxide(161,"dustMagnesiumOxide",true,"MgO"),
		Silicon(160, "dustSilicon",true,"Si"),
		Bischofite(159,"dustBischofite",true,"MgCl\u2082\u00B76H\u2082O"),
		DustMagnesium(158,"dustMagnesium",true,"Mg"),
		IngotMagnesium(157,"ingotMagnesium",true,"Mg"),
		Ignitron(154,"ignitron"),
		BoronCarbideElectrode(153,"stickBoronCarbide",true,"B\u2084C"),
		LeadFoil(152,"foilLead",true,"Pb"),
		DustLeadPlumbate(151,"dustLeadPlumbate",true,"Pb\u2082PbO\u2084"),
		VacuumSwitch(150,"vacuumSwitch"),
		HallSensor(149,"hallSensor"),
		BatteryCells(148,"batteryCellsEbonite"),
		//BatteryCellsRaw(147,"batteryCellsRawEbonite"),
		MuscoviteGetinax(146,"plateMica",true,"KAl\u2082(AlSi\u2083O\u2081\u2080)(OH)\u2082"),
		BoronCarbide(145,"dustBoronCarbide",true,"B\u2084C"),
		DustBoricAcid(144,"dustBoricAcid",true,"H\u2083BO\u2083"),
		DustDatolite(143,"dustDatolite",true,"H\u2082Ca\u2082B\u2082Si\u2082O\u2081\u2080"),
		DustFerrite(142,"dustFerrite",true,"Li\u2082Fe\u2082O\u2084"),
		RingFerrite(141,"ringFerrite",true,"Li\u2082Fe\u2082O\u2084"),
		RingRawFerrite(140,"ringRawFerrite"),
		LithiumOxide(139,"dustLithiumOxide",true,"Li\u2082O"),
		SolderingAlloyDust(138,"dustSolderingAlloy",true,"Sn\u2089Sb"),
		AntimonyTinyDust(137,"dustTinyAntimony",true,"Sb"),
		InsulatorPorcelain(136,"insulatorPorcelain"),
		InsulatorRawPorcelain(135,"insulatorRawPorcelain"),
		AntimonyDust(134,"dustAntimony",true,"Sb"),
		AntimonyOxide(133,"dustAntimonyOxide",true,"Sb\u2082O\u2083"),
		StibniteDust(132,"dustStibnite",true,"Sb\u2082S\u2083"),
		CrushedPurifiedStibnite(131,"crushedPurifiedStibnite",true,"Sb\u2082S\u2083"),
		CrushedStibnite(130,"crushedStibnite",true,"Sb\u2082S\u2083"),
		RingPorcelain(129,"ringPorcelain"),
		RingRawPorcelain(128,"ringRawPorcelain"),
		CopperFoil(127,"foilCopper",true,"Cu"),
		CrushedPurifiedBauxite(126, "crushedPurifiedBauxite",true,"Al\u2082O\u2083\u00B7H\u2082O"),
		CrushedBauxite(125, "crushedBauxite",true,"Al\u2082O\u2083�H\u2082O + SiO\u2082, Fe\u2082O\u2083"),
		SodiumSulfide(124,"dustSodiumSulfide",true,"NaS"),
		SodiumSulfate(123,"dustSodiumSulfate",true,"NaSO\u2084"),
		dustWood(122,"dustWood",true),
		Kenotron(121,"valveTube1C21P"),
		HighVolatgeCapacitor(120,"highVoltageCapacitor"),
		MuscovitePaper(119,"foilMica",true,"KAl\u2082(AlSi\u2083O\u2081\u2080)(OH)\u2082"),
		MuscoviteDust(118,"dustMica",true,"KAl\u2082(AlSi\u2083O\u2081\u2080)(OH)\u2082"),
		GaedesPumpBarrel(117, "gaedesPumpBarrelPorcelain"),
		RawGaedesPumpBarrel(116, "gaedesPumpBarrelRawPorcelain"),
		GlassDust(115, "dustGlass",true),
		Gu81m(114, "gu-81m"),
		TungstenFoil(113, "foilTungsten",true),
		TungstenIngot(112, "ingotTungsten",true),
		TungstenPlate(111, "plateTungsten",true),
		TungstenHotPlate(110, "plateHotTungsten",true),
		CrushedPurifiedCinnabar(109, "crushedPurifiedCinnabar",true,"HgS"),
		CrushedCinnabar(108, "crushedCinnabar",true,"HgS"),
		OvenRawPorcelain(107,"ovenRawPorcelain"),
		PrecipitatorCondenserRawPorcelain(106,"precipitatorCondenserRawPorcelain"),
		ChimneyKneeRawPorcelain(105,"chimneyKneeRawPorcelain"),
		TungstenDust(104, "dustTungsten",true,"W"),
		ElectrolysisBathPorcelain(103,"electrolysisBathPorcelain"),
		ElectrolysisBathRawPorcelain(102,"electrolysisBathRawPorcelain"),
		Porcelain(99,"dustPorcelain",true),
		FoilGold(98, "foilGold",true),
		TungsticAcid(97, "dustTungsticAcid",true,"WO\u2083\u00B7H\u2082O"),
		TurboCompressorSetOfMoldedParts(96,"turboCompressorSetOfMoldedPartsBronze"),
		TungstenOxide(94, "dustTungstenOxide",true,"WO\u2083"),
		CalciumTungstate(93, "dustCalciumTungstate",true,"CaWO\u2084"),
		SodiumHydrogenSulfate(92, "dustSodiumHydrogenSulfate",true,"NaHSO\u2084"),
		Salt(91, "dustSalt",true,"NaCl"),
		CalciumChloride(90, "dustCalciumChloride",true,"CaCl\u2082"),
		PotassiumSulphate(89, "dustPotassiumSulphate",true,"K\u2082SO\u2084"),
		RockSalt(88, "dustRockSalt",true,"KCl"),
		CrushedPurifiedGyuibnera(87, "crushedPurifiedGyubnera",true,"(Mn,Fe)WO\u2084 (5:1)"),
		CrushedGyuibnera(86, "crushedGyubnera",true,"(Mn,Fe)WO\u2084 (5:1)"),
		ManganeseOxide(85, "dustManganeseOxide",true,"Mn\u2083O\u2084"),
		IronOxide(84, "dustIronOxide",true,"Fe\u2082O\u2083"),
		SmallRound(83,"smallRoundSteel"),
		SmallRoundPolished(82,"smallRoundPolishedSteel"),
		RollingMachineSetOfMoldedParts(81,"rollingMachineSetOfMoldedPartsSteel"),
		PistonCylinder(80, "pistonCylinderSteel"),
		PipelineAccessoriesSteel(77, "pipelineAccessoriesSteel"),
		DustTrona(76, "dustTrona",true,"Na\u2082CO\u2083�NaHCO\u2083\u00B72H\u2082O"),
		HighPressureVessel(75, "highPressureVesselSteel"),
		GasJet(74, "gasJetSteel"),
		GasReducerSteel(73, "gasReducerSteel"),
		HotSteelIngot(70, "ingotHotSteel", true),
		BarD10Gold(69, "barD10Gold"),
		CalciumCarbide(68, "dustCalciumCarbide",true,"CaC\u2082"),
		foilRubber(67,"foilRubber", true,"(C\u2085H\u2088)n"),
		Fabric(66,"fabric"),
		ThinRubberWithSulfur(65,"foilRubberWithSulfur"),
		Bucket_tarPitch(64,"bucket_tarPitch"),
		DetonationSprayingMachineSetOfMoldedParts(63,"detonationSprayingMachineSetOfMoldedPartsBronze"),
		SetOfPartsForLVElemotorSteel(62,"setOfPartsForLVElemotorSteel"),
		//PlateNonVulcanizedRubber(60, "plateNonVulcanizedRubber"),
		TapM10SteelHot(59, "tapM10x1SteelHot"),
		DiceM10SteelHot(58, "diceM10x1SteelHot"),
		NailSteel(57,"nailSteel"),
		NutM10x1Steel(56,"nutM10x1Steel"),
		GraverSteel(55, "graverSteel"),
		GraverSteelHot(54, "graverSteelHot"),
		DrillSteel(53, "drillSteel"),
		DrillSteelHot(52, "drillSteelHot"),
		BarD10Steel(51, "barD10Steel"),
		BarD10SteelHot(50, "barD10SteelHot"),
		SawBladeSteel(49, "sawBladeSteel"),
		SawBladeSteelHot(48, "sawBladeSteelHot"),
		SawBladeSteelHardened(47, "sawBladeSteelHardened"),
		LinerIronGraphite(46, "linerIronGraphite"),
		LinerIronGraphiteHot(45, "linerIronGraphiteHot"),
		LinerIronGraphiteGreased(44, "linerIronGraphiteGreased"),
		BoltM10x1Steel(43, "boltM10x1Steel"),
		ExtruderSetOfMoldedParts(42, "extruderSetOfMoldedPartsSteel"),
		MeshGlass(41, "meshGlass"),
		TapM10Steel(39, "tapM10x1Steel"),
		DiceM10Steel(38, "diceM10x1Steel"),
		BlankSetOfFilesSteel(37, "blankSetOfFilesSteel"),
		HandDrillSetOfMoldedPartsBronze(36, "handDrillSetOfMoldedPartsBronze"),
		ViseSetOfMoldedPartsSteel(34, "viseSetOfMoldedPartsSteel"),
		DustIrongraphite(33, "dustIrongraphite",true),
		PlateSteelHot(32, "plateHotSteel"),
		Chisel(31, "blankChiselSteel"),
		CrucibleMixture(30, "crucibleMixture"),
		GraphiteDust(29, "dustGraphite", true, "C (graphite)"),
		BrickDust(28, "dustBrick", true),
		CalciumSoap(27, "ingotCalciumSoap"),
		Stearin(26, "ingotStearin"),
		MuttonLard(25, "muttonLard"),
		CarvingKnifeBronze(24, "carvingKnifeBronze"),
		DehydratedGypsum(23, "dustDehydratedGypsum"),
		BlankNeedleFileSteel(22, "blankNeedleFileSteel"),
		HammerHead(21, "toolHeadHammerSmallSteel"),
		SpringSteel(19, "springSteel"),
		CoalElectrodePremix(18, "dustCoalElectrodePremix"),
		rawCoalElectrode(17, "stickCoalElectrodePremix"),
		FoilSteel(16, "foilSteel",true),
		StickSteel(15, "stickSteel",true),
		Quicklime(14, "dustQuicklime",true,"CaO"),
		Calcite(13, "dustCalcite",true,"CaCO\u2082"),
		Gypsum(12, "dustGypsum",true,"CaSO\u2084\u00B72H\u2082O"),
		PotassiumFeldspar(11, "dustPotassiumFeldspar",true,"K[AlSi\u2083O\u2088]"),
		Apatite(10, "gemApatite",true,"Ca\u2085[PO\u2084]\u2083(F,Cl,OH)"),
		Saltpeter(9, "dustSaltpeter",true,"KNO\u2083"),
		TarPitch(8, "ingotTarPitch",true),
		nuggetTarPitch(7, "nuggetTarPitch",true),
		Graphite_Electrode(6, "stickGraphite",true, "C (graphite)"),
		Coal_Electrode(5, "stickCoal",true, "C (amorphic)"),
		Carborundum(4, "dustCarborundum",true,"SiC"),
	    IridiumAndSodiumOxide(3, "dustIridiumAndSodiumOxide",true,"IrO\u2082 + Na\u2082O"),
		PlatinumGroupSludge(2, "dustPlatinumGroupSludge",true),
		TinyPlatinumGroupSludge(1, "dustTinyPlatinumGroupSludge",true),
		SodiumPeroxide(0, "dustSodiumPeroxide",true,"Na\u2082O\u2082");
		Type(int damage1, String unlocalizedName1)
		{
			damage=damage1;
			textureName=unLocalizedName=unlocalizedName1;
		}
		Type(int damage1,String unlocalizedName1, boolean registerInOreDictionary1)
		{
			damage=damage1;
			textureName=oreRegistryName=unLocalizedName=unlocalizedName1;
			registerInOreDictionary=registerInOreDictionary1;
		}
		Type(int damage1,String unlocalizedName1, boolean registerInOreDictionary1,String description1)
		{
			damage=damage1;
			textureName=oreRegistryName=unLocalizedName=unlocalizedName1;
			registerInOreDictionary=registerInOreDictionary1;
			description=description1;
		}
		Type(int damage1,String unlocalizedName1, boolean registerInOreDictionary1,String description1, String textureName1)
		{
			damage=damage1;
			oreRegistryName=unLocalizedName=unlocalizedName1;
			registerInOreDictionary=registerInOreDictionary1;
			description=description1;
			textureName=textureName1;
		}
		public int damage;
		public String unLocalizedName;
		public String oreRegistryName;
		public String description;
		public String textureName;
		public boolean registerInOreDictionary=false;
	}

}