package ihl.flexible_cable;
import ic2.core.IC2;
import ic2.core.item.tool.ItemToolWrench;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.enviroment.LightBulbTileEntity;
import ihl.utils.IHLUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AnchorBlock extends Block implements ITileEntityProvider {

	private String unlocalizedName;
	private IIcon blockIconSide;
	
	public AnchorBlock(String unlocalizedName1) 
	{
		super(Material.circuits);
		this.setBlockName(unlocalizedName1);
    	GameRegistry.registerBlock(this, unlocalizedName1);
	}
	
	@Override
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta)
	{
		if(!world.isRemote)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te!=null && te instanceof AnchorTileEntity)
			{
				AnchorTileEntity ate = (AnchorTileEntity) te;
				ate.invalidate();
				for(short i=0;i<6;i++)
				{
					ate.energyNetNodes[i].removeAttachedChains();
				}
			}
		}
		super.onBlockPreDestroy(world, x, y, z, meta);
	}
		
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new AnchorTileEntity();
	}
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag){}
	
    @Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
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
	public int getRenderType()
	{
		return -2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) 
	{
		int facing=3;
		int mask[] = {
				0,1,2,3,4,5,
				1,0,3,2,4,5,
				3,2,0,1,4,5,
				2,3,1,0,4,5,
				2,3,5,4,0,1,
				2,3,4,5,1,0
				};
		TileEntity te = world.getTileEntity(x, y, z);
		if(te!=null)
		{
			AnchorTileEntity tebh = (AnchorTileEntity) te;
			facing=tebh.getFacing();
		}

		switch (mask[facing*6+side])
		{
		case 0:
			return this.blockIcon;
		case 1:
			return this.blockIconSide;
		case 2:
			return this.blockIconSide;
		case 3:
			return this.blockIconSide;
		case 4:
			return this.blockIconSide;
		case 5:
			return this.blockIconSide;
		default:
			return this.blockIconSide;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) 
	{
		
		switch(side)
		{
		case 0:
			return this.blockIconSide;
		case 1:
			return this.blockIconSide;
		case 2:
			return this.blockIconSide;
		case 3:
			return this.blockIcon;
		case 4:
			return this.blockIconSide;
		case 5:
			return this.blockIconSide;
		default:
			return this.blockIconSide;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":junctionBoxFront");
   		this.blockIconSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":junctionBox");
	}
	
    @Override
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess, int x, int y, int z)
    {
    	TileEntity te = iBlockAccess.getTileEntity(x, y, z);
    	if(te!=null && te instanceof AnchorTileEntity)
    	{
    		AnchorTileEntity ate = (AnchorTileEntity) te;
    		float portSize=0.1f;
    		boolean firstBB=false;
    		float bbMinX = 1f;
    		float bbMaxX = 0f;
    		float bbMinY = 1f;
    		float bbMaxY = 0f;
    		float bbMinZ = 1f;
    		float bbMaxZ = 0f;
    		for(short i=0;i<6;i++)
    		{
    			if(ate.hasCableOnSide[i])
    			{
        			float pMinX = (float) (ate.energyNetNodes[i].getPortPos(null)[0]-portSize-x);
        			float pMaxX = (float) (ate.energyNetNodes[i].getPortPos(null)[0]+portSize-x);
        			float pMinY = (float) (ate.energyNetNodes[i].getPortPos(null)[1]-portSize-y);
        			float pMaxY = (float) (ate.energyNetNodes[i].getPortPos(null)[1]+portSize-y);
        			float pMinZ = (float) (ate.energyNetNodes[i].getPortPos(null)[2]-portSize-z);
        			float pMaxZ = (float) (ate.energyNetNodes[i].getPortPos(null)[2]+portSize-z);
    	    		bbMinX=Math.min(pMinX,bbMinX);
    	    		bbMaxX=Math.max(pMaxX,bbMaxX);
    	    		bbMinY=Math.min(pMinY,bbMinY);
    	    		bbMaxY=Math.max(pMaxY,bbMaxY);
    	    		bbMinZ=Math.min(pMinZ,bbMinZ);
    	    		bbMaxZ=Math.max(pMaxZ,bbMaxZ);
    			}
    		}
            this.setBlockBounds(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY, bbMaxZ);
    	}
    }
    /*
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int i,float pos_x,float pos_y,float pos_z){
		TileEntity te = world.getTileEntity(x,y,z);
		if(IC2.platform.isSimulating())
		{
			AnchorTileEntity ate = (AnchorTileEntity) te;
			short facing = IHLUtils.getFacingFromPlayerView(player, true);
			System.out.println(ate.energyNetNodes[facing].getGridID());
			System.out.println(ate.getOfferedEnergy());
		}
		return false;
	}*/

}
