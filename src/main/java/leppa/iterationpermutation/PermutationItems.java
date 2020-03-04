package leppa.iterationpermutation;

import leppa.iterationpermutation.items.ResearchBook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(PermutationMod.MODID)
public class PermutationItems{
	
	public static final Item unnamed_book = null;
	
	public static ItemGroup group = new SupplierItemGroup(PermutationMod.MODID, () -> new ItemStack(unnamed_book));
	
	public static void registerItems(RegistryEvent.Register<Item> register){
		register.getRegistry().registerAll(
				setup(new ResearchBook(new Item.Properties().group(group), "iteration-and-permutation:arcana"), "unnamed_book")
		);
	}
	
	public static <T extends IForgeRegistryEntry<T>> T setup(T entry, String name) {
		return setup(entry, new ResourceLocation(PermutationMod.MODID, name));
	}
	
	public static <T extends IForgeRegistryEntry<T>> T setup(T entry, ResourceLocation registryName) {
		entry.setRegistryName(registryName);
		return entry;
	}
}