package ihl.crop_harvestors;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class BlobEntityFX extends EntityFX {

	public FluidType fluid = FluidType.RESIN;
	
	public BlobEntityFX(World world, double x, double y, double z) 
    {
        super(world, x, y, z);
        this.noClip = true;
	}
    
    public BlobEntityFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float par14, FluidType fluid1)
    {
   	super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
    this.motionX *= 0.1D;
    this.motionY *= 0.1D;
    this.motionZ *= 0.1D;
    this.motionX += par8;
    this.motionY += par10;
    this.motionZ += par12;
    this.particleScale *= par14;
    this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
    this.noClip = true;
    fluid=fluid1;
    }

	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var8 = ((float)this.particleAge) / (float)this.particleMaxAge * 32.0F;

        if (var8 < 0.0F)
        {
            var8 = 0.0F;
        }

        if (var8 > 1.0F)
        {
            var8 = 1.0F;
        }
        this.renderParticle2(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }
	
    public void renderParticle2(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var12 = 0.1F * this.particleScale;
        float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * par2 - interpPosX);
        float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * par2 - interpPosY);
        float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - interpPosZ);
        par1Tessellator.addVertexWithUV(var13 - par3 * var12 - par6 * var12, var14 - par4 * var12, var15 - par5 * var12 - par7 * var12, 1D, 1D);
        par1Tessellator.addVertexWithUV(var13 - par3 * var12 + par6 * var12, var14 + par4 * var12, var15 - par5 * var12 + par7 * var12, 1D, 0D);
        par1Tessellator.addVertexWithUV(var13 + par3 * var12 + par6 * var12, var14 + par4 * var12, var15 + par5 * var12 + par7 * var12, 0D, 0D);
        par1Tessellator.addVertexWithUV(var13 + par3 * var12 - par6 * var12, var14 - par4 * var12, var15 + par5 * var12 - par7 * var12, 0D, 1D);
    }
	
    @Override
	public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
        this.motionX *= 0.5D;
        this.motionZ *= 0.5D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionY -= 0.001D;
        this.motionY *= 0.96D;
    }
    
    public enum FluidType
    {
    	SAP,
    	RESIN
    }
}
