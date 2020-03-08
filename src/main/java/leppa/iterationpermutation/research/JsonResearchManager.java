package leppa.iterationpermutation.research;

import com.google.gson.*;
import leppa.iterationpermutation.PermutationNetwork;
import leppa.iterationpermutation.commands.ResearchCommand;
import leppa.iterationpermutation.network.PktSyncResearch;
import leppa.iterationpermutation.research.impl.EmptySection;
import leppa.iterationpermutation.research.impl.ImageSection;
import leppa.iterationpermutation.research.impl.StringSection;
import leppa.iterationpermutation.research.impl.VanillaCraftingSection;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.minecraft.util.JSONUtils.getInt;
import static net.minecraft.util.JSONUtils.getString;

public final class JsonResearchManager extends JsonReloadListener{
	
	private static final Gson GSON = new GsonBuilder().create();
	
	public JsonResearchManager(){
		super(GSON, "research");
	}
	
	protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn){
		splashList.forEach(JsonResearchManager::applyJson);
		PermutationNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new PktSyncResearch(Knowledges.getKnowledges()));
		ResearchCommand.resetSuggestionProvider();
	}
	
	public static void applyJson(ResourceLocation rl, JsonObject js){
		if(js.has("knowledges")){
			JsonArray knowledges = getJsonArray(js, "knowledges");
			knowledges.forEach(item -> {
				if(!item.isJsonObject())
					System.out.println("Non-Json-object in knowledges array, in " + rl + ".");
				else{
					JsonObject object = item.getAsJsonObject();
					Knowledge knew = new Knowledge();
					knew.key = getString(object, "key");
					// etc
					Knowledges.addKnowledge(new ResourceLocation(knew.key), knew);
				}
			});
		}
		if(js.has("categories")){
			// repeat above - use Knowledge's key
			JsonArray categories = getJsonArray(js, "categories");
			categories.forEach(item -> {
				if(!item.isJsonObject())
					System.err.println("Non-Json-object in categories array, in " + rl + ".");
				else{
					JsonObject object = item.getAsJsonObject();
					ResearchTree tree = new ResearchTree();
					
					tree.key = getString(object, "key");
					Knowledges.getKnowledge(getString(object, "in")).getTrees().add(tree);
					
					// TODO: placeholders and defaults
					if(object.has("icon"))
						tree.icon = new ResourceLocation(getString(object, "icon"));
					if(object.has("background"))
						tree.background = new ResourceLocation(getString(object, "background"));
					if(object.has("name"))
						tree.name = getString(object, "name");
				}
			});
		}
		if(js.has("entries")){
			// you got this already
			// TODO: this is not very nice
			JsonArray categories = getJsonArray(js, "entries");
			categories.forEach(item -> {
				if(!item.isJsonObject())
					System.err.println("Non-Json-object in entries array, in " + rl + ".");
				else{
					JsonObject object = item.getAsJsonObject();
					ResearchPage page = new JsonResearchPage(
							getString(object, "key"),
							getString(object, "name"),
							stream(getJsonArray(object, "icons"))
									.map(JsonElement::getAsString).collect(Collectors.toList()),
							stream(getJsonArray(object, "meta"))
									.map(JsonElement::getAsString).collect(Collectors.toList()),
							stream(getJsonArray(object, "parents")).map(JsonElement::getAsString)
									.map(Knowledges::pageForId)
									.filter(Optional::isPresent)
									.map(Optional::get).collect(Collectors.toList()),
							Stream.concat(
										isHidden(object) ? Stream.of(new EmptySection()) : Stream.of(),
										stream(getJsonArray(object, "sections")).map(JsonResearchManager::fromJson))
									.collect(Collectors.toList()),
							getInt(object, "x"),
							getInt(object, "y"),
							Knowledges.treeForId(getString(object, "category")).orElse(null)
					);
					page.tree().getPages().add(page);
				}
			});
		}
	}
	
	static boolean isHidden(JsonObject object){
		return stream(getJsonArray(object, "parents")).map(JsonElement::getAsString).anyMatch("hidden"::equals);
	}
	
	static JsonArray getJsonArray(JsonObject object, String id){
		if(object.has(id))
			return JSONUtils.getJsonArray(object, id);
		else
			return new JsonArray();
	}
	
	public static PageSection fromJson(JsonElement item){
		if(!item.isJsonObject())
			System.err.println("Non-Json-object in sections array!");
		else{
			JsonObject object = item.getAsJsonObject();
			PageSection section = new StringSection("");
			
			String type = getString(object, "type");
			if("string".equals(type))
				section = new StringSection(getString(object, "content"));
			else if("empty".equals(type))
				section = new EmptySection();
			else if("image".equals(type))
				section = new ImageSection(new ResourceLocation(getString(object, "content")));
			else if("crafting".equals(type))
				section = new VanillaCraftingSection(getString(object, "content"));
			
			// TODO: switch for other section types; perhaps allow registry of custom?
			
			if(object.has("requirements"))
				for(JsonElement requirement : getJsonArray(object, "requirements"))
					section.addRequirement(Requirement.fromDescription(requirement.getAsString()));
			
			return section;
		}
		return null;
	}
	
	private static <T> Stream<T> stream(Iterable<T> iterable){
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	/*
	in TC:
	root -> {}[] entries ->
	  String key
	  String name
	  Items (Strings) for icons
	  String category (here Tree)
	  String[] parents
	  String[] meta
	  int[] location
	  rewards ???
	  sections........
	
	k so I want it simpler
	in Ac:
	root -> {}[] entries ->
	  String id
	  String name
	  String category
	  String[] icons (items)
	  (opt) String[] parents
	  (opt) String[] meta
	  int[] location
	  {}[] sections ->
	    either:
	      String text
	      (opt) String[] requirements (items, exp, tasks)
	    or:
	      String recipe (somehow reference recipes?)
	      
	  that's not simpler (?), but it carries over all the info possible
	*/
}
