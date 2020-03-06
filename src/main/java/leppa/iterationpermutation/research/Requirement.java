package leppa.iterationpermutation.research;

import leppa.iterationpermutation.PermutationMod;
import leppa.iterationpermutation.research.impl.ItemsRequirement;
import leppa.iterationpermutation.research.impl.XpRequirement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public abstract class Requirement{
	
	public abstract boolean satisfiedBy(PlayerEntity player);
	public abstract void take(PlayerEntity player);
	public abstract void renderIcon(int x, int y, int ticks, float partialTicks);
	public abstract List<String> getTooltip(PlayerEntity player);
	
	public String describe(){
		return amount == 1 ? id.getNamespace() + "::" + id.getPath() : amount + "*" + id.getNamespace() + "::" + id.getPath();
	}
	
	public static final Map<ResourceLocation, IntFunction<Requirement>> requirements = new HashMap<>();
	
	protected int amount;
	protected ResourceLocation id;
	
	public Requirement setAmount(int amount){
		this.amount = amount;
		return this;
	}
	
	public Requirement setId(ResourceLocation id){
		this.id = id;
		return this;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public static <T extends Requirement> void addRequirementType(ResourceLocation rl, Supplier<T> constructor){
		requirements.put(rl, x -> constructor.get().setAmount(x).setId(rl));
	}
	
	public static void init(){
		addRequirementType(new ResourceLocation(PermutationMod.MODID, "xp"), XpRequirement::new);
	}
	
	public static Requirement fromDescription(String strRequirement){
		// check if custom
		int amount = 1;
		if(strRequirement.contains("*")){
			amount = Integer.parseInt(strRequirement.split("\\*")[0]);
			strRequirement = strRequirement.split("\\*")[1];
		}
		// its an item
		if(strRequirement.contains("::")){
			ResourceLocation res = new ResourceLocation(strRequirement.split("::")[0], strRequirement.split("::")[1]);
			return Requirement.requirements.get(res).apply(amount);
		}else
			return new ItemsRequirement(ForgeRegistries.ITEMS.getValue(new ResourceLocation(strRequirement)), amount);
	}
}