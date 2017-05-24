package ihl.items_blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IEnergyTile;
import ihl.IHLCreativeTab;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.flexible_cable.AnchorTileEntity;
import ihl.flexible_cable.NodeEntity;
import ihl.flexible_cable.PowerCableNodeEntity;
import ihl.interfaces.ICableHolder;
import ihl.interfaces.IEnergyNetNode;
import ihl.interfaces.IMultiPowerCableHolder;
import ihl.interfaces.IWire;
import ihl.utils.IHLUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class FlexibleCableItem extends Item implements IWire {

	public static FlexibleCableItem instance;
	public final Set<String> yellowColoredWires = new HashSet<String>(3);

	public FlexibleCableItem() {
		super();
		yellowColoredWires.add("Gold");
		yellowColoredWires.add("Copper");
		yellowColoredWires.add("Bronze");
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setMaxStackSize(1);
		this.setFull3D();
		instance = this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
		itemList.add(IHLUtils.getUninsulatedWire("Copper", 160, 15));
		itemList.add(IHLUtils.getUninsulatedWire("Copper", 160, 240));
		itemList.add(IHLUtils.getUninsulatedWire("Steel", 160, 15));
		itemList.add(IHLUtils.getUninsulatedWire("Steel", 160, 240));
		itemList.add(IHLUtils.getInsulatedWire("Copper", 160, 15, "RawRubber", 100));
		itemList.add(IHLUtils.getInsulatedWire("Copper", 160, 240, "RawRubber", 100));
		itemList.add(IHLUtils.getInsulatedWire("Steel", 160, 15, "RawRubber", 100));
		itemList.add(IHLUtils.getInsulatedWire("Steel", 160, 240, "RawRubber", 100));
		itemList.add(IHLUtils.getInsulatedWire("Copper", 160, 15, "Rubber", 100));
		itemList.add(IHLUtils.getInsulatedWire("Copper", 160, 240, "Rubber", 100));
		itemList.add(IHLUtils.getInsulatedWire("Steel", 160, 15, "Rubber", 100));
		itemList.add(IHLUtils.getInsulatedWire("Steel", 160, 240, "Rubber", 100));
	}

	public static void init() {
		GameRegistry.registerItem(new FlexibleCableItem(), "copperWire");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7,
			float par8, float par9, float par10) {
		TileEntity t = world.getTileEntity(x, y, z);
		short facing = IHLUtils.getFacingFromPlayerView(player, true);
		if (!world.isRemote && t instanceof IEnergyTile && !(t instanceof IMultiPowerCableHolder)
				&& !(t instanceof ICableHolder)) {
			ForgeDirection direction = ForgeDirection.getOrientation(facing);
			x += direction.offsetX;
			y += direction.offsetY;
			z += direction.offsetZ;
			t = world.getTileEntity(x, y, z);
			if (IHLUtils.isBlockCanBeReplaced(world, x, y, z)) {
				world.setBlock(x, y, z, IHLMod.cableAnchorBlock);
			} else if (!(t instanceof AnchorTileEntity)) {
				return false;
			}
			t = world.getTileEntity(x, y, z);
		}
		if (world.isRemote)
			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "step.stone", 1.0F, 0.8F);
		if (!world.isRemote && t != null
				&& ((t instanceof IEnergyNetNode)
						|| (t instanceof IMultiPowerCableHolder))
				&& stack.stackTagCompound.getInteger("fullLength") >= 1) {
			double ppx, ppy, ppz;
			ICableHolder te;
			if (t instanceof IMultiPowerCableHolder) {
				facing = ((IMultiPowerCableHolder) t).getSide(player);
				if (facing == -1) {
					return false;
				} else {
					te = ((IMultiPowerCableHolder) t).getEnergyNetNode(facing);
				}
			} else {
				te = (ICableHolder) t;
			}
			ppx = te.getPortPos(player)[0];
			ppy = te.getPortPos(player)[1];
			ppz = te.getPortPos(player)[2];
			if (stack.stackTagCompound.getBoolean("firstConnection")) {
				int x1 = stack.stackTagCompound.getInteger("connectorX");
				int y1 = stack.stackTagCompound.getInteger("connectorY");
				int z1 = stack.stackTagCompound.getInteger("connectorZ");
				int l1 = stack.stackTagCompound.getInteger("fullLength");
				int l2 = stack.stackTagCompound.getInteger("length");
				if (x == x1 && y == y1 && z == z1) {
					this.cleanUp(stack.stackTagCompound.getInteger("chainUID"));
					stack.stackTagCompound.setBoolean("firstConnection", false);
					stack.stackTagCompound.setInteger("length", l1);
					return true;
				}
				int t2DimensionId = stack.stackTagCompound.getInteger("connectorDimensionId");
				short facing2 = stack.stackTagCompound.getShort("connectorFacing");
				TileEntity t2 = MinecraftServer.getServer().worldServerForDimension(t2DimensionId).getTileEntity(x1, y1,
						z1);
				if (t2 == null) {
					stack.stackTagCompound.setBoolean("firstConnection", false);
					return false;
				} else {
					if (t instanceof IMultiPowerCableHolder) {
						facing = ((IMultiPowerCableHolder) t).getSide(player);
						if (facing == -1) {
							return false;
						} else {
							te = ((IMultiPowerCableHolder) t).getEnergyNetNode(facing);
						}
					} else {
						te = (ICableHolder) t;
					}
					te.setCableCheck(true);
				}
				this.connect(t, facing, t2, facing2, stack);
				stack.stackTagCompound.setInteger("fullLength", l2);
				if (l2 <= 0) {
					stack.stackSize--;
				}
				stack.stackTagCompound.setBoolean("firstConnection", false);
				stack.stackTagCompound.setInteger("chainArrangeNumber",
						stack.stackTagCompound.getInteger("chainArrangeNumber") + 1);
				x = x1;
				y = y1;
				z = z1;
			} else {
				te.setCableCheck(false);
				stack.stackTagCompound.setBoolean("firstConnection", true);
				stack.stackTagCompound.setInteger("connectorX", x);
				stack.stackTagCompound.setInteger("connectorY", y);
				stack.stackTagCompound.setInteger("connectorZ", z);
				stack.stackTagCompound.setShort("connectorFacing", facing);
				stack.stackTagCompound.setInteger("connectorDimensionId", world.provider.dimensionId);
				stack.stackTagCompound.setInteger("prevDimensionId", world.provider.dimensionId);
				stack.stackTagCompound.setInteger("chainArrangeNumber", 0);
				stack.stackTagCompound.setInteger("chainUID", world.rand.nextInt());
				stack.stackTagCompound.setDouble("prevNodePosX", ppx);
				stack.stackTagCompound.setDouble("prevNodePosY", ppy);
				stack.stackTagCompound.setDouble("prevNodePosZ", ppz);
			}
			NodeEntity node = newNode(world, player.posX, player.posY, player.posZ, stack,
					stack.stackTagCompound.getInteger("chainArrangeNumber"), x, y, z);
			node.virtualNodePosX = ppx;
			node.virtualNodePosY = ppy;
			node.virtualNodePosZ = ppz;
			return true;
		} else {
			return false;
		}
	}

	protected void connect(TileEntity t1, short facing, TileEntity t2, short facing2, ItemStack stack) {
		IEnergyNetNode te;
		IEnergyNetNode te1;
		if (t1 instanceof IEnergyNetNode) {
			te = (IEnergyNetNode) t1;
		} else {
			te = ((IMultiPowerCableHolder) t1).getEnergyNetNode(facing);
		}
		if (t2 instanceof IEnergyNetNode) {
			te1 = (IEnergyNetNode) t2;
		} else {
			te1 = ((IMultiPowerCableHolder) t2).getEnergyNetNode(facing2);
		}
		int newGridID = IHLMod.enet.mergeGrids(te.getGridID(), te1.getGridID());
		int l1 = stack.stackTagCompound.getInteger("fullLength");
		int l2 = stack.stackTagCompound.getInteger("length");
		NBTTagCompound cable = (NBTTagCompound) stack.stackTagCompound.copy();
		cable.setInteger("length", l1 - l2);
		cable.setBoolean("firstConnection", false);
		cable.setInteger("connectorX1", t1.xCoord);
		cable.setInteger("connectorY1", t1.yCoord);
		cable.setInteger("connectorZ1", t1.zCoord);
		cable.setShort("connectorFacing1", facing);
		cable.setInteger("connectorDimensionId1", t1.getWorldObj().provider.dimensionId);
		if (te.addCable(cable) && te1.addCable(cable)) {
			te.setGrid(newGridID);
			te1.setGrid(newGridID);
		}
	}

	private int getTransverseSection(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		return stack.stackTagCompound.getInteger("transverseSection");
	}

	public String getMaterial(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		return stack.stackTagCompound.getString("material");
	}

	public int getVoltageLimit(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		if (stack.stackTagCompound.hasKey("maxVoltage")) {
			return stack.stackTagCompound.getInteger("maxVoltage");
		}
		return 0;
	}

	private long getResistance(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		return IHLUtils.getResistance(stack.stackTagCompound);
	}

	private void cleanUp(int uid) {
		Set<NodeEntity> cs = IHLMod.proxy.nodeEntityRegistry.get(uid);
		if (cs != null) {
			for (NodeEntity ne : cs) {
				if (ne != null) {
					ne.setDead();
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
		if (!world.isRemote && stack.stackTagCompound != null) {
			if (stack.stackTagCompound.getBoolean("firstConnection")) {
				int leight = stack.stackTagCompound.getInteger("length");
				if (leight > 0) {
					double pnpx = stack.stackTagCompound.getDouble("prevNodePosX");
					double pnpy = stack.stackTagCompound.getDouble("prevNodePosY");
					double pnpz = stack.stackTagCompound.getDouble("prevNodePosZ");
					double ppx = entity.posX;
					double ppy = entity.posY;
					double ppz = entity.posZ;

					if (pnpx != 0D && pnpy != 0D && pnpz != 0D) {
						double sqd = (ppx - pnpx) * (ppx - pnpx) + (ppy - pnpy) * (ppy - pnpy)
								+ (ppz - pnpz) * (ppz - pnpz);
						if (sqd > 2D) {
							int can = stack.stackTagCompound.getInteger("chainArrangeNumber") + 1;
							int cx = stack.stackTagCompound.getInteger("connectorX");
							int cy = stack.stackTagCompound.getInteger("connectorY");
							int cz = stack.stackTagCompound.getInteger("connectorZ");
							NodeEntity node = newNode(world, ppx, ppy, ppz, stack, can, cx, cy, cz);
							node.shouldFollowPlayer = true;
							node.virtualNodePosX = ppx + (ppx - pnpx) * 2;
							node.virtualNodePosY = ppy + (ppy - pnpy) * 2;
							node.virtualNodePosZ = ppz + (ppz - pnpz) * 2;
							stack.stackTagCompound.setInteger("chainArrangeNumber", can);
							stack.stackTagCompound.setDouble("prevNodePosX", ppx);
							stack.stackTagCompound.setDouble("prevNodePosY", ppy);
							stack.stackTagCompound.setDouble("prevNodePosZ", ppz);
							stack.stackTagCompound.setInteger("length", --leight);
							stack.stackTagCompound.setInteger("prevDimensionId", world.provider.dimensionId);
						}
					}
				}
			}
		}
	}

	public void onPlayerTeleport(PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack != null && stack.getItem() instanceof FlexibleCableItem) {
				if (stack.stackTagCompound.getBoolean("firstConnection")) {
					int can = -1;
					Set<NodeEntity> ns = IHLMod.proxy.nodeEntityRegistry
							.get(stack.stackTagCompound.getInteger("chainUID"));
					if (ns != null) {
						NodeEntity lastNode = null;
						for (NodeEntity node : ns) {
							node.shouldFollowPlayer = false;
							if (node.chainArrangeNumber > can) {
								lastNode = node;
								can = node.chainArrangeNumber;
							}
						}
						if (lastNode != null) {
							lastNode.setVirtualNodePosToNearestPortal();
						}
					}
					double[] cc = IHLUtils.tracePlayerView(player);
					can = stack.stackTagCompound.getInteger("chainArrangeNumber") + 1;
					int cx = stack.stackTagCompound.getInteger("connectorX");
					int cy = stack.stackTagCompound.getInteger("connectorY");
					int cz = stack.stackTagCompound.getInteger("connectorZ");
					int leight = stack.stackTagCompound.getInteger("length");
					NodeEntity node = newNode(MinecraftServer.getServer().worldServerForDimension(event.toDim), cc[0],
							player.posY, cc[2], stack, can, cx, cy, cz);
					node.setVirtualNodePosToNearestPortal();
					stack.stackTagCompound.setInteger("chainArrangeNumber", can);
					stack.stackTagCompound.setDouble("prevNodePosX", cc[0]);
					stack.stackTagCompound.setDouble("prevNodePosY", cc[1]);
					stack.stackTagCompound.setDouble("prevNodePosZ", cc[2]);
					stack.stackTagCompound.setInteger("length", --leight);
					stack.stackTagCompound.setInteger("prevDimensionId", event.toDim);
				}
			}
		}
	}

	protected NodeEntity newNode(World world, double ppx, double ppy, double ppz, ItemStack stack, int can, int x,
			int y, int z) {
		PowerCableNodeEntity node = new PowerCableNodeEntity(world);
		node.setPosition(ppx, ppy, ppz);
		node.setChainUniqueID(stack.stackTagCompound.getInteger("chainUID"));
		short facing = stack.stackTagCompound.getShort("connectorFacing");
		int dimensionId = stack.stackTagCompound.getInteger("connectorDimensionId");
		node.setAnchor(x, y, z, facing, dimensionId);
		node.chainArrangeNumber = can;
		node.colorIndex = this.getColor(stack);
		node.addCable((NBTTagCompound) stack.stackTagCompound.copy());
		if (stack.stackTagCompound.hasKey("insulationMaterial")) {
			node.type = 1;
		} else {
			node.type = 0;
		}
		world.spawnEntityInWorld(node);
		return node;
	}

	private int getColor(ItemStack stack) {
		if (!stack.stackTagCompound.hasKey("insulationMaterial")) {
			if (yellowColoredWires.contains(this.getMaterial(stack))) {
				return 0xFF9900;
			} else {
				return 0xAAAABB;
			}
		}
		return 0xFFFFFF;
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag) {
		if (itemStack.stackTagCompound != null) {
			info.add(StatCollector.translateToLocal("ihl.lenght")+": " + itemStack.stackTagCompound.getInteger("length") + " m");
			info.add(StatCollector.translateToLocal("ihl.wire_material")+": " + this.getMaterial(itemStack));
			info.add(StatCollector.translateToLocal("ihl.resistivity")+": " + this.getResistance(itemStack) / 1000F + " V^2/(EU*m)");
			info.add(StatCollector.translateToLocal("ihl.transverse_section")+": " + this.getTransverseSection(itemStack) / 10F + " sq. mm.");
			if (itemStack.stackTagCompound.hasKey("insulationMaterial")) {
				info.add(StatCollector.translateToLocal("ihl.insulation_material")+": " + this.getInsulationMaterial(itemStack));
				info.add(StatCollector.translateToLocal("ihl.insulation_thickness")+": " + this.getInsulationThickness(itemStack) / 10f + " mm");
				info.add(StatCollector.translateToLocal("ihl.insulation_beakdown_voltage")+": " + this.getVoltageLimit(itemStack) / 1000 + " kV");
			}
			info.add(StatCollector.translateToLocal("ihl.powerCableTooltip"));
		}
	}

	@Override
	public String getTag() {
		return "length";
	}

	@Override
	public String getTagSecondary() {
		return "fullLength";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":copperWire");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.stackTagCompound != null && !this.getInsulationMaterial(stack).equals("null")) {
			return "cable";
		}
		return "wire";
	}

	@Override
	public boolean isSameWire(ItemStack stack1, ItemStack stack2) {
		if (stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage()) {
			if (stack1.stackTagCompound != null && stack2.stackTagCompound != null) {
				return this.getMaterial(stack1).equals(this.getMaterial(stack2))
						&& this.getTransverseSection(stack1) == this.getTransverseSection(stack2)
						&& this.getInsulationMaterial(stack1).equals(this.getInsulationMaterial(stack2))
						&& this.getInsulationThickness(stack1) == this.getInsulationThickness(stack2);
			}
		}
		return false;
	}

	public String getInsulationMaterial(ItemStack stack) {
		if (stack.stackTagCompound.hasKey("insulationMaterial")) {
			return stack.stackTagCompound.getString("insulationMaterial");
		}
		return "null";
	}

	public int getInsulationThickness(ItemStack stack) {
		if (stack.stackTagCompound.hasKey("insulationThickness")) {
			return stack.stackTagCompound.getInteger("insulationThickness");
		}
		return 0;
	}
}
