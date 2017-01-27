package ihl.model;

import ihl.utils.IHLRenderUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class RenderGameOverlayEventHandler 
{
	
	private static final ResourceLocation thermometerGUI = new ResourceLocation("ihl", "textures/gui/GUIThermometer.png");
	public static RenderGameOverlayEventHandler instance;
	public long temperature = 0;
	public int displayCurrentGUITimer = 0;
	
	public RenderGameOverlayEventHandler()
	{
		instance=this;
	}
	
	public void showThermometerTemperature(int temperature1, int displayCurrentGUITimer1)
	{
		
	}
	
	@SubscribeEvent
	public void renderGameOverlayEvent(RenderGameOverlayEvent.Pre/*.Post*/ event)
	{
		if(displayCurrentGUITimer>0)
		{
			displayCurrentGUITimer--;
			Minecraft.getMinecraft().renderEngine.bindTexture(thermometerGUI);
			IHLRenderUtils.instance.drawThermometerTemperature(temperature, displayCurrentGUITimer>500);
		}
	}

}
