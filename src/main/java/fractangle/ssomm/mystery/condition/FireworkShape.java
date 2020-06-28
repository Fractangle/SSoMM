package fractangle.ssomm.mystery.condition;

import fractangle.ssomm.misc.WTFException;

import static fractangle.ssomm.SSoMM.getUnlocalizedName;

public enum FireworkShape {
    SMALL_BALL(0, getUnlocalizedName("condition", "firework", "shape", "small_ball")),
    LARGE_BALL(1, getUnlocalizedName("condition", "firework", "shape", "large_ball")),
    STAR(2, getUnlocalizedName("condition", "firework", "shape", "star")),
    CREEPER(3, getUnlocalizedName("condition", "firework", "shape", "creeper")),
    BURST(4, getUnlocalizedName("condition", "firework", "shape", "burst"));
    
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