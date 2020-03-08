package leppa.iterationpermutation.research.impl;

import leppa.iterationpermutation.research.PageSection;
import leppa.iterationpermutation.research.Requirement;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static leppa.iterationpermutation.research.PageSection.fr;

public class StringSection implements PageSection{
	
	static int linesPerPage = (int)Math.ceil(140 / (float)fr().FONT_HEIGHT);
	static int pageWidth = 100;
	
	static{
		deserializers.put("StringSection", nbt -> new StringSection(nbt.getString("text")));
	}
	
	String text;
	List<Requirement> requirements = new ArrayList<>();
	
	public StringSection(String text){
		this.text = text;
	}
	
	public String getTranslatedText(){
		return I18n.format(text);
	}
	
	public int span(){
		return (int)Math.ceil(fr().listFormattedStringToWidth(getTranslatedText(), pageWidth).size() / (double)linesPerPage);
	}
	
	public void render(boolean right, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY){
		List<String> lines = fr().listFormattedStringToWidth(getTranslatedText(), pageWidth);
		lines = lines.subList(pageIndex * linesPerPage,
							  Math.min((pageIndex + 1) * linesPerPage,
									   lines.size()));
		
		for(int i = 0; i < lines.size(); i++)
			fr().drawString(lines.get(i), ((screenWidth - 256) / 2f) + (right ? 142 : 18), ((screenHeight - 256) / 2f) + 20 + i * 9, 0);
	}
	
	public void addRequirement(Requirement requirement){
		requirements.add(requirement);
	}
	
	public List<Requirement> getRequirements(){
		return Collections.unmodifiableList(requirements);
	}
	
	public CompoundNBT getData(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("text", text);
		return nbt;
	}
	
	public String getTypeId(){
		return "StringSection";
	}
}