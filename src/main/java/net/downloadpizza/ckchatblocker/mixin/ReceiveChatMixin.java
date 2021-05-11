package net.downloadpizza.ckchatblocker.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ReceiveChatMixin {
    private final Pattern playerChatPattern = Pattern.compile("^\\[(?<rank>G|M|T|R|H|B|#|ZK|SG|YT|A)] (?<name>\\w{3,16}) (?<msg>.*)$");
    private final SocialInteractionsManager sim = MinecraftClient.getInstance().getSocialInteractionsManager();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), cancellable = true)
    public void addMessage(Text text, int messageId, CallbackInfo info) {
        if(MinecraftClient.getInstance().world == null) return;
        String message = text.getString();
        Matcher m = playerChatPattern.matcher(message);
        if(!m.matches()) return;
        String name = m.group("name");

        UUID uuid = sim.method_31407(name);
        if(sim.isPlayerHidden(uuid) || sim.isPlayerBlocked(uuid))
            info.cancel();
    }
}
