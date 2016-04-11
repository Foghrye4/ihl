package ihl.model;

import ihl.handpump.BlockWithCoordinates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class IHLBlockRenderer {
	
	private RenderBlocks renderBlocks;
    private final Map<BlockWithCoordinates, Integer> renderCache = new HashMap<BlockWithCoordinates, Integer>();
	public static IHLBlockRenderer instance;
    
    public IHLBlockRenderer()
    {
    	renderBlocks=RenderBlocks.getInstance();
    	instance=this;
    }
    
    public void refreshDisplayLists(int diplayLists, List<BlockWithCoordinates> bwc, ChunkCache iBlockAccess) 
    {
        if (bwc==null || bwc.isEmpty())
        {
            return;
        }
       	renderBlocks.blockAccess=iBlockAccess;
       	GL11.glNewList(diplayLists, 4864 /*GL_COMPILE*/);
		Iterator<BlockWithCoordinates> bwci = bwc.iterator();
		while(bwci.hasNext())
		{
			BlockWithCoordinates bwce = bwci.next();
	       	renderBlock(bwce);
		}
       	GL11.glEndList();
    }
    
    
    public int getBlockDisplayLists(BlockWithCoordinates bwc, World world) {
        if (bwc.block == null)
        {
            return -1;
        }
        if(renderCache.containsKey(bwc))
        {
        	return renderCache.get(bwc);
        }
        int diplayLists = GLAllocation.generateDisplayLists(1);
        if(renderBlocks.blockAccess==null || !renderBlocks.blockAccess.equals(world))
        {
        	renderBlocks.blockAccess=world;
        }
       	GL11.glNewList(diplayLists, 4864 /*GL_COMPILE*/);
       	renderBlock(bwc);
       	GL11.glEndList();
        renderCache.put(bwc, diplayLists);
        return diplayLists;
    }
    
    private void renderBlock(BlockWithCoordinates bwc)
    {
       	Tessellator tessellator = Tessellator.instance;
       	tessellator.startDrawingQuads();
       	renderBlocks.renderBlockByRenderType(bwc.block, bwc.x, bwc.y, bwc.z);
       	tessellator.draw();
    }
}
