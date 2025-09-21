package radon.jujutsu_kaisen.ability.body_swap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BodySteal extends Ability implements Ability.IToggled, Ability.IAttack {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && target.isDeadOrDying() && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public boolean isValid(LivingEntity owner) {
        //ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        //return cap.getCopied().size() < ConfigHolder.SERVER.maximumCopiedTechniques.get() && JJKAbilities.hasToggled(owner, JJKAbilities.RIKA.get()) && super.isValid(owner);
        return true;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!HelperMethods.isMelee(source)) return false;
        if (!(target instanceof Player player) ) return false;
        //if (!(source.getEntity() instanceof LivingEntity living)) return false;
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        if (targetCap.getExperience() <= 1000) { //min exp to steal
            return false;
        }
        CursedTechnique current = ownerCap.getTechnique();
        CursedTechnique copied = targetCap.getTechnique();
        CursedEnergyNature nature = targetCap.getNature();
        
        if (copied == null || current == null) return false;

        if (current != copied) {
            owner.sendSystemMessage(Component.translatable(String.format("chat.%s.bodysteal", JujutsuKaisen.MOD_ID), target.getName()));
            ownerCap.setExperience(targetCap.getExperience());
            targetCap.setExperience(0);
            ownerCap.steal(copied);
            ownerCap.setNature(nature);

            //SkinManager skinManager = Minecraft.getInstance().getSkinManager();
            GameProfile profile = player.getGameProfile();
            ownerCap.setStolenSkinProfile(profile);
            // skinManager.registerSkins(profile, (type, texture) -> {
            //     if (type == MinecraftProfileTexture.Type.SKIN) {
            //         ResourceLocation skin = skinManager.registerTexture(texture, type);
            //         this.cachedSkin = skin; // save this somewhere (entity, renderer, etc.)
            //     }
            // }, true);
            if (owner instanceof ServerPlayer servPlayer) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), servPlayer);
            }
            return true;
        }
        return false;
    }
}
