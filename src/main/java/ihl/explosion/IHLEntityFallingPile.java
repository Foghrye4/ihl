package ihl.explosion;

import ihl.utils.IHLUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

public class IHLEntityFallingPile extends EntityItem{

	public IHLEntityFallingPile(World world) {
		super(world);
	}
	
	@Override
    public void onUpdate()
    {
        ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null)
        {
            if (stack.getItem().onEntityItemUpdate(this))
            {
                return;
            }
        }

        if (this.getEntityItem() == null)
        {
            this.setDead();
        }
        else
        {
            this.onEntityUpdate();

            if (this.delayBeforeCanPickup > 0)
            {
                --this.delayBeforeCanPickup;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

            if (flag || this.ticksExisted % 25 == 0)
            {
                if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() == Material.lava)
                {
                    this.motionY = 0.20000000298023224D;
                    this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }

            }

            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;

            if (this.onGround)
            {
                this.motionY *= -0.5D;
            }

            ++this.age;

            ItemStack item = this.getEntityItem();
    
            if (!this.worldObj.isRemote && 
            		(this.age >= lifespan || this.onGround))
            {
                if (item != null)
                {
                    ItemExpireEvent event = new ItemExpireEvent(this, (item.getItem() == null ? 6000 : item.getItem().getEntityLifespan(item, worldObj)));
                    if (MinecraftForge.EVENT_BUS.post(event))
                    {
                        lifespan += event.extraLife;
                    }
                    else
                    {
                        int x = (int)(this.boundingBox.minX);
                        int y = (int)(this.boundingBox.minY);
                        int z = (int)(this.boundingBox.minZ);
                        int i=1;
                        do
                        {
                        	int[] xz = new int[] {0,0,1,0,-1,0};
                        	if(i<xz.length)
                        	{
                        		if(IHLUtils.isBlockCanBeReplaced(worldObj, x+xz[i-1], y, z+xz[i]))
                        		{
                        			x+=xz[i-1];
                        			z+=xz[i];
                            		if(IHLUtils.isBlockCanBeReplaced(worldObj, x, y-1, z))
                            		{
                            			this.setPosition(x+0.5d, y-0.5d, z+0.5d);
                            			return;
                            		}
                                    PileTileEntity pte = new PileTileEntity();
                                    pte.content=this.getEntityItem();
                                    IHLUtils.setBlockAndTileEntityRaw(worldObj, x, y, z, PileBlock.instance, pte);
                            		for(Object player:worldObj.playerEntities)
                            		{
                            			if(player instanceof EntityPlayerMP)
                            			{
                            				EntityPlayerMP playerMP = (EntityPlayerMP)player;
                            				playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x,y,z,worldObj));
                            			}
                            		}

                                	break;
                        		}
                        		else
                        		{
                        			i++;
                        		}
                        	}
                        	else
                        	{
                        		i=1;
                        		y++;
                        	}
                        }
                        while(!IHLUtils.isBlockCanBeReplaced(worldObj, x, y, z) && y < 254);
                        this.setDead();
                    }
                }
                else
                {
                    this.setDead();
                }
            }
    
            if (item != null && item.stackSize <= 0)
            {
                this.setDead();
            }
        }
    }
	
    public void applyEntityCollision(Entity entity){}
}
