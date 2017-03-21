package ihl.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderBlocksExt extends RenderBlocks {
	
	public RenderBlocksExt(IBlockAccess blockAccess) {
		super(blockAccess);
	}

	@Override
    public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon)
    {
		this.brightnessTopLeft=16711935;
		this.brightnessTopRight=16711935;
		super.renderFaceYNeg(block,x,y,z,icon);
    }

    public void renderFaceYPos(Block block, double x, double y, double z, IIcon icon)
    {
        if (this.enableAO)
        {
        	System.out.println("AO is on");
        }
        else
        {
        	System.out.println("AO is off");
        }

		this.brightnessTopLeft=16711935;
		this.brightnessTopRight=16711935;
    	super.renderFaceYPos(block, x, y, z, icon);
    }

    public void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon)
    {
		this.brightnessTopLeft=16711935;
		this.brightnessTopRight=16711935;
    	super.renderFaceZNeg(block, x, y, z, icon);
    }

    public void renderFaceZPos(Block block, double x, double y, double z, IIcon icon)
    {
		this.brightnessTopLeft=16711935;
		this.brightnessTopRight=16711935;
    	super.renderFaceZPos(block, x, y, z, icon);
    }

    public void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon)
    {
		this.brightnessTopLeft=16711935;
		this.brightnessTopRight=16711935;
    	super.renderFaceXNeg(block, x, y, z, icon);
    }

    public void renderFaceXPos(Block block, double x, double y, double z, IIcon icon)
    {
		this.brightnessTopLeft=16711935;
		this.brightnessTopRight=16711935;
    	super.renderFaceXPos(block, x, y, z, icon);
    }


}
