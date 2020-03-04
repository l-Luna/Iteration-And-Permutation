package leppa.iterationpermutation.proxy;

import leppa.iterationpermutation.research.Knowledge;
import leppa.iterationpermutation.research.Knowledges;

public class ServerProxy implements Proxy{
	
	public Knowledge getKnowledge(String id){
		return Knowledges.getKnowledge(id);
	}
}