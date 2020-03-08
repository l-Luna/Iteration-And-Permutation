package leppa.iterationpermutation.items;

import leppa.iterationpermutation.PermutationMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ResearchBook extends Item{
	
	String knowledge;
	
	public ResearchBook(Properties properties, String knowledge){
		super(properties);
		this.knowledge = knowledge;
	}
	
	@SuppressWarnings("NullableProblems")
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand){
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> PermutationMod.proxy.openResearchScreen(knowledge, player));
		return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}
	
	public int getItemStackLimit(ItemStack stack){
		return 1;
	}
}