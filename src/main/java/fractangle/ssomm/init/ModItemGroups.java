package fractangle.ssomm.init;

import fractangle.ssomm.SSoMM;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public final class ModItemGroups {
    public static final ItemGroup MOD_ITEM_GROUP = new ModItemGroup(SSoMM.MOD_ID, () -> new ItemStack(ModItems.MYSTERIOUS_MYSTERY.get()));
    
    public static final class ModItemGroup extends ItemGroup {
        @Nonnull
        private final Supplier<ItemStack> iconSupplier;
        
        public ModItemGroup(@Nonnull final String name, @Nonnull final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }
        
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return iconSupplier.get();
        }
    }
}
