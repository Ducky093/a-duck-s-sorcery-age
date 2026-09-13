package radon.jujutsu_kaisen.ability.blood_manipulation;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlowingRedScale extends Ability implements Ability.IToggled {
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("641b629b-f7b7-4066-a486-8e1d670a7439");
 

    private static final double SPEED = 0.03D;
    //private boolean hasShieldDrained = false;



    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return true;
    }
      @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getTechnique() == CursedTechnique.BLOOD_MANIPULATION);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }


    @Override
    public boolean usesHands() {
        return false;
    }

    

    @Override
    public void run(LivingEntity owner) {
        if (!owner.level().getBlockState(owner.blockPosition()).getFluidState().isEmpty()) {
            Vec3 movement = owner.getDeltaMovement();

            if (movement.y < 0.0D) {
                double amount = 0.01D;
                if (owner.isInWater()) {
                    amount = 0.15D;
                }
                owner.setDeltaMovement(movement.x, amount, movement.z);
            }
            owner.setOnGround(true);
        }
        JJKEffectUtil.addEffect(owner, new MobEffectInstance(MobEffects.JUMP, 2, 2, false, false, false));
        if (owner instanceof Player player) {
            float f;

            if (owner.onGround() && !owner.isDeadOrDying() && !owner.isSwimming()) {
                f = Math.min(0.1F, (float) owner.getDeltaMovement().horizontalDistance());
            } else {
                f = 0.0F;
            }
            player.bob += (f - player.bob) * 0.4F;
        }


        // ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        // boolean isShielding = cap.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get());
        // if (isShielding) {
        //     if (!this.hasShieldDrained) {
        //         this.hasShieldDrained = true;
        //         cap.useEnergy(30);
        //     }
        // } else {
        //     this.hasShieldDrained = false;
        // }

        if (!(owner.level() instanceof ServerLevel level)) return;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
       
        //float scale = isShielding ? 1.4F : 1.0F;
        float scale = cap.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get()) ? 1.4F : 1.0F;
        if (cap.getNature() == CursedEnergyNature.LIGHTNING) {
            for (int i = 0; i < 4; i++) {
                double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2 * scale);
                double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * (owner.getBbHeight() * 1.25F * scale);
                double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2 * scale);
                level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                        x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            if (owner.isInWater()) {
                for (Entity entity : owner.level().getEntities(owner, owner.getBoundingBox().inflate(16.0D))) {
                    if (!entity.isInWater()) continue;

                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), LIGHTNING_DAMAGE * this.getPower(owner))) {
                        for (int i = 0; i < 16; i++) {
                            double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                            double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * (entity.getBbHeight() * 1.25F);
                            double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                            level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                                    x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
                        }
                        owner.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public void applyModifiers(LivingEntity owner) {
        double newSpeed = SPEED;
        double maxSpeed = ConfigHolder.SERVER.playerMaxSpeed.get().floatValue();

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getNature() == CursedEnergyNature.DIVERGENT) {
            newSpeed*=1.15;
        }

        float ratio = cap.getEnergy()/cap.getMaxEnergy();

       


        EntityUtil.applyModifier(owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), PROJECTION_STEP_HEIGHT_UUID, "Step height addition", 2.0F, AttributeModifier.Operation.ADDITION);
        EntityUtil.applyArmorBoost(owner);
        // if (!(cap.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get()))) {
            //cap.setShieldTicks(0);
       // } else {
           // cap.setShieldTicks(cap.getShieldTicks() + 1);
        //}
        

         if ( !(cap.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get()) && owner instanceof Player player ) ) {
            // if (ratio <= 0.5) {
            //     newSpeed *= Math.min(0.85, (0.3 + (ratio * 2)));
            // }
             if (ratio <= 0.5 && ratio > 0.3) {
            newSpeed *= 0.85;
            maxSpeed *= 0.85;
        }
            if (ratio <= 0.3 && ratio > 0.15) {
                newSpeed *=0.75;
                maxSpeed *= 0.75;
            }

            if (ratio <= 0.15) {
                newSpeed *= 0.65;
                maxSpeed *= 0.65;
            }

            if (cap.getBurnout() > 0) {
                newSpeed *= 0.8;
                maxSpeed *= 0.8;
            }

                EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed",
                Math.min(maxSpeed, newSpeed * this.getPower(owner)), AttributeModifier.Operation.ADDITION);
            
            }
    }

    @Override
    public void removeModifiers(LivingEntity owner) {
        EntityUtil.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
        EntityUtil.removeModifier(owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), PROJECTION_STEP_HEIGHT_UUID);
        EntityUtil.removeArmorBoost(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return 0.1F;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean isDisplayed(LivingEntity owner) {
        return true;
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(0.0F, 2.0F);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
    }

    @Override
    public void onDisabled(LivingEntity owner) {
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getEnergy() == 0.0F ? Status.FAILURE : super.isStillUsable(owner);
    }

}
                }
            }
        }
    }
}
