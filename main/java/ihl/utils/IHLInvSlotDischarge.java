package ihl.utils;

import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class IHLInvSlotDischarge  extends InvSlot
{
    public int tier;
    public boolean allowRedstoneDust;

    public IHLInvSlotDischarge(TileEntityInventory base, int oldStartIndex, InvSlot.Access access, int tier)
    {
        this(base, oldStartIndex, access, tier, InvSlot.InvSide.ANY);
    }

    public IHLInvSlotDischarge(TileEntityInventory base, int oldStartIndex, InvSlot.Access access, int tier, InvSlot.InvSide preferredSide)
    {
        this(base, oldStartIndex, access, tier, true, preferredSide);
    }

    public IHLInvSlotDischarge(TileEntityInventory base, int oldStartIndex, InvSlot.Access access, int tier, boolean allowRedstoneDust, InvSlot.InvSide preferredSide)
    {
        super(base, "discharge", oldStartIndex, access, 1, preferredSide);
        this.allowRedstoneDust = true;
        this.tier = tier;
        this.allowRedstoneDust = allowRedstoneDust;
    }

    @Override
	public boolean accepts(ItemStack stack)
    {
        return stack == null ? false : (stack.getItem() == Items.redstone && !this.allowRedstoneDust ? false : Info.itemEnergy.getEnergyValue(stack) > 0.0D || ElectricItem.manager.discharge(stack, Double.POSITIVE_INFINITY, this.tier, true, true, true) > 0.0D);
    }

    public double discharge(double amount, boolean ignoreLimit)
    {
        if (amount <= 0.0D)
        {
            throw new IllegalArgumentException("Amount must be > 0.");
        }
        else
        {
            ItemStack stack = this.get(0);

            if (stack == null)
            {
                return 0.0D;
            }
            else
            {
                double realAmount = ElectricItem.manager.discharge(stack, amount, this.tier, ignoreLimit, true, false);

                if (realAmount <= 0.0D)
                {
                    realAmount = Info.itemEnergy.getEnergyValue(stack);

                    if (realAmount <= 0.0D)
                    {
                        return 0.0D;
                    }

                    --stack.stackSize;

                    if (stack.stackSize <= 0)
                    {
                        this.put(0, (ItemStack)null);
                    }
                }

                return realAmount;
            }
        }
    }

    public void setTier(int tier1)
    {
        this.tier = tier1;
    }
}