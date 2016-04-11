package ihl.nei_integration;

import ihl.IHLModInfo;
import ihl.processing.chemistry.ChemicalReactorGui;
import ihl.processing.chemistry.CryogenicDistillerGui;
import ihl.processing.chemistry.ElectricEvaporatorGui;
import ihl.processing.chemistry.EvaporatorGui;
import ihl.processing.chemistry.FluidizedBedReactorGui;
import ihl.processing.chemistry.LabElectrolyzerGui;
import ihl.processing.chemistry.LeadOvenGui;
import ihl.processing.chemistry.LoomGui;
import ihl.processing.chemistry.PaperMachineGui;
import ihl.processing.metallurgy.AchesonFurnaceGui;
import ihl.processing.metallurgy.DetonationSprayingMachineGui;
import ihl.processing.metallurgy.ExtruderGui;
import ihl.processing.metallurgy.GasWeldingStationGui;
import ihl.processing.metallurgy.ImpregnatingMachineGui;
import ihl.processing.metallurgy.MuffleFurnaceGui;
import ihl.processing.metallurgy.RollingMachineGui;
import ihl.processing.metallurgy.WireMillGui;
import ihl.processing.metallurgy.WoodenRollingMachineGui;
import ihl.utils.IHLUtils;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.ItemInfo;

public class NEIIHLConfig implements IConfigureNEI
{
    @Override
	public void loadConfig()
    {
    	API.registerHighlightHandler(new IHLBlockHighlightHandler(), ItemInfo.Layout.HEADER);
        API.registerRecipeHandler(new EvaporatorRecipeHandler());
        API.registerUsageHandler(new EvaporatorRecipeHandler());
        API.registerGuiOverlay(EvaporatorGui.class, "evaporator", 5, 11);
        API.registerRecipeHandler(new ElectricEvaporatorRecipeHandler());
        API.registerUsageHandler(new ElectricEvaporatorRecipeHandler());
        API.registerGuiOverlay(ElectricEvaporatorGui.class, "electricevaporator", 5, 11);
        API.registerRecipeHandler(new AchesonFurnaceRecipeHandler());
        API.registerUsageHandler(new AchesonFurnaceRecipeHandler());
        API.registerGuiOverlay(AchesonFurnaceGui.class, "achesonFurnace", 5, 11);
        API.registerRecipeHandler(new MuffleFurnaceRecipeHandler());
        API.registerUsageHandler(new MuffleFurnaceRecipeHandler());
        API.registerGuiOverlay(MuffleFurnaceGui.class, "muffleFurnace", 5, 11);
        API.registerRecipeHandler(new DetonationSprayingMachineRecipeHandler());
        API.registerUsageHandler(new DetonationSprayingMachineRecipeHandler());
        API.registerGuiOverlay(DetonationSprayingMachineGui.class, "detonationSprayingMachine", 5, 11);
        API.registerRecipeHandler(new ExtruderRecipeHandler());
        API.registerUsageHandler(new ExtruderRecipeHandler());
        API.registerGuiOverlay(ExtruderGui.class, "extruder", 5, 11);
        API.registerRecipeHandler(new ImpregnatingMachineRecipeHandler());
        API.registerUsageHandler(new ImpregnatingMachineRecipeHandler());
        API.registerGuiOverlay(ImpregnatingMachineGui.class, "impregnatingMachine", 5, 11);
        API.registerRecipeHandler(new LeadOvenRecipeHandler());
        API.registerUsageHandler(new LeadOvenRecipeHandler());
        API.registerGuiOverlay(LeadOvenGui.class, "leadOven", 5, 11);
        API.registerRecipeHandler(new LoomRecipeHandler());
        API.registerUsageHandler(new LoomRecipeHandler());
        API.registerGuiOverlay(LoomGui.class, "loom", 5, 11);
        API.registerGuiOverlay(GasWeldingStationGui.class, "gasWeldingStation", 5, 11);
        API.registerRecipeHandler(new GasWeldingStationGasRecipeHandler());
        API.registerUsageHandler(new GasWeldingStationGasRecipeHandler());
        API.registerGuiOverlay(GasWeldingStationGui.class, "gasWeldingStationGas", 5, 11);
        API.registerRecipeHandler(new WoodenRollingMachineRecipeHandler());
        API.registerUsageHandler(new WoodenRollingMachineRecipeHandler());
        API.registerGuiOverlay(WoodenRollingMachineGui.class, "woodenRollingMachine", 5, 11);
        API.registerRecipeHandler(new CryogenicDistillerRecipeHandler());
        API.registerUsageHandler(new CryogenicDistillerRecipeHandler());
        API.registerGuiOverlay(CryogenicDistillerGui.class, "cryogenicDistiller", 5, 11);
        API.registerRecipeHandler(new ChemicalReactorRecipeHandler());
        API.registerUsageHandler(new ChemicalReactorRecipeHandler());
        API.registerGuiOverlay(ChemicalReactorGui.class, "chemicalReactor", 5, 11);
        API.registerRecipeHandler(new FluidizedBedReactorRecipeHandler());
        API.registerUsageHandler(new FluidizedBedReactorRecipeHandler());
        API.registerGuiOverlay(FluidizedBedReactorGui.class, "fluidizedBedReactor", 5, 11);
        API.registerRecipeHandler(new LabElectrolyzerRecipeHandler());
        API.registerUsageHandler(new LabElectrolyzerRecipeHandler());
        API.registerGuiOverlay(LabElectrolyzerGui.class, "labElectrolyzer", 5, 11);
        API.registerRecipeHandler(new RollingMachineRecipeHandler());
        API.registerUsageHandler(new RollingMachineRecipeHandler());
        API.registerGuiOverlay(RollingMachineGui.class, "rollingMachine", 5, 11);
        API.registerRecipeHandler(new PaperMachineRecipeHandler());
        API.registerUsageHandler(new PaperMachineRecipeHandler());
        API.registerGuiOverlay(PaperMachineGui.class, "paperMachine", 5, 11);
        API.registerRecipeHandler(new ElectrolysisBathRecipeHandler());
        API.registerUsageHandler(new ElectrolysisBathRecipeHandler());
        API.registerGuiOverlay(PaperMachineGui.class, "paperMachine", 5, 11);
        API.registerRecipeHandler(new WireMillRecipeHandler());
        API.registerUsageHandler(new WireMillRecipeHandler());
        API.registerGuiOverlay(WireMillGui.class, "wireMill", 5, 11);
        API.registerRecipeHandler(new InjectionMoldRecipeHandler());
        API.registerUsageHandler(new InjectionMoldRecipeHandler());
        API.registerRecipeHandler(new VulcanizationExtrudingMoldRecipeHandler());
        API.registerUsageHandler(new VulcanizationExtrudingMoldRecipeHandler());
        API.registerRecipeHandler(new CrucibleRecipeHandler());
        API.registerUsageHandler(new CrucibleRecipeHandler());
        API.registerRecipeHandler(new FractionationColumnRecipeHandler());
        API.registerUsageHandler(new FractionationColumnRecipeHandler());
        API.registerRecipeHandler(new ChimneyKneeRecipeHandler());
        API.registerUsageHandler(new ChimneyKneeRecipeHandler());
        API.registerRecipeHandler(new PrecipitatorCondenserRecipeHandler());
        API.registerUsageHandler(new PrecipitatorCondenserRecipeHandler());
        API.registerRecipeHandler(new IronWorkbenchRecipeHandler());
        API.registerUsageHandler(new IronWorkbenchRecipeHandler());
        this.addSubsets();
    }
    
    public void addSubsets() {}

    @Override
	public String getName()
    {
        return "IHL";
    }

    @Override
	public String getVersion()
    {
        return IHLModInfo.MODVERSION;
    }
}
