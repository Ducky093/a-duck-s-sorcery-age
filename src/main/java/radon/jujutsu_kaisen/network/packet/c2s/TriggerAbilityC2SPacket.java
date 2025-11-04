package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.TriggerAbilityS2CPacket;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import java.util.function.Supplier;

public class TriggerAbilityC2SPacket {
    private final ResourceLocation key;

    public TriggerAbilityC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            

            assert sender != null;
            Minecraft mc = Minecraft.getInstance();

            Ability.Status status;
            Ability ability = JJKAbilities.getValue(this.key);

            if ((status = AbilityHandler.trigger(sender, ability)) == Ability.Status.SUCCESS) {
                PacketHandler.sendToClient(new TriggerAbilityS2CPacket(JJKAbilities.getKey(ability)), sender );
            } else {    
                ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
                if (cap == null) return;    

            switch (status) {
            case ENERGY ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.energy", JujutsuKaisen.MOD_ID)), false);
            case COOLDOWN ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.cooldown", JujutsuKaisen.MOD_ID),
                            Math.max(1, cap.getRemainingCooldown(ability) / 20)), false);
            case BURNOUT ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.burnout", JujutsuKaisen.MOD_ID),
                        cap.getBurnout() / 20), false);
            case DISABLE ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.disable", JujutsuKaisen.MOD_ID)), false);     
            case FAILURE ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.failure", JujutsuKaisen.MOD_ID)), false);
            case CHANT ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.chant", JujutsuKaisen.MOD_ID)), false);
            case THROAT ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.throat", JujutsuKaisen.MOD_ID), 
                    Math.max(1, cap.getThroatDamage() / 20)), false);
            case EMPTYINV ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.emptyinv", JujutsuKaisen.MOD_ID)), false);
            }
        }
        });
        ctx.setPacketHandled(true);
    }
}