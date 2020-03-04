package leppa.iterationpermutation.network;

import leppa.iterationpermutation.PermutationNetwork;
import leppa.iterationpermutation.research.Knowledges;
import leppa.iterationpermutation.research.PageSection;
import leppa.iterationpermutation.research.ResearchPage;
import leppa.iterationpermutation.research.Researcher;
import leppa.iterationpermutation.research.client.ClientKnowledges;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PktTryAdvanceResearch{
	
	String researchKey;
	
	public PktTryAdvanceResearch(String researchKey){
		this.researchKey = researchKey;
	}
	
	public static void handle(PktTryAdvanceResearch msg, Supplier<NetworkEvent.Context> ctx){
		ctx.get().enqueueWork(() -> {
			//ClientKnowledges.pageForId(msg.researchKey).ifPresent(p -> Researcher.getFrom(Minecraft.getInstance().player).advance(p));
			
			// get player
			ServerPlayerEntity sender = ctx.get().getSender();
			Knowledges.pageForId(msg.researchKey).ifPresent(page -> {
				if(Researcher.canAdvance(sender, page)){
					Researcher.advanceAndTake(sender, page);
					// send back PktAdvanceResearch
					PermutationNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new PktAdvanceResearch(msg.researchKey));
				}
			});
		});
		ctx.get().setPacketHandled(true);
		// from player to server
	}
	
	public static void encode(PktTryAdvanceResearch msg, PacketBuffer packetBuffer){
		packetBuffer.writeString(msg.researchKey);
	}
	
	public static PktTryAdvanceResearch decode(PacketBuffer buffer){
		return new PktTryAdvanceResearch(buffer.readString());
	}
}
