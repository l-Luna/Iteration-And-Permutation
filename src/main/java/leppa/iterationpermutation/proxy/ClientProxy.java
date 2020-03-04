package leppa.iterationpermutation.proxy;

import leppa.iterationpermutation.gui.ResearchBookScreen;
import leppa.iterationpermutation.gui.ResearchPageScreen;
import leppa.iterationpermutation.research.Knowledge;
import leppa.iterationpermutation.research.client.ClientKnowledges;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements Proxy{
	
	public Knowledge getKnowledge(String id){
		return ClientKnowledges.getKnowledge(new ResourceLocation(id));
	}
	
	public void openResearchScreen(String knowledge, PlayerEntity player){
		Minecraft.getInstance().displayGuiScreen(new ResearchBookScreen(getKnowledge(knowledge), player));
	}
	
	public void setupClient(){
		MinecraftForge.EVENT_BUS.addListener(ResearchPageScreen::onResearchAdvanced);
	}
}