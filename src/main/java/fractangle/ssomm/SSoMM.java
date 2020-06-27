package fractangle.ssomm;

import fractangle.ssomm.init.ModItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SSoMM.MOD_ID)
public class SSoMM {
    public static final String MOD_ID = "ssomm";
    public static final String GUI_TEXTURE_DIR = "textures/gui/";
    public static final Logger PAUL_BUNYAN = LogManager.getLogger();
    
    public SSoMM() {
        PAUL_BUNYAN.log(Level.DEBUG, "Your mysteriosity mysteriously increases!");
    
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModItems.ITEMS.register(modEventBus);
    }
    
    public static ResourceLocation getGuiTexture(String name) {
        return new ResourceLocation(MOD_ID, GUI_TEXTURE_DIR + name);
    }
}
