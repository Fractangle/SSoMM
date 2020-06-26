package fractangle.ssomm.mystery.condition;

import fractangle.ssomm.SSoMM;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

import static fractangle.ssomm.mystery.condition.MysteryCondition.CONDITION_TYPE;

public final class ConditionCoordinate {
    protected static final String COORDINATE_X = "x";
    protected static final String COORDINATE_Z = "z";
    protected static final int COORDINATE_MAX_DIST = 16;
    protected static final int COORDINATE_FUZZ_DIST = 1;
    
    public static CompoundNBT get(@Nonnull World world, @Nonnull PlayerEntity player) {
        CompoundNBT coordinate = new CompoundNBT();
        coordinate.putString(CONDITION_TYPE, MysteryCondition.COORDINATE.getTagString());
        coordinate.putInt(COORDINATE_X, (int)player.getPosX() + ThreadLocalRandom.current().nextInt(COORDINATE_MAX_DIST *2) - COORDINATE_MAX_DIST);
        coordinate.putInt(COORDINATE_Z, (int)player.getPosZ() + ThreadLocalRandom.current().nextInt(COORDINATE_MAX_DIST *2) - COORDINATE_MAX_DIST);
        return coordinate;
    }
    
    public static boolean isSatisfied(CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player) {
        int cX, cZ;
        double pX, pZ;
        cX = conditionData.getInt(COORDINATE_X);
        cZ = conditionData.getInt(COORDINATE_Z);
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
}
