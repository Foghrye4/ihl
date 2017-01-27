package ihl.datanet;

import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiInvisibleButton extends GuiButton {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GuiInvisibleButton(int id1, int x, int y, int w, int h, Map<Integer, Integer[]> linksCoordinatesMap, List buttonList) 
	{
        super(id1, x, y, w, h, "");
        linksCoordinatesMap.put(id1, new Integer[] {x+w/2,y+h/2});
        buttonList.add(this);
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int i, int j){}

}
