package leppa.iterationpermutation.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface PageSection{
	
	Map<String, Function<CompoundNBT, PageSection>> deserializers = new HashMap<>();
	
	int span();
	void render(boolean right, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY);
	default void renderAfter(boolean right, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY){};
	void addRequirement(Requirement requirement);
	List<Requirement> getRequirements();
	
	CompoundNBT getData();
	String getTypeId();
	
	default CompoundNBT getPassData(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("data", getData());
		nbt.putString("type", getTypeId());
		nbt.put("requirements", ResearchPage.nbtList(getRequirements().stream().map(Requirement::describe).collect(Collectors.toList())));
		return nbt;
	}
	
	static PageSection fromNBT(CompoundNBT nbt){
		PageSection ret = deserializers.get(nbt.getString("type")).apply((CompoundNBT)nbt.get("data"));
		nbt.getList("requirements", 8).forEach(inbt -> ret.addRequirement(Requirement.fromDescription(inbt.getString())));
		return ret;
	}
	
	static Minecraft mc(){
		return Minecraft.getInstance();
	}
	static FontRenderer fr(){
		return mc().fontRenderer;
	}
}