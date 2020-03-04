package leppa.iterationpermutation.network;

import leppa.iterationpermutation.research.Researcher;
import leppa.iterationpermutation.research.client.ClientKnowledges;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PktResetResearch{
	
	String researchKey;
	
	public PktResetResearch(String researchKey){
		this.researchKey = researchKey;
	}
	
	public static void handle(PktResetResearch msg, Supplier<NetworkEvent.Context> ctx){
		ctx.get().enqueueWork(() -> ClientKnowledges.pageForId(msg.researchKey).ifPresent(p -> Researcher.getFrom(Minecraft.getInstance().player).reset(p)));
		ctx.get().setPacketHandled(true);
		// from server to player
	}
	
	public static void encode(PktResetResearch msg, PacketBuffer packetBuffer){
		packetBuffer.writeString(msg.researchKey);
	}
	
	public static PktResetResearch decode(PacketBuffer buffer){
		return new PktResetResearch(buffer.readString());
	}
}