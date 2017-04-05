package ihl_coremod;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(value = "IHL CoreMod")
@IFMLLoadingPlugin.MCVersion(value = "1.7.10")
@IFMLLoadingPlugin.TransformerExclusions(value = "ihl_coremod.")
@IFMLLoadingPlugin.SortingIndex(value = 0)
public class IHLCoremod implements IFMLLoadingPlugin{
    public static Logger log;

    public IHLCoremod()
    {
        log = LogManager.getLogger("IHLCoremod");
        log.info("IHL coremod logger created.");
    }
    
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {WorldRendererClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return IHLCoremodContainer.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
