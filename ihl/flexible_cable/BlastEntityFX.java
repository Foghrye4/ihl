package ihl.flexible_cable;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class BlastEntityFX extends EntityFX {
	
	public BlastEntityFX(World world, double x, double y, double z) 
    {
        super(world, x, y, z);
        this.renderDistanceWeight = 5.0D;
	}
    
    public BlastEntityFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float par14)
    {
    	super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
    	this.motionX *= 0.10000000149011612D;
    	this.motionY *= 0.10000000149011612D;
    	this.motionZ *= 0.10000000149011612D;
   		this.motionX += par8;
   		this.motionY += par10;
   		this.motionZ += par12;
   		this.particleScale *= par14;
   		this.particleMaxAge = 3;
   		this.particleAge=0;
   		this.noClip = false;
   		this.renderDistanceWeight = 6.0D;
    }

	@Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var8 = this.particleTextureIndexX / 2.0F;
        float var9 = var8 + 0.5f;
        float var10 = this.particleTextureIndexY / 2.0F;
        float var11 = var10 + 0.5f;
        float var12 = 0.1F * this.particleScale;
        float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * par2 - interpPosX);
        float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * par2 - interpPosY);
        float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - interpPosZ);
        par1Tessellator.addVertexWithUV(var13 - par3 * var12 - par6 * var12, var14 - par4 * var12, var15 - par5 * var12 - par7 * var12, var9, var11);
        par1Tessellator.addVertexWithUV(var13 - par3 * var12 + par6 * var12, var14 + par4 * var12, var15 - par5 * var12 + par7 * var12, var9, var10);
        par1Tessellator.addVertexWithUV(var13 + par3 * var12 + par6 * var12, var14 + par4 * var12, var15 + par5 * var12 + par7 * var12, var8, var10);
        par1Tessellator.addVertexWithUV(var13 + par3 * var12 - par6 * var12, var14 - par4 * var12, var15 + par5 * var12 - par7 * var12, var8, var11);
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
        this.setParticleTextureIndex(this.particleAge * 3 / this.particleMaxAge);
    }
    
    @Override
    public void setParticleTextureIndex(int par1)
    {
            this.particleTextureIndexX = par1 % 2;
            this.particleTextureIndexY = par1 / 2;
    }

}
