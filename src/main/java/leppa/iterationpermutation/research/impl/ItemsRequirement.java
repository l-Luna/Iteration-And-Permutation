package leppa.iterationpermutation.research.impl;

import leppa.iterationpermutation.research.Requirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ItemsRequirement extends Requirement{
	
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
