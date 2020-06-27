package fractangle.ssomm.mystery.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
        if(Math.hypot(pX-cX, pZ-cZ) > COORDINATE_FUZZ_DIST) {
            return false;
        }
        
        Vec3d pos = player.getPositionVec();
        BlockRayTraceResult trace = world.rayTraceBlocks(new RayTraceContext(pos, new Vec3d(pos.getX(), 256, pos.getZ()), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, player));
        if(trace.getType() != BlockRayTraceResult.Type.MISS) {
            return false;
        }
        
        return true;
    }
    
    public static String typeset(CompoundNBT condition) {
        return "Meet our agent at x=" + condition.getInt(COORDINATE_X) + ", z=" + condition.getInt(COORDINATE_Z) + ", under open sky.";
    }
}
