package fractangle.ssomm.init;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.item.MysteriousMysteryItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, SSoMM.MOD_ID);
    
    public static final RegistryObject<Item> MYSTERIOUS_MYSTERY = ITEMS.register("mysterious_mystery", () -> new MysteriousMysteryItem(new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP).maxStackSize(1)));
}
