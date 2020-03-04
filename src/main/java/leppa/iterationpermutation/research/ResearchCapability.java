package leppa.iterationpermutation.research;

import leppa.iterationpermutation.PermutationMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ResearchCapability{
	
	@CapabilityInject(Researcher.class)
	static Capability<Researcher> PROVIDER_CAPABILITY = null;
	
	public static void init(){
		CapabilityManager.INSTANCE.register(Researcher.class, new ResearchStorage(), DefaultResearcher::new);
	}
	
	static class ResearchStorage implements Capability.IStorage<Researcher>{
		
		@Nullable
		public INBT writeNBT(Capability<Researcher> capability, Researcher instance, Direction side){
			return instance.serialize();
		}
		
		public void readNBT(Capability<Researcher> capability, Researcher instance, Direction side, INBT nbt){
			if(nbt instanceof CompoundNBT)
				instance.deserialize((CompoundNBT)nbt);
		}
	}
	
	public static class Provider implements ICapabilitySerializable<CompoundNBT>{
		
		public static final ResourceLocation NAME = new ResourceLocation(PermutationMod.MODID, "researcher");
		private final Researcher cap = new DefaultResearcher();
		
		public CompoundNBT serializeNBT(){
			return cap.serialize();
		}
		
		public void deserializeNBT(CompoundNBT nbt){
			cap.deserialize(nbt);
		}
		
		@Nonnull
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side){
			return PROVIDER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> cap));
		}
	}
	
	private static class DefaultResearcher implements Researcher{
		
		Map<String, Integer> pageStages = new HashMap<>();
		
		public int stage(ResearchPage page){
			return pageStages.getOrDefault(page.id(), 0);
		}
		
		public void advance(ResearchPage page){
			if(stage(page) < page.sections().size()){
				do
					pageStages.put(page.id(), Math.min(stage(page) + 1, page.sections().size()));
				while(pageStages.get(page.id()) < page.sections().size() && // not at the end yet
						page.sections().get(pageStages.get(page.id())).getRequirements().size() == 0); // and skipping through sections with no requirements
				MinecraftForge.EVENT_BUS.post(new ResearchAdvancedEvent(this, page));
			}
		}
		
		public void reset(ResearchPage page){
			pageStages.put(page.id(), 0);
		}
		
		public void complete(ResearchPage page){
			pageStages.put(page.id(), page.sections().size());
		}
		
		public Map<String, Integer> copyData(){
			return new HashMap<>(pageStages);
		}
		
		public void setData(Researcher other){
			pageStages = other.copyData();
		}
		
		public CompoundNBT serialize(){
			CompoundNBT nbt = new CompoundNBT();
			pageStages.forEach(nbt::putInt);
			return nbt;
		}
		
		public void deserialize(CompoundNBT nbt){
			pageStages = new HashMap<>();
			for(String s : nbt.keySet())
				pageStages.put(s, nbt.getInt(s));
		}
	}
}