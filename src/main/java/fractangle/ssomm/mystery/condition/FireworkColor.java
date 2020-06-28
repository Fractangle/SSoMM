package fractangle.ssomm.mystery.condition;

import fractangle.ssomm.misc.WTFException;

import static fractangle.ssomm.SSoMM.getUnlocalizedName;

public enum FireworkColor {
    ERROR(-1, -1, getUnlocalizedName("condition", "firework", "color", "error")),
    WHITE(0, 15790320, getUnlocalizedName("condition", "firework", "color", "white")),
    ORANGE(1, 15435844, getUnlocalizedName("condition", "firework", "color", "orange")),
    MAGENTA(2, 12801229, getUnlocalizedName("condition", "firework", "color", "magenta")),
    LIGHT_BLUE(3, 6719955, getUnlocalizedName("condition", "firework", "color", "light_blue")),
    YELLOW(4, 14602026, getUnlocalizedName("condition", "firework", "color", "yellow")),
    LIME(5, 4312372, getUnlocalizedName("condition", "firework", "color", "lime")),
    PINK(6, 14188952, getUnlocalizedName("condition", "firework", "color", "pink")),
    GRAY(7, 4408131, getUnlocalizedName("condition", "firework", "color", "gray")),
    LIGHT_GRAY(8, 11250603, getUnlocalizedName("condition", "firework", "color", "light_gray")),
    CYAN(9, 2651799, getUnlocalizedName("condition", "firework", "color", "cyan")),
    PURPLE(10, 8073150, getUnlocalizedName("condition", "firework", "color", "purple")),
    BLUE(11, 2437522, getUnlocalizedName("condition", "firework", "color", "blue")),
    BROWN(12, 5320730, getUnlocalizedName("condition", "firework", "color", "brown")),
    GREEN(13, 3887386, getUnlocalizedName("condition", "firework", "color", "green")),
    RED(14, 11743532, getUnlocalizedName("condition", "firework", "color", "red")),
    BLACK(15, 1973019, getUnlocalizedName("condition", "firework", "color", "black"));
    
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