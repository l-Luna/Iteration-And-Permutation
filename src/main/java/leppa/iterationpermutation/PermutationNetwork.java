package leppa.iterationpermutation;

import leppa.iterationpermutation.network.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PermutationNetwork{
	
	private static int id = -100;
	
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(PermutationMod.MODID, "main"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	
	@SuppressWarnings("Convert2MethodRef")
	static void init(){
		INSTANCE.registerMessage(id++, PktSyncResearch.class, PktSyncResearch::encode, PktSyncResearch::decode, PktSyncResearch::handle);
		INSTANCE.registerMessage(id++, PktResetResearch.class, (m, packetBuffer) -> PktResetResearch.encode(m, packetBuffer), buffer -> PktResetResearch.decode(buffer), (m, c) -> PktResetResearch.handle(m, c));
		INSTANCE.registerMessage(id++, PktAdvanceResearch.class, (m, p) -> PktAdvanceResearch.encode(m, p), buffer -> PktAdvanceResearch.decode(buffer), (m, c) -> PktAdvanceResearch.handle(m, c));
		INSTANCE.registerMessage(id++, PktCompleteResearch.class, (m, packetBuffer) -> PktCompleteResearch.encode(m, packetBuffer), buffer -> PktCompleteResearch.decode(buffer), (m, c) -> PktCompleteResearch.handle(m, c));
		INSTANCE.registerMessage(id++, PktTryAdvanceResearch.class, (m, p) -> PktTryAdvanceResearch.encode(m, p), buffer -> PktTryAdvanceResearch.decode(buffer), (m, c) -> PktTryAdvanceResearch.handle(m, c));
	}
}