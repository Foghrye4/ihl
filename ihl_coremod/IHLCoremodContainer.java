package ihl_coremod;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

public class IHLCoremodContainer extends DummyModContainer {

	public IHLCoremodContainer() {
		super(new ModMetadata());
		ModMetadata myMeta = super.getMetadata();
		myMeta.authorList = Arrays.asList(new String[] { "Foghrye4" });
		myMeta.description = "Core mod for ASM tweaks";
		myMeta.modId = "ihl_coremod";
		myMeta.version = "1.0";
		myMeta.name = "IHL core mod";
	}
	
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	bus.register(this);
        return true;
    }
}
