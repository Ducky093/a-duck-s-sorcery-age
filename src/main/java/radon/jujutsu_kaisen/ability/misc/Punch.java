package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RiderShieldingMount;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;

public class Punch extends Ability implements Ability.ICharged{
    private static final float DAMAGE = 7.5F;
    private static final double RANGE = 6.5D;
    private static final double LAUNCH_POWER = 2.5D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }


    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target) || owner.distanceTo(target) > RANGE) return false;
        return HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean isMelee() {
        return true;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner instanceof Player) || !owner.level().isClientSide) return;
        ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.charge", JujutsuKaisen.MOD_ID),
                Math.round(((float) Math.min(20, this.getCharge(owner)) / 20) * 100)), false);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        if (owner.hasEffect(JJKEffects.STUN.get())) {
            return Status.FAILURE;
        }
        return super.isStillUsable(owner);
    }

    @Override
    public boolean onRelease(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        float power = (float) Math.min(20, this.getCharge(owner)) / 20;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        float num = 3;
        if (power >= 0.25) {
            float mod = 1;
            num = 5;
            if (JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION)) {
                mod = 2.5f;
            }
            if (cap.getSpeedStacks() > 0) {
                mod = 1+0.15f*cap.getSpeedStacks();
            }
            if (mod != 1) {
                Vec3 look2 = look.scale(power*mod);
                owner.push(look2.x,look2.y,look2.z);
            }
        }


        List<String> targets = new ArrayList<String>();
        Level level1 = owner.level();
        for (int i = 0; i < num; i++) {
            cap.delayTickEvent(() -> {


                Vec3 offset = owner.getEyePosition().add(look.scale(RANGE / 2));

                for (LivingEntity entity : owner.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(offset, RANGE, RANGE, RANGE),
                        entity -> entity != owner )) {
                    //&& owner.hasLineOfSight(entity)
                    boolean found = false;
                    for (String target: targets) {
                        if (target == entity.getStringUUID()) {
                            found = true;
                        }
                    }
                    if (found) {
                        continue;
                    }
                    targets.add(entity.getStringUUID());
                    if (level1 instanceof ServerLevel) {
                        Vec3 center = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);
                        entity.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

                        ((ServerLevel) level1).sendParticles(ParticleTypes.FLASH, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                    }
                    if (owner instanceof Player player) {
                        player.attack(entity);
                    } else {
                        owner.doHurtTarget(entity);
                    }
                    entity.invulnerableTime = 0;
                    float newDMG;
                    newDMG = DAMAGE;
                    if (!(owner instanceof Player player)) {
                        newDMG/=1.65F;
                    }
                    float newPower = (float) (LAUNCH_POWER*(1+0.5*power));
                    newDMG *= (float) (1+0.5*power);
                    if (JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION)) {
                        if (entity.hurt(owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner), (newDMG * 1.45F) * this.getPower(owner))) {
                            entity.setDeltaMovement(look.scale(newPower * (1.0F + this.getPower(owner) * 0.1F) * 2.0F)
                                    .multiply(1.0D, 0.25D, 1.0D));
                        }
                    } else {
                        if (power == 1) {
                            cap.moreBlackFlash(true);
                            cap.delayTickEvent(() -> {
                                cap.moreBlackFlash(false);
                            }, 2);
                        }
                        if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), (newDMG) * this.getPower(owner))) {
                            entity.setDeltaMovement(look.scale(newPower * (1.0F + this.getPower(owner) * 0.1F))
                                    .multiply(1.0D, 0.25D, 1.0D));
                        }
                    }
                }
            }, i*1);
        }

        if ((owner.level() instanceof ServerLevel level)) {
            for (int i = 0; i < 4; i++) {
                Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
                level.sendParticles(owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem ? ParticleTypes.SWEEP_ATTACK : ParticleTypes.CLOUD,
                        pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        0, 0.0D, 0.0D, 0.0D, 1.0D);
            }
            for (int i = 0; i < 4; i++) {
                Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
                level.sendParticles(ParticleTypes.CRIT,
                        pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        0, 0.0D, 0.0D, 0.0D, 1.0D);
            }

            Vec3 pos = owner.getEyePosition().add(look);
            owner.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 0.3F);

        }



        return true;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        return owner.isUsingItem() ? Status.FAILURE : super.isTriggerable(owner);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return (!(owner instanceof ISorcerer sorcerer) || sorcerer.hasMeleeAttack() && sorcerer.hasArms()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 15.0F;
    }

    @Override
    public int getCooldown() {
        return 15;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }
}
