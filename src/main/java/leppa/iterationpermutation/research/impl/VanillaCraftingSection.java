package leppa.iterationpermutation.research.impl;

import leppa.iterationpermutation.research.PageSection;
import leppa.iterationpermutation.research.Requirement;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VanillaCraftingSection implements PageSection{
	
	// reference recipe jsons by ResourceLocation, for data/.../recipes/...
	// IRecipe::getIngredients, IRecipe::getResult
	// GhostRecipe -- sadly client-only
	
	List<Requirement> requirements = new ArrayList<>();
	String craftingLocation;
	
	static{
		deserializers.put("VanillaCraftingSection", nbt -> new VanillaCraftingSection(nbt.getString("crafting")));
	}
	
	public VanillaCraftingSection(String craftingLocation){
		this.craftingLocation = craftingLocation;
	}
	
	public int span(){
		return 1;
	}
	
	public void render(boolean right, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY){
		float x = (screenWidth / 2f - 128f) + (right ? 125 : 1); // slightly further left on the right page
		float y = (screenHeight / 2f - 128f) + 1;
		// just defer
		ClientVanillaCrafting.renderRecipe((int)x, (int)y, craftingLocation);
	}
	
	public void renderAfter(boolean right, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY){
		float x = (screenWidth / 2f - 128f) + (right ? 125 : 1); // slightly further left on the right page
		float y = (screenHeight / 2f - 128f) + 1;
		ClientVanillaCrafting.renderRecipeTooltips((int)x, (int)y, craftingLocation, mouseX, mouseY, screenWidth, screenHeight);
	}
	
	public void addRequirement(Requirement requirement){
		requirements.add(requirement);
	}
	
	public List<Requirement> getRequirements(){
		return Collections.unmodifiableList(requirements);
	}
	
	public CompoundNBT getData(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("crafting", craftingLocation);
		return nbt;
	}
	
	public String getTypeId(){
		return "VanillaCraftingSection";
	}
}