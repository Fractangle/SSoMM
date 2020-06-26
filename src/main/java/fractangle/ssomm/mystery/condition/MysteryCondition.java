package fractangle.ssomm.mystery.condition;

import fractangle.ssomm.SSoMM;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

public enum MysteryCondition {
    INVALID((@Nonnull World world, @Nonnull PlayerEntity player) -> null, (CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player) -> false, "INVALID")
    ,COORDINATE(ConditionCoordinate::get, ConditionCoordinate::isSatisfied, "coordinate")
    //,FIREWORK()
    ;
    
    public static final String CONDITION_TYPE = "conditionType";
    
    IGetNewNBT nbtMaker;
    ICheckSatisfaction satisfactionChecker;
    String tag;
    
    MysteryCondition(IGetNewNBT nbtMaker, ICheckSatisfaction satisfactionChecker, String tag) {
        this.nbtMaker = nbtMaker;
        this.satisfactionChecker = satisfactionChecker;
        this.tag = tag;
    }
    
    public static MysteryCondition fromString(String tag) {
        for(MysteryCondition mc : MysteryCondition.values()) {
            if(mc.tag.equals(tag)) {
                return mc;
            }
        }
        SSoMM.PAUL_BUNYAN.log(Level.ERROR, "Unknown mystery condition \"" + tag + "\"");
        return INVALID;
    }
    
    public CompoundNBT getRandom(@Nonnull World world, @Nonnull PlayerEntity player) {
        return nbtMaker.get(world, player);
    }
    
    public boolean isSatisfied(CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player) {
        return satisfactionChecker.isSatisfied(conditionData, world, player);
    }
    
    public String getTagString() {
        return this.tag;
    }
    
    @FunctionalInterface
    public interface IGetNewNBT {
        CompoundNBT get(@Nonnull World world, @Nonnull PlayerEntity player);
    }
    
    @FunctionalInterface
    public interface ICheckSatisfaction {
        boolean isSatisfied(CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player);
    }
}
