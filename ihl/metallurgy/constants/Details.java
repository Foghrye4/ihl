package ihl.metallurgy.constants;

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
	wire(48);
	Details(int moltenAmount1)
	{
		moltenAmount=moltenAmount1;
	}
	private int moltenAmount;
	public static int getMeltingFluidAmount(String name)
	{
		if(Details.valueOf(name)==null)
		{
			return 0;
		}
		else
		{
			return Details.valueOf(name).moltenAmount;
		}
	}
}
