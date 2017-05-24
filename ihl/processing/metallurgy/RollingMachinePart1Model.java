package ihl.processing.metallurgy;


import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;


public class RollingMachinePart1Model extends ModelBase
{
  //fields
    IHLModelRenderer Base;
    IHLModelRenderer RotatingPart2;
    IHLModelRenderer MotorPart1;
    IHLModelRenderer MotorPart2;
    IHLModelRenderer RotatingPart1;
    IHLModelRenderer Plate;
  
  public RollingMachinePart1Model()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape6", 0, 0);
    setTextureOffset("Base.Shape16", 0, 0);
    setTextureOffset("Base.Shape5", 0, 0);
    setTextureOffset("Base.Shape8", 0, 0);
    setTextureOffset("Base.Shape11", 0, 0);
    setTextureOffset("Base.Shape9", 0, 0);
    setTextureOffset("RotatingPart2.PipeN7", 0, 0);
    setTextureOffset("RotatingPart2.PipeN17", 0, 0);
    setTextureOffset("RotatingPart2.Shape3", 0, 0);
    setTextureOffset("RotatingPart2.Shape12", 0, 0);
    setTextureOffset("MotorPart1.PipeN41", 0, 0);
    setTextureOffset("MotorPart1.PipeN12", 0, 0);
    setTextureOffset("MotorPart1.PipeN13", 0, 0);
    setTextureOffset("MotorPart2.Shape4", 0, 24);
    setTextureOffset("MotorPart2.Shape7", 0, 9);
    setTextureOffset("MotorPart2.Shape10", 0, 9);
    setTextureOffset("RotatingPart1.Shape1", 0, 0);
    setTextureOffset("RotatingPart1.Shape2", 0, 0);
    setTextureOffset("RotatingPart1.Shape13", 0, 0);
    setTextureOffset("Plate.Shape14", 0, 0);
    
    Base = new IHLModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 16F, 0F);
    setRotation(Base, 0F, 0F, 0F);
    Base.mirror = true;
      Base.addBox("Shape6", -8F, 7F, -8F, 16, 1, 16);
      Base.addBox("Shape16", -3F, -7F, -6F, 9, 14, 1);
      Base.addBox("Shape5", 2F, -1F, -5F, 6, 1, 13);
      Base.addBox("Shape8", -8F, -1F, -5F, 6, 1, 13);
      Base.addBox("Shape11", 3F, 0F, 7F, 4, 7, 1);
      Base.addBox("Shape9", -7F, 0F, 7F, 4, 7, 1);
    RotatingPart2 = new IHLModelRenderer(this, "RotatingPart2");
    RotatingPart2.setRotationPoint(0F, 16F, -7F);
    setRotation(RotatingPart2, 0F, 0F, 0F);
    RotatingPart2.mirror = true;
      RotatingPart2.addTube("PipeN7", -5.5F, -5.5F, -0.9F, 11, 11, 1, 0F,1F,ForgeDirection.NORTH);
      RotatingPart2.addTube("PipeN17", -2F, -2F, 2F, 4, 4, 26, 0F,1F,ForgeDirection.NORTH);
      RotatingPart2.addTube("Shape3", -2.5F, -2.5F, 0F, 5, 5, 1, 0F,1F,ForgeDirection.NORTH);
      RotatingPart2.addTube("Shape12", -1F, -1F, 28F, 2, 2, 3, 0F,1F,ForgeDirection.NORTH);
      MotorPart1 = new IHLModelRenderer(this, "MotorPart1");
      MotorPart1.setRotationPoint(4.5F, 20.5F, 0F);
      setRotation(MotorPart1, 0F, 0F, 0F);
      MotorPart1.mirror = true;
        MotorPart1.addTube("PipeN41", -1.5F, -1.5F, -8F, 3, 3, 1, 0F,1F,ForgeDirection.NORTH);
        MotorPart1.addTube("PipeN12", -0.5F, -0.5F, -7F, 1, 1, 1, 0F,1F,ForgeDirection.NORTH);
        MotorPart1.addTube("PipeN13", -1.5F, -1.5F, -5F, 3, 3, 5, 0F,1F,ForgeDirection.NORTH);
      MotorPart2 = new IHLModelRenderer(this, "MotorPart2");
      MotorPart2.setRotationPoint(0F, 16F, 0F);
      setRotation(MotorPart2, 0F, 0F, 0F);
      MotorPart2.mirror = true;
        MotorPart2.addBox("Shape4", 3F, 3F, 0F, 3, 4, 4);
        MotorPart2.addBox("Shape7", 6F, 3F, -5F, 2, 4, 5);
        MotorPart2.addBox("Shape10", 1F, 3F, -5F, 2, 4, 5);
    RotatingPart1 = new IHLModelRenderer(this, "RotatingPart1");
    RotatingPart1.setRotationPoint(0F, 11.9F, -5F);
    setRotation(RotatingPart1, 0F, 0F, 0F);
    RotatingPart1.mirror = true;
      RotatingPart1.addTube("Shape1", -2F, -2F, 0F, 4, 4, 26, 0F,1F,ForgeDirection.NORTH);
      RotatingPart1.addTube("Shape2", -2.5F, -2.5F, -1.9F, 5, 5, 1, 0F,1F,ForgeDirection.NORTH);
      RotatingPart1.addTube("Shape13", -1F, -1F, 26F, 2, 2, 3, 0F,1F,ForgeDirection.NORTH);
      Plate = new IHLModelRenderer(this, "Plate");
      Plate.setRotationPoint(0F, 13.5F, 0F);
      setRotation(Plate, 0F, 0F, 0F);
      Plate.mirror = true;
        Plate.addBox("Shape14", -18F, 0F, 0F, 16, 1, 16);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
