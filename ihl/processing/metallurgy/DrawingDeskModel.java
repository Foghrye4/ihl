package ihl.processing.metallurgy;

import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;
import ihl.model.IHLModelRenderer;

public class DrawingDeskModel extends ModelBase
{
  //fields
    IHLModelRenderer RotatePart;
    IHLModelRenderer Piece1;
  
  public DrawingDeskModel()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("RotatePart.Shape1", 0, 14);
    setTextureOffset("RotatePart.Pipe2", 0, 17);
    setTextureOffset("RotatePart.Shape4", 3, 14);
    setTextureOffset("RotatePart.Shape3", 0, 26);
    setTextureOffset("RotatePart.Pipe5", 16, 20);
    setTextureOffset("RotatePart.Pipe6", 0, 18);
    setTextureOffset("RotatePart.Pipe11", 13, 22);
    setTextureOffset("RotatePart.Pipe12", 13, 22);
    setTextureOffset("RotatePart.Pipe13", 13, 22);
    setTextureOffset("RotatePart.Pipe14", 13, 22);
    setTextureOffset("RotatePart.Pipe15", 13, 22);
    setTextureOffset("Piece1.Shape7", 0, 17);
    setTextureOffset("Piece1.Shape8", 0, 17);
    setTextureOffset("Piece1.PipeW9", 0, 18);
    setTextureOffset("Piece1.PipeW10", 0, 20);
    setTextureOffset("Piece1.Shape2", 0, 0);
    
    RotatePart = new IHLModelRenderer(this, "RotatePart");
    RotatePart.setRotationPoint(0F, 23F, -6F);
    setRotation(RotatePart, 0.4F, 0F, 0F);
    RotatePart.mirror = true;
      RotatePart.addBox("Shape1", 7F, -1F, -1F, 1, 2, 16,false);
      RotatePart.addTube("Pipe2", -7.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
      RotatePart.addBox("Shape4", -8F, -1F, -1F, 1, 2, 16,false);
      RotatePart.addBox("Shape3", -7F, -1F, -1F, 14, 2, 2,false);
      RotatePart.addTube("Pipe5", -5.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
      RotatePart.addTube("Pipe6", -3.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
      RotatePart.addTube("Pipe11", -1.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
      RotatePart.addTube("Pipe12", 0.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
      RotatePart.addTube("Pipe13", 2.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
      RotatePart.addTube("Pipe14", 4.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
      RotatePart.addTube("Pipe15", 6.5F, -3F, 0F, 1, 2, 1, 0F,1F,ForgeDirection.UP);
    Piece1 = new IHLModelRenderer(this, "Piece1");
    Piece1.setRotationPoint(0F, 16F, 0F);
    setRotation(Piece1, 0F, 0F, 0F);
    Piece1.mirror = true;
      Piece1.addBox("Shape7", 6F, -4F, 6F, 1, 12, 2,false);
      Piece1.addBox("Shape8", -7F, -4F, 6F, 1, 12, 2,false);
      Piece1.addTube("PipeW9", -6F, 7F, 7F, 12, 1, 1, 0F,1F,ForgeDirection.EAST);
      Piece1.addTube("PipeW10", -6F, -4F, 6F, 12, 1, 1, 0F,1F,ForgeDirection.EAST);
      Piece1.addBox("Shape2", 0F, 7F, 0F, 4, 1, 1,false);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
}
