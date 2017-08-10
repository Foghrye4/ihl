package ihl.interfaces;

import ic2.api.tile.IWrenchable;

public interface IWorkspaceElement extends IWrenchable 
{
	boolean canBeUsed();
	void use();
	boolean getIsInvalid();
}
