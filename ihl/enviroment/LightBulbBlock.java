package ihl.enviroment;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;

import ihl.IHLCreativeTab;
import ihl.items_blocks.IHLItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class LightBulbBlock extends Block implements ITileEntityProvider
{
	public static GlowningAirBlock glowningAir;
	
    public LightBulbBlock(String unlocalizedName1)
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
    
	public static void init()
	{
		glowningAir = new GlowningAirBlock();
		GameRegistry.registerBlock(glowningAir, "glowningAir");
		LightBulbBlock mblock = new LightBulbBlock("lightBulb");
		GameRegistry.registerTileEntity(LightBulbTileEntity.class, "lightBulb");
		SpotlightBlock sblock = new SpotlightBlock("spotlight");
		GameRegistry.registerTileEntity(SpotlightTileEntity.class, "spotlight");
		SpotlightTileEntity.createLightSphereVectors();
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
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess, int x, int y, int z)
    {
    	TileEntity te = iBlockAccess.getTileEntity(x, y, z);
    	if(te!=null && te instanceof LightBulbTileEntity)
    	{
    		LightBulbTileEntity ate = (LightBulbTileEntity) te;
    		setBlockBoundsBasedOnFacing(ate.getFacing());
    	}
    }

    private void setBlockBoundsBasedOnFacing(int facing)
    {
        int var2 = facing & 7;
        float var6 = 0.1875F;
        float var7 = 0.5F;

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
		return new LightBulbTileEntity();
	}
}
