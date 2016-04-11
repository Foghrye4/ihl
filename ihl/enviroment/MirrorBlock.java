package ihl.enviroment;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.utils.IHLUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MirrorBlock extends Block implements ITileEntityProvider {

	private String unlocalizedName;
	private IIcon blockIconSide;
	
	public MirrorBlock(String unlocalizedName1) 
	{
		super(Material.glass);
		this.setBlockName(unlocalizedName1);
    	GameRegistry.registerBlock(this, unlocalizedName1);
    	this.setCreativeTab(IHLCreativeTab.tab);
    	this.setHardness(0.3f);
    	this.setResistance(0.3f);
    	this.setBlockTextureName("clay");
	}
	
	public static void init()
	{
		MirrorBlock mblock = new MirrorBlock("mirror");
		GameRegistry.registerTileEntity(MirrorTileEntity.class, "mirror");
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new MirrorTileEntity();
	}
	
    @Override
	public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess, int x, int y, int z)
    {
    	TileEntity te = iBlockAccess.getTileEntity(x, y, z);
    	if(te!=null && te instanceof MirrorTileEntity)
    	{
    		MirrorTileEntity ate = (MirrorTileEntity) te;
    		setBlockBoundsBasedOnFacing(ate.getFacing());
    	}
    }
    
    @Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
    	TileEntity te = world.getTileEntity(x, y, z);
    	if(te!=null && te instanceof MirrorTileEntity)
    	{
   		MirrorTileEntity ate = (MirrorTileEntity) te;
        int var2 = ate.getFacing() & 7;
        float var4 = 0.375F;
        float var5 = 0.625F;
        float var6 = 0.5F;
        float var7 = 0.09F;
        if (var2 == 0)
        {
            return AxisAlignedBB.getBoundingBox(x+0.5F - var6,y+1.0F - var7,z+ 0.5F - var6, x+0.5F + var6, y+1.0F, z+0.5F + var6);
        }
        else if (var2 == 1)
        {
            return AxisAlignedBB.getBoundingBox(x+0.5F - var6,y+ 0.0F,z+ 0.5F - var6, x+0.5F + var6, y+var7, z+0.5F + var6);
        }
        else if (var2 == 2)
        {
            return AxisAlignedBB.getBoundingBox(x+0.5F - var6,y+ 0.5F - var6,z+ 1.0F - var7, x+0.5F + var6, y+0.5F + var6, z+1.0F);
        }
        else if (var2 == 3)
        {
            return AxisAlignedBB.getBoundingBox(x+0.5F - var6,y+ 0.5F - var6,z+ 0.0F, x+0.5F + var6, y+0.5F + var6, z+var7);
        }
        else if (var2 == 4)
        {
            return AxisAlignedBB.getBoundingBox(x+1.0F - var7,y+ 0.5F - var6,z+0.5F - var6, x+1.0F, y+0.5F + var6, z+0.5F + var6);
        }
        else if (var2 == 5)
        {
            return AxisAlignedBB.getBoundingBox(x+0.0F,y+ 0.5F - var6, z+0.5F - var6, x+var7, y+0.5F + var6, z+0.5F + var6);
        }
    	}
    	return null;
    }

    private void setBlockBoundsBasedOnFacing(int facing)
    {
        int var2 = facing & 7;
        float var4 = 0.375F;
        float var5 = 0.625F;
        float var6 = 0.5F;
        float var7 = 0.09F;
        if (var2 == 0)
        {
            this.setBlockBounds(0.5F - var6, 1.0F - var7, 0.5F - var6, 0.5F + var6, 1.0F, 0.5F + var6);
        }
        else if (var2 == 1)
        {
            this.setBlockBounds(0.5F - var6, 0.0F, 0.5F - var6, 0.5F + var6, var7, 0.5F + var6);
        }
        else if (var2 == 2)
        {
            this.setBlockBounds(0.5F - var6, 0.5F - var6, 1.0F - var7, 0.5F + var6, 0.5F + var6, 1.0F);
        }
        else if (var2 == 3)
        {
            this.setBlockBounds(0.5F - var6, 0.5F - var6, 0.0F, 0.5F + var6, 0.5F + var6, var7);
        }
        else if (var2 == 4)
        {
            this.setBlockBounds(1.0F - var7, 0.5F - var6, 0.5F - var6, 1.0F, 0.5F + var6, 0.5F + var6);
        }
        else if (var2 == 5)
        {
            this.setBlockBounds(0.0F, 0.5F - var6, 0.5F - var6, var7, 0.5F + var6, 0.5F + var6);
        }
    }
    
    /**
     * Sets the block's bounds for rendering it as an item
     */
    @Override
	public void setBlockBoundsForItemRender()
    {
        float var1 = 0.5F;
        float var2 = 0.5F;
        float var3 = 0.05F;
        this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
    }
    
	@Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
    {
		int var6 = MathHelper.floor_double(player.rotationPitch * 4.0F / 360.0F + 0.5D) & 3;
		int var7 = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        TileEntity t = world.getTileEntity(x, y, z);
        if(t!=null && t instanceof MirrorTileEntity)
        {
        	MirrorTileEntity te = (MirrorTileEntity)t;
        	if(var6==1)
        	{
        		te.setFacing((short) 1);
        	}
        	else if(var6==3)
        	{
        		te.setFacing((short) 0);
        	}
        	else
        	{
        		switch(var7)
	        	{
	        	case 0:
		        	te.setFacing((short) 2);
		        	break;
	        	case 1:
		        	te.setFacing((short) 5);
		        	break;
	        	case 2:
		        	te.setFacing((short) 3);
		        	break;
	        	case 3:
		        	te.setFacing((short) 4);
		        	break;
	        	default:
	        		break;
	        	}
        	}
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":mirror");
	}
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
		this.dropBlockAsItem(world, x, y, z, IHLUtils.getThisModItemStack("dustGlass"));
	}
	/*	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int i,float pos_x,float pos_y,float pos_z){
		TileEntity te = world.getTileEntity(x,y,z);
		if(IC2.platform.isRendering())
		{
			MirrorTileEntity rtu = (MirrorTileEntity)te;
			if(player.getCurrentEquippedItem()==null)
			{
				IC2.platform.messagePlayer(player, "ic2.tooltip.mode", new Object[] {"Facing " + rtu.getFacing()});
			}
		}
		return  false;
	}
	*/
}
