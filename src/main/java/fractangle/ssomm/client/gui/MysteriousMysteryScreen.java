package fractangle.ssomm.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import fractangle.ssomm.SSoMM;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class MysteriousMysteryScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SSoMM.MOD_ID + ":textures/gui/mysterious_mystery.png");
    private final String text;
    
    public int x, y, xPad, yPad, xSize, ySize, xContent, yContent;
    
    public MysteriousMysteryScreen(final ItemStack mystery) {
        super(NarratorChatListener.EMPTY);
        this.text = textFromNBT(mystery.getTag());
    }
    
    protected String textFromNBT(CompoundNBT tag) {
        if(tag == null) {
            return "ERROR: Uninitialized mystery somehow made a GUI! This should never happen. Yell at Fractangle.";
        }
        return "placeholder";
    }
    
    @Override
    protected void init() {
        super.init();
        xSize = 144;
        ySize = 192;
        xPad = 12;
        yPad = 12;
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        xContent = x + xPad;
        yContent = y + yPad;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null; // Shut up Intellij
        minecraft.getTextureManager().bindTexture(MysteriousMysteryScreen.BACKGROUND_TEXTURE);
        RenderSystem.color3f(1F, 1F, 1F);
        blit(x, y, 0, 0, xSize, ySize);
        this.font.drawSplitString(text, xContent, yContent, xSize - 2*xPad, 0);
    
        super.render(mouseX, mouseY, partialTicks);
    }
}
