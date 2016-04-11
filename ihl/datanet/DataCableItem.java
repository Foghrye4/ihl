package ihl.datanet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import ihl.IHLCreativeTab;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.flexible_cable.NodeEntity;
import ihl.flexible_cable.PowerCableNodeEntity;
import ihl.interfaces.IDataCableHolder;
import ihl.interfaces.IEnergyNetNode;
import ihl.interfaces.IWire;
import ihl.items_blocks.FlexibleCableItem;
import ihl.utils.IHLUtils;

public class DataCableItem extends FlexibleCableItem {
    
	private static Map<Integer, IIcon> iconMap = new HashMap();
	private static Map<Integer, String> nameMap = new HashMap();
	private static Map<Integer, String> descriptionMap = new HashMap();
	public static DataCableItem dataCableInstance; 
	
    public DataCableItem() 
    {
    	super();
		this.isDataCable=true;
		this.setUnlocalizedName("dataCable");
		dataCableInstance=this;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList)
    {
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			ItemStack stack = IHLUtils.getThisModWireItemStackWithLength(var1[i].unlocalizedName,16);
	        itemList.add(stack);
		}
    }
    
	public static void init()
	{
		DataCableItem item = new DataCableItem();
		GameRegistry.registerItem(item,item.getUnlocalizedName());
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			nameMap.put(var1[i].damage,var1[i].unlocalizedName);
			IHLUtils.registerLocally(var1[i].unlocalizedName, new ItemStack(item,1,var1[i].damage));
			if(var1[i].description!=null)
			{
				descriptionMap.put(var1[i].damage,var1[i].description);
			}
		}

	}

	@Override
    protected void connect(TileEntity t1, short facing, TileEntity t2, short facing2, ItemStack stack)
    {
    	IDataCableHolder te = (IDataCableHolder)t1;
    	IDataCableHolder te1 = (IDataCableHolder)t2;
		NBTTagCompound cable = (NBTTagCompound) stack.stackTagCompound.copy();
		int l2=stack.stackTagCompound.getInteger("length");
		int fl=stack.stackTagCompound.getInteger("fullLength");
		cable.setInteger("fullLength", fl-l2);
		cable.setInteger("length", fl-l2);
		cable.setBoolean("firstConnection", false);
		cable.setInteger("connectorX1", t1.xCoord);
		cable.setInteger("connectorY1", t1.yCoord);
		cable.setInteger("connectorZ1", t1.zCoord);
		cable.setInteger("connectorDimensionId1", t1.getWorldObj().provider.dimensionId);
		if(te.addDataCable(cable) && te1.addDataCable(cable))
		{
			Contact[] c = te.getContacts(stack.stackTagCompound.getInteger("chainUID"));
			Contact[] c1 = te1.getContacts(stack.stackTagCompound.getInteger("chainUID"));
			for(int i=0;i<c.length;i++)
			{
				c[i].establishLink(c1[i]);
			}
		}
    }
	 
	 	@Override
	    protected NodeEntity newNode(World world, double ppx, double ppy, double ppz, ItemStack stack, int can, int x, int y, int z)
	    {
    		NodeEntity node = new  NodeEntity(world);
			node.setPosition(ppx, ppy, ppz);
			node.setChainUniqueID(stack.stackTagCompound.getInteger("chainUID"));
			short facing = stack.stackTagCompound.getShort("connectorFacing");
			int dimensionId = stack.stackTagCompound.getInteger("connectorDimensionId");
			node.setAnchor(x, y, z,facing,dimensionId);
			node.chainArrangeNumber=can;
			node.colorIndex = 0xFF0000;
			node.type=3;
			world.spawnEntityInWorld(node);
			return node;
	    }


    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
        if(itemStack.stackTagCompound!=null)
        {
        	info.add("Length " + itemStack.stackTagCompound.getInteger("length") +"m");
        }
    }

	@Override
	public String getTag() 
	{
		return "length";
	}

	@Override
	public String getTagSecondary() 
	{
		return "fullLength";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			iconMap.put(var1[i].damage, register.registerIcon(IHLModInfo.MODID + ":"+var1[i].unlocalizedName));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int i) 
	{
		return iconMap.get(i);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
		return nameMap.get(stack.getItemDamage());
	}
	
	public enum Type
	{
		Pin8DataCable(0,"EightPinDataCable");
		Type(int damage1, String unlocalizedName1)
		{
			damage=damage1;
			unlocalizedName=unlocalizedName1;
		}
		public int damage;
		public String unlocalizedName;
		public String description;
	}

	@Override
	public boolean isSameWire(ItemStack stack1,ItemStack stack2) 
	{
		return stack1.getItem()==stack2.getItem();
	}
}
