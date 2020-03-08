package leppa.iterationpermutation.research.impl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import leppa.iterationpermutation.PermutationMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClientVanillaCrafting{
	
	static private final Map<ResourceLocation, IRecipe<?>> recipeCache = new HashMap<>();
	static private final int slotColor = 0x80ffffff; // white tint
	public static ResourceLocation CRAFTING_OVERLAY = new ResourceLocation(PermutationMod.MODID, "textures/gui/vanilla_crafting_overlay.png");
	public static ResourceLocation SMELTING_OVERLAY = new ResourceLocation(PermutationMod.MODID, "textures/gui/vanilla_smelting_overlay.png");
	
	public static void renderRecipe(int x, int y, String recipe){
		IRecipe<?> iRecipe = getRecipe(new ResourceLocation(recipe));
		if(iRecipe instanceof ICraftingRecipe)
			renderCraftingRecipe(x, y, (ICraftingRecipe)iRecipe);
		else if(iRecipe instanceof AbstractCookingRecipe)
			renderCookingRecipe(x, y, (AbstractCookingRecipe)iRecipe);
	}
	
	public static void renderRecipeTooltips(int x, int y, String recipe, int mouseX, int mouseY, int screenWidth, int screenHeight){
		IRecipe<?> iRecipe = getRecipe(new ResourceLocation(recipe));
		if(iRecipe instanceof ICraftingRecipe)
			renderCraftingRecipeTooltips(x, y, mouseX, mouseY, screenWidth, screenHeight, (ICraftingRecipe)iRecipe);
		else if(iRecipe instanceof AbstractCookingRecipe)
			renderCookingRecipeTooltips(x, y, mouseX, mouseY, screenWidth, screenHeight, (AbstractCookingRecipe)iRecipe);
	}
	
	public static void renderCraftingRecipe(int x, int y, ICraftingRecipe recipe){
		// background
		Minecraft.getInstance().getTextureManager().bindTexture(CRAFTING_OVERLAY);
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
		AbstractGui.blit(x, y, 0, 0, 127, 180, 127, 180);
		// (x, y) is top-left of whole thing
		// first item is (40, 21)
		// each item is +18 x/y
		for(int xx = 0; xx < 3; xx++)
			for(int yy = 0; yy < 3; yy++){
				int index = xx + yy * 3;
				if(index < recipe.getIngredients().size()){
					int itemX = x + 40 + xx * 18;
					int itemY = y + 21 + yy * 18;
					ItemStack[] stacks = recipe.getIngredients().get(index).getMatchingStacks();
					if(stacks.length > 0){
						RenderHelper.enableStandardItemLighting();
						mc().getItemRenderer().renderItemAndEffectIntoGUI(stacks[dispIndex(stacks.length)], itemX, itemY);
					}
				}
			}
		// 57, 137 is the result's location
		RenderHelper.enableStandardItemLighting();
		mc().getItemRenderer().renderItemAndEffectIntoGUI(recipe.getRecipeOutput(), x + 57, y + 137);
	}
	
	public static void renderCraftingRecipeTooltips(int x, int y, int mouseX, int mouseY, int screenWidth, int screenHeight, ICraftingRecipe recipe){
		RenderHelper.disableStandardItemLighting(); //TODO: check what lighting changes are actually necessary or not, this is ridiculous
		// Tooltips & hover-glows:
		// find hovered item
		// if mouse is between (40, 21) and (91, 72) (the lower-right corner) then it may be over an item
		if(mouseX >= x + 40 && mouseX <= 91 + x     &&     mouseY >= y + 21 && mouseY <= y + 72){
			// get the item
			int xx = ((mouseX - x - 40) / 18);
			int yy = ((mouseY - y - 21) / 18);
			int index = ((mouseX - x - 40) / 18) + 3 * ((mouseY - y - 21) / 18);
			if(recipe.getIngredients().size() > index){
				ItemStack[] stacks = recipe.getIngredients().get(index).getMatchingStacks();
				if(stacks.length > 0){
					// display hover
					GuiUtils.drawGradientRect(100, x + 40 + xx * 18, y + 21 + yy * 18, x + 40 + xx * 18 + 16, y + 21 + yy * 18 + 16, slotColor, slotColor);
					// display tooltip
					GuiUtils.drawHoveringText(
							getTooltipFromItem(stacks[dispIndex(stacks.length)]),
							mouseX, mouseY, screenWidth, screenHeight, -1, mc().fontRenderer
					);
				}
			}
		}
		// if mouse is between (57, 137) and (72, 152), its over the result
		else if(mouseX >= x + 57 && mouseX <= 72 + x     &&     mouseY >= y + 137 && mouseY <= y + 152){
			// display hover
			GuiUtils.drawGradientRect(100, x + 57, y + 137, x + 57 + 16, y + 137 + 16, slotColor, slotColor);
			// display tooltip
			GuiUtils.drawHoveringText(getTooltipFromItem(recipe.getRecipeOutput()), mouseX, mouseY, screenWidth, screenHeight, -1, mc().fontRenderer);
		}
		// if mouse is over (57, 91) and (73, 114), its over the arrow
		else if(mouseX >= x + 57 && mouseX <= 74 + x     &&     mouseY >= y + 91 && mouseY <= y + 114)
			GuiUtils.drawHoveringText(Lists.newArrayList(I18n.format(recipe.getIcon().getTranslationKey())), mouseX, mouseY, screenWidth, screenHeight, -1, mc().fontRenderer);
		
		RenderHelper.enableStandardItemLighting();
	}
	
	public static void renderCookingRecipe(int x, int y, AbstractCookingRecipe recipe){
		// background
		Minecraft.getInstance().getTextureManager().bindTexture(SMELTING_OVERLAY);
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
		AbstractGui.blit(x, y, 0, 0, 127, 180, 127, 180);
		// there is definitely one input and one output
		ItemStack[] stacks = recipe.getIngredients().get(0).getMatchingStacks();
		RenderHelper.enableStandardItemLighting();
		mc().getItemRenderer().renderItemAndEffectIntoGUI(stacks[dispIndex(stacks.length)], x + 57, y + 34); // (57, 34)
		mc().getItemRenderer().renderItemAndEffectIntoGUI(recipe.getRecipeOutput(), x + 57, y + 130); // (57, 130)
	}
	
	public static void renderCookingRecipeTooltips(int x, int y, int mouseX, int mouseY, int screenWidth, int screenHeight, AbstractCookingRecipe recipe){
		RenderHelper.disableStandardItemLighting();
		// Tooltips & hover-glows:
		ItemStack[] stacks = recipe.getIngredients().get(0).getMatchingStacks();
		// if mouse is over (57, 34) and (72, 49), its over the ingredient
		if(mouseX >= x + 57 && mouseX <= 72 + x     &&     mouseY >= y + 34 && mouseY <= y + 49){
			// display hover
			GuiUtils.drawGradientRect(100, x + 57, y + 34, x + 57 + 16, y + 34 + 16, slotColor, slotColor);
			// display tooltip
			GuiUtils.drawHoveringText(getTooltipFromItem(stacks[dispIndex(stacks.length)]), mouseX, mouseY, screenWidth, screenHeight, -1, mc().fontRenderer);
		}
		// if mouse is over (57, 130) and (72, 145), its over the result
		else if(mouseX >= x + 57 && mouseX <= 72 + x     &&     mouseY >= y + 130 && mouseY <= y + 145){
			// display hover
			GuiUtils.drawGradientRect(100, x + 57, y + 130, x + 57 + 16, y + 130 + 16, slotColor, slotColor);
			// display tooltip
			GuiUtils.drawHoveringText(getTooltipFromItem(recipe.getRecipeOutput()), mouseX, mouseY, screenWidth, screenHeight, -1, mc().fontRenderer);
		}
		// if mouse is over (47, 78) and (82, 101), its over the arrow
		else if(mouseX >= x + 47 && mouseX <= 82 + x     &&     mouseY >= y + 78 && mouseY <= y + 101)
			GuiUtils.drawHoveringText(Lists.newArrayList(I18n.format(recipe.getIcon().getTranslationKey())), mouseX, mouseY, screenWidth, screenHeight, -1, mc().fontRenderer);
		RenderHelper.enableStandardItemLighting();
	}
	
	private static IRecipe<?> getRecipe(ResourceLocation recipe){
		return recipeCache.computeIfAbsent(recipe, x -> mc().player.connection.getRecipeManager().getRecipe(x).orElse(null));
	}
	
	private static int dispIndex(int max){
		return (mc().player.ticksExisted / 30) % max;
	}
	
	private static Minecraft mc(){
		return Minecraft.getInstance();
	}
	
	/**
	 * From {@link net.minecraft.client.gui.screen.Screen#getTooltipFromItem(ItemStack)}
	 */
	private static List<String> getTooltipFromItem(ItemStack item){
		List<ITextComponent> list = item.getTooltip(mc().player, mc().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		List<String> strings = Lists.newArrayList();
		
		for(ITextComponent itextcomponent : list)
			strings.add(itextcomponent.getFormattedText());
		
		return strings;
	}
}