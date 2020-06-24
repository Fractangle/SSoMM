package fractangle.ssomm.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fractangle.ssomm.SSoMM;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class MysteriousMysteryScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SSoMM.MOD_ID + ":textures/gui/mysterious_mystery.png");
    private final String text;
    
    public int x, y, xPad, yPad, xSize, ySize, xContent, yContent;
    
    public MysteriousMysteryScreen(final String text) {
        super(NarratorChatListener.EMPTY);
        this.text = text;
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
