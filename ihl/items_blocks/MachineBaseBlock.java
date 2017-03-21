package ihl.items_blocks;

import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.item.tool.ItemToolCutter;
import ihl.IHLCreativeTab;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.flexible_cable.BatterySwitchUnitTileEntity;
import ihl.flexible_cable.IronWorkbenchTileEntity;
import ihl.flexible_cable.RectifierTransformerUnitTileEntity;
import ihl.interfaces.IEnergyNetNode;
import ihl.interfaces.IMultiPowerCableHolder;
import ihl.processing.chemistry.ChemicalReactorTileEntity;
import ihl.processing.chemistry.CryogenicDistillerTileEntity;
import ihl.processing.chemistry.ElectrolysisBathTileEntity;
import ihl.processing.chemistry.FluidizedBedReactorTileEntity;
import ihl.processing.chemistry.FractionatorBottomTileEntity;
import ihl.processing.chemistry.FractionatorCoverTileEntity;
import ihl.processing.chemistry.FractionatorSectionTileEntity;
import ihl.processing.chemistry.GaedesMercuryRotaryPumpTileEntity;
import ihl.processing.chemistry.LabElectrolyzerTileEntity;
import ihl.processing.chemistry.LeadOvenTileEntity;
import ihl.processing.chemistry.LoomTileEntity;
import ihl.processing.chemistry.PaperMachineTileEntity;
import ihl.processing.chemistry.RefluxCondenserTileEntity;
import ihl.processing.metallurgy.AchesonFurnanceTileEntity;
import ihl.processing.metallurgy.CoilerTileEntity;
import ihl.processing.metallurgy.DetonationSprayingMachineTileEntity;
import ihl.processing.metallurgy.ExtruderTileEntity;
import ihl.processing.metallurgy.GasWeldingStationTileEntity;
import ihl.processing.metallurgy.ImpregnatingMachineTileEntity;
import ihl.processing.metallurgy.LathePart1TileEntity;
import ihl.processing.metallurgy.LathePart2TileEntity;
import ihl.processing.metallurgy.MuffleFurnanceTileEntity;
import ihl.processing.metallurgy.RollingMachinePart1TileEntity;
import ihl.processing.metallurgy.RollingMachinePart2TileEntity;
import ihl.processing.metallurgy.VulcanizationExtrudingMoldTileEntity;
import ihl.processing.metallurgy.WireMillTileEntity;
import ihl.processing.metallurgy.WoodenRollingMachinePart1TileEntity;
import ihl.processing.metallurgy.WoodenRollingMachinePart2TileEntity;
import ihl.utils.IHLUtils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MachineBaseBlock extends Block implements ITileEntityProvider{
	
	MachineType type;
	private static List<MachineBaseBlock> instances = new ArrayList<MachineBaseBlock>();
	
	@SideOnly(Side.CLIENT)
	IIcon textureSide, 
	textureBack, 
	textureFrontMuffleFurnance, 
	textureTop, 
	textureTopAchesonFurnance,
	textureTopGoldFurnace, 
	textureFrontGoldFurnace, 
	textureSideGoldFurnace, 
	textureFrontCryogenicDistiller, 
	textureTopCryogenicDistiller,
	textureBackCryogenicDistiller,
	textureFrontChemicalReactor,
	textureLeftMachineCasing,
	textureTopMachineCasing,
	textureRightMachineCasing,
	textureFrontMachineCasing,
	textureFrontPaperMachine, 
	textureBackMachineCasing,
	bronzeTubTop,
	bronzeTubSide,
	steel,
   	redPaint,
	greenPaint,
   	rubberInsulatedCase,
   	powerPort;

	public MachineBaseBlock(MachineType type1) 
	{
		super(Material.iron);
		this.type=type1;
        this.setCreativeTab(IHLCreativeTab.tab);
        this.setBlockName(type.unlocalizedName);
        this.setHardness(2F);
        this.setResistance(1F);
        instances.add(this);
	}
	
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if(IC2.platform.isSimulating())
		{
			TileEntity te = world.getTileEntity(x,y,z);
			if(world.getBlock(x, y+1, z)==Blocks.fire)
			{
				if(te instanceof DetonationSprayingMachineTileEntity)
				{
					((DetonationSprayingMachineTileEntity)te).operate();
					world.setBlockToAir(x, y+1, z);
				}
			}
		}
	}
	
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta)
	{
		if(!world.isRemote)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te!=null)
			{
				if(te instanceof IEnergyNetNode)
				{
					IEnergyNetNode ate = (IEnergyNetNode) te;
	        		ate.removeAttachedChains();
				}
				if(te instanceof IMultiPowerCableHolder)
				{
					IMultiPowerCableHolder ate = (IMultiPowerCableHolder) te;
	        		ate.removeAttachedChains();
				}
				if(te instanceof IronWorkbenchTileEntity)
				{
					IronWorkbenchTileEntity iwb=(IronWorkbenchTileEntity) te;
					iwb.dropContents();
				}
				else if(te instanceof IInventory)
				{
					IInventory inventory = (IInventory)te;
					for(int i = 0; i< inventory.getSizeInventory();i++)
					{
						if(inventory.getStackInSlot(i)!=null)world.spawnEntityInWorld(new EntityItem(world, x, y+1, z, inventory.getStackInSlot(i)));
					}
				}
			}
		}
		super.onBlockPreDestroy(world, x, y, z, meta);
	}

	@SuppressWarnings("rawtypes")
	@Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
		switch(this.type)
		{
		case BronzeTub:
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.1F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.9F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.0F, 0.0F, 0.9F, 1.0F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBoundsForItemRender();
			break;
        default:
        	super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        	break;
		}
    }
	
    @Override
	public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
    
	public static void init()
	{
		MachineType[] var1 = MachineType.values();
		for(int i=0;i<var1.length;i++)
		{
			if(!var1[i].unlocalizedName.equalsIgnoreCase(IHLMod.config.preventMachineBlockRegistrationName))
			{
				GameRegistry.registerBlock(new MachineBaseBlock(var1[i]), IHLItemBlock.class,var1[i].unlocalizedName);
				GameRegistry.registerTileEntity(var1[i].teclass,var1[i].unlocalizedName);
			}
		}
		IHLUtils.getThisModBlock("tubBronze").setBlockTextureName("tubBronzeIcon");
	}
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
		if(this.type.itemDroppedOnBlockDestroy!=null)
		{
			this.dropBlockAsItem(world, x, y, z, this.type.itemDroppedOnBlockDestroy.copy());
		}
		else
		{
			super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, flag);
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		TileEntity newTE = null;
		try 
		{
				newTE = (TileEntity) type.teclass.newInstance();
		}
		catch (InstantiationException e) 
		{
				e.printStackTrace();
		}
		catch (IllegalAccessException e) 
		{
				e.printStackTrace();
		}
		return newTE;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenSide");
	   	this.textureBack = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenBack");
		this.textureSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenSide");
	   	this.textureTop = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenTop");
	   	this.textureFrontMuffleFurnance = par1IconRegister.registerIcon(IHLModInfo.MODID + ":muffleOvenFront");
	   	this.textureTopAchesonFurnance = par1IconRegister.registerIcon(IHLModInfo.MODID + ":achesonOvenTop");
	   	this.textureTopGoldFurnace = par1IconRegister.registerIcon(IHLModInfo.MODID + ":porcelainFurnaceTop");
	   	this.textureFrontGoldFurnace = par1IconRegister.registerIcon(IHLModInfo.MODID + ":porcelainFurnaceFront");
	   	this.textureSideGoldFurnace = par1IconRegister.registerIcon(IHLModInfo.MODID + ":porcelainFurnaceSide");
	   	this.textureLeftMachineCasing = par1IconRegister.registerIcon(IHLModInfo.MODID + ":fiberglassSpinneretsLeft");
	   	this.textureTopMachineCasing = par1IconRegister.registerIcon(IHLModInfo.MODID + ":fiberglassSpinneretsTop");
	   	this.textureRightMachineCasing = par1IconRegister.registerIcon(IHLModInfo.MODID + ":fiberglassSpinneretsRight");
	   	this.textureFrontMachineCasing = par1IconRegister.registerIcon(IHLModInfo.MODID + ":fiberglassSpinneretsFront");
	   	this.textureBackMachineCasing = par1IconRegister.registerIcon(IHLModInfo.MODID + ":fiberglassSpinneretsBack");
	   	this.textureFrontCryogenicDistiller = par1IconRegister.registerIcon(IHLModInfo.MODID + ":cryogenicDistillerOxygenSide");
	   	this.textureTopCryogenicDistiller = par1IconRegister.registerIcon(IHLModInfo.MODID + ":cryogenicDistillerNitrogenSide");
	   	this.textureBackCryogenicDistiller = par1IconRegister.registerIcon(IHLModInfo.MODID + ":cryogenicDistillerInputSide");
	   	this.textureFrontChemicalReactor = par1IconRegister.registerIcon(IHLModInfo.MODID + ":chemicalReactorOutputSide");
	   	this.textureFrontPaperMachine = par1IconRegister.registerIcon(IHLModInfo.MODID + ":paperMachineFront");
	   	this.bronzeTubTop = par1IconRegister.registerIcon(IHLModInfo.MODID + ":tubBronzeTop");
	   	this.bronzeTubSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":tubBronzeSide");
	   	this.steel = par1IconRegister.registerIcon(IHLModInfo.MODID + ":steel");
	   	this.redPaint = par1IconRegister.registerIcon(IHLModInfo.MODID + ":redPaint");
	   	this.greenPaint = par1IconRegister.registerIcon(IHLModInfo.MODID + ":greenPaint");
	   	this.rubberInsulatedCase = par1IconRegister.registerIcon(IHLModInfo.MODID + ":rubberInsulatedCase");
	   	this.powerPort = par1IconRegister.registerIcon(IHLModInfo.MODID + ":powerPort");
	}
   	
	@Override
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}
	
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int i,float pos_x,float pos_y,float pos_z){
		TileEntity te = world.getTileEntity(x,y,z);
		if(IC2.platform.isSimulating())
		{
		if(te instanceof DetonationSprayingMachineTileEntity && player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
		{
			((DetonationSprayingMachineTileEntity)te).operate();
			return true;
		}
		if(te instanceof LathePart2TileEntity)
		{
			LathePart2TileEntity lte = ((LathePart2TileEntity)te);
			te = world.getTileEntity(x-ForgeDirection.getOrientation(lte.getFacing()).offsetX,y-ForgeDirection.getOrientation(lte.getFacing()).offsetY,z-ForgeDirection.getOrientation(lte.getFacing()).offsetZ);
		}
		if(te instanceof RollingMachinePart2TileEntity)
		{
			RollingMachinePart2TileEntity lte = ((RollingMachinePart2TileEntity)te);
			te = world.getTileEntity(x-ForgeDirection.getOrientation(lte.getFacing()).offsetX,y-ForgeDirection.getOrientation(lte.getFacing()).offsetY,z-ForgeDirection.getOrientation(lte.getFacing()).offsetZ);
		}
		if(te instanceof WoodenRollingMachinePart2TileEntity)
		{
			WoodenRollingMachinePart2TileEntity lte = ((WoodenRollingMachinePart2TileEntity)te);
			te = world.getTileEntity(x-ForgeDirection.getOrientation(lte.getFacing()).offsetX,y-ForgeDirection.getOrientation(lte.getFacing()).offsetY,z-ForgeDirection.getOrientation(lte.getFacing()).offsetZ);
		}
		if(te instanceof RectifierTransformerUnitTileEntity)
		{
			RectifierTransformerUnitTileEntity rtu = (RectifierTransformerUnitTileEntity)te;
			if(player.getCurrentEquippedItem()==null)
			{
				if(IC2.keyboard.isModeSwitchKeyDown(player))
				{
					rtu.switchModeDown();
				}
				else
				{
					rtu.switchModeUp();
				}
				IC2.platform.messagePlayer(player, "ic2.tooltip.mode", new Object[] {" Voltage output was set to x" + rtu.mode});
			}
		}
		if(te instanceof IEnergyNetNode)
		{
			IEnergyNetNode node = (IEnergyNetNode)te;
			if(player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemToolCutter)
			{
				node.removeAttachedChains();
			}
		}
		}
		return te instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, (IHasGui)te) : true) : false;
	}
	
	
    /**
     * Called when the block is placed in the world.
     */
	@Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
    {
        TileEntity t = world.getTileEntity(x, y, z);
        if(t!=null && t instanceof IWrenchable)
        {
        		((IWrenchable)t).setFacing(IHLUtils.getFacingFromPlayerView(player,false));
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) 
	{
		int facing=3;
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IWrenchable)
		{
			IWrenchable tebh = (IWrenchable) te;
			facing=tebh.getFacing();
		}
		return this.getIconFromFacing(facing, side);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) 
	{
		if(this.type==MachineType.IronWorkbench)
		{
			return this.steel;
		}
		if(this.type==MachineType.RectifierTransformerUnit)
		{
			switch(side)
			{	
				case 0:
					return this.bronzeTubSide;
				case 1:
					return this.textureSideGoldFurnace;
				case 2:
					return this.redPaint;
				case 3:
					return this.greenPaint;
				case 4:
					return this.rubberInsulatedCase;
				case 5:
					return this.powerPort;
			}
		}
		return this.getIconFromFacing(3, side);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromFacing(int facing, int side) 
	{
		int mask[] = {
				0,1,2,3,4,5,
				1,0,3,2,4,5,
				2,3,0,1,4,5,
				2,3,1,0,4,5,
				2,3,5,4,0,1,
				2,3,4,5,1,0
				};
		switch(this.type)
		{
			case RefluxCondenser:
				return this.steel;
			case RectifierTransformerUnit:
				return this.bronzeTubSide;
			case IronWorkbench:
				return this.steel;
			default:
		}
		switch (mask[facing*6+side])
		{
			case 0:
				switch(this.type)
				{
				case BronzeTub:
					return this.bronzeTubSide;
				case AchesonFurnace:
					return this.blockIcon;
				case MuffleFurnace:
					return this.textureFrontMuffleFurnance;
				case LeadOven:
					return this.textureFrontGoldFurnace;
				case WireMill:
					return this.textureFrontMachineCasing;
				case LabElectrolyzer:
					return this.textureTopMachineCasing;
				case FluidizedBedReactor:
					return this.textureTopMachineCasing;
				case ChemicalReactor:
					return this.textureFrontChemicalReactor;
				case CryogenicDistiller:
					return this.textureFrontCryogenicDistiller;
				case PaperMachine:
					return this.textureFrontPaperMachine;
				default:
					break;
				}
			case 1:
				switch(this.type)
				{
					case BronzeTub:
						return this.bronzeTubSide;
					case LeadOven:
						return this.textureSideGoldFurnace;
					case WireMill:
						return this.textureBackMachineCasing;
					case CryogenicDistiller:
						return this.textureBackCryogenicDistiller;
					case PaperMachine:
						return this.textureBackCryogenicDistiller;
					case LabElectrolyzer:
						return this.textureBackCryogenicDistiller;
					case FluidizedBedReactor:
						return this.textureBackCryogenicDistiller;
					case ChemicalReactor:
						return this.textureBackCryogenicDistiller;
					default:
						return this.textureBack;
				}

			case 2:
				switch(this.type)
				{
					case BronzeTub:
						return this.bronzeTubSide;
					case LeadOven:
						return this.textureSideGoldFurnace;
					case WireMill:
						return this.textureBackMachineCasing;
					case CryogenicDistiller:
						return this.textureBackMachineCasing;
					case PaperMachine:
						return this.textureBackMachineCasing;
					case LabElectrolyzer:
						return this.textureBackMachineCasing;
					case FluidizedBedReactor:
						return this.textureBackMachineCasing;
					case ChemicalReactor:
						return this.textureBackMachineCasing;
					default:
						return this.textureTop;
				}
			case 3:
				switch(this.type)
				{
				case BronzeTub:
					return this.bronzeTubTop;
				case AchesonFurnace:
					return this.textureTopAchesonFurnance;
				case MuffleFurnace:
					return this.textureTop;
				case LeadOven:
					return this.textureTopGoldFurnace;
				case WireMill:
					return this.textureTopMachineCasing;
				case CryogenicDistiller:
					return this.textureTopCryogenicDistiller;
				case PaperMachine:
					return this.textureTopMachineCasing;
				case LabElectrolyzer:
					return this.textureTopMachineCasing;
				case FluidizedBedReactor:
					return this.textureTopMachineCasing;
				case ChemicalReactor:
					return this.textureTopMachineCasing;
				default:
					return this.textureTop;
				}
			case 4:
				switch(this.type)
				{
					case BronzeTub:
						return this.bronzeTubSide;
					case LeadOven:
						return this.textureSideGoldFurnace;
					case WireMill:
						return this.textureRightMachineCasing;
					case CryogenicDistiller:
						return this.textureRightMachineCasing;
					case PaperMachine:
						return this.textureRightMachineCasing;
					case LabElectrolyzer:
						return this.textureRightMachineCasing;
					case FluidizedBedReactor:
						return this.textureRightMachineCasing;
					case ChemicalReactor:
						return this.textureRightMachineCasing;
					default:
						return this.textureSide;
				}
			case 5:
				switch(this.type)
				{
					case BronzeTub:
						return this.bronzeTubSide;
					case LeadOven:
						return this.textureSideGoldFurnace;
					case CryogenicDistiller:
						return this.textureLeftMachineCasing;
					case PaperMachine:
						return this.textureLeftMachineCasing;
					case WireMill:
						return this.textureLeftMachineCasing;
					case ChemicalReactor:
						return this.textureLeftMachineCasing;
					case LabElectrolyzer:
						return this.textureLeftMachineCasing;
					case FluidizedBedReactor:
						return this.textureLeftMachineCasing;
					default:
						return this.textureSide;
				}
			default:
				return this.textureSide;
		}
	}

	@Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int metadata, int flag)
    {
		return true;
    }
    
    @Override
	public boolean canProvidePower()
    {
        return true;
    }
	
	@Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int metadata)
    {
        return this.isProvidingWeakPower(world, x, y, z, metadata);
    }

    public enum MachineType
    {
    	
    	IronWorkbench("ironWorkbench",IronWorkbenchTileEntity.class, false, true, null),
    	ElectrolysisBath("electrolysisBath",ElectrolysisBathTileEntity.class, false, IHLUtils.getThisModItemStack("plateGraphite")),
    	RectifierTransformerUnit("rectifierTransformerUnit",RectifierTransformerUnitTileEntity.class, false, true, IHLUtils.getThisModItemStack("foilSteel")),
    	BatterySwitchUnit("batterySwitchUnit",BatterySwitchUnitTileEntity.class, false, IHLUtils.getThisModItemStack("foilSteel")),
    	FractionatorBottom("fractionatorBottom",FractionatorBottomTileEntity.class, false, IHLUtils.getThisModItemStack("foilSteel")),
    	FractionatorSection("fractionatorSection",FractionatorSectionTileEntity.class, false, IHLUtils.getThisModItemStackWithSize("ringPorcelain",16)),
    	FractionatorCover("fractionatorCover",FractionatorCoverTileEntity.class, false, IHLUtils.getThisModItemStack("foilSteel")),
    	RefluxCondenser("refluxCondenser",RefluxCondenserTileEntity.class, false,false, IHLUtils.getThisModItemStack("foilSteel")),
    	PaperMachine("paperMachine",PaperMachineTileEntity.class, true, IHLUtils.getThisModItemStack("stickSteel")),
    	GaedesMercuryRotaryPump("gaedesMercuryRotaryPump",GaedesMercuryRotaryPumpTileEntity.class,false, IHLUtils.getThisModItemStack("foilSteel")),
    	LabElectrolyzer("labElectrolyzer",LabElectrolyzerTileEntity.class, true, IHLUtils.getThisModItemStack("stickGraphite")),
    	FluidizedBedReactor("fluidizedBedReactor",FluidizedBedReactorTileEntity.class, true, IHLUtils.getThisModItemStack("highPressureVesselSteel")),
    	ChemicalReactor("chemicalReactor",ChemicalReactorTileEntity.class, true, IHLUtils.getThisModItemStack("highPressureVesselSteel")),
    	CryogenicDistiller("cryogenicDistiller",CryogenicDistillerTileEntity.class, true, IHLUtils.getThisModItemStack("highPressureVesselSteel")),
    	GasWeldingStation("gasWeldingStation",GasWeldingStationTileEntity.class, false, null),
    	WoodenRollingMachine1("woodenRollingMachinePart1",WoodenRollingMachinePart1TileEntity.class, false, IHLUtils.getThisModItemStack("barD10Steel")),
    	WoodenRollingMachine2("woodenRollingMachinePart2",WoodenRollingMachinePart2TileEntity.class, false, IHLUtils.getThisModItemStack("barD10Steel")),
    	WireMill("wireMill",WireMillTileEntity.class, true, IHLUtils.getThisModItemStack("stickSteel")),
    	VulcanizationExtrudingMold("vulcanizationExtrudingMold",VulcanizationExtrudingMoldTileEntity.class, false, IHLUtils.getThisModItemStack("dustCarborundum")),
    	Extruder("extruder",ExtruderTileEntity.class, false, IHLUtils.getThisModItemStack("dustCarborundum")),
    	RollingMachine1("rollingMachinePart1",RollingMachinePart1TileEntity.class, false, IHLUtils.getThisModItemStack("stickSteel")),
    	RollingMachine2("rollingMachinePart2",RollingMachinePart2TileEntity.class, false, IHLUtils.getThisModItemStack("stickSteel")),
    	Loom("loom", LoomTileEntity.class, false, new ItemStack(Items.stick)),
    	Coiler("coiler",CoilerTileEntity.class, false, new ItemStack(Items.stick)),
    	Lathe1("lathePart1",LathePart1TileEntity.class, false, IHLUtils.getThisModItemStack("stickSteel")),
    	Lathe2("lathePart2",LathePart2TileEntity.class, false, IHLUtils.getOreDictItemStack("plateSteel")),
    	LeadOven("leadOven",LeadOvenTileEntity.class, true, IHLUtils.getThisModItemStack("dustPorcelain")),
    	DetonationSprayingMachine("cannonBronze",DetonationSprayingMachineTileEntity.class, false, new ItemStack(Items.stick)),
    	BronzeTub("tubBronze",ImpregnatingMachineTileEntity.class, false, true, null),
    	AchesonFurnace("achesonFurnance",AchesonFurnanceTileEntity.class, true, new ItemStack(Items.brick)),
    	MuffleFurnace("muffleFurnance",MuffleFurnanceTileEntity.class, true, new ItemStack(Items.brick));
    	MachineType(String unlocalizedName1, Class<? extends TileEntity> teclass1, boolean isNormalBlock1,ItemStack itemDroppedOnBlockDestroy1)
    	{
    		unlocalizedName=unlocalizedName1;
    		teclass=teclass1;
    		isNormalBlock=isNormalBlock1;
    		if(isNormalBlock)
    		{
    			specialBlockRendererId=0;
    		}
    		else
    		{
    			specialBlockRendererId=-2;
    		}
    		itemDroppedOnBlockDestroy=itemDroppedOnBlockDestroy1;
    	}
    	MachineType(String unlocalizedName1, Class<? extends TileEntity> teclass1, boolean isNormalBlock1,boolean hasSpecialBlockRenderer1, ItemStack itemDroppedOnBlockDestroy1)
    	{
    		unlocalizedName=unlocalizedName1;
    		teclass=teclass1;
    		isNormalBlock=isNormalBlock1;
    		hasSpecialBlockRenderer=hasSpecialBlockRenderer1;
    		itemDroppedOnBlockDestroy=itemDroppedOnBlockDestroy1;
    		
    	}
		public String unlocalizedName;
		Class<? extends TileEntity> teclass;
		boolean isNormalBlock=true;
		boolean hasSpecialBlockRenderer=false;
		int specialBlockRendererId=-2;
		ItemStack itemDroppedOnBlockDestroy;
    }
    
	@Override
	public int getRenderType()
	{
		if(this.type.hasSpecialBlockRenderer)
		{
			return IHLMod.proxy.shareBlockRendererByMachineType(this.type);
		}
		return this.type.specialBlockRendererId;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return this.type==null?false:this.type.isNormalBlock;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return this.type.isNormalBlock;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getAdditionalIconsForBlockRenderer(int flag)
	{
		switch(this.type)
		{
			case BronzeTub:
				return this.bronzeTubSide;
			default:
				return this.blockIcon;
		}
	}

}