package fractangle.ssomm.mystery.condition;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.misc.WTFException;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static fractangle.ssomm.SSoMM.getUnlocalizedName;

public abstract class MysteryCondition {
    public static final String CONDITION_TYPE = "conditionType";
    
    abstract public CompoundNBT getRandom(@Nonnull World world, @Nonnull PlayerEntity player);
    abstract public boolean isSatisfied(CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player);
    abstract public String typeset(CompoundNBT conditionData);
    
    protected String tag;
    protected static Map<String, MysteryCondition> registry = new HashMap<>();
    
    MysteryCondition(String tag) {
        this.tag = tag;
        registry.put(tag, this);
    }
    
    public static MysteryCondition fromString(String tag) {
        return registry.get(tag);
    }
    
    public static final MysteryCondition COORDINATE = new MysteryCondition("coordinate") {
        protected final String COORDINATE_X = "x";
        protected final String COORDINATE_Z = "z";
        protected final int COORDINATE_MAX_DIST = 16;
        protected final int COORDINATE_FUZZ_DIST = 1;
        
        @Override
        public CompoundNBT getRandom(@Nonnull World world, @Nonnull PlayerEntity player) {
            CompoundNBT coordinate = new CompoundNBT();
            coordinate.putString(CONDITION_TYPE, this.tag);
            coordinate.putInt(COORDINATE_X, (int)player.getPosX() + ThreadLocalRandom.current().nextInt(COORDINATE_MAX_DIST *2) - COORDINATE_MAX_DIST);
            coordinate.putInt(COORDINATE_Z, (int)player.getPosZ() + ThreadLocalRandom.current().nextInt(COORDINATE_MAX_DIST *2) - COORDINATE_MAX_DIST);
            return coordinate;
        }
        
        @Override
        public boolean isSatisfied(CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player) {
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
            //noinspection RedundantIfStatement
            if(trace.getType() != BlockRayTraceResult.Type.MISS) {
                return false;
            }
            
            return true;
        }
        
        @Override
        public String typeset(CompoundNBT conditionData) {
            return "Meet our agent at x=" + conditionData.getInt(COORDINATE_X) + ", z=" + conditionData.getInt(COORDINATE_Z) + ", under open sky.";
        }
    };
    public static final MysteryCondition FIREWORK = new MysteryCondition("firework") {
        protected final String NEEDS_TWINKLE = "needsTwinkle";
        protected final String NEEDS_TRAIL = "needsTrail";
        protected final String SHAPE = "shape";
        protected final String COLORS = "colors";
        protected final String FADE_COLORS = "fadeColors";
        protected final String FIREWORK_ITEM_SRG = "field_184566_a";
        protected final Field FIREWORK_ITEM_FIELD = ObfuscationReflectionHelper.findField(FireworkRocketEntity.class, FIREWORK_ITEM_SRG);
        
        @Override
        public CompoundNBT getRandom(@Nonnull World world, @Nonnull PlayerEntity player) {
            CompoundNBT condition = new CompoundNBT();
            
            condition.putString(CONDITION_TYPE, this.tag);
            
            boolean needsTwinkle = ThreadLocalRandom.current().nextBoolean();
            boolean needsTrail = ThreadLocalRandom.current().nextBoolean();
            
            int shape = FireworkShape.values()[ThreadLocalRandom.current().nextInt(FireworkShape.values().length)].getIndex();
            
            List<Integer> possibleColors = new ArrayList<>();
            for(int i=0; i<16; i++) {
                possibleColors.add(i);
            }
            
            List<Integer> colorDecimals = new ArrayList<>();
            int howManyColors = ThreadLocalRandom.current().nextInt(3) + 1;
            for(int i=0; i<howManyColors; i++) {
                colorDecimals.add(FireworkColor.getByIndex(possibleColors.remove(ThreadLocalRandom.current().nextInt(possibleColors.size()))).getColorDecimal());
            }
            
            List<Integer> fadeColorDecimals = new ArrayList<>();
            if(ThreadLocalRandom.current().nextBoolean()) {
                possibleColors = new ArrayList<>();
                for(int i=0; i<16; i++) {
                    possibleColors.add(i);
                }
                howManyColors = ThreadLocalRandom.current().nextInt(3) + 1;
                for(int i=0; i<howManyColors; i++) {
                    fadeColorDecimals.add(FireworkColor.getByIndex(possibleColors.remove(ThreadLocalRandom.current().nextInt(possibleColors.size()))).getColorDecimal());
                }
            }
            
            condition.putBoolean(NEEDS_TWINKLE, needsTwinkle);
            condition.putBoolean(NEEDS_TRAIL, needsTrail);
            condition.putInt(SHAPE, shape);
            
            ListNBT colorsNBT = new ListNBT();
            for(int color : colorDecimals) {
                colorsNBT.add(IntNBT.valueOf(color));
            }
            condition.put(COLORS, colorsNBT);
            
            ListNBT fadeColorsNBT = new ListNBT();
            for(int color : fadeColorDecimals) {
                fadeColorsNBT.add(IntNBT.valueOf(color));
            }
            if(fadeColorsNBT.size() > 0) {
                condition.put(FADE_COLORS, fadeColorsNBT);
            }
            
            return condition;
        }
        
        @Override
        public boolean isSatisfied(CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player) {
            double ax, ay, az, bx, by, bz;
            double range = 32;
            ax = player.getPosX() - range;
            ay = player.getPosY() - range;
            az = player.getPosZ() - range;
            bx = player.getPosX() + range;
            by = player.getPosY() + range;
            bz = player.getPosZ() + range;
            AxisAlignedBB boundingBox = new AxisAlignedBB(ax, ay, az, bx, by, bz);
            List<FireworkRocketEntity> rocketsLotsOfRockets = world.getEntitiesWithinAABB(FireworkRocketEntity.class, boundingBox, null);
            boolean foundMatch;
            for(FireworkRocketEntity rocket : rocketsLotsOfRockets) {
                foundMatch = false;
                EntityDataManager rocketDataManager = rocket.getDataManager();
                DataParameter<ItemStack> FIREWORK_ITEM;
                try {
                    // Dear IntelliJ, I know more about this than Java's type system does. I even cover my ass later on. So please kindly shut up.
                    //noinspection unchecked
                    FIREWORK_ITEM = (DataParameter<ItemStack>) FIREWORK_ITEM_FIELD.get(rocket);
                } catch(IllegalAccessException e) {
                    throw new WTFException("findField() was supposed to make this accessible! *shakes fist at Forge*");
                } catch(ClassCastException e) {
                    throw new WTFException("FireworkRocketEntity apparently changed the type of its FIREWORK_ITEM field...?");
                }
                
                CompoundNBT fireworkData = rocketDataManager.get(FIREWORK_ITEM).getTag();
                
                if(fireworkData == null) return false;
                ListNBT explosions = fireworkData.getCompound("Fireworks").getList("Explosions", Constants.NBT.TAG_COMPOUND);
                
                boolean needsTwinkle = conditionData.getBoolean(NEEDS_TWINKLE);
                boolean needsTrail = conditionData.getBoolean(NEEDS_TRAIL);
                int shape = conditionData.getByte(SHAPE);
                
                checkExplosion:
                for(INBT explosionRaw : explosions) {
                    CompoundNBT explosion = (CompoundNBT) explosionRaw;
                    
                    if(needsTwinkle && (!explosion.contains("Flicker") || explosion.getByte("Flicker") != 1)) {
                        break;
                    }
                    
                    if(needsTrail && (!explosion.contains("Trail") || explosion.getByte("Trail") != 1)) {
                        break;
                    }
                    
                    if(!explosion.contains("Type") || explosion.getByte("Type") != shape) {
                        break;
                    }
                    
                    if(!explosion.contains("Colors")) {
                        break;
                    } else {
                        ListNBT requiredColorsNBT = conditionData.getList(COLORS, Constants.NBT.TAG_INT);
                        int[] explosionColorsArray = explosion.getIntArray("Colors");
                        Set<Integer> requiredColors = new HashSet<>();
                        for(INBT colorRaw : requiredColorsNBT) {
                            int color = ((IntNBT) colorRaw).getInt();
                            requiredColors.add(color);
                        }
                        Set<Integer> explosionColors = new HashSet<>();
                        for(int color : explosionColorsArray) {
                            explosionColors.add(color);
                        }
                        for(int color : requiredColors) {
                            if(!explosionColors.contains(color)) {
                                break checkExplosion;
                            }
                        }
                        for(int color : explosionColors) {
                            if(!requiredColors.contains(color)) {
                                break checkExplosion;
                            }
                        }
                    }
                    
                    if(!explosion.contains("FadeColors") && conditionData.contains(FADE_COLORS) && conditionData.getList(FADE_COLORS, Constants.NBT.TAG_INT).size() > 0) {
                        break;
                    } else {
                        ListNBT requiredColorsNBT = conditionData.getList(FADE_COLORS, Constants.NBT.TAG_INT);
                        int[] explosionColorsArray = explosion.getIntArray("FadeColors");
                        Set<Integer> requiredColors = new HashSet<>();
                        for(INBT colorRaw : requiredColorsNBT) {
                            int color = ((IntNBT) colorRaw).getInt();
                            requiredColors.add(color);
                        }
                        Set<Integer> explosionColors = new HashSet<>();
                        for(int color : explosionColorsArray) {
                            explosionColors.add(color);
                        }
                        for(int color : requiredColors) {
                            if(!explosionColors.contains(color)) {
                                break checkExplosion;
                            }
                        }
                        for(int color : explosionColors) {
                            if(!requiredColors.contains(color)) {
                                break checkExplosion;
                            }
                        }
                    }
                    
                    foundMatch = true;
                }
                
                if(foundMatch) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String typeset(CompoundNBT conditionData) {
            // Launch a red, green, and blue star-shaped firework that fades to orange and purple with trails and twinkling
            // |______| |__________________| |_________| |______| |___________| |_______________| |__| |____| |_| |_______|
            StringBuilder result = new StringBuilder();
            result.append(I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "1"))); // "Launch one"
            result.append(" ");
            result.append(buildColorString(conditionData.getList(COLORS, Constants.NBT.TAG_INT))); // [colors]
            result.append(" ");
            result.append(I18n.format(FireworkShape.getByIndex(conditionData.getInt(SHAPE)).getUnlocalizedShapeName())); // shape
            result.append(I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "2"))); // "-shaped firework"
            if(conditionData.contains(FADE_COLORS) && conditionData.getList(FADE_COLORS, Constants.NBT.TAG_INT).size() > 0) {
                result.append(" ");
                result.append(I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "3"))); // "that fades to"
                result.append(" ");
                result.append(buildColorString(conditionData.getList(FADE_COLORS, Constants.NBT.TAG_INT))); // [colors]
            }
            if(conditionData.contains(NEEDS_TRAIL) && conditionData.getByte(NEEDS_TRAIL) != 0) {
                result.append(" ");
                result.append(I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "4"))); // "with trails"
                if(conditionData.contains(NEEDS_TWINKLE) && conditionData.getByte(NEEDS_TWINKLE) != 0) {
                    result.append(" ");
                    result.append(I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "5"))); // "and twinkling"
                }
            } else {
                if(conditionData.contains(NEEDS_TWINKLE) && conditionData.getByte(NEEDS_TWINKLE) != 0) {
                    result.append(" ");
                    result.append(I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "6"))); // "with twinkling"
                }
            }
            result.append(I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "7"))); // "."
            
            return result.toString();
        }
        
        private String buildColorString(ListNBT colorNBT) {
            switch(colorNBT.size()) {
                case 1:
                    return I18n.format(FireworkColor.getByColorDecimal(((IntNBT)colorNBT.get(0)).getInt()).getUnlocalizedColorName());
                case 2:
                    String colorLoc1 = I18n.format(FireworkColor.getByColorDecimal(((IntNBT)colorNBT.get(0)).getInt()).getUnlocalizedColorName());
                    String colorLoc2 = I18n.format(FireworkColor.getByColorDecimal(((IntNBT)colorNBT.get(1)).getInt()).getUnlocalizedColorName());
                    String and2 = I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "and"));
                    return String.format("%s %s %s", colorLoc1, and2, colorLoc2);
                case 3:
                    String colorLocA = I18n.format(FireworkColor.getByColorDecimal(((IntNBT)colorNBT.get(0)).getInt()).getUnlocalizedColorName());
                    String colorLocB = I18n.format(FireworkColor.getByColorDecimal(((IntNBT)colorNBT.get(1)).getInt()).getUnlocalizedColorName());
                    String colorLocC = I18n.format(FireworkColor.getByColorDecimal(((IntNBT)colorNBT.get(2)).getInt()).getUnlocalizedColorName());
                    String comma = I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "comma"));
                    String and3 = I18n.format(getUnlocalizedName("condition", "firework", "boilerplate", "and"));
                    return String.format("%s%s %s%s %s %s", colorLocA, comma, colorLocB, comma, and3, colorLocC);
                default:
                    return I18n.format(getUnlocalizedName("condition", "firework", "color", "error"));
            }
        }
    };
}
