package leppa.iterationpermutation.research;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResearchTree{
	
	List<ResearchPage> pages = new ArrayList<>();
	String key;
	String name;
	ResourceLocation icon;
	
	public List<ResearchPage> getPages(){
		return pages;
	}
	
	public Optional<ResearchPage> forId(String id){
		for(ResearchPage x : pages)
			if(x.id().equals(id))
				return Optional.of(x);
		return Optional.empty();
	}
	
	public CompoundNBT getData(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("key", key);
		nbt.putString("name", name);
		nbt.putString("icon", icon.getNamespace() + ":" + icon.getPath());
		nbt.put("pages", ResearchPage.nbtListOfNbt(pages.stream().map(ResearchPage::getData).collect(Collectors.toList())));
		return nbt;
	}
	
	public static ResearchTree fromNBT(CompoundNBT nbt){
		ResearchTree tree = new ResearchTree();
		tree.pages = nbt.getList("pages", 10).stream().map(CompoundNBT.class::cast)
				.map(ResearchPage::fromNBT).collect(Collectors.toList());
		tree.name = nbt.getString("name");
		tree.key = nbt.getString("key");
		tree.icon = new ResourceLocation(nbt.getString("icon"));
		return tree;
	}
	
	public ResourceLocation icon(){
		return icon;
	}
	
	public String name(){
		return name;
	}
	
	public String key(){
		return key;
	}
}