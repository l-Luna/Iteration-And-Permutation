package leppa.iterationpermutation.research.client;

import leppa.iterationpermutation.research.PageSection;
import leppa.iterationpermutation.research.ResearchPage;
import leppa.iterationpermutation.research.ResearchTree;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientResearchPage extends ResearchPage{
	
	String id, name, desc;
	List<String> icons;
	List<String> meta;
	List<PageSection> sections;
	int x, y;
	
	boolean resolved = false;
	String unresTreeId;
	List<String> unresParentsId;
	
	ResearchTree tree;
	List<ResearchPage> parents;
	
	public ClientResearchPage(String id, String name, String desc, List<String> icons, List<String> meta, List<PageSection> sections, int x, int y, String treeId, List<String> parentsId){
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.icons = icons;
		this.meta = meta;
		this.sections = sections;
		this.x = x;
		this.y = y;
		this.unresTreeId = treeId;
		this.unresParentsId = parentsId;
	}
	
	public void resolve(){
		parents = unresParentsId.stream()
				.map(ClientKnowledges::pageForId)
				.filter(Optional::isPresent)
				.map(Optional::get).collect(Collectors.toList());
		tree = ClientKnowledges.treeForId(unresTreeId).orElse(null);
		resolved = true;
	}
	
	public String id(){
		return id;
	}
	
	public String name(){
		return name;
	}
	
	public ResearchTree tree(){
		return resolved ? tree : null;
	}
	
	public List<String> icons(){
		return icons;
	}
	
	public List<String> meta(){
		return meta;
	}
	
	public List<ResearchPage> parents(){
		return resolved ? parents : null;
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
	
	public String desc(){
		return desc;
	}
}