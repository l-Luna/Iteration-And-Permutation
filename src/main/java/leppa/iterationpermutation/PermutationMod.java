package leppa.iterationpermutation;

import leppa.iterationpermutation.commands.ResearchCommand;
import leppa.iterationpermutation.gui.ResearchPageScreen;
import leppa.iterationpermutation.network.PktSyncResearch;
import leppa.iterationpermutation.proxy.ClientProxy;
import leppa.iterationpermutation.proxy.Proxy;
import leppa.iterationpermutation.proxy.ServerProxy;
import leppa.iterationpermutation.research.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod("iteration-and-permutation")
public class PermutationMod{
	
	public static final String MODID = "iteration-and-permutation";
	public static JsonResearchManager researchManager;
	
	public static Proxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	
	public PermutationMod(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
		MinecraftForge.EVENT_BUS.addListener(this::setupServer);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::playerJoined);
		MinecraftForge.EVENT_BUS.addListener(this::attachEntityCapabilities);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
		
		Requirement.init();
	}
	
	private void setupClient(FMLClientSetupEvent event){
		proxy.setupClient();
	}
	
	private void setupServer(FMLServerAboutToStartEvent event){
		event.getServer().getResourceManager().addReloadListener(researchManager = new JsonResearchManager());
	}
	
	private void serverStarting(FMLServerStartingEvent event){
		ResearchCommand.register(event.getCommandDispatcher());
	}
	
	private void setup(FMLCommonSetupEvent event){
		// pre-init
		ResearchCapability.init();
		PermutationNetwork.init();
	}
	
	private void playerJoined(PlayerEvent.PlayerLoggedInEvent event){
		ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
		PermutationNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PktSyncResearch(Knowledges.getKnowledges()));
	}
	
	private void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof PlayerEntity)
			event.addCapability(ResearchCapability.Provider.NAME, new ResearchCapability.Provider());
	}
	
	private void onPlayerClone(PlayerEvent.Clone event){
		Researcher.getFrom(event.getPlayer()).setData(Researcher.getFrom(event.getOriginal()));
	}
	
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	static class RegisterEvents{
		
		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> register){
			PermutationItems.registerItems(register);
		}
		
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> register){
			// PermutationBlocks.registerBlocks(register);
		}
	}
}
