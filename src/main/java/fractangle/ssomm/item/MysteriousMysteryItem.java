package fractangle.ssomm.item;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.client.gui.MysteriousMysteryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class MysteriousMysteryItem extends Item {
    protected static final String MYSTERY_TAG_NAME = SSoMM.MOD_ID + ":mysterious_mystery";
    
    public MysteriousMysteryItem(Properties properties) {
        super(properties);
    }
    
    public static CompoundNBT generateMysteryNBT() {
        CompoundNBT nbt = new CompoundNBT();
        
        return nbt;
    }
    
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack mystery = player.getHeldItem(hand);
        CompoundNBT nbt = mystery.getTag();
        
        if(nbt != null && nbt.contains(MYSTERY_TAG_NAME)) {
            if(world.isRemote) {
                Minecraft.getInstance().displayGuiScreen(new MysteriousMysteryScreen(mystery));
            }
        } else {
            if(!world.isRemote) {
                mystery.getOrCreateTag().put(MYSTERY_TAG_NAME, generateMysteryNBT());
            } else {
                player.sendMessage(new StringTextComponent("Your Mysterious Mystery wasn't initialized. Now it is. Try again."));
            }
        }
        
        return ActionResult.resultSuccess(mystery);
    }
}
