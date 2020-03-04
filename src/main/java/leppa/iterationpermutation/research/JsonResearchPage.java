package leppa.iterationpermutation.research;

import java.util.List;

public class JsonResearchPage extends ResearchPage{
	
	String id, name;
	List<String> icons, meta;
	List<ResearchPage> parents;
	List<PageSection> sections;
	int x, y;
	ResearchTree tree;
	
	public JsonResearchPage(String id, String name, List<String> icons, List<String> meta, List<ResearchPage> parents, List<PageSection> sections, int x, int y, ResearchTree tree){
		this.id = id;
		this.name = name;
		this.icons = icons;
		this.meta = meta;
		this.parents = parents;
		this.sections = sections;
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
		return icons;
	}
	
	public List<String> meta(){
		return meta;
	}
	
	public List<ResearchPage> parents(){
		return parents;
	}
	
	public List<PageSection> sections(){
		return sections;
	}
	
	public int x(){
		return x;
	}
	
	public int y(){
		return y;
	}
}
