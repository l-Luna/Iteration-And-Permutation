package leppa.iterationpermutation.research.impl;

import leppa.iterationpermutation.research.PageSection;
import leppa.iterationpermutation.research.Requirement;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmptySection implements PageSection{
	
	List<Requirement> requirements = new ArrayList<>();
	
	static{
		deserializers.put("EmptySection", __ -> new EmptySection());
	}
	
	public int span(){
		return 0;
	}
	
	public void render(boolean right, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY){}
	
	public void addRequirement(Requirement requirement){
		requirements.add(requirement);
		// do empty sections really need requirements? might be useful for multiple stages of requirements
		// for one section, but it's quite ambiguous on the player's side as to why more stuff is appearing.
	}
	
	public List<Requirement> getRequirements(){
		return Collections.unmodifiableList(requirements);
	}
	
	public CompoundNBT getData(){
		return new CompoundNBT();
	}
	
	public String getTypeId(){
		return "EmptySection";
	}
}