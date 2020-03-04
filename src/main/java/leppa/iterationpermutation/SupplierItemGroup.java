package leppa.iterationpermutation;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class SupplierItemGroup extends ItemGroup{

	private Supplier<ItemStack> iconSupplier;
	
	public SupplierItemGroup(String name, Supplier<ItemStack> iconSupplier){
		super(name);
		this.iconSupplier = iconSupplier;
	}
	
	public ItemStack createIcon(){
		return iconSupplier.get();
	}
}
