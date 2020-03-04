package leppa.iterationpermutation.research;

import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Knowledge{
	
	/*
	 A knowledge contains
	 research trees, which contain
	 nodes, which reference each-other and can unlock nodes/pages, trees, recipes, & items (etc).
	*/
	
	List<ResearchTree> trees;
	String key;
	// String name;
	
	public List<ResearchTree> getTrees(){
		return trees;
	}
	
	public String getKey(){
		return key;
	}
	
	public Knowledge(List<ResearchTree> trees, String key){
		this.trees = trees;
		this.key = key;
	}
	
	protected Knowledge(){
		this(new ArrayList<>(), "");
	}
	
	public Optional<ResearchTree> treeForId(String id){
		for(ResearchTree tree : getTrees()){
			if(tree.key.equals(id))
				return Optional.of(tree);
		}
		return Optional.empty();
	}
	
	// cache at some point
	public Optional<ResearchPage> forId(String id){
		for(ResearchTree tree : getTrees()){
			Optional<ResearchPage> opr = tree.forId(id);
			if(opr.isPresent())
				return opr;
		}
		return Optional.empty();
	}
	
	public CompoundNBT getData(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("key", key);
		nbt.put("trees", ResearchPage.nbtListOfNbt(trees.stream().map(ResearchTree::getData).collect(Collectors.toList())));
		return nbt;
	}
	
	public static Knowledge fromData(CompoundNBT nbt){
		return new Knowledge(
				nbt.getList("trees", 10).stream().map(CompoundNBT.class::cast)
						.map(ResearchTree::fromNBT).collect(Collectors.toList()),
				nbt.getString("key")
		);
	}
}