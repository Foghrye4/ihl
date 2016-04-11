package ihl.tunneling_shield;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class DriverModel extends ModelBase {
	  //fields
		ModelRenderer Shield;
	    ModelRenderer ShieldA;
	    ModelRenderer ShieldB;
	    ModelRenderer ShieldC;
	    ModelRenderer AdvancedShieldA;

	    ModelRenderer Base;
		ModelRenderer ShaftA;
		ModelRenderer ShaftB;
	  
	    public DriverModel()
	    {
	    	   textureWidth = 512;
	    	    textureHeight = 256;
	    	    setTextureOffset("ShieldB.Shape1", 0, 84);
	    	    setTextureOffset("ShieldB.Shape2", 0, 84);
	    	    setTextureOffset("ShieldB.Shape3", 0, 84);
	    	    setTextureOffset("ShieldB.Shape4", 0, 84);
	    	    setTextureOffset("ShieldC.Shape1", 0, 84);
	    	    setTextureOffset("ShieldC.Shape2", 0, 84);
	    	    setTextureOffset("ShieldC.Shape3", 0, 84);
	    	    setTextureOffset("ShieldC.Shape4", 0, 84);
	    	    setTextureOffset("ShieldA.Shape1", 0, 132);
	    	    setTextureOffset("ShieldA.Shape2", 0, 132);
	    	    setTextureOffset("ShieldA.Shape3", 0, 132);
	    	    
	    	    setTextureOffset("AdvancedShieldA.Shape1", 173, 132);
	    	    setTextureOffset("AdvancedShieldA.Shape2", 173, 132);
	    	    setTextureOffset("AdvancedShieldA.Shape3", 173, 132);
	    	    
	    	    setTextureOffset("Shield.Shape1", 0, 0);
	    	    setTextureOffset("Shield.Shape2", 0, 0);
	    	    setTextureOffset("Shield.Shape3", 0, 0);
	    	    setTextureOffset("Shield.Shape4", 98, 84);
	    	    setTextureOffset("Shield.Shape5", 98, 84);
	    	    setTextureOffset("Shield.Shape6", 0, 0);
	    	    setTextureOffset("Shield.Shape7", 0, 0);
	    	    setTextureOffset("Shield.Shape8", 98, 84);
	    	    setTextureOffset("Shield.Shape9", 0, 0);
	    	    setTextureOffset("Shield.Shape10", 0, 0);
	    	    setTextureOffset("Shield.Shape11", 98, 84);
	    	    
	    	    setTextureOffset("Base.Base1", 80, 0);
	    	    setTextureOffset("Base.Base2", 83, 0);
	    	    setTextureOffset("Base.Base3", 98, 0);
	    	    setTextureOffset("Base.Base4", 98, 0);
	    	    setTextureOffset("Base.Base5", 98, 4);
	    	    setTextureOffset("Base.Base6", 98, 4);
	    	    setTextureOffset("Base.Base7", 103, 1);
	    	    setTextureOffset("Base.Base8", 98, 0);
	    	    setTextureOffset("Base.Base9", 98, 0);
	    	    setTextureOffset("Base.Base10", 98, 0);
	    	    setTextureOffset("Base.Base11", 98, 0);
	    	    setTextureOffset("Base.Base12", 98, 0);
	    	    setTextureOffset("Base.Base13", 99, 3);
	    	    setTextureOffset("ShaftA.ShapeA", 144, 0);
	    	    setTextureOffset("ShaftB.ShapeA", 144, 0);
	    	    
	    	    ShieldB = new ModelRenderer(this, "ShieldB");
	    	    ShieldB.setRotationPoint(0F, 16F, 0F);
	    	    setRotation(ShieldB, 0F, 0F, 0F);
	    	    ShieldB.mirror = true;
	    	      ShieldB.addBox("Shape1", -7F, -36F, 9F, 14, 16, 12);
	    	      ShieldB.addBox("Shape2", 20F, -7F, 9F, 16, 14, 12);
	    	      ShieldB.addBox("Shape3", -36F, -7F, 9F, 16, 14, 12);
	    	      ShieldB.addBox("Shape4", -7F, 20F, 9F, 14, 16, 12);
		    	ShieldC = new ModelRenderer(this, "ShieldB");
		        ShieldC.setRotationPoint(0F, 16F, 0F);
		    	setRotation(ShieldC, 0F, 0F, 0F);
		    	ShieldC.mirror = true;
		    	  ShieldC.addBox("Shape1", -7F, -36F, 9F, 14, 16, 12);
		    	  ShieldC.addBox("Shape2", 20F, -7F, 9F, 16, 14, 12);
		    	  ShieldC.addBox("Shape3", -36F, -7F, 9F, 16, 14, 12);
		    	  ShieldC.addBox("Shape4", -7F, 20F, 9F, 14, 16, 12);
	    	    ShieldA = new ModelRenderer(this, "ShieldA");
	    	    ShieldA.setRotationPoint(0F, 16F, 0F);
	    	    setRotation(ShieldA, 0F, 0F, 0F);
	    	    ShieldA.mirror = true;
	    	      ShieldA.addBox("Shape1", -36F, -8F, 10F, 72, 16, 12);
	    	      ShieldA.addBox("Shape2", -8F, -36F, 10F, 16, 28, 12);
	    	      ShieldA.addBox("Shape3", -8F, 8F, 10F, 16, 28, 12);
	    	      
		    	AdvancedShieldA = new ModelRenderer(this, "AdvancedShieldA");
		    	AdvancedShieldA.setRotationPoint(0F, 16F, 0F);
		    	setRotation(AdvancedShieldA, 0F, 0F, 0F);
		    	AdvancedShieldA.mirror = true;
		    	    AdvancedShieldA.addBox("Shape1", -36F, -8F, 10F, 72, 16, 12);
		    	    AdvancedShieldA.addBox("Shape2", -8F, -36F, 10F, 16, 28, 12);
		    	    AdvancedShieldA.addBox("Shape3", -8F, 8F, 10F, 16, 28, 12);
	    	      
	    	    Shield = new ModelRenderer(this, "Shield");
	    	    Shield.setRotationPoint(0F, 16F, 0F);
	    	    setRotation(Shield, 0F, 0F, 0F);
	    	    Shield.mirror = true;
	    	      Shield.addBox("Shape1", -6F, -6F, 8F, 12, 12, 2);
	    	      Shield.addBox("Shape2", -8F, -35F, 11F, 7, 70, 12);
	    	      Shield.addBox("Shape3", 1F, -35F, 11F, 7, 70, 12);
	    	      Shield.addBox("Shape4", -2F, -36F, 20F, 2, 36, 4);
	    	      Shield.addBox("Shape5", 0F, 0F, 20F, 2, 36, 4);
	    	      Shield.addBox("Shape6", -35F, 1F, 11F, 27, 7, 12);
	    	      Shield.addBox("Shape7", -35F, -8F, 11F, 27, 7, 12);
	    	      Shield.addBox("Shape8", -36F, 0F, 20F, 28, 2, 4);
	    	      Shield.addBox("Shape9", 8F, 1F, 11F, 27, 7, 12);
	    	      Shield.addBox("Shape10", 8F, -8F, 11F, 27, 7, 12);
	    	      Shield.addBox("Shape11", 8F, -2F, 20F, 28, 2, 4);
	    	      
	    	    Base = new ModelRenderer(this, "Base");
	    	    Base.setRotationPoint(-8F, 16F, 0F);
	    	    setRotation(Base, 0F, 0F, 0F);
	    	    Base.mirror = true;
	    	      Base.addBox("Base1", 0F, 7F, -8F, 16, 1, 16);
	    	      Base.addBox("Base2", 1F, -7F, -7F, 14, 13, 15);
	    	      Base.addBox("Base3", 0F, -6F, -6F, 1, 1, 13);
	    	      Base.addBox("Base4", 2F, -8F, -6F, 1, 1, 13);
	    	      Base.addBox("Base5", 0F, -4F, -6F, 1, 7, 14);
	    	      Base.addBox("Base6", 15F, -4F, -6F, 1, 7, 14);
	    	      Base.addBox("Base7", 5F, -8F, -6F, 6, 1, 14);
	    	      Base.addBox("Base8", 3F, 6F, -7F, 10, 1, 15);
	    	      Base.addBox("Base9", 0F, 4F, -6F, 1, 1, 13);
	    	      Base.addBox("Base10", 13F, -8F, -6F, 1, 1, 13);
	    	      Base.addBox("Base11", 15F, -6F, -6F, 1, 1, 13);
	    	      Base.addBox("Base12", 15F, 4F, -6F, 1, 1, 13);
	    	      Base.addBox("Base13", 3F, -5F, -8F, 10, 9, 1);

	    	    ShaftA = new ModelRenderer(this, "ShaftA");
	    	    ShaftA.setRotationPoint(0F, 16F, 0F);
	    	    setRotation(ShaftA, 0F, 0F, 0F);
	    	    ShaftA.mirror = true;
	    	      ShaftA.addBox("ShapeA", -5F, -5F, -0F, 10, 10, 8);
	    	      
	    	    ShaftB = new ModelRenderer(this, "ShaftB");
		    	ShaftB.setRotationPoint(0F, 16F, 0F);
		    	setRotation(ShaftB, 0F, 0F, 0F);
		    	ShaftB.mirror = true;
		    	  ShaftB.addBox("ShapeA", -5F, -5F, -8F, 10, 10, 8);
	      
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


