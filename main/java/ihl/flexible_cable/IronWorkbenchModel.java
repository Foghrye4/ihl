package ihl.flexible_cable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class IronWorkbenchModel extends ModelBase {
	  //fields
    ModelRenderer Base;
    
    public IronWorkbenchModel()
    {
    	   textureWidth = 64;
    	    textureHeight = 32;
    	    setTextureOffset("Base.Shape1", 20, 0);
    	    setTextureOffset("Base.Shape2", 20, 0);
    	    setTextureOffset("Base.Shape3", 23, 0);
    	    setTextureOffset("Base.Shape4", 23, 0);
    	    setTextureOffset("Base.Shape5", 0, 0);
    	    setTextureOffset("Base.Shape6", 16, 0);
    	    setTextureOffset("Base.Shape7", 1, 21);
    	    setTextureOffset("Base.Shape8", 0, 17);
    	    setTextureOffset("Base.Shape9", 0, 0);
    	    setTextureOffset("Base.Shape10", 0, 0);
    	    setTextureOffset("Base.Shape11", 0, 0);
    	    setTextureOffset("Base.Shape12", 0, 0);
    	    setTextureOffset("Base.Shape13", 0, 0);
    	    setTextureOffset("Base.Shape14", 0, 0);
    	    
    	    Base = new ModelRenderer(this, "Base");
    	    Base.setRotationPoint(0F, 8F, 0F);
    	    Base.mirror = true;
    	      Base.addBox("Shape1", 5F, 1F, -7F, 2, 15, 2);
    	      Base.addBox("Shape2", 5F, 1F, 5F, 2, 15, 2);
    	      Base.addBox("Shape3", -7F, 1F, -7F, 2, 15, 2);
    	      Base.addBox("Shape4", -7F, 1F, 5F, 2, 15, 2);
    	      Base.addBox("Shape5", -8F, 0F, -8F, 16, 1, 16);
    	      Base.addBox("Shape6", -6F, 10F, -6F, 12, 1, 12);
    	      Base.addBox("Shape7", 5F, 2F, -8F, 2, 2, 1);
    	      Base.addBox("Shape8", -1F, -7F, 4F, 8, 1, 3);
    	      Base.addBox("Shape9", 5F, -1F, 4F, 3, 1, 3);
    	      Base.addBox("Shape10", 7F, -7F, 5F, 1, 6, 1);
    	      Base.addBox("Shape11", -5F, 1F, -6F, 10, 2, 1);
    	      Base.addBox("Shape12", -5F, 1F, 5F, 10, 2, 1);
    	      Base.addBox("Shape13", 5F, 1F, -5F, 1, 2, 10);
    	      Base.addBox("Shape14", -6F, 1F, -5F, 1, 2, 10);
    }

}
