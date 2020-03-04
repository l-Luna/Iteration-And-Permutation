package leppa.iterationpermutation.research;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

public class HardcodedResearchPage extends ResearchPage{
	
	String id, name, icon;
	List<ResearchPage> parents;
	int x, y;
	ResearchTree tree;
	
	public static HardcodedResearchPage make(String id, String name, String icon, int x, int y, ResearchTree tree, String... parents){
		HardcodedResearchPage page = new HardcodedResearchPage(id, name, icon, x, y, tree);
		page.parents = Arrays.stream(parents)
				.map(Knowledges::pageForId)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		return page;
	}
	
	private HardcodedResearchPage(String id, String name, String icon, int x, int y, ResearchTree tree){
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.x = x;
		this.y = y;
		this.tree = tree;
	}
	
	public String id(){
		return id;
	}
	
	public String name(){
		return name;
	}
	
	public ResearchTree tree(){
		return tree;
	}
	
	public List<String> icons(){
		return !icon.contains(",") ? Lists.newArrayList(icon) : Arrays.asList(icon.split(","));
	}
	
	public List<String> meta(){
		return Collections.emptyList();
	}
	
	public List<ResearchPage> parents(){
		return Collections.unmodifiableList(parents);
	}
	
	public List<PageSection> sections(){
		return Collections.emptyList();
	}
	
	public int x(){
		return x;
	}
	
	public int y(){
		return y;
	}
}