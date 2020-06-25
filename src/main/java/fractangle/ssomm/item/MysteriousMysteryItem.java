package fractangle.ssomm.item;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.client.gui.MysteriousMysteryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class MysteriousMysteryItem extends Item {
    protected static final String MYSTERY_TAG_NAME = SSoMM.MOD_ID + ":mysterious_mystery";
    protected static final int COORD_MAX_DIST = 16;
    
    public MysteriousMysteryItem(Properties properties) {
        super(properties);
    }
    
    public static CompoundNBT generateNewMysteryNBT(@Nonnull World world, PlayerEntity player) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("stepsCompleted", (byte) 0);
        nbt.put("step", generateStep(world, player));
        return nbt;
    }
    
    public static CompoundNBT generateStep(@Nonnull World world, PlayerEntity player) {
        CompoundNBT step = new CompoundNBT();
        ListNBT conditions = new ListNBT();
        
        CompoundNBT coord = new CompoundNBT();
        coord.putInt("x", (int)player.getPosX() + random.nextInt(COORD_MAX_DIST*2) - COORD_MAX_DIST);
        coord.putInt("z", (int)player.getPosZ() + random.nextInt(COORD_MAX_DIST*2) - COORD_MAX_DIST);
        conditions.add(coord);
        
        step.put("conditions", conditions);
        
        return step;
    }
    
    @Override
    public void inventoryTick(ItemStack mystery, @Nonnull World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if(!mystery.hasTag() && entity instanceof PlayerEntity) {
            if(!world.isRemote) {
                mystery.getOrCreateTag().put(MYSTERY_TAG_NAME, generateNewMysteryNBT(world, (PlayerEntity) entity));
            }
        }
    }
    
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack mystery = player.getHeldItem(hand);
        CompoundNBT nbt = mystery.getTag();
        
        if(nbt != null && nbt.contains(MYSTERY_TAG_NAME)) {
            if(world.isRemote) {
                player.sendMessage(new StringTextComponent(nbt.toString()));
                Minecraft.getInstance().displayGuiScreen(new MysteriousMysteryScreen(mystery));
            }
        }
        
        return ActionResult.resultSuccess(mystery);
    }
}
