package ihl.processing.metallurgy;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.fluids.FluidStack;

public class VacuumInductionMeltingFurnaceContainer extends ContainerBase {
    public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
    public short lastProgress = -1;
    public short lastEnergy = -1;
	public VacuumInductionMeltingFurnaceTileEntity tileEntity;
    private final static int height=166;
    public List<FluidStack> fluidTankFluidList;
    private boolean vacuumPumpState=true;
    
	public VacuumInductionMeltingFurnaceContainer(EntityPlayer entityPlayer, VacuumInductionMeltingFurnaceTileEntity tileEntity1) {
		super(tileEntity1);
		this.tileEntity=tileEntity1;
        fluidTankFluidList = this.tileEntity.fluidTank.getFluidList();
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 0, 141, 8));
	}
	  @Override
	public void detectAndSendChanges()
	    {
	        super.detectAndSendChanges();
	        for (int i = 0; i < this.crafters.size(); ++i)
	        {
	            ICrafting icrafting = (ICrafting)this.crafters.get(i);

	            if (this.tileEntity.fluidTank.getFluidAmount() != this.lastFluidAmount || this.tileEntity.fluidTank.getNumberOfFluids() != this.lastNumberOfFluids)
	            {
	                IC2.network.get().sendContainerField(this, "fluidTankFluidList");
	            }
	            
	            if (this.tileEntity.progress != this.lastProgress)
	            {
	                icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
	            }
	            
	            if (this.tileEntity.vacuumPumpConnected != this.vacuumPumpState)
	            {
	                icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.vacuumPumpConnected?1:0);
	            }
	        }

	        this.lastNumberOfFluids = this.tileEntity.fluidTank.getNumberOfFluids();
	        this.lastFluidAmount = this.tileEntity.fluidTank.getFluidAmount();
	        this.lastProgress = this.tileEntity.progress;
	        this.vacuumPumpState = this.tileEntity.vacuumPumpConnected;
	    }
	    
	    @Override
		public void updateProgressBar(int index, int value)
	    {
	        super.updateProgressBar(index, value);
	        switch (index)
	        {
	            case 0:
	                this.tileEntity.progress=(short) value;
	                break;
	            case 1:
	                this.tileEntity.vacuumPumpConnected=value>0;
	                break;
	        }
	    }
	    
		@Override
		public boolean canInteractWith(EntityPlayer var1) {
			return tileEntity.isUseableByPlayer(var1);
		}
}
