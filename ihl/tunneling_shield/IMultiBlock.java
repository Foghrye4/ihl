package ihl.tunneling_shield;

public interface IMultiBlock 
{
	public boolean isAPartOfStructure(int x, int y, int z, boolean onBlockBeak);
	public boolean getIsInvalid();
}
