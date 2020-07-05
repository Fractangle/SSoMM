package fractangle.ssomm.item;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.client.gui.MysteriousMysteryScreen;
import fractangle.ssomm.init.ModItems;
import fractangle.ssomm.misc.WTFException;
import fractangle.ssomm.mystery.condition.MysteryCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MysteriousMysteryItem extends Item {
    public static final String MYSTERY_TAG_NAME = SSoMM.MOD_ID + ":mysterious_mystery";
    protected static final String STEPS_COMPLETED = "stepsCompleted";
    public static final String CONDITIONS = "conditions";
    
    public static final int STEPS_MIN = 3;
    public static final int STEPS_MAX = 5;
    
    private static final ResourceLocation mysteryLootTable = new ResourceLocation("ssomm", "mysterious_mystery_loot");
    
    // For "Bring [thing] to [location]" steps
    public static final MysteryCondition[] LOCATION_CONDITIONS = {MysteryCondition.COORDINATE};
    public static final MysteryCondition[] BRING_CONDITIONS = {MysteryCondition.FIREWORK};
    
    public MysteriousMysteryItem(Properties properties) {
        super(properties);
    }
    
    public static CompoundNBT generateNewMysteryNBT(@Nonnull World world, PlayerEntity player) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(STEPS_COMPLETED, 0);
        nbt.put(CONDITIONS, generateStepConditions(world, player));
        return nbt;
    }
    
    public static ListNBT generateStepConditions(@Nonnull World world, PlayerEntity player) {
        ListNBT conditions = new ListNBT();
        
        switch(ThreadLocalRandom.current().nextInt(1)) {
            case 0: // Go to [location]. Bring [things].
                MysteryCondition locationCondition = LOCATION_CONDITIONS[ThreadLocalRandom.current().nextInt(LOCATION_CONDITIONS.length)];
                CompoundNBT locationNBT = locationCondition.getRandom(world, player);
                conditions.add(locationNBT);
        
                MysteryCondition bringCondition = BRING_CONDITIONS[ThreadLocalRandom.current().nextInt(BRING_CONDITIONS.length)];
                CompoundNBT bringNBT = bringCondition.getRandom(world, player);
                conditions.add(bringNBT);
                return conditions;
            default:
                throw new WTFException("Fractangle messed up the step-type switch statement, go yell at her on github");
        }
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
        if(!world.isRemote) {
            SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Ta-da, completed a mysterious mystery step");
            CompoundNBT tag = mystery.getTag();
            if (tag != null) {
                CompoundNBT mysteryTag = tag.getCompound(MYSTERY_TAG_NAME);
                int steps = mysteryTag.getInt(STEPS_COMPLETED)+1;
                mysteryTag.putInt(STEPS_COMPLETED, steps);
                if(ThreadLocalRandom.current().nextInt(STEPS_MAX+1-STEPS_MIN)+STEPS_MIN == steps) {
                    completeMystery(mystery, world, player);
                } else {
                    mysteryTag.put(CONDITIONS, generateStepConditions(world, player));
                }
            } else {
                throw new WTFException("How did we complete a mystery step that has no NBT tag?!");
            }
        }
    }
    
    private static void completeMystery(@Nonnull ItemStack mystery, @Nonnull World world, @Nonnull PlayerEntity player) {
        if(!world.isRemote) {
            SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Ta-da, completed a mysterious mystery");
            if(!(player instanceof ServerPlayerEntity)) {
                return;
            }
            MinecraftServer server = world.getServer();
            if(server == null) {
                return;
            }
            ServerPlayerEntity servPlayer = (ServerPlayerEntity) player;
            LootContext context = (new LootContext.Builder(servPlayer.getServerWorld()))
                    .withParameter(LootParameters.THIS_ENTITY, servPlayer)
                    .withParameter(LootParameters.POSITION, servPlayer.getPosition())
                    .build(LootParameterSets.GIFT);
            List<ItemStack> loot = server.getLootTableManager().getLootTableFromLocation(mysteryLootTable).generate(context);
            for(ItemStack stack : loot) {
                player.addItemStackToInventory(stack);
            }
            mystery.shrink(1);
        }
    }
    
    @Override
    public void onCreated(@Nonnull ItemStack mystery, @Nonnull World world, @Nonnull PlayerEntity player) {
        super.onCreated(mystery, world, player);
        if (!world.isRemote) {
            if (!mystery.hasTag()) {
                mystery.getOrCreateTag().put(MYSTERY_TAG_NAME, generateNewMysteryNBT(world, player));
            }
        }
    }
    
    @Override
    public void inventoryTick(@Nonnull ItemStack mystery, @Nonnull World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            if(entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                
                if (!mystery.hasTag()) { // This should never happen...? Pretty sure onCreated() fires before inventoryTick()
                    mystery.getOrCreateTag().put(MYSTERY_TAG_NAME, generateNewMysteryNBT(world, player));
                }
                
                if (areConditionsSatisfied(mystery, world, player)) {
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
