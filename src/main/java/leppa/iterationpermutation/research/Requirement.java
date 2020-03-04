package leppa.iterationpermutation.research;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import leppa.iterationpermutation.PermutationMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
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
	
	int amount;
	ResourceLocation id;
	
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
			return new Requirement.ItemsRequirement(ForgeRegistries.ITEMS.getValue(new ResourceLocation(strRequirement)), amount);
	}
	
	public static class ItemsRequirement extends Requirement{
		
		ItemStack itemStack;
		
		public ItemsRequirement(Item item, int amount){
			itemStack = new ItemStack(item, amount);
			setAmount(amount);
		}
		
		public Requirement setAmount(int amount){
			itemStack.setCount(amount);
			return super.setAmount(amount);
		}
		
		public String describe(){
			return getAmount() == 1
					? ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString()
					: getAmount() + "*" + ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString();
		}
		
		public boolean satisfiedBy(PlayerEntity player){
			int found = 0;
			for(int j = 0; j < player.inventory.getSizeInventory(); ++j){
				ItemStack curStack = player.inventory.getStackInSlot(j);
				if(!curStack.isEmpty() && curStack.isItemEqual(itemStack))
					found += curStack.getCount();
				if(found >= getAmount())
					break;
			}
			return found >= getAmount();
		}
		
		public void take(PlayerEntity player){
			player.inventory.clearMatchingItems(x -> x.isItemEqual(itemStack), getAmount());
		}
		
		public void renderIcon(int x, int y, int ticks, float partialTicks){
			RenderHelper.disableStandardItemLighting();
			Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, x, y);
			RenderHelper.enableGUIStandardItemLighting();
		}
		
		public List<String> getTooltip(PlayerEntity player){
			if(getAmount() == 1){
				List<String> list = new ArrayList<>();
				for(ITextComponent component : itemStack.getTooltip(player, ITooltipFlag.TooltipFlags.NORMAL)){
					String string = component.getString();
					list.add(string);
				}
				return list;
			}else{
				List<String> list = new ArrayList<>();
				for(ITextComponent component : itemStack.getTooltip(player, ITooltipFlag.TooltipFlags.NORMAL)){
					String string = component.getString();
					list.add(string);
				}
				list.set(0, amount + "x " + list.get(0));
				return list;
			}
		}
	}
	
	public static class XpRequirement extends Requirement{
		
		public boolean satisfiedBy(PlayerEntity player){
			return player.experienceLevel >= getAmount();
		}
		
		public void take(PlayerEntity player){
			player.addExperienceLevel(-getAmount());
		}
		
		public void renderIcon(int x, int y, int ticks, float partialTicks){
			doXPRender(ticks, x, y, partialTicks);
		}
		
		public List<String> getTooltip(PlayerEntity player){
			// TODO: translate
			return Lists.newArrayList(amount == 1 ? "1 experience level" : getAmount() + " experience levels");
		}
	}
	
	private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
	
	/**
	 * Copied from {@link net.minecraft.client.renderer.entity.ExperienceOrbRenderer}.
	 */
	public static void doXPRender(int ticks, double x, double y, float partialTicks){
		final int u = 0, v = 16;
		float f8 = (ticks + partialTicks) / 2f;
		final float i1 = (MathHelper.sin(f8 + 0.0F) + 1.0F) * 0.5F;
		final float k1 = (MathHelper.sin(f8 + 4.1887903F) + 1.0F) * 0.1F;
		GlStateManager.pushMatrix();
		GlStateManager.color4f(i1, 1.0F, k1, 1.0F);
		Minecraft.getInstance().textureManager.bindTexture(EXPERIENCE_ORB_TEXTURES);
		AbstractGui.blit((int)x, (int)y, 16, 16, u, v, 16, 16, 64, 64);
		GlStateManager.disableBlend();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}
}