package leppa.iterationpermutation.network;

import leppa.iterationpermutation.research.Researcher;
import leppa.iterationpermutation.research.client.ClientKnowledges;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PktAdvanceResearch{
	
	String researchKey;
	
	public PktAdvanceResearch(String researchKey){
		this.researchKey = researchKey;
	}
	
	public static void handle(PktAdvanceResearch msg, Supplier<NetworkEvent.Context> ctx){
		ctx.get().enqueueWork(() -> ClientKnowledges.pageForId(msg.researchKey).ifPresent(p -> Researcher.getFrom(Minecraft.getInstance().player).advance(p)));
		ctx.get().setPacketHandled(true);
		// from server to player
	}
	
	public static void encode(PktAdvanceResearch msg, PacketBuffer packetBuffer){
		packetBuffer.writeString(msg.researchKey);
	}
	
	public static PktAdvanceResearch decode(PacketBuffer buffer){
		return new PktAdvanceResearch(buffer.readString());
	}
}
