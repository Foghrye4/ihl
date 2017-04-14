package ihl.processing.chemistry;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class CryogenicDistillerContainer extends BasicElectricMotorContainer<CryogenicDistillerTileEntity> {

	public CryogenicDistillerTileEntity tileEntity;
	private int lastNumberOfFluids = -1;
	private int lastFluidAmount = -1;
    public List<FluidStack> fluidTankFluidList;

	public CryogenicDistillerContainer(EntityPlayer entityPlayer,
			CryogenicDistillerTileEntity lathePart1TileEntity) {
		super(entityPlayer, lathePart1TileEntity);
		tileEntity=lathePart1TileEntity;
		fluidTankFluidList=tileEntity.fluidTankProducts.getFluidList();
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fillInputSlotInput,0, 58, 51));	
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fillInputSlotProducts,0, 103, 51));	
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fluidItemsSlot,0, 58, 15));	
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fluidItemsSlot,1, 103, 15));	
	}

	   @Override
	public void detectAndSendChanges()
	    {
	        super.detectAndSendChanges();
            if (this.tileEntity.fluidTankProducts.getFluidAmount() != this.lastFluidAmount || this.tileEntity.fluidTankProducts.getNumberOfFluids() != this.lastNumberOfFluids)
            {
                IC2.network.get().sendContainerField(this, "fluidTankFluidList");
            }
	        this.lastNumberOfFluids = this.tileEntity.fluidTankProducts.getNumberOfFluids();
	        this.lastFluidAmount = this.tileEntity.fluidTankProducts.getFluidAmount();
	    }
}
