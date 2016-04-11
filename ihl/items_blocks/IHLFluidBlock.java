package ihl.items_blocks;

import ihl.IHLModInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class IHLFluidBlock extends BlockFluidClassic {

	private String textureName = "fluidRubberTreeSap";
    @SideOnly(Side.CLIENT)
    protected IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    protected IIcon flowingIcon;
	private boolean flammable;
    
    public IHLFluidBlock(Fluid fluid, Material material, String textureName1, String registryName1) {
            super(fluid, material);
        	textureName = textureName1;
        	GameRegistry.registerBlock(this, registryName1);
    }
    
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
    	return this.blockMaterial==Material.lava?15:0;
    }
    
	public IHLFluidBlock setFlammable(boolean flammable) 
	{
		this.flammable = flammable;
		return this;
	}
	
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return flammable ? 300 : 0;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return flammable ? 5 : 0;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return flammable;
	}

   
    @Override
    public IIcon getIcon(int side, int meta) {
            return (side == 0 || side == 1)? stillIcon : flowingIcon;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
            stillIcon = register.registerIcon(IHLModInfo.MODID + ":" + textureName + "Still");
            flowingIcon = register.registerIcon(IHLModInfo.MODID + ":" + textureName + "Flowing");
    }
    
    @Override
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
            if (world.getBlock(x,  y,  z).getMaterial().isLiquid()) return false;
            return super.canDisplace(world, x, y, z);
    }
    
    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
            if (world.getBlock(x,  y,  z).getMaterial().isLiquid()) return false;
            return super.displaceIfPossible(world, x, y, z);
    } 
    
	@Override
    public int onBlockPlaced(World world, int x, int y, int z, int meta, float xF, float yF, float zF, int meta2)
    {
		int var = super.onBlockPlaced(world, x, y, z, meta, xF, yF, zF, meta2);
		if(!world.isRemote && this.isAirCompound())
		{
			world.setBlockToAir(x, y, z);
		}
		return var;
    }
	
	@Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
    {
		super.onBlockPlacedBy(world, x, y, z, player, itemStack);
		if(!world.isRemote && this.isAirCompound())
		{
			world.setBlockToAir(x, y, z);
		}
    }
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		if(!world.isRemote && this.isAirCompound())
		{
			world.setBlockToAir(x, y, z);
		}
	}
	
	private boolean isAirCompound()
	{
		return this.fluidName.equals("air") || this.fluidName.equals("nitrogen") || this.fluidName.equals("oxygen");
	}
}
