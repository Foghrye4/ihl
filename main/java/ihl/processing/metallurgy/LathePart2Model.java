package ihl.processing.metallurgy;

import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;

public class LathePart2Model extends ModelBase
{
  //fields
    IHLModelRenderer Base;
  
  public LathePart2Model()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape6", 0, 0);
    setTextureOffset("Base.Shape1", 0, 0);
    setTextureOffset("Base.Shape2", 38, 5);
    setTextureOffset("Base.Shape3", 0, 0);
    setTextureOffset("Base.Shape4", 0, 0);
    setTextureOffset("Base.Shape5", 0, 0);
    setTextureOffset("Base.PipeN7", 0, 0);
    setTextureOffset("Base.Shape7", 0, 0);
    setTextureOffset("Base.PipeW9", 0, 0);
    setTextureOffset("Base.PipeW9", 0, 0);
    setTextureOffset("Base.Shape9", 0, 0);
    setTextureOffset("Base.Shape10", 0, 0);
    setTextureOffset("Base.Shape11", 0, 0);
    setTextureOffset("Base.Shape12", 0, 0);
    setTextureOffset("Base.PipeW13", 0, 0);
    setTextureOffset("Base.PipeN13", 0, 0);
    setTextureOffset("Base.Shape13", 0, 0);
    setTextureOffset("Base.Shape14", 0, 0);
    setTextureOffset("Base.PipeU15", 0, 0);
    setTextureOffset("Base.PipeU16", 0, 0);
    setTextureOffset("Base.PipeU17", 0, 0);
    setTextureOffset("Base.PipeU18", 0, 0);
    setTextureOffset("Base.Shape15", 0, 0);
    setTextureOffset("Base.Shape16", 0, 0);
    setTextureOffset("Base.PipeW20", 0, 0);
    setTextureOffset("Base.PipeW19", 0, 0);
    setTextureOffset("Base.PipeN21", 0, 0);
    setTextureOffset("Base.PipeN22", 0, 0);
    
    Base = new IHLModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 16F, 0F);
    setRotation(Base, 0F, 0F, 0F);
    Base.mirror = true;
      Base.addBox("Shape6", -8F, 7F, -8F, 14, 1, 16);
      Base.addBox("Shape1", 0F, 1F, 0F, 4, 1, 4);
      Base.addBox("Shape2", -2F, 2F, 1F, 5, 1, 1);
      Base.addBox("Shape3", 0F, 3F, 0F, 4, 2, 4);
      Base.addBox("Shape4", 2F, 6F, -8F, 1, 1, 16);
      Base.addBox("Shape5", 5F, 6F, -8F, 1, 1, 13);
      Base.addTube("PipeN7", 3.5F, 6F, -7F, 1, 1, 12, 0F,1F,ForgeDirection.NORTH);
      Base.addBox("Shape7", 3F, 6F, -8F, 2, 1, 1);
      Base.addTube("PipeW9", 6F, 5F, 5F, 1, 3, 3, 0F,1F,ForgeDirection.WEST);
      Base.addTube("PipeW9", 7F, 6F, 5.2F, 1, 1, 1, 0F,1F,ForgeDirection.WEST);
      Base.addBox("Shape9", 3F, 5F, 5F, 3, 2, 3);
      Base.addBox("Shape10", -2F, 5F, 0F, 8, 1, 1);
      Base.addBox("Shape11", -2F, 5F, 3F, 8, 1, 1);
      Base.addBox("Shape12", 1F, 5F, 1F, 2, 1, 2);
      Base.addTube("PipeW13", -1F, 5F, 1.5F, 6, 1, 1, 0F,1F,ForgeDirection.WEST);
      Base.addTube("PipeN13", -4F, 1F, 3F, 2, 2, 3, 0F,1F,ForgeDirection.NORTH);
      Base.addBox("Shape13", -3.5F, 2F, 3.8F, 1, 5, 2);
      Base.addBox("Shape14", -4.5F, 6F, 2F, 3, 1, 4);
      Base.addTube("PipeU15", 0F, 2F, 3F, 1, 1, 1, 0F,1F,ForgeDirection.UP);
      Base.addTube("PipeU16", 3F, 2F, 3F, 1, 1, 1, 0F,1F,ForgeDirection.UP);
      Base.addTube("PipeU17", 3F, 2F, 0F, 1, 1, 1, 0F,1F,ForgeDirection.UP);
      Base.addTube("PipeU18", 0F, 2F, 0F, 1, 1, 1, 0F,1F,ForgeDirection.UP);
      Base.addBox("Shape15", 5F, 5F, 1F, 1, 1, 2);
      Base.addBox("Shape16", -2F, 5F, 1F, 1, 1, 2);
      Base.addTube("PipeW20", 6F, 4F, 0.5F, 1, 3, 3, 0F,1F,ForgeDirection.WEST);
      Base.addTube("PipeW19", 7F, 5F, 0.8F, 1, 1, 1, 0F,1F,ForgeDirection.WEST);
      Base.addTube("PipeN21", -4.5F, 0.5F, 6F, 3, 3, 1, 0F,1F,ForgeDirection.NORTH);
      Base.addTube("PipeN22", -3F, 1F, 7F, 1, 1, 1, 0F,1F,ForgeDirection.NORTH);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }


}
