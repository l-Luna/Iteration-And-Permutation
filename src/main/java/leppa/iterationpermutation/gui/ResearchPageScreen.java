package leppa.iterationpermutation.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import leppa.iterationpermutation.PermutationMod;
import leppa.iterationpermutation.PermutationNetwork;
import leppa.iterationpermutation.network.PktTryAdvanceResearch;
import leppa.iterationpermutation.research.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.List;

public class ResearchPageScreen extends Screen{
	
	private static final ResourceLocation bg = new ResourceLocation(PermutationMod.MODID, "textures/gui/double_book.png");
	
	static int requirementsPerRow = 7;
	
	ResearchPage page;
	PlayerEntity player;
	Researcher researcher;
	
	GChangePageButton left, right;
	CompleteResearchButton complete;
	
	int index = 0;
	
	protected ResearchPageScreen(ResearchPage page, PlayerEntity player){
		super(new StringTextComponent(""));
		this.page = page;
		this.player = player;
		researcher = Researcher.getFrom(player);
	}
	
	public static void onResearchAdvanced(ResearchAdvancedEvent event){
		if(Minecraft.getInstance().currentScreen instanceof ResearchPageScreen)
			((ResearchPageScreen)Minecraft.getInstance().currentScreen).refreshButtonVisibility();
	}
	
	protected void init(){
		int x = width / 2 - 12;
		final int y = 200;
		final int dist = 180;
		left  = addButton(new GChangePageButton(x - dist / 2, y, button -> left(), false));
		right = addButton(new GChangePageButton(x + dist / 2, y, button -> right(), true));
		
		final String text = "Continue";
		final int strWidth = getMinecraft().fontRenderer.getStringWidth(text);
		x = (width - strWidth - 10) / 2;
		complete = addButton(new CompleteResearchButton(x, 221, strWidth + 10, 14, text, button -> cont()));
		
		refreshButtonVisibility();
	}
	
	public void left(){
		if(index >= 2)
			index -= 2;
		
		refreshButtonVisibility();
	}
	
	public void right(){
		if(index < Math.ceil(getTotalVisibleLength() / 2f) - 1)
			index += 2;
		
		refreshButtonVisibility();
	}
	
	private void cont(){
		PermutationNetwork.INSTANCE.sendToServer(new PktTryAdvanceResearch(page.id()));
		refreshButtonVisibility();
	}
	
	private void refreshButtonVisibility(){
		left.visible = index > 0;
		right.visible = index < Math.ceil(getTotalVisibleLength() / 2f) - 1;
		complete.visible = researcher.stage(page) < page.sections().size() && page.sections().get(researcher.stage(page)).getRequirements().size() > 0;
	}
	
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		renderBg();
		
		PageSection left = getVisibleSectionAtIndex(index);
		if(left != null)
			left.render(false, sectionIndex(index), width, height);
		PageSection right = getVisibleSectionAtIndex(index + 1);
		if(right != null)
			right.render(true, sectionIndex(index + 1), width, height);
		
		// render current requirements
		if(researcher.stage(page) < page.sections().size() && page.sections().get(researcher.stage(page)).getRequirements().size() > 0){
			List<Requirement> requirements = page.sections().get(researcher.stage(page)).getRequirements();
			final int y = 200; // TODO: multiline after a few
			final int baseX = (width / 2) - (10 * requirements.size());
			for(int i = 0, size = requirements.size(); i < size; i++){
				Requirement requirement = requirements.get(i);
				requirement.renderIcon(baseX + i * 20, y, player.ticksExisted, partialTicks);
				renderAmount(baseX + i * 20, y, requirement.getAmount(), requirement.satisfiedBy(player));
			}
			// Show tooltips
			for(int i = 0, size = requirements.size(); i < size; i++)
				if(mouseX >= 20 * i + baseX && mouseX <= 20 * i + baseX + 16 && mouseY >= y && mouseY <= y + 16)
					GuiUtils.drawHoveringText(requirements.get(i).getTooltip(player), mouseX, mouseY, width, height, -1, getMinecraft().fontRenderer);
		}
		
		super.render(mouseX, mouseY, partialTicks);
	}
	
	private void renderAmount(int xPosition, int yPosition, int amount, boolean complete){
		String s = String.valueOf(amount);
		GlStateManager.disableLighting();
		GlStateManager.disableDepthTest();
		GlStateManager.disableBlend();
		getMinecraft().fontRenderer.drawStringWithShadow(s, (float)(xPosition + 19 - 2 - getMinecraft().fontRenderer.getStringWidth(s)), (float)(yPosition + 6 + 3), complete ? 0xaaffaa : 0xffaaaa);
		GlStateManager.enableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableDepthTest();
	}
	
	private PageSection getSectionAtIndex(int index){
		if(index == 0)
			return page.sections().get(0);
		int cur = 0;
		for(PageSection section : page.sections()){
			if(cur <= index && cur + section.span() > index)
				return section;
			cur += section.span();
		}
		return null;
	}
	
	private PageSection getVisibleSectionAtIndex(int index){
		if(visible(index))
			return getSectionAtIndex(index);
		else
			return null;
	}
	
	private int sectionIndex(int index){
		int cur = 0;
		for(PageSection section : page.sections()){
			if(cur <= index && cur + section.span() > index)
				return index - cur;
			cur += section.span();
		}
		return 0; // throw/show an error
	}
	
	private boolean visible(int index){
		return Researcher.getFrom(player).stage(page) >= index;
	}
	
	private int getIndexOf(PageSection section){
		return page.sections().indexOf(section);
	}
	
	private int getTotalLength(){
		return page.sections().stream()
				.mapToInt(PageSection::span)
				.sum();
	}
	
	private int getTotalVisibleLength(){
		return page.sections().stream()
				.filter(X -> visible(getIndexOf(X)))
				.mapToInt(PageSection::span)
				.sum();
	}
	
	private void renderBg(){
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		getMinecraft().getTextureManager().bindTexture(bg);
		blit((width - 256) / 2, (height - 256) / 2, 0, 0, 256, 182);
	}
	
	public boolean isPauseScreen(){
		return false;
	}
	
	static class GChangePageButton extends Button{
		
		private final boolean right;
		
		public GChangePageButton(int x, int y, IPressable onPress, boolean right){
			super(x, y, 23, 13, "", onPress);
			this.right = right;
		}
		
		public void renderButton(int p1, int p2, float p3){
			GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
			Minecraft.getInstance().getTextureManager().bindTexture(bg);
			
			int i = 0;
			int j = 192;
			
			if(this.isHovered())
				i += 23;
			if(!right)
				j += 13;
			this.blit(this.x, this.y, i, j, 23, 13);
		}
	}
	
	class CompleteResearchButton extends GuiButtonExt{
		
		public CompleteResearchButton(int x, int y, int width, int height, String text, IPressable onPress){
			super(x, y, width, height, text, onPress);
		}
		
		public void render(int p_render_1_, int p_render_2_, float p_render_3_){
			active = page.sections().stream().flatMap(p -> p.getRequirements().stream()).allMatch(f -> f.satisfiedBy(player));
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			super.render(p_render_1_, p_render_2_, p_render_3_);
		}
	}
}