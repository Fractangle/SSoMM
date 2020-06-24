package fractangle.ssomm.item;

import fractangle.ssomm.client.gui.MysteriousMysteryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class MysteriousMysteryItem extends Item {
    public MysteriousMysteryItem(Properties properties) {
        super(properties);
    }
    
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(world.isRemote) {
            Minecraft.getInstance().displayGuiScreen(new MysteriousMysteryScreen("Test text\nSecond line is very long and will get wrapped somewhere"));
        }
        return ActionResult.resultSuccess(itemStack);
    }
}
