package leppa.iterationpermutation.network;

import leppa.iterationpermutation.research.Researcher;
import leppa.iterationpermutation.research.client.ClientKnowledges;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PktCompleteResearch{
	
	String researchKey;
	
	public PktCompleteResearch(String researchKey){
		this.researchKey = researchKey;
	}
	
	public static void handle(PktCompleteResearch msg, Supplier<NetworkEvent.Context> ctx){
		ctx.get().enqueueWork(() -> ClientKnowledges.pageForId(msg.researchKey).ifPresent(p -> Researcher.getFrom(Minecraft.getInstance().player).complete(p)));
		ctx.get().setPacketHandled(true);
		// from server to player
	}
	
	public static void encode(PktCompleteResearch msg, PacketBuffer packetBuffer){
		packetBuffer.writeString(msg.researchKey);
	}
	
	public static PktCompleteResearch decode(PacketBuffer buffer){
		return new PktCompleteResearch(buffer.readString());
	}
}