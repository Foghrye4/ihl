package ihl.processing.metallurgy;

import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;
import ihl.model.IHLModelRenderer;

public class VulcanizationExtrudingMoldModel extends ModelBase
{
  //fields
    IHLModelRenderer Base;
  
  public VulcanizationExtrudingMoldModel()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape6", 0, 0);
    setTextureOffset("Base.Shape3", 0, 0);
    setTextureOffset("Base.Shape16", 0, 0);
    setTextureOffset("Base.PipeW161", 0, 0);
    setTextureOffset("Base.PipeN16", 0, 0);
    setTextureOffset("Base.PipeW15", 22, 22);
    setTextureOffset("Base.PipeW17", 0, 0);
    setTextureOffset("Base.Shape14", 0, 0);
    setTextureOffset("Base.PipeN14", 0, 0);
    setTextureOffset("Base.Shape2", 0, 0);
    setTextureOffset("Base.Shape4", 42, 17);
    setTextureOffset("Base.Shape5", 37, 17);
    
    Base = new IHLModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 16F, 0F);
    setRotation(Base, 0F, 0F, 0F);
    Base.mirror = true;
      Base.addBox("Shape6", -8F, 7F, -8F, 16, 1, 16);
      Base.addBox("Shape3", -5F, 0F, -7F, 4, 7, 1);
      Base.addBox("Shape16", 6.9F, 3F, -1.5F, 1, 4, 3);
      Base.addTube("PipeW161", -7F, 0.5F, -1.5F, 15, 3, 3, 0.5F,1F,ForgeDirection.WEST);
      Base.addTube("PipeN16", -4.5F, 0.5F, -6F, 3, 3, 6, 0.8F,1F,ForgeDirection.NORTH);
      Base.addTube("PipeW15", -5.5F, -0.5F, -2.5F, 12, 5, 5, 0.5F,1F,ForgeDirection.WEST);
      Base.addTube("PipeW17", -8F, -0.5F, -2.5F, 1, 5, 5, 0.1F,1F,ForgeDirection.WEST);
      Base.addBox("Shape14", -5F, 3F, -4.9F, 4, 4, 1);
      Base.addTube("PipeN14", -5.5F, -0.5F, -8F, 5, 5, 1, 0.5F,1F,ForgeDirection.NORTH);
      Base.addBox("Shape2", -7F, 3F, -1.5F, 1, 4, 3);
      Base.addBox("Shape4", 5F, 3F, 1F, 1, 4, 1);
      Base.addBox("Shape5", 5F, 6F, 2F, 3, 1, 1);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
