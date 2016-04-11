package ihl.enviroment;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class GlowningAirBlock extends BlockAir 
{
	public GlowningAirBlock()
	{
		super();
		this.setBlockName("glowningAir");
		this.setLightLevel(1.0f);
		this.setBlockTextureName("glass");
	}

	@Override 
	public boolean isAir(IBlockAccess world, int x, int y, int z)
	{
		return true;
	}
	
	@Override 
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity)
    {
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
        {
            entity.setFire(20);
        }
    }
}
