package ihl.crop_harvestors;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SackModel extends ModelBase {
	  //fields
	   ModelRenderer Base;
	    ModelRenderer Top;
	    ModelRenderer Liquid;
	    ModelRenderer Liquid_overflow;
	    ModelRenderer Liquid2;
	    ModelRenderer Liquid2_overflow;
	    ModelRenderer Rope1;
	    ModelRenderer Rope2;
	  
	    public SackModel()
	    {
	    	textureWidth = 64;
    	    textureHeight = 64;
	        setTextureOffset("Base.Shape1", 0, 0);
	        setTextureOffset("Base.Shape2", 0, 0);
	        setTextureOffset("Base.Shape3", 0, 0);
	        setTextureOffset("Base.Shape4", 0, 0);
	        setTextureOffset("Base.Shape5", 0, 0);
	        setTextureOffset("Top.Shape6", 0, 0);
	        setTextureOffset("Top.Shape7", 0, 0);
	        setTextureOffset("Top.Shape8", 0, 0);
	        setTextureOffset("Liquid.Shape9", 20, 20);
	        setTextureOffset("Liquid_overflow.Shape12", 46, 25);
	        setTextureOffset("Rope1.Shape12", 56, 17);
	        setTextureOffset("Rope2.Shape13", 48, 17);
	        
	        setTextureOffset("Liquid2.Shape14", 0, 32);
	        setTextureOffset("Liquid2_overflow.Shape15", 46, 57);
	        
	        Base = new ModelRenderer(this, "Base");
	        Base.setRotationPoint(0F, 8F, 0F);
	        setRotation(Base, 0F, 0F, 0F);
	        Base.mirror = true;
	          Base.addBox("Shape1", -6F, 15F, -4F, 12, 1, 12);
	          Base.addBox("Shape2", -6F, 2F, -4F, 12, 13, 1);
	          Base.addBox("Shape3", -6F, 2F, -3F, 1, 13, 10);
	          Base.addBox("Shape4", 5F, 2F, -3F, 1, 13, 10);
	          Base.addBox("Shape5", -6F, 0F, 7F, 12, 15, 1);
	        Top = new ModelRenderer(this, "Top");
	        Top.setRotationPoint(0F, 9F, 0F);
	        setRotation(Top, 0.05F, 0F, 0F);
	        Top.mirror = true;
	          Top.addBox("Shape6", -6.2F, -0.5F, -4.4F, 1, 2, 12);
	          Top.addBox("Shape7", 5.2F, -0.5F, -4.4F, 1, 2, 12);
	          Top.addBox("Shape8", -6F, -0.4F, -4.5F, 12, 2, 1);
	        Liquid = new ModelRenderer(this, "Liquid");
	        Liquid.setRotationPoint(0F, 8F, 0F);
	        setRotation(Liquid, 0F, 0F, 0F);
	        Liquid.mirror = true;
	          Liquid.addBox("Shape9", -5.5F, 0.5F, -3.5F, 11, 1, 11);
	        Liquid_overflow = new ModelRenderer(this, "Liquid_overflow");
	        Liquid_overflow.setRotationPoint(0F, 8F, 0F);
	        setRotation(Liquid_overflow, 0.051F, 0F, 0.01F);
	        Liquid_overflow.mirror = true;
	          Liquid_overflow.addBox("Shape12", -4F, 0.55F, -4.4F, 8, 2, 2);
	         
		     Liquid2 = new ModelRenderer(this, "Liquid2");
		       Liquid2.setRotationPoint(0F, 8F, 0F);
		       setRotation(Liquid2, 0F, 0F, 0F);
		        Liquid2.mirror = true;
		          Liquid2.addBox("Shape14", -5.5F, 0.5F, -3.5F, 11, 1, 11);
		     Liquid2_overflow = new ModelRenderer(this, "Liquid2_overflow");
		        Liquid2_overflow.setRotationPoint(0F, 8F, 0F);
		        setRotation(Liquid2_overflow, 0.051F, 0F, 0.01F);
		        Liquid2_overflow.mirror = true;
		          Liquid2_overflow.addBox("Shape15", -4F, 0.55F, -4.4F, 8, 2, 2);
	          
	          
	        Rope1 = new ModelRenderer(this, "Rope1");
	          Rope1.setRotationPoint(0F, 8F, 0F);
	          setRotation(Rope1, 0F, 0F, -0.2F);
	          Rope1.mirror = true;
	            Rope1.addBox("Shape12", 5F, 0.7F, 7.9F, 3, 1, 1);
	        Rope2 = new ModelRenderer(this, "Rope2");
	          Rope2.setRotationPoint(0F, 8F, 0F);
	          setRotation(Rope2, 0F, 0F, 0.2F);
	          Rope2.mirror = true;
	            Rope2.addBox("Shape13", -8F, 0.7F, 7.9F, 3, 1, 1);
	    }

	  private void setRotation(ModelRenderer model, float x, float y, float z)
	  {
	    model.rotateAngleX = x;
	    model.rotateAngleY = y;
	    model.rotateAngleZ = z;
	  }
	  
	  @Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	  {
	    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	  }
}


