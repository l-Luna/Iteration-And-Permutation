package leppa.iterationpermutation.research.impl;

import leppa.iterationpermutation.research.PageSection;
import leppa.iterationpermutation.research.Requirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageSection implements PageSection{
	
	ResourceLocation img;
	List<Requirement> requirements = new ArrayList<>();
	
	private int width = -1, height = -1;
	private static final int MAX_WIDTH = 96;
	private static final int MAX_HEIGHT = 144;
	
	static{
		deserializers.put("ImageSection", nbt -> new ImageSection(new ResourceLocation(nbt.getString("image"))));
	}
	
	public ImageSection(ResourceLocation img){
		if(!img.getPath().startsWith("textures/"))
			img = new ResourceLocation(img.getNamespace(), "textures/" + img.getPath());
		this.img = img;
	}
	
	public int span(){
		return 1;
	}
	
	public void render(boolean right, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY){
		float x = (screenWidth / 2f - 128f) + (right ? 142 : 18);
		float y = (screenHeight / 2f - 128f) + 20;
		// 96 x 145 max
		if(width == -1 || height == -1)
			refreshImageSize();
		Minecraft.getInstance().getTextureManager().bindTexture(img);
		AbstractGui.blit((int)x, (int)y, 0, width, height, 0, 0, width, height);
	}
	
	private void refreshImageSize(){
		try{
			Image image = ImageIO.read(Minecraft.getInstance().getResourceManager().getResource(img).getInputStream());
			width = Math.min(image.getWidth(null), MAX_WIDTH);
			height = Math.min(image.getHeight(null), MAX_HEIGHT);
		}catch(IOException e){
			System.err.println("Could not get size of image " + img + " for rendering ImageSection!");
			e.printStackTrace();
			width = 16;
			height = 16;
		}
		
	}
	
	public void addRequirement(Requirement requirement){
		requirements.add(requirement);
	}
	
	public List<Requirement> getRequirements(){
		return Collections.unmodifiableList(requirements);
	}
	
	public CompoundNBT getData(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("image", img.toString());
		return nbt;
	}
	
	public String getTypeId(){
		return "ImageSection";
	}
}