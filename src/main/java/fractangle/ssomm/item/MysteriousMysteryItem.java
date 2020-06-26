package fractangle.ssomm.item;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.client.gui.MysteriousMysteryScreen;
import fractangle.ssomm.init.ModItems;
import fractangle.ssomm.mystery.condition.MysteryCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

public class MysteriousMysteryItem extends Item {
    protected static final String MYSTERY_TAG_NAME = SSoMM.MOD_ID + ":mysterious_mystery";
    protected static final String STEPS_COMPLETED = "stepsCompleted";
    protected static final String CONDITIONS = "conditions";
    
    public MysteriousMysteryItem(Properties properties) {
        super(properties);
    }
    
    public static CompoundNBT generateNewMysteryNBT(@Nonnull World world, PlayerEntity player) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte(STEPS_COMPLETED, (byte) 0);
        nbt.put(CONDITIONS, generateStepConditions(world, player));
        return nbt;
    }
    
    public static ListNBT generateStepConditions(@Nonnull World world, PlayerEntity player) {
        ListNBT conditions = new ListNBT();
        
        CompoundNBT coordinate = MysteryCondition.COORDINATE.getRandom(world, player);
        conditions.add(coordinate);
        
        return conditions;
    }
    
    private static boolean areConditionsSatisfied(ItemStack stack, @Nonnull World world, PlayerEntity player) {
        if(!(stack.getItem().equals(ModItems.MYSTERIOUS_MYSTERY.get()))) {
            SSoMM.PAUL_BUNYAN.log(Level.ERROR, "Somehow a Mysterious Mystery turned into something else... how mysterious!");
            return false;
        }
        CompoundNBT mysteryNBT = stack.getChildTag(MYSTERY_TAG_NAME);
        if(mysteryNBT != null) {
            ListNBT conditions = mysteryNBT.getList(CONDITIONS, Constants.NBT.TAG_COMPOUND);
            for (INBT rawCondition : conditions) {
                CompoundNBT condition = (CompoundNBT) rawCondition;
                String type = condition.getString(MysteryCondition.CONDITION_TYPE);
                if(!MysteryCondition.fromString(type).isSatisfied(condition, world, player)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private static void completeMysteryStep(@Nonnull ItemStack mystery, @Nonnull World world, @Nonnull PlayerEntity player) {
        SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Ta-da, completed a mysterious mystery step");
    }
    
    @Override
    public void onCreated(@Nonnull ItemStack mystery, @Nonnull World world, @Nonnull PlayerEntity player) {
        super.onCreated(mystery, world, player);
        if (!mystery.hasTag()) {
            if (!world.isRemote) {
                mystery.getOrCreateTag().put(MYSTERY_TAG_NAME, generateNewMysteryNBT(world, player));
            }
        }
    }
    
    @Override
    public void inventoryTick(@Nonnull ItemStack mystery, @Nonnull World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            
            if (!mystery.hasTag()) { // This should never happen...? Pretty sure onCreated() fires before inventoryTick()
                if (!world.isRemote) {
                    mystery.getOrCreateTag().put(MYSTERY_TAG_NAME, generateNewMysteryNBT(world, player));
                }
            }
            
            if (areConditionsSatisfied(mystery, world, player)) {
                if(!world.isRemote) {
                    completeMysteryStep(mystery, world, player);
                }
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
