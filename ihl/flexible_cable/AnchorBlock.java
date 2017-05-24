package ihl.flexible_cable;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AnchorBlock extends Block implements ITileEntityProvider {

	public AnchorBlock(String unlocalizedName1) {
		super(Material.circuits);
		this.setBlockName(unlocalizedName1);
		GameRegistry.registerBlock(this, unlocalizedName1);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof AnchorTileEntity) {
				AnchorTileEntity ate = (AnchorTileEntity) te;
				ate.invalidate();
				for (short i = 0; i < 6; i++) {
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
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag) {
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_,
			int p_149668_4_) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":junctionBox");
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess, int x, int y, int z) {
		TileEntity te = iBlockAccess.getTileEntity(x, y, z);
		if (te != null && te instanceof AnchorTileEntity) {
			AnchorTileEntity ate = (AnchorTileEntity) te;
			float portSize = 0.1f;
			float bbMinX = 1f;
			float bbMaxX = 0f;
			float bbMinY = 1f;
			float bbMaxY = 0f;
			float bbMinZ = 1f;
			float bbMaxZ = 0f;
			for (short i = 0; i < 6; i++) {
				if (ate.hasCableOnSide[i]) {
					float pMinX = (float) (ate.energyNetNodes[i].getPortPos(null)[0] - portSize - x);
					float pMaxX = (float) (ate.energyNetNodes[i].getPortPos(null)[0] + portSize - x);
					float pMinY = (float) (ate.energyNetNodes[i].getPortPos(null)[1] - portSize - y);
					float pMaxY = (float) (ate.energyNetNodes[i].getPortPos(null)[1] + portSize - y);
					float pMinZ = (float) (ate.energyNetNodes[i].getPortPos(null)[2] - portSize - z);
					float pMaxZ = (float) (ate.energyNetNodes[i].getPortPos(null)[2] + portSize - z);
					bbMinX = Math.min(pMinX, bbMinX);
					bbMaxX = Math.max(pMaxX, bbMaxX);
					bbMinY = Math.min(pMinY, bbMinY);
					bbMaxY = Math.max(pMaxY, bbMaxY);
					bbMinZ = Math.min(pMinZ, bbMinZ);
					bbMaxZ = Math.max(pMaxZ, bbMaxZ);
				}
			}
			this.setBlockBounds(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY, bbMaxZ);
		}
	}
}
