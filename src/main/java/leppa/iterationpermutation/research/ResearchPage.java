package leppa.iterationpermutation.research;

import leppa.iterationpermutation.research.client.ClientResearchPage;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ResearchPage{
	
	private List<Item> cached;
	
	public abstract String id();
	public abstract String name();
	public abstract ResearchTree tree();
	public abstract List<String> icons();
	public abstract List<String> meta();
	public abstract List<ResearchPage> parents();
	public abstract List<PageSection> sections();
	
	public abstract int y();
	public abstract int x();
	
	public String desc(){
		return null;
	}
	
	public List<Item> iconsAsItems(){
		return cached == null ? cached = icons().stream()
				.map(ResourceLocation::new)
				.map(ForgeRegistries.ITEMS::getValue)
				.collect(Collectors.toList()) : cached;
	}
	
	public CompoundNBT getData(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("id", id());
		nbt.putString("name", name());
		nbt.putString("tree_key", tree().key);
		nbt.put("icons", nbtList(icons()));
		nbt.put("meta", nbtList(meta()));
		nbt.put("parents",  nbtList     (parents() .stream().map(ResearchPage::id)        .collect(Collectors.toList())));
		nbt.put("sections", nbtListOfNbt(sections().stream().map(PageSection::getPassData).collect(Collectors.toList())));
		nbt.putInt("x", x());
		nbt.putInt("y", y());
		return nbt;
	}
	
	public static ClientResearchPage fromNBT(CompoundNBT nbt){
		ClientResearchPage page = new ClientResearchPage(nbt.getString("id"),
														 nbt.getString("name"),
														 nbt.getList("icons", 8).stream().map(StringNBT.class::cast).map(StringNBT::getString).collect(Collectors.toList()),
														 nbt.getList("meta", 8).stream().map(StringNBT.class::cast).map(StringNBT::getString).collect(Collectors.toList()),
														 nbt.getList("sections", 10).stream().map(CompoundNBT.class::cast).map(PageSection::fromNBT).collect(Collectors.toList()),
														 nbt.getInt("x"),
														 nbt.getInt("y"),
														 nbt.getString("tree_key"),
														 nbt.getList("parents", 8).stream().map(StringNBT.class::cast).map(StringNBT::getString).collect(Collectors.toList()));
		return page;
	}
	
	public static INBT nbtList(List<String> data){
		ListNBT nbt = new ListNBT();
		nbt.addAll(data.stream().map(StringNBT::new).collect(Collectors.toList()));
		return nbt;
	}
	
	public static INBT nbtListOfNbt(List<INBT> data){
		ListNBT nbt = new ListNBT();
		nbt.addAll(data);
		return nbt;
	}
	
	public boolean equals(Object other){
		return other instanceof ResearchPage && ((ResearchPage)other).id().equals(id());
	}
	
	public int hashCode(){
		return Objects.hash(id());
	}
}