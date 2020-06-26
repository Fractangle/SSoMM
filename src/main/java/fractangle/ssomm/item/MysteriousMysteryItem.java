package fractangle.ssomm.item;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.client.gui.MysteriousMysteryScreen;
import fractangle.ssomm.init.ModItems;
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
    protected static final String CONDITION_TYPE = "conditionType";
    
    protected static final int CONDITION_COORDINATE = 1;
    protected static final String COORDINATE_X = "x";
    protected static final String COORDINATE_Z = "z";
    protected static final int COORDINATE_MAX_DIST = 16;
    protected static final int COORDINATE_FUZZ_DIST = 1;
    
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
        
        CompoundNBT coordinate = new CompoundNBT();
        coordinate.putInt(CONDITION_TYPE, CONDITION_COORDINATE);
        coordinate.putInt(COORDINATE_X, (int)player.getPosX() + random.nextInt(COORDINATE_MAX_DIST *2) - COORDINATE_MAX_DIST);
        coordinate.putInt(COORDINATE_Z, (int)player.getPosZ() + random.nextInt(COORDINATE_MAX_DIST *2) - COORDINATE_MAX_DIST);
        conditions.add(coordinate);
        
        return conditions;
    }
    
    private static boolean areConditionsSatisfied(ItemStack stack, @Nonnull World world, PlayerEntity player) {
        if(!(stack.getItem().equals(ModItems.MYSTERIOUS_MYSTERY.get()))) {
            SSoMM.PAUL_BUNYAN.log(Level.ERROR, "Somehow a Mysterious Mystery turned into something else... how mysterious!");
            return false;
        }
        SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Checking conditions...");
        CompoundNBT mysteryNBT = stack.getChildTag(MYSTERY_TAG_NAME);
        if(mysteryNBT != null) {
            ListNBT conditions = mysteryNBT.getList(CONDITIONS, Constants.NBT.TAG_COMPOUND);
            SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Found " + conditions.size() + " conditions");
            for (INBT rawCondition : conditions) {
                SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Checking a condition: " + rawCondition.toString());
                CompoundNBT condition = (CompoundNBT) rawCondition;
                if (!isConditionSatisfied(condition, world, player)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private static boolean isConditionSatisfied(CompoundNBT condition, @Nonnull World world, PlayerEntity player) {
        int conditionType = condition.getInt(CONDITION_TYPE);
        
        switch(conditionType){
            case CONDITION_COORDINATE:
                return isCoordinateSatisfied(condition, player);
            default:
                SSoMM.PAUL_BUNYAN.log(Level.ERROR, "Attempted to check an unknown CONDITION_TYPE: " + conditionType);
                return false;
        }
    }
    
    private static boolean isCoordinateSatisfied(CompoundNBT condition, PlayerEntity player) {
        int cX, cZ;
        double pX, pZ;
        cX = condition.getInt(COORDINATE_X);
        cZ = condition.getInt(COORDINATE_Z);
        pX = player.getPosX();
        pZ = player.getPosZ();
        SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Checking coordinate condition...");
        if(Math.hypot(pX-cX, pZ-cZ) < COORDINATE_FUZZ_DIST) {
            SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Coordinate success!");
            return true;
        }
        SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Coordinate failure!");
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
