package ihl.items_blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.reactor.IReactor;
import ic2.core.Ic2Items;
import ihl.IHLCreativeTab;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.flexible_cable.SetOfDiesMiniGUI;
import ihl.interfaces.IHasTemperature;
import ihl.interfaces.IItemHasMiniGUI;
import ihl.interfaces.ItemMiniGUI;
import ihl.utils.IHLUtils;
public class IHLTool extends Item implements IItemHasMiniGUI{

	private static Map<Integer, IIcon> iconMap = new HashMap();
	private static Map<Integer, String> nameMap = new HashMap();
	private static Map<Integer, String> hintMap = new HashMap();
	private static Map<Integer, Integer> maxToolDamageMap = new HashMap();
	private static Map<Integer, Boolean> isWeaponMap = new HashMap();
	private static Map<Integer, Float> damageVersusEntityMap = new HashMap();
	public static Item instance;
	
	private IIcon dice240;
	
	public IHLTool() 
	{
		super();
		this.setMaxDamage(0);
		this.setNoRepair();
		this.setHasSubtypes(true);
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setUnlocalizedName("ihlTool");
		this.setMaxStackSize(1);
		instance=this;
	}
	
	@Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
		if(itemStack.getItemDamage()!=19 || world.isRemote)
		{
			return itemStack;
		}
    	MovingObjectPosition movingobjectposition = IHLUtils.returnMOPFromPlayer(entityPlayer, world);
    	if(movingobjectposition!=null)
    	{
    		int x=movingobjectposition.blockX;
    		int y=movingobjectposition.blockY;
    		int z=movingobjectposition.blockZ;
    		TileEntity te = world.getTileEntity(x, y, z);
    		if(te instanceof IHasTemperature)
    		{
    			this.setThermometerTemperature(itemStack, entityPlayer, ((IHasTemperature)te).getTemperature());
    		}
    		if(te instanceof IReactor)
    		{
    			this.setThermometerTemperature(itemStack, entityPlayer, ((IReactor)te).getHeat()+273);
    		}
    	}
		return itemStack;
    }
	
	@Override
    public final ItemStack getContainerItem(ItemStack stack)
    {
		ItemStack outStack = stack.copy();
		IHLUtils.damageItemViaNBTTag(outStack, 100);
		if(outStack.stackSize<=0)
    	{
            return null;
    	}
    	else
    	{
    		return outStack;
    	}
    }
	
	@Override
    public final boolean doesContainerItemLeaveCraftingGrid(ItemStack aStack)
    {
        return false;
    }
    
	@Override
    public final boolean hasContainerItem(ItemStack aStack)
    {
        return true;
    }
	
	public static void init()
	{
		if(IHLUtils.getFirstOreDictName(Ic2Items.bronzeAxe).equals(""))
		{
			OreDictionary.registerOre("craftingToolAxe", Ic2Items.bronzeAxe.getItem());
		}
		if(IHLUtils.getFirstOreDictName(Ic2Items.bronzePickaxe).equals(""))
		{
			OreDictionary.registerOre("craftingToolPickaxe", Ic2Items.bronzePickaxe.getItem());
		}
		IHLTool item = new IHLTool(); 
		GameRegistry.registerItem(item,item.getUnlocalizedName());
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			nameMap.put(var1[i].itemDamage,var1[i].unLocalizedName);
			ItemStack stack = new ItemStack(item,1,var1[i].itemDamage);
			stack.stackTagCompound = new NBTTagCompound();
			NBTTagCompound stats = new NBTTagCompound();
			stats.setInteger("MaxDamage", var1[i].maxToolDamage);
			stack.stackTagCompound.setTag("GT.ToolStats", stats);
			if(var1[i].equals(Type.SetOfDies1_5sqmm))
			{
				stack.stackTagCompound.setInteger("transverseSection",15);
			}
			IHLUtils.registerLocally(var1[i].unLocalizedName, stack);
			if(var1[i].hint!=null)
			{
				hintMap.put(var1[i].itemDamage, var1[i].hint);
			}
			isWeaponMap.put(var1[i].itemDamage, var1[i].isWeapon);
			damageVersusEntityMap.put(var1[i].itemDamage, var1[i].damageVersusEntity);
			if(var1[i].oreDictName!=null && var1[i].oreDictName.length>0)
			{
				for(int i1=0;i1<var1[i].oreDictName.length;i1++)
				{
					OreDictionary.registerOre(var1[i].oreDictName[i1], stack);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList)
    {
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			ItemStack stack = IHLUtils.getThisModItemStack(var1[i].unLocalizedName);
	        itemList.add(stack);
	        if(var1[i].equals(Type.SetOfDies1_5sqmm))
	        {
				stack = IHLUtils.getItemStackWithTag(var1[i].unLocalizedName, "transverseSection",240);
		        itemList.add(stack);
	        }
		}
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
		return nameMap.get(stack.getItemDamage());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			iconMap.put(var1[i].itemDamage, register.registerIcon(IHLModInfo.MODID + ":"+var1[i].unLocalizedName));
		}
		dice240 = register.registerIcon(IHLModInfo.MODID + ":setOfDies24sqmm");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int i) 
	{
		if(stack.stackTagCompound!=null && stack.stackTagCompound.hasKey("transverseSection"))
		{
			if(stack.stackTagCompound.getInteger("transverseSection")>=240)
			{
				return dice240;
			}
		}
		return this.getIconFromDamage(stack.getItemDamage());
	}

		
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int i) 
	{
		return iconMap.get(i);
	}
	
	public enum Type
	{
		Thermometer("mercuryThermometer",19,1,0.1f,false,null,(String[])null),
		FiberGlassDie("fiberGlassDieSteel",18,20000,0.5f,false,null,(String[])null),
		SharpenedCarvingKnifeBronze("sharpenedCarvingKnifeBronze",17,2000,3.5f, true, "ihl.mutton_drop_hint", "craftingToolBlade", "craftingToolKnife"),
		GlassBlowingTube("glassBlowingTubeSteel",16,2000,0.5f,false,null,(String[])null),
		PolishingPucks("polishingPucksSteel",15,2000,0.5f,false,null,(String[])null),
		GravelSteel("graverSteelHardened",14,10,0.5f,false,null,(String[])null),
		DiceM10Steel("diceM10x1SteelHardened",13,2000,0.5f,false,null,(String[])null),
		TapM10("tapM10x1SteelHardened",12,2000,0.5f,false,null,(String[])null),
		Vise("viseSteel",11,200000,0.5f,false,null,(String[])null),
		SetOfFiles("setOfFilesSteel",10,20000,1.5f,false,null,"craftingToolFile"),
		HandDrillBronze("handDrillBronze",9,20000,0.5f,false,null,(String[])null),
		HackSawSteel("hackSawSteel",8,200,0.5f,false,null,"craftingToolSaw"),
		DrillSteel("drillSteelHardened",7,200,0.5f,false,null,(String[])null),
		NeedleFile("needleFile",6,2000,0.1f,false,null,(String[])null),
		Chisel("chisel",5,2000,1.5f,false,null,"craftingToolChisel"),
		Grindstone("grindstone",4,2000,0.5f,false,null,(String[])null),
		Hammer("hammer",3,20000,2.5f,false,null,"craftingToolHardHammer","craftingToolForgeHammer"),
		TinSnipsSteel("tinSnipsSteel",2,2000,0.5f,false,null,"craftingToolWireCutter"),
		SetOfDies1_5sqmm("setOfDies1_5sqmm",0,2000,0.5f,false,null,(String[])null);
		Type(String unlocalizedName1,int itemDamage1, int maxToolDamage1, float damageVersusEntity1, boolean isWeapon1, String hint1, String... oreDictName1)
		{
			unLocalizedName=unlocalizedName1;
			itemDamage=itemDamage1;
			maxToolDamage=maxToolDamage1;
			damageVersusEntity=damageVersusEntity1; 
			isWeapon=isWeapon1; 
			hint=hint1;
			oreDictName=oreDictName1;
		}
		public final String unLocalizedName;
		public final String[] oreDictName;
		public final int itemDamage;
		public final int maxToolDamage;
		public final float damageVersusEntity;
		public final boolean isWeapon;
		public final String hint;
	}
	
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
       	info.add(EnumChatFormatting.WHITE+StatCollector.translateToLocal("ihl.durability") +EnumChatFormatting.GREEN + (IHLUtils.getMaxDamageValueViaNBTTag(itemStack) - IHLUtils.getDamageValueViaNBTTag(itemStack))+" / "+IHLUtils.getMaxDamageValueViaNBTTag(itemStack));
       	info.add(EnumChatFormatting.WHITE+StatCollector.translateToLocal("ihl.attack_damage") +EnumChatFormatting.DARK_BLUE + damageVersusEntityMap.get(itemStack.getItemDamage()));
       	if(hintMap.containsKey(itemStack.getItemDamage()))
       	{
       		info.add(EnumChatFormatting.DARK_RED+StatCollector.translateToLocal(hintMap.get(itemStack.getItemDamage())));
       	}
       	if(itemStack.stackTagCompound.hasKey("transverseSection"))
       	{
           	info.add(EnumChatFormatting.WHITE+StatCollector.translateToLocal("ihl.transversesection") +EnumChatFormatting.GREEN + (itemStack.stackTagCompound.getInteger("transverseSection")/10f)+" sq. mm.");
       	}
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
    	IHLUtils.damageItemViaNBTTag(stack, 1);
    	if(target!=null && target instanceof EntitySheep && !target.isChild() && target.getHealth()<=0F)
    	{
    		target.entityDropItem(IHLUtils.getThisModItemStackWithSize("muttonLard", target.worldObj.rand.nextInt(2)+2), 1F);
    	}
        return true;
    }
    
    @Override//That was taken from Gregtech. I think Greg wouldn't mind.
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        if (entity.canAttackWithItem() && !entity.hitByEntity(player))
        {
                float tMagicDamage = entity instanceof EntityLivingBase ? EnchantmentHelper.getEnchantmentModifierLiving(player, (EntityLivingBase)entity) : 0.0F;
                float tDamage = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() + damageVersusEntityMap.get(stack.getItemDamage());

                if (tDamage + tMagicDamage > 0.0F)
                {
                    boolean tCriticalHit = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && entity instanceof EntityLivingBase;

                    if (tCriticalHit && tDamage > 0.0F)
                    {
                        tDamage *= 1.5F;
                    }

                    tDamage += tMagicDamage;

                    if (entity.attackEntityFrom(DamageSource.generic, tDamage))
                    {
                        if (entity instanceof EntityLivingBase)
                        {
                            entity.setFire(EnchantmentHelper.getFireAspectModifier(player) * 4);
                        }

                        int tKnockcack = (player.isSprinting() ? 1 : 0) + (entity instanceof EntityLivingBase ? EnchantmentHelper.getKnockbackModifier(player, (EntityLivingBase)entity) : 0);

                        if (tKnockcack > 0)
                        {
                            entity.addVelocity(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * tKnockcack * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * tKnockcack * 0.5F);
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (tCriticalHit)
                        {
                            player.onCriticalHit(entity);
                        }

                        if (tMagicDamage > 0.0F)
                        {
                            player.onEnchantmentCritical(entity);
                        }

                        if (tDamage >= 18.0F)
                        {
                            player.triggerAchievement(AchievementList.overkill);
                        }

                        player.setLastAttacker(entity);

                        if (entity instanceof EntityLivingBase)
                        {
                            EnchantmentHelper.func_151384_a((EntityLivingBase)entity, player);
                        }

                        EnchantmentHelper.func_151385_b(player, entity);

                        if (entity instanceof EntityLivingBase)
                        {
                            player.addStat(StatList.damageDealtStat, Math.round(tDamage * 10.0F));
                        }
                        entity.hurtResistantTime = isWeaponMap.get(stack.getItemDamage())?0:1;
                    	if(entity!=null && entity instanceof EntitySheep && !((EntityLivingBase) entity).isChild() && ((EntityLivingBase) entity).getHealth()<=0F && isWeaponMap.get(stack.getItemDamage()))
                    	{
                    		entity.entityDropItem(IHLUtils.getThisModItemStackWithSize("muttonLard", entity.worldObj.rand.nextInt(2)+1), 1F);
                    	}
                        player.addExhaustion(0.3F);
                        IHLUtils.damageItemViaNBTTag(stack, 1);
                    }
                }
            }
            if (stack.stackSize <= 0)
            {
                player.destroyCurrentEquippedItem();
            }
            return true;
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
    	return true;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return ((double)IHLUtils.getDamageValueViaNBTTag(stack))/(double)IHLUtils.getMaxDamageValueViaNBTTag(stack);
    }
    
    @Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem)
    {
    	if(stack.stackTagCompound!=null && !world.isRemote)
    	{
    		if(stack.stackTagCompound.hasKey("showTemperature"))
    		{
    			byte timerDelay = stack.stackTagCompound.getByte("showTemperature");
    			if(timerDelay>0)
    			{
    				timerDelay--;
        			if(Math.abs(stack.stackTagCompound.getDouble("playerPosX")-entity.prevPosX)>0.2d ||
        			Math.abs(stack.stackTagCompound.getDouble("playerPosY")-entity.prevPosY)>0.2d ||
        			Math.abs(stack.stackTagCompound.getDouble("playerPosZ")-entity.prevPosZ)>0.2d)
        			{
        				timerDelay=0;
        			}
        			stack.stackTagCompound.setByte("showTemperature",timerDelay);
        			if(timerDelay==0 && entity instanceof EntityPlayer)
        			{
        				((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
        			}
    			}
    		}
    	}
    }
    private void setThermometerTemperature(ItemStack itemStack, EntityPlayer entityPlayer, long l)
    {
		if(itemStack.stackTagCompound==null)
		{
			itemStack.stackTagCompound=new NBTTagCompound();
		}
		itemStack.stackTagCompound.setLong("temperature", l);
		itemStack.stackTagCompound.setByte("showTemperature", (byte) 60);
		itemStack.stackTagCompound.setDouble("playerPosX", entityPlayer.prevPosX);
		itemStack.stackTagCompound.setDouble("playerPosY", entityPlayer.prevPosY);
		itemStack.stackTagCompound.setDouble("playerPosZ", entityPlayer.prevPosZ);
		entityPlayer.inventoryContainer.detectAndSendChanges();

    }

	@Override
	@SideOnly(Side.CLIENT)
	public ItemMiniGUI getMiniGUI(GuiContainer gui, Slot slot)
	{
		if(slot.getHasStack() && slot.getStack().getItemDamage()==0 && slot.getStack().stackTagCompound!=null)
		{
			return new SetOfDiesMiniGUI(gui, slot);
		}
		return null;
	}
}
