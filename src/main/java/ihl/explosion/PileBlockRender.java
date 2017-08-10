package ihl.explosion;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import ihl.utils.IHLMathUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class PileBlockRender implements ISimpleBlockRenderingHandler {
	public static int renderId;
	public final PileTileEntityRender pileTileEntityRender;

	public PileBlockRender() {
		renderId = RenderingRegistry.getNextAvailableRenderId();
		pileTileEntityRender = new PileTileEntityRender(this);
	}

	@Override
	public int getRenderId() {
		return renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {
		Tessellator tessellator = Tessellator.instance;
		block.setBlockBoundsForItemRender();
		renderblocks.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D,
				renderblocks.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D,
				renderblocks.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D,
				renderblocks.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D,
				renderblocks.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D,
				renderblocks.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D,
				renderblocks.getBlockIconFromSideAndMetadata(block, 5, metadata));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta,
			RenderBlocks blockRenderer) {
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		if (te instanceof PileTileEntity) {
			PileTileEntity pte = (PileTileEntity) te;
			long itemHash = getItemStackHash(pte.content);
			if (this.pileTileEntityRender.subIconIdMap.containsKey(itemHash)) {
				Tessellator tessellator = Tessellator.instance;
				int subIconId = this.pileTileEntityRender.subIconIdMap.get(itemHash);
				int[][][] brightness = new int[3][3][3];
				this.generateBrightnessMatrix(blockAccess, x, y, z, brightness);
				tessellator.setColorOpaque_F(1f, 1f, 1f);
				boolean[] blocksViewAround = this.getBlocksViewAround(blockAccess, x, y, z);
				if (blocksViewAround[0] && blocksViewAround[1] && blocksViewAround[2] && blocksViewAround[3]) {
					this.addFlatTop(tessellator, x, y, z, brightness, subIconId);
				} else if (!blocksViewAround[0] && blocksViewAround[1] && blocksViewAround[2] && blocksViewAround[3]) {
					this.addSlope(tessellator, x, y, z, 0, brightness, subIconId);
				} else if (blocksViewAround[0] && !blocksViewAround[1] && blocksViewAround[2] && blocksViewAround[3]) {
					this.addSlope(tessellator, x, y, z, 1, brightness, subIconId);
				} else if (blocksViewAround[0] && blocksViewAround[1] && !blocksViewAround[2] && blocksViewAround[3]) {
					this.addSlope(tessellator, x, y, z, 2, brightness, subIconId);
				} else if (blocksViewAround[0] && blocksViewAround[1] && blocksViewAround[2] && !blocksViewAround[3]) {
					this.addSlope(tessellator, x, y, z, 3, brightness, subIconId);
				}
				// 2-sided piramid
				else if (blocksViewAround[0] && blocksViewAround[1] && !blocksViewAround[2] && !blocksViewAround[3]) {
					this.addTwoSidedPyramid(tessellator, x, y, z, 2, brightness, subIconId);
				} else if (!blocksViewAround[0] && blocksViewAround[1] && blocksViewAround[2] && !blocksViewAround[3]) {
					this.addTwoSidedPyramid(tessellator, x, y, z, 3, brightness, subIconId);
				} else if (!blocksViewAround[0] && !blocksViewAround[1] && blocksViewAround[2] && blocksViewAround[3]) {
					this.addTwoSidedPyramid(tessellator, x, y, z, 0, brightness, subIconId);
				} else if (blocksViewAround[0] && !blocksViewAround[1] && !blocksViewAround[2] && blocksViewAround[3]) {
					this.addTwoSidedPyramid(tessellator, x, y, z, 1, brightness, subIconId);
				} else {
					this.addPyramid(tessellator, x, y, z, brightness, subIconId);
				}
				if (!blockAccess.getBlock(x, y - 1, z).isOpaqueCube()) {
					this.addBottom(tessellator, x, y, z, brightness, subIconId);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean[] getBlocksViewAround(IBlockAccess blockAccess, int x, int y, int z) {
		// +Z
		// ^
		// [ ][0][ ]
		// [3][x][1] ->+X
		// [ ][2][ ]
		return new boolean[] { blockAccess.getBlock(x, y, z + 1).isOpaqueCube(),
				blockAccess.getBlock(x + 1, y, z).isOpaqueCube(), blockAccess.getBlock(x, y, z - 1).isOpaqueCube(),
				blockAccess.getBlock(x - 1, y, z).isOpaqueCube(), };
	}

	private void addBottom(Tessellator tessellator, int x, int y, int z, int[][][] brightness, int subIconId) {
		double[][] quad1 = new double[4][3];
		quad1[0] = new double[] { 0D, 0D, 1D };
		quad1[1] = new double[] { 0D, 0D, 0D };
		quad1[2] = new double[] { 1D, 0D, 0D };
		quad1[3] = new double[] { 1D, 0D, 1D };
		this.addQuad(tessellator, quad1, x, y, z, ForgeDirection.UP, brightness, subIconId);
	}

	private void addFlatTop(Tessellator tessellator, int x, int y, int z, int[][][] brightness, int subIconId) {
		double[][] quad1 = new double[4][3];
		double height = 0.9D;
		quad1[0] = new double[] { 1D, height, 0D };
		quad1[1] = new double[] { 0D, height, 0D };
		quad1[2] = new double[] { 0D, height, 1D };
		quad1[3] = new double[] { 1D, height, 1D };
		this.addQuad(tessellator, quad1, x, y, z, ForgeDirection.UP, brightness, subIconId);
	}

	private void addSlope(Tessellator tessellator, int x, int y, int z, int rotation,
			int[][][] brightness, int subIconId) {
		double[][] quad = new double[4][3];
		quad[0] = new double[] { 1D,1D, 0D }; // +X -Z
		quad[1] = new double[] { 0D, 1D, 0D }; // -X -Z
		quad[2] = new double[] { 0D, 0D, 1D }; // -X +Z
		quad[3] = new double[] { 1D, 0D, 1D };
		while (rotation-- > 0) {
			rotateQuadByYAxis(quad);
		}
		this.addQuad(tessellator, quad, x, y, z, ForgeDirection.UP, brightness, subIconId);
	}
	
	private void addTwoSidedPyramid(Tessellator tessellator, int x, int y, int z, int rotation,
			int[][][] brightness, int subIconId) {
		double[][] quad = new double[4][3];
		quad[0] = new double[] { 1D, 0D, 0D }; // +X -Z
		quad[1] = new double[] { 0D, 1D, 0D }; // -X -Z
		quad[2] = new double[] { 0D, 0D, 1D }; // -X +Z
		quad[3] = new double[] { 1D, 0D, 1D };
		while (rotation-- > 0) {
			rotateQuadByYAxis(quad);
		}
		this.addQuad(tessellator, quad, x, y, z, ForgeDirection.UP, brightness, subIconId);
	}

	private void addPyramid(Tessellator tessellator, int x, int y, int z, int[][][] brightness, int subIconId) {
		double[][] quad1 = new double[4][3];
		double[][] quad2 = new double[4][3];
		double pileHeight = 0.3D;
		quad1[0] = new double[] { 1D, 0D, 1D };
		quad1[1] = new double[] { 1D, 0D, 0D };
		quad1[2] = new double[] { 0D, 0D, 0D };
		quad1[3] = new double[] { 0.5D, pileHeight, 0.5D };
		quad2 = copyAndRotateQuadByYAxis(quad1,2);
		this.addQuad(tessellator, quad1, x, y, z, ForgeDirection.UP, brightness, subIconId);
		this.addQuad(tessellator, quad2, x, y, z, ForgeDirection.UP, brightness, subIconId);
	}

	private void rotateQuadByYAxis(double[][] quad) {
		for (double[] v : quad) {
			double newV2 = 1 - v[0];
			v[0] = v[2];
			v[2] = newV2;
		}
	}

	private double[][] copyAndRotateQuadByYAxis(double[][] quad, int rotation) {
		double[][] quadOut = new double[4][3];
		for (int i = 0; i < quad.length * 3; i++) {
			quadOut[i / 3][i % 3] = quad[i / 3][i % 3];
		}

		while (rotation-- > 0) {
			rotateQuadByYAxis(quadOut);
		}
		return quadOut;
	}

	private void addQuad(Tessellator tessellator, double[][] quad, int x, int y, int z, ForgeDirection uvmapping,
			int[][][] brightness, int subIconId) {
		int iu = 0;
		int iv = 2;
		if (uvmapping.offsetY == 0) {
			iv = 1;
			if (uvmapping.offsetZ == 0) {
				iu = 2;
			}
		}
		float[] normal = IHLMathUtils.get_triangle_normal(quad);
		float minu = pileTileEntityRender.getSubIconMinU(subIconId);
		float minv = pileTileEntityRender.getSubIconMinV(subIconId);
		float du = pileTileEntityRender.getSubIconDU(subIconId);
		float dv = pileTileEntityRender.getSubIconDV(subIconId);

		double u1 = minu + quad[0][iu] * du;
		double u2 = minu + quad[1][iu] * du;
		double u3 = minu + quad[2][iu] * du;
		double u4 = minu + quad[3][iu] * du;
		double v1 = minv + quad[0][iv] * dv;
		double v2 = minv + quad[1][iv] * dv;
		double v3 = minv + quad[2][iv] * dv;
		double v4 = minv + quad[3][iv] * dv;

		tessellator.setNormal(normal[0], normal[1], normal[2]);
		tessellator.setBrightness(this.getBrightness(quad[0], brightness));
		tessellator.addVertexWithUV(x + quad[0][0], y + quad[0][1], z + quad[0][2], u1, v1);
		tessellator.setBrightness(this.getBrightness(quad[1], brightness));
		tessellator.addVertexWithUV(x + quad[1][0], y + quad[1][1], z + quad[1][2], u2, v2);
		tessellator.setBrightness(this.getBrightness(quad[2], brightness));
		tessellator.addVertexWithUV(x + quad[2][0], y + quad[2][1], z + quad[2][2], u3, v3);
		tessellator.setBrightness(this.getBrightness(quad[3], brightness));
		tessellator.addVertexWithUV(x + quad[3][0], y + quad[3][1], z + quad[3][2], u4, v4);
	}

	public long getItemStackHash(ItemStack stack) {
		if (stack == null) {
			return 0;
		}
		return ((long) Item.getIdFromItem(stack.getItem()) << 31 ^ stack.getItemDamage());
	}

	private void generateBrightnessMatrix(IBlockAccess blockAccess, int x, int y, int z, int[][][] brightness) {
		for (int ix = -1; ix <= 1; ix++)
			for (int iy = -1; iy <= 1; iy++)
				for (int iz = -1; iz <= 1; iz++) {
					Block block = blockAccess.getBlock(ix + x, iy + y, iz + z);
					if (block != null && block != Blocks.air) {
						brightness[ix + 1][iy + 1][iz + 1] = block.getMixedBrightnessForBlock(blockAccess, ix + x,
								iy + y, iz + z);
					} else {
						brightness[ix + 1][iy + 1][iz + 1] = block.getMixedBrightnessForBlock(blockAccess, x, y, z);
					}
				}
	}

	private int getBrightness(double v[], int[][][] brightness) {
		int x1 = v[0] < 0.5d ? 0 : 1;
		int x2 = x1 + 1;
		int y1 = v[1] < 0.5d ? 0 : 1;
		int y2 = y1 + 1;
		int z1 = v[2] < 0.5d ? 0 : 1;
		int z2 = z1 + 1;
		float dx = v[0] < 0.5d ? (float) v[0] * 2f : (float) v[0] * 2f - 1f;
		float dy = v[1] < 0.5d ? (float) v[1] * 2f : (float) v[1] * 2f - 1f;
		float dz = v[2] < 0.5d ? (float) v[2] * 2f : (float) v[2] * 2f - 1f;
		int brightness1 = brightness[x1][y1][z1];
		int brightness2 = brightness[x2][y2][z2];
		float d = IHLMathUtils.sqrt(dx * dx + dy * dy + dz * dz);
		return (int) (brightness1 * (1f - d) * 0.8f + brightness2 * d * 0.8f + brightness[1][1][1] * 0.2f) & 16711935;
	}

	@Override
	public boolean shouldRender3DInInventory(int arg0) {
		return true;
	}
}
