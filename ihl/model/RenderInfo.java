package ihl.model;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class RenderInfo {

    public Block template = Blocks.stone;
    public IIcon[] texture = null;
    public IIcon override = null;
    public float minX = 0;
    public float minY = 0;
    public float minZ = 0;
    public float maxX = 1;
    public float maxY = 1;
    public float maxZ = 1;
    public boolean[] renderSide = new boolean[6];
    public float light = -1f;
    public int brightness = -1;

    public RenderInfo() {
        setRenderAllSides();
    }

    public RenderInfo(Block template, IIcon[] texture) {
        this();
        this.template = template;
        this.texture = texture;
    }

    public RenderInfo(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this();
        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public final void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public final void setRenderSingleSide(int side) {
        Arrays.fill(renderSide, false);
        renderSide[side] = true;
    }

    public final void setRenderAllSides() {
        Arrays.fill(renderSide, true);
    }
    
    public IIcon getBlockTextureFromSide(int i) {
        if (override != null)
            return override;
        if (texture == null || texture.length == 0)
            return template.getBlockTextureFromSide(i);
        else {
            if (i >= texture.length)
                i = 0;
            return texture[i];
        }
    }
}
