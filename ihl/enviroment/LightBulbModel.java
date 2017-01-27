package ihl.enviroment;

import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;

public class LightBulbModel extends ModelBase
{
  //fields
    IHLModelRenderer Base;
    IHLModelRenderer BaseON;
  
  public LightBulbModel()
  {
    textureWidth = 32;
    textureHeight = 32;
    setTextureOffset("Base.Shape1", 0, 19);
    setTextureOffset("Base.TubeNorth", 20, 0);
    setTextureOffset("Base.CylinderNorth", 0, 0);
    setTextureOffset("Base.CylinderNorth2", 0, 0);
    setTextureOffset("Base.SpyralNorth", 16, 24);

    setTextureOffset("BaseON.Shape1", 0, 19);
    setTextureOffset("BaseON.TubeNorth", 20, 0);
    setTextureOffset("BaseON.CylinderNorth", 0, 9);
    setTextureOffset("BaseON.CylinderNorth2", 10, 10);
    setTextureOffset("BaseON.SpyralNorth", 0, 24);
    
    Base = new IHLModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 16F, 0F);
    Base.mirror = true;
      Base.addBox("Shape1", -2F, -2F, 7F, 4, 4, 1);
      Base.addTube("TubeNorth", -2F, -2F, 5F, 4, 4, 2, 0.8f, 1f, ForgeDirection.NORTH);
      Base.drawFromInside = true;
      Base.addTube("CylinderNorth", -1.5F, -1.5F, 0F, 3, 3, 7, 0f, 1f, ForgeDirection.NORTH);
      Base.addTube("CylinderNorth2", -1F, -1F, 0.5F, 2, 2, 5, 0f, 1f, ForgeDirection.NORTH);
      Base.drawFromInside = false;
      Base.addTube("SpyralNorth", -0.5F, -0.5F, 0.5F, 1, 1, 7, 0f, 1f, ForgeDirection.NORTH);

      BaseON = new IHLModelRenderer(this, "BaseON");
      BaseON.setRotationPoint(0F, 16F, 0F);
      BaseON.mirror = true;
        BaseON.addBox("Shape1", -2F, -2F, 7F, 4, 4, 1);
        BaseON.addTube("TubeNorth", -2F, -2F, 5F, 4, 4, 2, 0.8f, 1f, ForgeDirection.NORTH);
        BaseON.drawFromInside = true;
        BaseON.addTube("CylinderNorth", -1.5F, -1.5F, 0F, 3, 3, 7, 0f, 1f, ForgeDirection.NORTH);
        BaseON.addTube("CylinderNorth2", -1F, -1F, 0.5F, 2, 2, 5, 0f, 1f, ForgeDirection.NORTH);
        BaseON.drawFromInside = false;
        BaseON.addTube("SpyralNorth", -0.5F, -0.5F, 0.5F, 1, 1, 7, 0f, 1f, ForgeDirection.NORTH);
  }
}
