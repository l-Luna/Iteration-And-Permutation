package leppa.iterationpermutation.proxy;

import leppa.iterationpermutation.research.Knowledge;
import leppa.iterationpermutation.research.ResearchPage;
import net.minecraft.entity.player.PlayerEntity;

public interface Proxy{
	
	Knowledge getKnowledge(String id);
	
	default void openResearchScreen(String knowledge, PlayerEntity player){}
	
	default void setupClient(){}
}