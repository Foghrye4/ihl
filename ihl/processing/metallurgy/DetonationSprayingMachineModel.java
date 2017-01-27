package ihl.processing.metallurgy;

import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;

public class DetonationSprayingMachineModel extends ModelBase {
	  //fields
    IHLModelRenderer Base;
	  
	    public DetonationSprayingMachineModel()
	    {
	        textureWidth = 64;
	        textureHeight = 32;
	        setTextureOffset("Base.Shape1", 20, 0);
	        setTextureOffset("Base.Shape2", 34, 0);
	        setTextureOffset("Base.Shape3", 23, 0);
	        setTextureOffset("Base.Shape4", 23, 0);
	        setTextureOffset("Base.Shape5", 0, 0);
	        setTextureOffset("Base.Tube1", 0, 0);
	        setTextureOffset("Base.Tube2", 0, 0);
	        setTextureOffset("Base.Shape12", 0, 20);
	        setTextureOffset("Base.Shape13", 0, 20);
	        setTextureOffset("Base.Shape14", 0, 20);
	        setTextureOffset("Base.Shape7", 26, 22);
	        setTextureOffset("Base.Shape8", 18, 19);
	        setTextureOffset("Base.Shape9", 18, 22);
	        setTextureOffset("Base.Tube11", 8, 5);
	        
	        Base = new IHLModelRenderer(this, "Base");
	        Base.setRotationPoint(0F, 8F, 0F);
	        Base.mirror = true;
	          Base.addBox("Shape1", 4F, 6F, 7F, 2, 9, 1, false);
	          Base.addBox("Shape2", -7F, 4F, 6F, 14, 11, 1, false);
	          Base.addBox("Shape3", -1F, 6F, 7F, 2, 9, 1, false);
	          Base.addBox("Shape4", -6F, 6F, 7F, 2, 9, 1, false);
	          Base.addBox("Shape5", -8F, 15F, -8F, 16, 1, 16, false);
	          Base.addTube("Tube1", -1F, 10F, -7F, 2, 2, 6, 0.5F,1F,ForgeDirection.NORTH);
	          Base.addTube("Tube2", -2.5F, 10.5F, -6F, 5, 1, 1, 0F,1F,ForgeDirection.EAST);
	          Base.addBox("Shape12", -2.5F, 11.7F, -2.2F, 5, 4, 1, false);
	          Base.addBox("Shape13", 1F, 9F, -8F, 1, 6, 6, false);
	          Base.addBox("Shape14", -2F, 9F, -8F, 1, 6, 6, false);
	          Base.addBox("Shape7", -1F, 14F, -8F, 2, 1, 5, false);
	          Base.addBox("Shape8", 2F, 14F, -8F, 2, 1, 9, false);
	          Base.addBox("Shape9", -4F, 14F, -8F, 2, 1, 9, false);
	          Base.addTube("Tube11", -1F, 10F, -8F, 2, 2, 1, 0F,1F,ForgeDirection.NORTH);
	    }

	  @Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	  {
	    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	  }
}


