package leppa.iterationpermutation.network;

import leppa.iterationpermutation.research.Knowledge;
import leppa.iterationpermutation.research.client.ClientKnowledges;
import leppa.iterationpermutation.research.client.ClientResearchPage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PktSyncResearch{
	
	List<Knowledge> knowledges;
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String KNOWLEDGES_KEY = "knowledges";
	
	public PktSyncResearch(List<Knowledge> knowledges){
		this.knowledges = knowledges;
	}
	
	public static void handle(PktSyncResearch msg, Supplier<NetworkEvent.Context> ctx){
		ctx.get().enqueueWork(() -> {
			ClientKnowledges.clear();
			LOGGER.info("Cleared client researches.");
			for(Knowledge knowledge : msg.knowledges){
				ClientKnowledges.addKnowledge(new ResourceLocation(knowledge.getKey()), knowledge);
				LOGGER.info("Added client knowledge: " + knowledge.getKey() + ".");
			}
			ClientKnowledges.allPages()
					.filter(ClientResearchPage.class::isInstance)
					.map(ClientResearchPage.class::cast)
					.forEach(ClientResearchPage::resolve);
			LOGGER.info("Resolved client researches.");
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static void encode(PktSyncResearch msg, PacketBuffer packetBuffer){
		CompoundNBT nbt = new CompoundNBT();
		ListNBT listNBT = new ListNBT();
		for(Knowledge knowledge : msg.knowledges)
			listNBT.add(knowledge.getData());
		nbt.put(KNOWLEDGES_KEY, listNBT);
		packetBuffer.writeCompoundTag(nbt);
	}
	
	public static PktSyncResearch decode(PacketBuffer packetBuffer){
		CompoundNBT nbt = packetBuffer.readCompoundTag();
		assert nbt != null;
		List<Knowledge> knowledges = nbt.getList(KNOWLEDGES_KEY, 10).stream()
				.map(CompoundNBT.class::cast)
				.map(Knowledge::fromData)
				.collect(Collectors.toList());
		return new PktSyncResearch(knowledges);
	}
}
