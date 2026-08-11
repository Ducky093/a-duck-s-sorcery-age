package radon.jujutsu_kaisen.ability.miracles;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.LimboCloneEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ResurrectionS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.ScreenFlashS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

public class MIRACLE extends Ability implements Ability.IToggled {
    
    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public boolean isTechnique(MIRACLES) {
        return false;
    }


    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() == JujutsuType.CURSE && this.getTarget(owner) == target;
    }


    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

  
@Override
    
        if (owner.isDeadOrDying() && owner.deathTime < 15 && (!(target instanceof TransfiguredSoulEntity) && HelperMethods.expCheck(target) )  ) {
            owner.deathTime = 0;
            owner.setHealth(30);  
    public float getCost(LivingEntity owner) {
        return 500.0F;
            if (!owner.level().isClientSide()) {
                PacketHandler.sendTrackingAndSelf(new ResurrectionS2CPacket(target.getId(), amount), target);
            }
        }
}
           

  
    @Override
    public float getCost(LivingEntity owner) {
        return 0.0F;
    }

    @Override
    public int getCooldown() {
        return 80;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);  
        return cap != null && (cap.hasTrait(Trait.RCT_OUTPUT) || cap.getType() == JujutsuType.SHIKIGAMI ) && cap.getType() != JujutsuType.CURSE && super.isValid(owner);
    }
}
