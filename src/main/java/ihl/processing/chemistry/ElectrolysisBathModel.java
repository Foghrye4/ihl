package ihl.processing.chemistry;

// Date: 12.04.2015 13:06:31
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ElectrolysisBathModel extends ModelBase
{
  //fields
    ModelRenderer Piece1;
    ModelRenderer FrontPylone;
    ModelRenderer BackPylone;
    ModelRenderer LeftPylone;
    ModelRenderer RightPylone;
  
  public ElectrolysisBathModel()
  {
    textureWidth = 64;
    textureHeight = 128;
    setTextureOffset("Piece1.Stick01", 48, 4);
    setTextureOffset("Piece1.Shape1", 16, 18);
    setTextureOffset("Piece1.Body04", 0, 48);
    setTextureOffset("Piece1.Shape2", 0, 18);
    setTextureOffset("Piece1.Shape3", 16, 0);
    setTextureOffset("Piece1.Shape4", 0, 0);
    setTextureOffset("Piece1.Box01", 41, 18);
    setTextureOffset("Piece1.Shape5", 23, 84);
    setTextureOffset("Piece1.Shape6", 23, 84);
    setTextureOffset("Piece1.Shape7", 23, 84);
    setTextureOffset("Piece1.Shape8", 23, 83);
    setTextureOffset("FrontPylone.Shape9", 23, 84);
    setTextureOffset("BackPylone.Shape10", 23, 84);
    setTextureOffset("LeftPylone.Shape12", 23, 84);
    setTextureOffset("RightPylone.Shape11", 23, 82);
    
    Piece1 = new ModelRenderer(this, "Piece1");
    Piece1.setRotationPoint(0F, 16F, 0F);
    setRotation(Piece1, 0F, 0F, 0F);
    Piece1.mirror = false;
      Piece1.addBox("Stick01", -1F, -16F, -1F, 2, 9, 2);
      Piece1.addBox("Shape1", -8F, -8F, -8F, 2, 16, 14);
      Piece1.addBox("Body04", -6F, -7F, -6F, 12, 15, 12);
      Piece1.addBox("Shape2", 6F, -8F, -6F, 2, 16, 14);
      Piece1.addBox("Shape3", -6F, -8F, -8F, 14, 16, 2);
      Piece1.addBox("Shape4", -8F, -8F, 6F, 14, 16, 2);
      Piece1.addBox("Box01", -2F, -13F, -2F, 4, 2, 4);
      Piece1.addBox("Shape5", 5F, -9F, -1.5F, 2, 2, 3);
      Piece1.addBox("Shape6", -7F, -9F, -1.5F, 2, 2, 3);
      Piece1.addBox("Shape7", -1.5F, -9F, 5F, 3, 2, 2);
      Piece1.addBox("Shape8", -1.5F, -9F, -7F, 3, 2, 2);
    FrontPylone = new ModelRenderer(this, "FrontPylone");
    FrontPylone.setRotationPoint(0F, 7F, -5F);
    setRotation(FrontPylone, -0.9F, 0F, 0);
    FrontPylone.mirror = true;
      FrontPylone.addBox("Shape9", -1F, -5F, -1F, 2, 6, 1);
    BackPylone = new ModelRenderer(this, "BackPylone");
    BackPylone.setRotationPoint(0F, 7F, 5F);
    setRotation(BackPylone, 0.9F, 0F, 0F);
    BackPylone.mirror = true;
      BackPylone.addBox("Shape10", -1F, -5F, 0F, 2, 6, 1);
    LeftPylone = new ModelRenderer(this, "LeftPylone");
    LeftPylone.setRotationPoint(5F, 7F, 0F);
    setRotation(LeftPylone, 0F, 0F, -0.9F);
    LeftPylone.mirror = true;
      LeftPylone.addBox("Shape12", 0F, -5F, -1F, 1, 6, 2);
    RightPylone = new ModelRenderer(this, "RightPylone");
    RightPylone.setRotationPoint(-5F, 7F, 0F);
    setRotation(RightPylone, 0, 0F, 0.9F);
    RightPylone.mirror = true;
      RightPylone.addBox("Shape11", -1F, -5F, -1F, 1, 6, 2);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
