package ihl.metallurgy.constants;

import ihl.IHLMod;

public enum Details {
	block(1296),
	highPressureVessel(432),
	turboCompressorSetOfMoldedParts(576),
	rollingMachineSetOfMoldedParts(576),
	gasJet(36),
	setOfPartsForLVElemotor(288),
	pipelineAccessories(288),
	detonationSprayingMachineSetOfMoldedParts(432),
	blankChisel(36),
	carvingKnife(36),
	machineCasingSetOfMoldedParts(288),
	polishingPucks(288),
	extruderSetOfMoldedParts(288),
	handDrillSetOfMoldedParts(72),
	barD10(1),
	latheSetOfMoldedParts(144),
	viseSetOfMoldedParts(144),
	ingot(144),	
	tub(144),
	nugget(16),
	screw(16),
	round(16),
	gear(576),
	bolt(18),
	stick(72),
	frameGt(288),
	toolHeadHammer(864),
	toolHeadHammerSmall(72),
	toolTinSnipsParts(72),
	ring(36),
	toolHeadPickaxe(432),
	gearSmall(144),
	nozzle(864),
	wire(48);
	Details(int moltenAmount1)
	{
		IHLMod.moltenAmounts.put(this.name(), moltenAmount1);
	}
	public static int getMeltingFluidAmount(String name)
	{
		if(IHLMod.moltenAmounts.containsKey(name))
		{
			return IHLMod.moltenAmounts.get(name);
		}
		else
		{
			return 0;
		}
	}
}
