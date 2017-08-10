package ihl.flexible_cable;

import ihl.metallurgy.constants.ElectricConductor;
import net.minecraft.nbt.NBTTagCompound;

public class IHLCable {

	public final int chainUID;
	public final int fullLength;
	public final int length;
	public final String material;
	public final int transverseSection;
	public final String insulationMaterial;
	public final int insulationThickness;
	public final int maxVoltage;
	public final int connectorX1;
	public final int connectorY1;
	public final int connectorZ1;
	public final int connectorDimensionId1;
	public final short connectorFacing1;
	public final int connectorX;
	public final int connectorY;
	public final int connectorZ;
	public final int connectorDimensionId;
	public final short connectorFacing;

	public IHLCable(int chainUIDIn, int fullLengthIn, int lengthIn, String materialIn, int transverseSectionIn,
			String insulationMaterialIn, int insulationThicknessIn, int maxVoltageIn, int connectorX1In,
			int connectorY1In, int connectorZ1In, int connectorDimensionId1In, short connectorFacing1In,
			int connectorXIn, int connectorYIn, int connectorZIn, int connectorDimensionIdIn, short connectorFacingIn) {
		chainUID = chainUIDIn;
		fullLength = fullLengthIn;
		length = lengthIn;
		material = materialIn;
		transverseSection = transverseSectionIn;
		insulationMaterial = insulationMaterialIn;
		insulationThickness = insulationThicknessIn;
		maxVoltage = maxVoltageIn;
		connectorX1 = connectorX1In;
		connectorY1 = connectorY1In;
		connectorZ1 = connectorZ1In;
		connectorDimensionId1 = connectorDimensionId1In;
		connectorFacing1 = connectorFacing1In;
		connectorX = connectorXIn;
		connectorY = connectorYIn;
		connectorZ = connectorZIn;
		connectorDimensionId = connectorDimensionIdIn;
		connectorFacing = connectorFacingIn;
	}

	public static IHLCable fromNBT(NBTTagCompound tag) {
		return new IHLCable(tag.getInteger("chainUID"),
		tag.getInteger("fullLength"),
		tag.getInteger("length"),
		tag.getString("material"),
		tag.getInteger("transverseSection"),
		tag.getString("insulationMaterial"),
		tag.getInteger("insulationThickness"),
		tag.getInteger("maxVoltage"),
		tag.getInteger("connectorX1"),
		tag.getInteger("connectorY1"),
		tag.getInteger("connectorZ1"),
		tag.getInteger("connectorDimensionId1"),
		tag.getShort("connectorFacing1"),
		tag.getInteger("connectorX"),
		tag.getInteger("connectorY"),
		tag.getInteger("connectorZ"),
		tag.getInteger("connectorDimensionId"),
		tag.getShort("connectorFacing"));
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("fullLength", fullLength);
		tag.setInteger("length", length);
		tag.setString("material", material);
		tag.setInteger("transverseSection", transverseSection);
		tag.setString("insulationMaterial", insulationMaterial);
		tag.setInteger("insulationThickness", insulationThickness);
		tag.setInteger("maxVoltage", maxVoltage);
		tag.setInteger("connectorX1",connectorX1);
		tag.setInteger("connectorY1",connectorY1);
		tag.setInteger("connectorZ1",connectorZ1);
		tag.setInteger("connectorDimensionId1",connectorDimensionId1);
		tag.setShort("connectorFacing1",connectorFacing1);
		tag.setInteger("connectorX",connectorX);
		tag.setInteger("connectorY",connectorY);
		tag.setInteger("connectorZ",connectorZ);
		tag.setInteger("connectorDimensionId",connectorDimensionId);
		tag.setShort("connectorFacing",connectorFacing);
		return tag;
	}

	@Override
	public int hashCode() {
		return chainUID;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IHLCable))
			return false;
		IHLCable otherCable = (IHLCable) o;
		return otherCable.chainUID == this.chainUID;
	}
	
	public long getResistance(){
		return ElectricConductor.getResistivity(material) * 100L / transverseSection;
	}

}
