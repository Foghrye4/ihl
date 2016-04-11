package ihl.servitor;

import ic2.api.item.IC2Items;
import ihl.IHLMod;
import ihl.IHLModInfo;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class LostHeadEntity extends EntityFlying implements IMob
{
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private Entity targetedEntity;

    /** Cooldown time between target loss and new target aquirement. */
    private int aggroCooldown;
    public int prevAttackCounter;
    public int attackCounter;
    public int attackCounterIncrement;

    private final double psr = 0.5D;
    private final double delta = 0.35D;
    
    private final double damage = 4D;
    private final double knockbackStrength=2D;
    

    public LostHeadEntity(World par1World)
    {
        super(par1World);
        this.setSize(1.0F, 1.0F);
        this.isImmuneToFire = true;
        this.experienceValue = 5;
    }

    public boolean func_110182_bF()
    {
        return this.dataWatcher.getWatchableObjectByte(16) != 0;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    @Override
	protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }
    
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(worldObj.isRemote)
        {
        	double x1,y1,z1,x2,z2;
        	x1=this.prevPosX+psr*Math.sin(-rotationYaw/180D*Math.PI+delta+Math.random()*0.1D-0.05D);
        	z1=this.prevPosZ+psr*Math.cos(-rotationYaw/180D*Math.PI+delta+Math.random()*0.1D-0.05D);
        	x2=this.prevPosX+psr*Math.sin(-rotationYaw/180D*Math.PI-delta+Math.random()*0.1D-0.05D);
        	z2=this.prevPosZ+psr*Math.cos(-rotationYaw/180D*Math.PI-delta+Math.random()*0.1D-0.05D);
        	y1=this.prevPosY+psr*Math.sin(rotationPitch/180D*Math.PI+Math.random()*0.1D-0.05D)+0.4D;
        	IHLMod.proxy.spawnParticle(0,worldObj, x1,y1,z1,0D,0.1D,0D,1F);
        	IHLMod.proxy.spawnParticle(0,worldObj, x2,y1,z2,0D,0.1D,0D,1F);
        }
        //this part was mindlessly taken from EntityArrow.
        Vec3 var17 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 var3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition var4 = this.worldObj.func_147447_a(var17, var3, false, true, false);
        var17 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        var3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (var4 != null)
        {
            var3 = Vec3.createVectorHelper(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);
        }
        
        Entity var5 = null;
        List var6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
        double var7 = 0.0D;
        int var9;
        float var11;

        for (var9 = 0; var9 < var6.size(); ++var9)
        {
            Entity var10 = (Entity)var6.get(var9);

            if (var10.canBeCollidedWith())
            {
                var11 = 0.3F;
                AxisAlignedBB var12 = var10.boundingBox.expand(var11, var11, var11);
                MovingObjectPosition var13 = var12.calculateIntercept(var17, var3);

                if (var13 != null)
                {
                    double var14 = var17.distanceTo(var13.hitVec);

                    if (var14 < var7 || var7 == 0.0D)
                    {
                        var5 = var10;
                        var7 = var14;
                    }
                }
            }
        }
        

        if (var5 != null)
        {
            var4 = new MovingObjectPosition(var5);
        }

        float var19;
        float var26;

        if (var4 != null)
        {
            if (var4.entityHit != null)
            {
                var19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                int var23 = MathHelper.ceiling_double_int(var19 * this.damage);

                DamageSource var21 = DamageSource.causeThrownDamage(this, this);

                if (var4.entityHit.attackEntityFrom(var21, var23))
                {
                    if (var4.entityHit instanceof EntityLivingBase)
                    {
                        EntityLivingBase var24 = (EntityLivingBase)var4.entityHit;

                        var26 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                        if (var26 > 0.0F)
                        {
                            var4.entityHit.addVelocity(this.motionX * this.knockbackStrength * 0.6D / var26, 0.1D, this.motionZ * this.knockbackStrength * 0.6D / var26);
                        }
                    }
                }
            }
        }
    }

    
    @Override
	protected void updateEntityActionState()
    {
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
        {
            this.setDead();
        }

        this.despawnEntity();
        this.prevAttackCounter = this.attackCounter;
        double var1 = this.waypointX - this.posX;
        double var3 = this.waypointY - this.posY;
        double var5 = this.waypointZ - this.posZ;
        double var7 = var1 * var1 + var3 * var3 + var5 * var5;

        if (var7 < 1.0D || var7 > 3600.0D)
        {
            this.waypointX = this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.waypointY = this.posY + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
        }

        if (this.courseChangeCooldown-- <= 0)
        {
            this.courseChangeCooldown += this.rand.nextInt(5) + 2;
            var7 = MathHelper.sqrt_double(var7);

            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, var7))
            {
                this.motionX += var1 / var7 * 0.02D;
                this.motionY += var3 / var7 * 0.02D;
                this.motionZ += var5 / var7 * 0.02D;
            }
            else
            {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.isDead)
        {
            if (!this.worldObj.isRemote)
            {
            	this.playSound(IHLModInfo.MODID+":lostHeadLaughtLong", 10F, this.getSoundPitch());
            }
            this.targetedEntity = null;
        }

        if (this.targetedEntity == null || this.aggroCooldown-- <= 0)
        {
            this.targetedEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 100.0D);

            if (this.targetedEntity != null)
            {
                this.aggroCooldown = 20;
            }
        }

        double var9 = 64.0D;

        if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < var9 * var9)
        {
            double var11 = this.targetedEntity.posX - this.posX;
            double var13 = this.targetedEntity.boundingBox.minY + this.targetedEntity.height / 2.0F - (this.posY + this.height / 2.0F);
            double var15 = this.targetedEntity.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(var11, var15)) * 180.0F / (float)Math.PI;

            if (this.canEntityBeSeen(this.targetedEntity))
            {
                if (this.attackCounter == 70)
                {
                	//play attack scream before attack
                    if (!this.worldObj.isRemote)
                    {
                    	this.playSound(IHLModInfo.MODID+":lostHeadLaught", 10F, this.getSoundPitch());
                    }
                }

                ++this.attackCounter;

                if (this.attackCounter == 80)
                {
                	//play fire sound before attack
                    this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1008, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    double vX = this.targetedEntity.posX-this.posX;
                    double vY = this.targetedEntity.posY+this.targetedEntity.height-this.posY;
                    double vZ = this.targetedEntity.posZ-this.posZ;
                    this.motionX=vX*0.1D;
                    this.motionY=vY*0.1D;
                    this.motionZ=vZ*0.1D;
                    this.courseChangeCooldown=40;
                    this.attackCounter = -60;
                }
            }
            else if (this.attackCounter > 0)
            {
                --this.attackCounter;
            }
        }
        else
        {
            float rYaw=-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI;
            float rYawD=this.rotationYaw-rYaw;
            this.renderYawOffset = this.rotationYaw -= rYawD*0.2F;
            
            if (this.attackCounter > 0)
            {
                --this.attackCounter;
            }
        }

        if (!this.worldObj.isRemote)
        {
            byte var21 = this.dataWatcher.getWatchableObjectByte(16);
            byte var12 = (byte)(this.attackCounter > 10 ? 1 : 0);

            if (var21 != var12)
            {
                this.dataWatcher.updateObject(16, Byte.valueOf(var12));
            }
        }
    }

    /**
     * True if the ghast has an unobstructed line of travel to the waypoint.
     */
    private boolean isCourseTraversable(double par1, double par3, double par5, double par7)
    {
        double var9 = (this.waypointX - this.posX) / par7;
        double var11 = (this.waypointY - this.posY) / par7;
        double var13 = (this.waypointZ - this.posZ) / par7;
        AxisAlignedBB var15 = this.boundingBox.copy();

        for (int var16 = 1; var16 < par7; ++var16)
        {
            var15.offset(var9, var11, var13);

            if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected String getLivingSound()
    {
        return IHLModInfo.MODID+":lostHeadCry";
    }
    
    @Override
	public int getTalkInterval()
    {
        return 400;
    }
    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
    	return IHLModInfo.MODID+":lostHeadScream";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
    	return IHLModInfo.MODID+":lostHeadDeath";
    }

    /**
     * Drop 0-2 items of this living's type
     */
    @Override
	protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.rand.nextInt(2) + this.rand.nextInt(1 + par2);
        int var4;

        if(var3==0)
        {
        	this.entityDropItem(new ItemStack(IHLMod.ihlSkull,1), 1);
        }
        else
        {
            for (var4 = 0; var4 < var3; ++var4)
            {
            	this.entityDropItem(new ItemStack(Items.dye, 1, 15), 1);
            }
        }
        
        var3 = this.rand.nextInt(3) + this.rand.nextInt(1 + par2);
        
        for (var4 = 0; var4 < var3; ++var4)
        {
            this.entityDropItem(IC2Items.getItem("smallTinDust"), 1);
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
	protected float getSoundVolume()
    {
        return 1.0F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
	public boolean getCanSpawnHere()
    {
        return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
	public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
    }
    
    @Override
    public void moveEntityWithHeading(float par1, float par2)
    {
        if (this.isInWater())
        {
            this.moveFlying(par1, par2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
        }
        else if (this.handleLavaMovement())
        {
            this.moveFlying(par1, par2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        }
        else
        {
            float var3 = 0.91F;

            if (this.onGround)
            {
                var3 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
            }

            float var4 = 0.16277136F / (var3 * var3 * var3);
            this.moveFlying(par1, par2, this.onGround ? 0.1F * var4 : 0.02F);
            var3 = 0.98F;

            if (this.onGround)
            {
                var3 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= var3;
            this.motionY *= var3;
            this.motionZ *= var3;
        }
    }
}
