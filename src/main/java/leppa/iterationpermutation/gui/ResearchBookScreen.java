package leppa.iterationpermutation.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import leppa.iterationpermutation.PermutationMod;
import leppa.iterationpermutation.PermutationNetwork;
import leppa.iterationpermutation.network.PktTryAdvanceResearch;
import leppa.iterationpermutation.research.Knowledge;
import leppa.iterationpermutation.research.ResearchPage;
import leppa.iterationpermutation.research.ResearchTree;
import leppa.iterationpermutation.research.Researcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

@OnlyIn(Dist.CLIENT)
public class ResearchBookScreen extends Screen{
	
	// TODO: make more generic
	private static final ResourceLocation nebula_bg = new ResourceLocation(PermutationMod.MODID, "textures/gui/nebula_bg.png");
	private static final ResourceLocation bases = new ResourceLocation(PermutationMod.MODID, "textures/gui/research_bases.png");
	private static final ResourceLocation arrows = new ResourceLocation(PermutationMod.MODID, "textures/gui/research_arrows.png");
	
	static final int borderZ = 20;
	
	Knowledge knowledge;
	PlayerEntity player;
	
	int ticks = 0;
	int curTab = 0;
	static float xPan, yPan;
	float zoom = 1f;
	
	public ResearchBookScreen(Knowledge knowledge, PlayerEntity player){
		super(new StringTextComponent(""));
		this.knowledge = knowledge;
		this.player = player;
	}
	
	public void tick(){
		super.tick();
		ticks++;
	}
	
	private float xOffset(){
		return xPan + (width / 2f);
	}
	
	private float yOffset(){
		return yPan + (height / 2f);
	}
	
	public void render(int mouseX, int mouseY, float mouseZQ){
		renderBackground();
		
		// background -> nebula_bg
		renderBg();
		// page sfx -> later
		// pages and arrows
		renderKnowledge();
		renderKnowledgeArrows();
		// tabs are just buttons
		// frame
		renderFrame();
		// tooltips
		renderKnowledgeTooltip(mouseX, mouseY);
		// tabs sfx??
		super.render(mouseX, mouseY, mouseZQ);
	}
	
	protected void init(){
		super.init();
		
		List<ResearchTree> trees = knowledge.getTrees();
		for(int i = 0, size = trees.size(); i < size; i++){
			ResearchTree tree = trees.get(i);
			int finalI = i; // lambdas, ehh
			// TODO: put tabs on right hand side after too many
			addButton(new TreeTab((width - 256) / 2 - 8, (height - 256) / 2 + 10 + i * 20, 16, 16, button -> setTab(finalI), tree.icon(), tree.name()));
		}
	}
	
	public void setTab(int curTab){
		if(curTab >= 0 && curTab < knowledge.getTrees().size())
			this.curTab = curTab;
	}
	
	private void renderBg(){
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		getMinecraft().getTextureManager().bindTexture(nebula_bg);
		blit((width - 256) / 2, (height - 256) / 2, blitOffset, -(xPan / 2f) % 512, -(yPan / 2f) % 512, 256, 256, 512, 512);
	}
	
	private void renderKnowledge(){
		// for every page, draw the base and its icon, then arrows leading from its parents
		// bases
		getMinecraft().getTextureManager().bindTexture(bases);
		
		for(ResearchPage researchPage : curPages()){
			if(inBounds(researchPage.x(), researchPage.y(), 0)){
				int base = getBase(researchPage);
				blit((int)((30 * researchPage.x() + xOffset()) * zoom),
					 (int)((30 * researchPage.y() + yOffset()) * zoom),
					 (base % 9) * 26,
					 (base / 9) * 26,
					 26,
					 26);
			}
		}
		
		// icons
		RenderHelper.disableStandardItemLighting();
		for(ResearchPage p : curPages())
			if(inBounds(p.x(), p.y(), 7) && pageStyle(p) != RenderStyle.None){
				/*if(pageStyle(p) == RenderStyle.Pending)
					GlStateManager.color4f(.5f, .5f, .5f, 1f);*/ //cant modify how items look like, its overridden for me :)
				itemRenderer.zLevel = -150 + borderZ / 2f;
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(p.iconsAsItems().get(ticks / 30 % p.icons().size())),
														(int)((30 * p.x() + xOffset() + 5) * zoom),
														(int)((30 * p.y() + yOffset() + 5) * zoom));
			}
		
		RenderHelper.enableGUIStandardItemLighting();
	}
	
	private void renderKnowledgeTooltip(int mouseX, int mouseY){
		// iterate through in-bound entries and check for one under mouse
		for(ResearchPage p : curPages()){
			RenderStyle style = pageStyle(p);
			if(style == RenderStyle.Complete || style == RenderStyle.In_Progress)
				if(inBounds(p.x(), p.y(), 6) && mouseUnderEntry(p.x(), p.y(), mouseX, mouseY)){
					GuiUtils.drawHoveringText(p.desc() == null ? Collections.singletonList(p.name()) : Arrays.asList(p.name(), p.desc()), mouseX, mouseY, width, height, -1, getMinecraft().fontRenderer);
					break;
				}
		}
	}
	
	private void renderKnowledgeArrows(){
		// for every page, render arrows from its parents
		getMinecraft().getTextureManager().bindTexture(arrows);
		
		for(ResearchPage p : curPages()){
			if(pageStyle(p) != RenderStyle.None)
				for(ResearchPage parent : p.parents())
					if(parent.tree().equals(p.tree())){
						int xdiff = Math.abs(p.x() - parent.x());
						int ydiff = Math.abs(p.y() - parent.y());
						
						if(xdiff == 0)
							renderVerticalLine(p.x(), p.y(), parent.y());
						else if(ydiff == 0)
							renderHorizontalLine(p.y(), p.x(), parent.x());
						else if(xdiff == 1 && ydiff == 1)
							renderSmallCurve(p.x(), p.y(), parent.x(), parent.y());
						else // horizontal line until curve
							if(ydiff == 1){
								renderHorizontalLine(p.y(), p.x(), parent.x());
								if(p.x() > parent.x())
									if(p.y() > parent.y())
										drawUrCurve(parent.x(), p.y());
									else
										drawBrCurve(parent.x(), p.y());
								else if(p.y() > parent.y())
									drawUlCurve(parent.x(), p.y());
								else
									drawBlCurve(parent.x(), p.y());
							}else if(xdiff == 1){
								// vertical line until curve
								if(p.y() > parent.y()){
									renderVerticalLine(parent.x(), p.y(), parent.y());
									if(p.x() > parent.x())
										drawUrCurve(parent.x(), p.y()); // lower-right
									else
										drawUlCurve(parent.x(), p.y()); // lower-left
								}else{
									renderVerticalLine(parent.x(), p.y(), parent.y());
									if(p.x() > parent.x())
										drawBrCurve(parent.x(), p.y()); // upper-right
									else
										drawBlCurve(parent.x(), p.y()); // upper-left
								}
							}else if(xdiff == 2 && ydiff == 2){
								// large curve
								if(p.x() > parent.x())
									// BR curve above parent
									if(p.y() > parent.y())
										// UR curve below parent
										drawLurCurve(parent.x(), parent.y() + 1);
									else
										// BR curve above parent
										drawLbrCurve(parent.x(), p.y());
								else if(p.y() > parent.y())
									// UL curve below parent
									drawLulCurve(parent.x() - 1, parent.y() + 1);
								else
									// BL curve above parent
									drawLblCurve(p.x() + 1, p.y());
							}else if(xdiff == 2){
								// vertical line until large curve
								renderVerticalLine(parent.x(), p.y(), parent.y());
								if(p.x() > parent.x())
									// BR curve above parent
									if(p.y() > parent.y())
										// UR curve below parent
										drawLurCurve(parent.x(), parent.y() + 1);
									else
										// BR curve above parent
										drawLbrCurve(parent.x(), p.y());
								else if(p.y() > parent.y())
									// UL curve below parent
									drawLulCurve(parent.x() - 1, parent.y() + 1);
								else
									// BL curve above parent
									drawLblCurve(p.x() + 1, p.y());
							}else if(ydiff == 2){
								// horizontal line until large curve TODO: large curves
							}else{
								// horizontal line until large curve, then vertical line TODO: large curves
							}
					}
		}
	}
	
	private void renderSmallCurve(int pX, int pY, int parentX, int parentY){
		if(pX - parentX == 1 && pY - parentY == 1) // p is lower-right to parent
			drawUrCurve(pX - 1, pY);
		else if(pX - parentX == -1 && pY - parentY == -1) // p is upper-left to parent
			drawBlCurve(pX + 1, pY);
		else if(pX - parentX == 1 && pY - parentY == -1) // p is upper-right to parent
			drawUlCurve(parentX + 1, parentY);
		else if(pX - parentX == -1 && pY - parentY == 1) // p is lower-left to parent
			drawBrCurve(parentX - 1, parentY);
	}
	
	private void renderVerticalLine(int x, int pY, int parentY){
		if(pY > parentY)
			for(int i = parentY + 1; i < pY; i++){
				if(inBounds(x, i, 0))
					blit((int)(x * 30 - 5 + xOffset()), (int)(i * 30 - 5 + yOffset()), 0, 0, 35, 35);
			}
		else
			for(int i = pY + 1; i < parentY; i++)
				if(inBounds(x, i, 0))
					blit((int)(x * 30 - 5 + xOffset()), (int)(i * 30 - 5 + yOffset()), 0, 0, 35, 35);
	}
	
	private void renderHorizontalLine(int y, int pX, int parentX){
		if(pX > parentX)
			for(int i = parentX + 1; i < pX; i++){
				if(inBounds(i, y, 0))
					blit((int)(i * 30 - 5 + xOffset()), (int)(y * 30 - 5 + yOffset()), 0, 35, 35, 35);
			}
		else
			for(int i = pX + 1; i < parentX; i++)
				if(inBounds(i, y, 0))
					blit((int)(i * 30 - 5 + xOffset()), (int)(y * 30 - 5 + yOffset()), 0, 35, 35, 35);
	}
	
	private void drawUlCurve(int gX, int gY){
		if(inBounds(gX, gY, 0))
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 35, 70, 35, 35);
	}
	
	private void drawUrCurve(int gX, int gY){
		if(inBounds(gX, gY, 0))
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 35, 105, 35, 35);
	}
	
	private void drawBlCurve(int gX, int gY){
		if(inBounds(gX, gY, 0))
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 35, 35, 35, 35);
	}
	
	private void drawBrCurve(int gX, int gY){
		if(inBounds(gX, gY, 0))
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 35, 0, 35, 35);
	}
	
	private void drawLbrCurve(int gX, int gY){
		if(inBounds(gX, gY, 0)) // TODO: split into parts and bounds check for each bc I don't want to stencil
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 70, 0, 65, 65);
	}
	
	private void drawLblCurve(int gX, int gY){
		if(inBounds(gX, gY, 0))
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 70, 70, 65, 65);
	}
	
	private void drawLulCurve(int gX, int gY){
		if(inBounds(gX, gY, 0))
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 70, 105, 65, 65);
	}
	
	private void drawLurCurve(int gX, int gY){
		if(inBounds(gX, gY, 0))
			blit((int)(gX * 30 - 5 + xOffset()), (int)(gY * 30 - 5 + yOffset()), 135, 0, 65, 65);
	}
	
	private void renderFrame(){
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		final int borderSize = 25;
		GuiUtils.drawContinuousTexturedBox(arrows, (width - 256) / 2 - borderSize / 2, (height - 256) / 2 - borderSize / 2, 256 - borderSize * 3, 256 - borderSize * 3, 256 + borderSize / 2, 256 + borderSize / 2, borderSize * 3, borderSize * 3, borderSize, blitOffset + borderZ);
	}
	
	private int getBase(ResearchPage page){
		int result = 0;
		List<String> meta = page.meta();
		if(!meta.contains("base_normal"))
			if(meta.contains("base_star"))
				result = 1;
			else if(meta.contains("base_thin"))
				result = 2;
			else if(meta.contains("base_spiky"))
				result = 3;
			else if(meta.contains("base_round"))
				result = 4;
		
		// 9 per row
		
		RenderStyle style = pageStyle(page);
		switch(style){
			case None:
				result += 9;
			case Pending:
				result += 9;
			case In_Progress:
				result += 9;
		}
		
		return result;
	}
	
	private RenderStyle pageStyle(ResearchPage page){
		// if all of that page's sections are unlocked, its complete.
		// if it has a complete parent, *or* it has *some* sections unlocked, its in progress.
		// if it has in progress parents, its pending.
		// else, it doesn't display.
		
		if(Researcher.getFrom(player).stage(page) >= page.sections().size())
			return RenderStyle.Complete;
		// TODO: addenda
		if(Researcher.getFrom(player).stage(page) > 0)
			return RenderStyle.In_Progress;
		if(page.parents().size() == 0){
			if(!page.meta().contains("hidden"))
				return RenderStyle.In_Progress;
		}else if(page.parents().stream().map(this::pageStyle).allMatch(RenderStyle.Complete::equals))
			return RenderStyle.In_Progress;
		if(page.parents().stream().map(this::pageStyle).anyMatch(RenderStyle.In_Progress::equals))
			return RenderStyle.Pending;
		return RenderStyle.None;
	}
	
	private List<ResearchPage> curPages(){
		return knowledge.getTrees().get(curTab).getPages();
	}
	
	private boolean mouseUnderEntry(int gridX, int gridY, float mouseX, float mouseY){
		return mouseX >= (gridX * 30 + xOffset()) && mouseX <= (gridX * 30 + xOffset() + 25) && mouseY >= (gridY * 30 + yOffset()) && mouseY <= (gridY * 30 + yOffset() + 25);
	}
	
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY){
		xPan += dragX;
		yPan += dragY;
		xPan = max(min(xPan, 512), -512);
		yPan = max(min(yPan, 512), -512);
		super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
		return true;
	}
	
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta){
		super.mouseScrolled(mouseX, mouseY, scrollDelta);
		return false;
	}
	
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		// find entry under mouse
		// if present, open ResearchPageScreen w/ it
		if(button == 0){
			for(ResearchPage p : curPages())
				if(inBounds(p.x(), p.y(), 0) && mouseUnderEntry(p.x(), p.y(), (float)mouseX, (float)mouseY)){
					RenderStyle style = pageStyle(p);
					if(style == RenderStyle.Complete || style == RenderStyle.In_Progress){
						getMinecraft().displayGuiScreen(new ResearchPageScreen(p, player));
						break;
					}
				}
		}else if(button == 2)
			for(ResearchPage p : curPages())
				if(inBounds(p.x(), p.y(), 0) && mouseUnderEntry(p.x(), p.y(), (float)mouseX, (float)mouseY)){
					// The server checks if the page is visible
					PermutationNetwork.INSTANCE.sendToServer(new PktTryAdvanceResearch(p.id()));
					break;
				}
		super.mouseClicked(mouseX, mouseY, button);
		return true;
	}
	
	private boolean inBounds(int gx, int gy, int insets){
		int x = (int)((30 * gx + xOffset()) * zoom);
		int y = (int)((30 * gy + yOffset()) * zoom);
		int size = 30 - insets * 2;
		return x + insets + 9 > (width - 256) / 2 && y + insets + 9 > (height - 256) / 2 && x + insets + size < (width - 256) / 2 + 256 && y + insets + size < (height - 256) / 2 + 256;
	}
	
	public boolean isPauseScreen(){
		return false;
	}
	
	enum RenderStyle{
		Complete,
		In_Progress,
		Pending,
		None
	}
	
	static class TreeTab extends Button{
		
		ResourceLocation icon;
		String name;
		
		public TreeTab(int x, int y, int width, int height, IPressable onPress, ResourceLocation icon, String name){
			super(x, y, width, height, "", onPress);
			if(icon != null)
				this.icon = new ResourceLocation(icon.getNamespace(), "textures/" + icon.getPath());
			this.name = name;
		}
		
		public void renderButton(int p_render_1_, int p_render_2_, float p_render_3_){
			if(icon != null){
				if(!isHovered())
					GlStateManager.color4f(.5f, .5f, .5f, 1f);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableLighting();
				Minecraft.getInstance().getTextureManager().bindTexture(icon);
				// set size of either 16, 32, etc
				// will render to 16x though
				blit(x, y, blitOffset + borderZ, 0, 0, 16, 16, 16, 16);
				GlStateManager.color4f(1f, 1f, 1f, 1f);
			}
			if(isHovered() && name != null){
				// render text
				Minecraft.getInstance().fontRenderer.drawStringWithShadow(name, x - Minecraft.getInstance().fontRenderer.getStringWidth(name) - 10, y + 4, -1);
			}
		}
	}
}