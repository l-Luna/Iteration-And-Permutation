package leppa.iterationpermutation.research;

import net.minecraftforge.eventbus.api.Event;

public class ResearchAdvancedEvent extends Event{
	
	private final Researcher researcher;
	private final ResearchPage page;
	
	public ResearchAdvancedEvent(Researcher researcher, ResearchPage page){
		this.researcher = researcher;
		this.page = page;
	}
	
	public Researcher getResearcher(){
		return researcher;
	}
	
	public ResearchPage getPage(){
		return page;
	}
}