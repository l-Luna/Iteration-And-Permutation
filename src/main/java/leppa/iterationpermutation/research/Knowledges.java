package leppa.iterationpermutation.research;

import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.stream.Stream;

public final class Knowledges{
	
	// for add-on support, make a list of knowledges, allow registry for adding to it, and return in knowledges().
	
	// private static List<Knowledge> knowledges = new ArrayList<>();
	private static Map<ResourceLocation, Knowledge> knowledges = new LinkedHashMap<>();
	
	public static List<Knowledge> getKnowledges(){
		return new ArrayList<>(knowledges.values());
	}
	
	public static void addKnowledge(ResourceLocation id, Knowledge knowledge){
		knowledges.put(id, knowledge);
	}
	
	public static Knowledge getKnowledge(ResourceLocation key){
		return knowledges.get(key);
	}
	
	public static Knowledge getKnowledge(String key){
		return getKnowledge(new ResourceLocation(key));
	}
	
	public static Optional<ResearchPage> pageForId(String id){
		for(Knowledge tree : getKnowledges()){
			Optional<ResearchPage> opr = tree.forId(id);
			if(opr.isPresent())
				return opr;
		}
		return Optional.empty();
	}
	
	public static Optional<ResearchTree> treeForId(String id){
		for(Knowledge tree : getKnowledges()){
			Optional<ResearchTree> opr = tree.treeForId(id);
			if(opr.isPresent())
				return opr;
		}
		return Optional.empty();
	}
	
	public static Stream<ResearchPage> allPages(){
		return getKnowledges().stream()
				.flatMap(x -> x.getTrees().stream())
				.flatMap(x -> x.getPages().stream());
	}
	
	public static void clear(){
		knowledges.clear();
	}
}