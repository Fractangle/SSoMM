package fractangle.ssomm.mystery.condition;

import fractangle.ssomm.SSoMM;
import fractangle.ssomm.misc.WTFException;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static fractangle.ssomm.mystery.condition.MysteryCondition.CONDITION_TYPE;

public class ConditionFirework {
    public static final String NEEDS_TWINKLE = "needsTwinkle";
    public static final String NEEDS_TRAIL = "needsTrail";
    public static final String SHAPE = "shape";
    public static final String COLORS = "colors";
    public static final String FADE_COLORS = "fadeColors";
    public static final String FIREWORK_ITEM_SRG = "field_184566_a";
    public static final Field FIREWORK_ITEM_FIELD = ObfuscationReflectionHelper.findField(FireworkRocketEntity.class, FIREWORK_ITEM_SRG);
    
    public enum FireworkColor {
        ERROR(-1, -1, SSoMM.MOD_ID + ".color_name.error"),
        WHITE(0, 15790320, SSoMM.MOD_ID + ".color_name.white"),
        ORANGE(1, 15435844, SSoMM.MOD_ID + ".color_name.orange"),
        MAGENTA(2, 12801229, SSoMM.MOD_ID + ".color_name.magenta"),
        LIGHT_BLUE(3, 6719955, SSoMM.MOD_ID + ".color_name.light_blue"),
        YELLOW(4, 14602026, SSoMM.MOD_ID + ".color_name.yellow"),
        LIME(5, 4312372, SSoMM.MOD_ID + ".color_name.lime"),
        PINK(6, 14188952, SSoMM.MOD_ID + ".color_name.pink"),
        GRAY(7, 4408131, SSoMM.MOD_ID + ".color_name.gray"),
        LIGHT_GRAY(8, 11250603, SSoMM.MOD_ID + ".color_name.light_gray"),
        CYAN(9, 2651799, SSoMM.MOD_ID + ".color_name.cyan"),
        PURPLE(10, 8073150, SSoMM.MOD_ID + ".color_name.purple"),
        BLUE(11, 2437522, SSoMM.MOD_ID + ".color_name.blue"),
        BROWN(12, 5320730, SSoMM.MOD_ID + ".color_name.brown"),
        GREEN(13, 3887386, SSoMM.MOD_ID + ".color_name.green"),
        RED(14, 11743532, SSoMM.MOD_ID + ".color_name.red"),
        BLACK(15, 1973019, SSoMM.MOD_ID + ".color_name.black");
        
        private final int decimal, index;
        private final String unlocalizedColorName;
        
        FireworkColor(int index, int decimal, String unlocalizedColorName) {
            this.index = index;
            this.decimal = decimal;
            this.unlocalizedColorName = unlocalizedColorName;
        }
        
        public static FireworkColor getByIndex(int index) {
            if(index < 0 || index > 15) {
                return ERROR;
            }
            
            for(FireworkColor fc : FireworkColor.values()) {
                if(fc.getColorIndex() == index) {
                    return fc;
                }
            }
            
            throw new WTFException("Impossible firework color index");
        }
        
        public static FireworkColor getByColorDecimal(int decimal) {
            for(FireworkColor fc : FireworkColor.values()) {
                if(fc.getColorDecimal() == decimal) {
                    return fc;
                }
            }
            
            return ERROR;
        }
        
        public int getColorIndex() {
            return index;
        }
        
        public int getColorDecimal() {
            return decimal;
        }
        
        public String getUnlocalizedColorName() {
            return unlocalizedColorName;
        }
    }
    
    public enum FireworkShape {
        SMALL_BALL(0, SSoMM.MOD_ID + "firework_shape_name.small_ball"),
        LARGE_BALL(1, SSoMM.MOD_ID + "firework_shape_name.large_ball"),
        STAR(2, SSoMM.MOD_ID + "firework_shape_name.star"),
        CREEPER(3, SSoMM.MOD_ID + "firework_shape_name.creeper"),
        BURST(4, SSoMM.MOD_ID + "firework_shape_name.burst");
        
        int index;
        String unlocalizedShapeName;
        
        FireworkShape(int index, String unlocalizedShapeName) {
            this.index = index;
            this.unlocalizedShapeName = unlocalizedShapeName;
        }
        
        public static FireworkShape getByIndex(int index) {
            if(index < 0 || index > 4) {
                throw new IllegalArgumentException("Firework shape index must be in range [0,4]");
            }
            
            for(FireworkShape fs : FireworkShape.values()) {
                if(fs.index == index) {
                    return fs;
                }
            }
            
            throw new WTFException("Impossible firework shape index");
        }
        
        public int getIndex() {
            return index;
        }
        
        public String getUnlocalizedShapeName() {
            return unlocalizedShapeName;
        }
    }
    
    public static CompoundNBT get(@Nonnull World world, @Nonnull PlayerEntity player) {
        CompoundNBT condition = new CompoundNBT();
    
        condition.putString(CONDITION_TYPE, MysteryCondition.FIREWORK.getTagString());
        
        boolean needsTwinkle = ThreadLocalRandom.current().nextBoolean();
        boolean needsTrail = ThreadLocalRandom.current().nextBoolean();
        
        int shape = FireworkShape.values()[ThreadLocalRandom.current().nextInt(FireworkShape.values().length)].getIndex();
        
        List<Integer> possibleColors = new ArrayList<Integer>();
        for(int i=0; i<16; i++) {
            possibleColors.add(i);
        }
        
        List<Integer> colorDecimals = new ArrayList<Integer>();
        int howManyColors = ThreadLocalRandom.current().nextInt(3) + 1;
        for(int i=0; i<howManyColors; i++) {
            colorDecimals.add(FireworkColor.getByIndex(possibleColors.remove(ThreadLocalRandom.current().nextInt(possibleColors.size()))).getColorDecimal());
        }
        
        List<Integer> fadeColorDecimals = new ArrayList<Integer>();
        if(ThreadLocalRandom.current().nextBoolean()) {
            possibleColors = new ArrayList<Integer>();
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
    
    public static boolean isSatisfied(CompoundNBT conditionData, @Nonnull World world, @Nonnull PlayerEntity player) {
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
            FIREWORK_ITEM_FIELD.setAccessible(true);
            try {
                FIREWORK_ITEM = (DataParameter<ItemStack>) FIREWORK_ITEM_FIELD.get(rocket);
            } catch(IllegalAccessException e) {
                throw new WTFException("I literally just called setAccessible(true)");
            }
            
            CompoundNBT fireworkData = rocketDataManager.get(FIREWORK_ITEM).getTag();

            if(fireworkData == null) return false;
            SSoMM.PAUL_BUNYAN.log(Level.DEBUG, "Firework: " + fireworkData);
            ListNBT explosions = fireworkData.getCompound("Fireworks").getList("Explosions", Constants.NBT.TAG_COMPOUND);

            boolean needsTwinkle = conditionData.getBoolean(NEEDS_TWINKLE);
            boolean needsTrail = conditionData.getBoolean(NEEDS_TRAIL);
            int shape = conditionData.getByte(SHAPE);
            ListNBT fadeColors = conditionData.getList(FADE_COLORS, Constants.NBT.TAG_INT);

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
                            break;
                        }
                    }
                    for(int color : explosionColors) {
                        if(!requiredColors.contains(color)) {
                            break;
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
                            break;
                        }
                    }
                    for(int color : explosionColors) {
                        if(!requiredColors.contains(color)) {
                            break;
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
}
