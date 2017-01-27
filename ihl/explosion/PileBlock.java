package ihl.explosion;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLModInfo;
import ihl.items_blocks.IHLItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PileBlock extends Block implements ITileEntityProvider
{
	public static PileBlock instance;
	public static int id;
	protected PileBlock(Material material) {
		super(material);
		this.setBlockName("pileBlock");
		this.setBlockTextureName("tubBronzeIcon");
		instance = this;
		this.setBlockBounds(0f, 0f, 0f, 1f, 0.5f, 1f);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if(!world.isRemote)
		{
			PileTileEntity pte = (PileTileEntity)world.getTileEntity(x, y, z);
			pte.checkAndFall();
		}
	}
	
	@Override
	public int getRenderType()
	{
		return PileBlockRender.renderId;
	}

	@Override
	public TileEntity createNewTileEntity(World word, int metadata) {
		return new PileTileEntity();
	}
	
	public static void init()
	{
		GameRegistry.registerBlock(new PileBlock(Material.sand), IHLItemBlock.class,"pileBlock");
		GameRegistry.registerTileEntity(PileTileEntity.class, "pileBlockTileEntity");
		id = Block.getIdFromBlock(instance);
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
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":fluidAcetyleneFlowing");
	}
	
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z)
	{
		if(!world.isRemote && world.getTileEntity(x,y,z) instanceof PileTileEntity)
    	{
			PileTileEntity te = (PileTileEntity)world.getTileEntity(x,y,z);
			if(entityPlayer.inventory.addItemStackToInventory(te.content))
			{
				te.content=null;
				world.setBlockToAir(x, y, z);
				entityPlayer.inventoryContainer.detectAndSendChanges();
				return true;
			}
    	}
		return false;
	}
	
	@Override
	public void onBlockHarvested(World world,int x,int y,int z,int  meta, EntityPlayer entityPlayer)
	{
		if(!world.isRemote && world.getTileEntity(x,y,z) instanceof PileTileEntity)
    	{
			PileTileEntity te = (PileTileEntity)world.getTileEntity(x,y,z);
			if(entityPlayer.inventory.addItemStackToInventory(te.content))
			{
				te.content=null;
				entityPlayer.inventoryContainer.detectAndSendChanges();
			}
    	}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x,int y,int z,int  meta)
	{
		if(world.getTileEntity(x,y,z) instanceof PileTileEntity)
    	{
			PileTileEntity te = (PileTileEntity)world.getTileEntity(x,y,z);
			if(te.content!=null)
			{
				return te.content.getIconIndex();
			}
    	}
		return this.blockIcon;
	}
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag){}
}
