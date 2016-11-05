package ihl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL30;

import ihl.collector.ChargerEjectorModel;
import ihl.collector.ChargerEjectorRender;
import ihl.collector.ChargerEjectorTileEntity;
import ihl.collector.CollectorEntity;
import ihl.collector.CollectorHeavyEntity;
import ihl.collector.CollectorItemRender;
import ihl.collector.CollectorRender;
import ihl.collector.GlassBoxRender;
import ihl.collector.GlassBoxTileEntity;
import ihl.crop_harvestors.BlobEntityFX;
import ihl.crop_harvestors.BlobEntityFX.FluidType;
import ihl.crop_harvestors.BlobRenderFX;
import ihl.crop_harvestors.SackRender;
import ihl.crop_harvestors.SackTileEntity;
import ihl.datanet.DataCableItem;
import ihl.enviroment.LightBulbModel;
import ihl.enviroment.LightBulbRender;
import ihl.enviroment.LightBulbTileEntity;
import ihl.enviroment.MirrorRender;
import ihl.enviroment.MirrorTileEntity;
import ihl.enviroment.SpotlightModel;
import ihl.enviroment.SpotlightRender;
import ihl.enviroment.SpotlightTileEntity;
import ihl.flexible_cable.AnchorTileEntity;
import ihl.flexible_cable.BatterySwitchUnitModel;
import ihl.flexible_cable.BatterySwitchUnitTileEntity;
import ihl.flexible_cable.BlastEntityFX;
import ihl.flexible_cable.IronWorkbenchModel;
import ihl.flexible_cable.IronWorkbenchTileEntity;
import ihl.flexible_cable.NodeEntity;
import ihl.flexible_cable.NodeRender;
import ihl.flexible_cable.RectifierTransformerUnitTileEntity;
import ihl.interfaces.INetworkListener;
import ihl.interfaces.ISelectionBoxSpecialRenderer;
import ihl.items_blocks.FlexibleCableItem;
import ihl.items_blocks.IHLTool;
import ihl.items_blocks.MachineBaseBlock.MachineType;
import ihl.model.*;
import ihl.processing.chemistry.ElectrolysisBathModel;
import ihl.processing.chemistry.ElectrolysisBathTileEntity;
import ihl.processing.chemistry.FractionatorBottomModel;
import ihl.processing.chemistry.FractionatorBottomTileEntity;
import ihl.processing.chemistry.FractionatorCoverModel;
import ihl.processing.chemistry.FractionatorCoverTileEntity;
import ihl.processing.chemistry.FractionatorSectionModel;
import ihl.processing.chemistry.FractionatorSectionTileEntity;
import ihl.processing.chemistry.GaedesMercuryRotaryPumpModel;
import ihl.processing.chemistry.GaedesMercuryRotaryPumpTileEntity;
import ihl.processing.chemistry.GoldChimneyKneeModel;
import ihl.processing.chemistry.GoldChimneyKneeRender;
import ihl.processing.chemistry.GoldChimneyKneeTileEntity;
import ihl.processing.chemistry.LoomModel;
import ihl.processing.chemistry.LoomTileEntity;
import ihl.processing.chemistry.PrecipitatorCondenserModel;
import ihl.processing.chemistry.PrecipitatorCondenserRender;
import ihl.processing.chemistry.PrecipitatorCondenserTileEntity;
import ihl.processing.chemistry.RefluxCondenserModel;
import ihl.processing.chemistry.RefluxCondenserTileEntity;
import ihl.processing.metallurgy.CoilerModel;
import ihl.processing.metallurgy.CoilerRender;
import ihl.processing.metallurgy.CoilerTileEntity;
import ihl.processing.metallurgy.DetonationSprayingMachineModel;
import ihl.processing.metallurgy.DetonationSprayingMachineRender;
import ihl.processing.metallurgy.DetonationSprayingMachineTileEntity;
import ihl.processing.metallurgy.ExtruderModel;
import ihl.processing.metallurgy.ExtruderTileEntity;
import ihl.processing.metallurgy.GasWeldingStationModel;
import ihl.processing.metallurgy.GasWeldingStationTileEntity;
import ihl.processing.metallurgy.ImpregnatingMachineRender;
import ihl.processing.metallurgy.ImpregnatingMachineTileEntity;
import ihl.processing.metallurgy.LathePart1Model;
import ihl.processing.metallurgy.LathePart1TileEntity;
import ihl.processing.metallurgy.LathePart2Model;
import ihl.processing.metallurgy.LathePart2TileEntity;
import ihl.processing.metallurgy.RollingMachinePart1Model;
import ihl.processing.metallurgy.RollingMachinePart1TileEntity;
import ihl.processing.metallurgy.RollingMachinePart2Model;
import ihl.processing.metallurgy.RollingMachinePart2TileEntity;
import ihl.processing.metallurgy.RollingMachineRender;
import ihl.processing.metallurgy.VulcanizationExtrudingMoldModel;
import ihl.processing.metallurgy.VulcanizationExtrudingMoldTileEntity;
import ihl.processing.metallurgy.WoodenRollingMachinePart1TileEntity;
import ihl.processing.metallurgy.WoodenRollingMachinePart2Model;
import ihl.processing.metallurgy.WoodenRollingMachinePart2TileEntity;
import ihl.processing.metallurgy.WoodenRollingMachineRender;
import ihl.servitor.FlameEntityFX;
import ihl.servitor.FlameRenderFX;
import ihl.servitor.LostHeadEntity;
import ihl.servitor.LostHeadRender;
import ihl.servitor.SkullItemRender;
import ihl.tunneling_shield.BlockItemRender;
import ihl.tunneling_shield.DriverEntity;
import ihl.tunneling_shield.DriverModel;
import ihl.tunneling_shield.DriverRender;
import ihl.tunneling_shield.DriverRenderEntity;
import ihl.tunneling_shield.DriverTileEntity;
import ihl.utils.EntityDropEventHandler;
import ihl.utils.IHLRenderUtils;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientProxy extends ServerProxy {
	
	public boolean loadMirrorRender=true;
	public IHLRenderUtils renderUtils;
	public Map<MachineType,Integer> sharedBlockRenders = new HashMap();
	public Map<Class<? extends TileEntity>,ISelectionBoxSpecialRenderer> selectionBoxSpecialRendererRegistry = new HashMap();

	public ClientProxy() {}
	
	@Override
	public void load() throws ParserConfigurationException 
	{
    	if(this.channel==null)
    	{
    		this.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(IHLModInfo.MODID);
    		this.channel.register(this);
    	}
		this.renderUtils=new IHLRenderUtils();
		MinecraftForge.EVENT_BUS.register(this.renderUtils);
		
		registerBlockHandler(new ImpregnatingMachineBlockRender(), MachineType.BronzeTub, MachineType.GlassBox);
		registerBlockHandler(new RefluxCondenserBlockRender(), MachineType.RefluxCondenser);
		registerBlockHandler(new SwitchBoxBlockRender(), MachineType.RedstoneSignalConverter);
		registerBlockHandler(new RectifierTransformerUnitBlockRender(), MachineType.RectifierTransformerUnit);
		registerBlockHandler(new IronWorkbenchBlockRender(), MachineType.IronWorkbench);

		RenderingRegistry.registerEntityRenderingHandler(CollectorEntity.class, new CollectorRender(false));
		MinecraftForgeClient.registerItemRenderer(IHLMod.collectorItem, new CollectorItemRender(false));
		RenderingRegistry.registerEntityRenderingHandler(CollectorHeavyEntity.class, new CollectorRender(true));
		MinecraftForgeClient.registerItemRenderer(IHLMod.collectorHeavyItem, new CollectorItemRender(true));
		RenderingRegistry.registerEntityRenderingHandler(DriverEntity.class, new DriverRenderEntity());
		ClientRegistry.bindTileEntitySpecialRenderer(ChargerEjectorTileEntity.class, new ChargerEjectorRender());
		ClientRegistry.bindTileEntitySpecialRenderer(DriverTileEntity.class, new DriverRender());
		ClientRegistry.bindTileEntitySpecialRenderer(SackTileEntity.class, new SackRender());
		ClientRegistry.bindTileEntitySpecialRenderer(GlassBoxTileEntity.class, new GlassBoxRender());
		ClientRegistry.bindTileEntitySpecialRenderer(LathePart1TileEntity.class, new UniversalTileRender(new LathePart1Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(LathePart2TileEntity.class, new UniversalTileRender(new LathePart2Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(LoomTileEntity.class, new UniversalTileRender(new LoomModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/detonationSprayingMachine.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(ImpregnatingMachineTileEntity.class, new ImpregnatingMachineRender());
		ClientRegistry.bindTileEntitySpecialRenderer(DetonationSprayingMachineTileEntity.class, new DetonationSprayingMachineRender());
		ClientRegistry.bindTileEntitySpecialRenderer(GoldChimneyKneeTileEntity.class, new GoldChimneyKneeRender());
		ClientRegistry.bindTileEntitySpecialRenderer(PrecipitatorCondenserTileEntity.class, new PrecipitatorCondenserRender());
		ClientRegistry.bindTileEntitySpecialRenderer(CoilerTileEntity.class, new CoilerRender());
		ClientRegistry.bindTileEntitySpecialRenderer(RollingMachinePart1TileEntity.class, new RollingMachineRender());
		ClientRegistry.bindTileEntitySpecialRenderer(RollingMachinePart2TileEntity.class, new UniversalTileRender(new RollingMachinePart2Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(VulcanizationExtrudingMoldTileEntity.class, new UniversalTileRender(new VulcanizationExtrudingMoldModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(ExtruderTileEntity.class, new UniversalTileRender(new ExtruderModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(WoodenRollingMachinePart1TileEntity.class, new WoodenRollingMachineRender());
		ClientRegistry.bindTileEntitySpecialRenderer(WoodenRollingMachinePart2TileEntity.class, new UniversalTileRender(new WoodenRollingMachinePart2Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/woodenRollingMachine.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(GasWeldingStationTileEntity.class, new UniversalTileRender(new GasWeldingStationModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/gasWeldingStation.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(GaedesMercuryRotaryPumpTileEntity.class, new UniversalTileRender(new GaedesMercuryRotaryPumpModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/extruder.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(FractionatorBottomTileEntity.class, new UniversalTileRender(new FractionatorBottomModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(FractionatorSectionTileEntity.class, new UniversalTileRender(new FractionatorSectionModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(FractionatorCoverTileEntity.class, new UniversalTileRender(new FractionatorCoverModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(RefluxCondenserTileEntity.class, new UniversalTileRender(new RefluxCondenserModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(BatterySwitchUnitTileEntity.class, new UniversalTileRender(new BatterySwitchUnitModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/batterySwitchUnit.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(ElectrolysisBathTileEntity.class, new UniversalTileRender(new ElectrolysisBathModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/electrolysisBath.png")));
		if(loadMirrorRender && IHLMod.config.mirrorReflectionRange>0)
		{
			ClientRegistry.bindTileEntitySpecialRenderer(MirrorTileEntity.class, new MirrorRender());
		}
		ClientRegistry.bindTileEntitySpecialRenderer(LightBulbTileEntity.class, new LightBulbRender());
		ClientRegistry.bindTileEntitySpecialRenderer(SpotlightTileEntity.class, new SpotlightRender());
		selectionBoxSpecialRendererRegistry.put(AnchorTileEntity.class, new CableHolderSelectionBoxSpecialRenderer());
		selectionBoxSpecialRendererRegistry.put(RectifierTransformerUnitTileEntity.class, new RectifierTransformerUnitSelectionBoxSpecialRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(IHLMod.driverBlock), new BlockItemRender(new DriverModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/shield.png"), 5, 2, 0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(IHLMod.chargerEjectorBlock), new BlockItemRender(new ChargerEjectorModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/chargerEjector.png"), 0, 6, 0.5F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("goldPrecipitatorCondenser"), new BlockItemRender(new PrecipitatorCondenserModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/porcelainBox.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("goldChimneyKnee"), new BlockItemRender(new GoldChimneyKneeModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/porcelainBox.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("cannonBronze"), new BlockItemRender(new DetonationSprayingMachineModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/detonationSprayingMachine.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("lathePart1"), new BlockItemRender(new LathePart1Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("lathePart2"), new BlockItemRender(new LathePart2Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("rollingMachinePart1"), new BlockItemRender(new RollingMachinePart1Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png"), 0, 5, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("rollingMachinePart2"), new BlockItemRender(new RollingMachinePart2Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("coiler"), new BlockItemRender(new CoilerModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png"), 0, 6, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("loom"), new BlockItemRender(new LoomModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/detonationSprayingMachine.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("vulcanizationExtrudingMold"), new BlockItemRender(new VulcanizationExtrudingMoldModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("extruder"), new BlockItemRender(new ExtruderModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("woodenRollingMachinePart1"), new BlockItemRender(new WoodenRollingMachinePart2Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/woodenRollingMachine.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("woodenRollingMachinePart2"), new BlockItemRender(new WoodenRollingMachinePart2Model(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/woodenRollingMachine.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("gasWeldingStation"), new BlockItemRender(new GasWeldingStationModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/gasWeldingStation.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("gaedesMercuryRotaryPump"), new BlockItemRender(new GaedesMercuryRotaryPumpModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/extruder.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLMod.ihlSkull, new SkullItemRender());
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("fractionatorBottom"), new BlockItemRender(new FractionatorBottomModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("fractionatorSection"), new BlockItemRender(new FractionatorSectionModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("fractionatorCover"), new BlockItemRender(new FractionatorCoverModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("refluxCondenser"), new BlockItemRender(new RefluxCondenserModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/splitInjectionMold.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("batterySwitchUnit"), new BlockItemRender(new BatterySwitchUnitModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/batterySwitchUnit.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("electrolysisBath"), new BlockItemRender(new ElectrolysisBathModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/electrolysisBath.png"), 0, 0, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("lightBulb"), new BlockItemRender(new LightBulbModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/lightBulb.png"), 0, 1, 0.0F, 0.0F, true));
		MinecraftForgeClient.registerItemRenderer(IHLUtils.getThisModItem("spotlight"), new BlockItemRender(new SpotlightModel(), new ResourceLocation(IHLModInfo.MODID+":textures/blocks/spotlight.png"), 0, 1, 0.0F, 0.0F));
		MinecraftForgeClient.registerItemRenderer(IHLTool.instance, new IHLToolRenderer());
		MinecraftForgeClient.registerItemRenderer(FlexibleCableItem.instance, new FlexibleCableItemRender());
		MinecraftForgeClient.registerItemRenderer(DataCableItem.dataCableInstance, new FlexibleCableItemRender());
		RenderingRegistry.registerEntityRenderingHandler(LostHeadEntity.class, new LostHeadRender());
		RenderingRegistry.registerEntityRenderingHandler(FlameEntityFX.class, new FlameRenderFX(IHLModInfo.MODID+":textures/particles/flameTongue.png"));
		RenderingRegistry.registerEntityRenderingHandler(BlastEntityFX.class, new FlameRenderFX(IHLModInfo.MODID+":textures/particles/blast.png"));
		RenderingRegistry.registerEntityRenderingHandler(BlobEntityFX.class, new BlobRenderFX());
		RenderingRegistry.registerEntityRenderingHandler(NodeEntity.class, new NodeRender());
		MinecraftForge.EVENT_BUS.register(new RenderGameOverlayEventHandler());
		
	}
	
	@Override
	public void spawnParticle(int particle, World world, double x, double y, double z, double mx, double my, double mz, float particleScale)
	{
		switch(particle)
		{
			case 0:
		    	FlameEntityFX flamePEFX1 = new FlameEntityFX(world,x,y,z,mx,my,mz,particleScale);
		    	world.spawnEntityInWorld(flamePEFX1);
		    	break;
			case 1:
		    	BlobEntityFX blob = new BlobEntityFX(world,x,y,z,mx,my,mz,particleScale, FluidType.RESIN);
		    	world.spawnEntityInWorld(blob);
		    	break;
			case 2:
		    	BlobEntityFX blob2 = new BlobEntityFX(world,x,y,z,mx,my,mz,particleScale, FluidType.SAP);
		    	world.spawnEntityInWorld(blob2);
		    	break;
			case 3:
				BlastEntityFX blast = new BlastEntityFX(world,x,y,z,mx,my,mz,particleScale);
		    	world.spawnEntityInWorld(blast);
		    	break;
			default:
		    	FlameEntityFX flamePEFX2 = new FlameEntityFX(world,x,y,z,mx,my,mz,particleScale);
		    	world.spawnEntityInWorld(flamePEFX2);
		    	break;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) 
	{
		if (event.map.getTextureType() == 0) 
		{
			IHLFluid.registerIcons(event.map);
		}
	}

	@Override
	public Render getRenderForEntityClass(Class<? extends Entity> entityClass) 
	{
		return (Render) RenderManager.instance.entityRenderMap.get(entityClass);
	}
	
	@Override
	public void initBlockRenderer()
	{
		new IHLBlockRenderer();
	}

	@Override
	public int getGLDisplayList() 
	{
		return GLAllocation.generateDisplayLists(1);
	}
	
	@Override
    public File getMinecraftDir()
    {
        return Minecraft.getMinecraft().mcDataDir;
    }

	@Override
	public int shareBlockRendererByMachineType(MachineType type) 
	{
		return sharedBlockRenders.get(type);
	}
	
	public void registerBlockHandler(ISimpleBlockRenderingHandler handler, MachineType... types)
	{
		RenderingRegistry.registerBlockHandler(handler);
		for(MachineType type:types)
		{
			sharedBlockRenders.put(type, handler.getRenderId());
		}
	}
    
    @SubscribeEvent
	public void onPacketFromServerToClient(FMLNetworkEvent.ClientCustomPacketEvent event) throws IOException
	{
    	ByteBuf data = event.packet.payload();
    	ByteBufInputStream byteBufInputStream = new ByteBufInputStream(data);
        switch(byteBufInputStream.read())
        {
        	case 0:
        		int particleId = byteBufInputStream.read();
        		float x=byteBufInputStream.readFloat();
    			float y=byteBufInputStream.readFloat();
    			float z=byteBufInputStream.readFloat();
    			float mx=byteBufInputStream.readFloat();
    			float my=byteBufInputStream.readFloat();
    			float mz=byteBufInputStream.readFloat();
    			float particleScale=byteBufInputStream.readFloat();
    			this.spawnParticle(particleId, Minecraft.getMinecraft().theWorld, x, y, z, mx, my, mz, particleScale);
    			break;
        	case 1:
        		int entityId = byteBufInputStream.readInt();
        		INetworkListener listener = (INetworkListener) Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
        		if(listener!=null)
        		{
        			listener.recieveData(byteBufInputStream);
        			IHLMod.log.debug("Data delivered to client");
        		}
        		else
        		{
        			delayedEntityDataPacket.put(entityId, data);
        			IHLMod.log.debug("Data delayed. Entity ID="+entityId);
        		}
    			break;
        }
        byteBufInputStream.close();
	}
    
    @Override
	public boolean renderTESpecialSelectionBox(TileEntity te, EntityPlayer player,	ItemStack currentItem, MovingObjectPosition target,	float partialTicks) 
	{
    	ISelectionBoxSpecialRenderer ssbr = selectionBoxSpecialRendererRegistry.get(te.getClass());
		if(ssbr!=null)
		{
			ssbr.drawSelectionBox(player, currentItem, target, partialTicks);
			return true;
		}
		else
		{
			return false;
		}
	}
    
    @Override
	public void sendItemStackNBTTagFromClientToServerPlayer(EntityPlayer player, int slotNumber, String fieldName, int fieldValue)
    {
		ByteBuf bb = Unpooled.buffer(36); 
		ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(bb);
		try 
		{
			byteBufOutputStream.write(0);
			byteBufOutputStream.writeInt(player.getEntityId());
			byteBufOutputStream.writeInt(player.worldObj.provider.dimensionId);
			byteBufOutputStream.writeInt(slotNumber);
			byteBufOutputStream.writeInt(fieldValue);
			byteBufOutputStream.writeUTF(fieldName);
			channel.sendToServer(new FMLProxyPacket(byteBufOutputStream.buffer(),IHLModInfo.MODID));
			byteBufOutputStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

    }

}
