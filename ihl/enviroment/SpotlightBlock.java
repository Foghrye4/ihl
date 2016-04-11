package ihl.enviroment;

import ic2.core.IC2;
import ihl.IHLCreativeTab;
import ihl.items_blocks.IHLItemBlock;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class SpotlightBlock extends Block implements ITileEntityProvider
{

    public SpotlightBlock(String unlocalizedName1)
    {
        super(Material.glass);
        this.setStepSound(soundTypeGlass);
		this.setBlockName(unlocalizedName1);
    	GameRegistry.registerBlock(this,IHLItemBlock.class, unlocalizedName1);
        this.setHardness(0.3F);
        this.setResistance(0.5F);
        this.setCreativeTab(IHLCreativeTab.tab);
        this.setBlockTextureName("glass");
    }
    
	@Override
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}
    
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
	public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
	public int getRenderType()
    {
        return -2;
    }

    @Override
	public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
	public boolean isNormalCube()
    {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) 
	{
		return new SpotlightTileEntity();
	}
	
	@Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
    {
        TileEntity t = world.getTileEntity(x, y, z);
        if(IC2.platform.isSimulating() && t instanceof SpotlightTileEntity)
        {
        	SpotlightTileEntity te = (SpotlightTileEntity)t;
        	te.setDirectionVector(player);
        }
    }
	
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int i,float pos_x,float pos_y,float pos_z)
	{
		TileEntity te = world.getTileEntity(x,y,z);
		if(IC2.platform.isSimulating() && te instanceof SpotlightTileEntity)
		{
			((SpotlightTileEntity)te).setDirectionVector(player);
			return true;
		}
		return false;
	}

	
	
	
}