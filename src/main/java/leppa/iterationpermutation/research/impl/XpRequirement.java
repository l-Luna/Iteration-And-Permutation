package leppa.iterationpermutation.research.impl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import leppa.iterationpermutation.research.Requirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class XpRequirement extends Requirement{
	
	private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
	
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
