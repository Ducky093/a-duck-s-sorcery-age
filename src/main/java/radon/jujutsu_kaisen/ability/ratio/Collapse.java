package radon.jujutsu_kaisen.ability.ratio;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import radon.jujutsu_kaisen.ExplosionHandler;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import net.minecraft.core.particles.ParticleTypes;
import radon.jujutsu_kaisen.util.ParticleUtil;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Collapse extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final int RANGE = 10;
    private static final int DELAY = 20;
    private static final float DAMAGE = 15.0F;
    private static final int DURATION = 24;
    private static final float EXPLOSIVE_POWER = 3.0F;
    private static final float MAX_EXPLOSIVE_POWER = 10.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target)) return false;

        if (JJKAbilities.isChanneling(owner, this)) {
            return HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return HelperMethods.RANDOM.nextInt(3) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    /*private @Nullable BlockHitResult getBlockHit(LivingEntity owner) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = RotationUtil.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) result;
        } else if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            Vec3 offset = entity.position().subtract(0.0D, 5.0D, 0.0D);
            return owner.level().clip(new ClipContext(entity.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        }
        return null;
    }*/


    @Override
    public void run(LivingEntity owner) {
            if (owner.level().isClientSide) return;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();


            Vec3 pos = owner.position();
            int index = this.getCharge(owner);

            if (index == 16) {
                if (owner instanceof ServerPlayer player) {
                    owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.COLLAPSE.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    //owner.level().sendParticles(ParticleTypes.SONIC_BOOM, pos.X, pos.Y, pos.Z, 0, 0D, 0.0D, 0.0D, 1.0D);
                }
            }
        }


    @Override
    public void onStop(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        Vec3 start = owner.getEyePosition();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = RotationUtil.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            int index = this.getCharge(owner);

            Vec3 realpos = result.getLocation();

            if (index >= 20 && index < DURATION) {
                ExplosionHandler.spawn(owner.level().dimension(), realpos, Math.min(MAX_EXPLOSIVE_POWER * 1.5F, ((EXPLOSIVE_POWER) * (this.getPower(owner))) * 1.5F),
                        20, (DAMAGE * this.getPower(owner)) * 0.25F, owner, JJKDamageSources.indirectJujutsuAttack(owner, owner, JJKAbilities.COLLAPSE.get()), false);
                System.out.println(this.getPower(owner));
            } else if (index < 24) {
                ExplosionHandler.spawn(owner.level().dimension(), realpos, Math.min(MAX_EXPLOSIVE_POWER * 0.5F, ((EXPLOSIVE_POWER) * (this.getPower(owner))) * 0.5F),
                        20, (DAMAGE * this.getPower(owner)) * 0.125F, owner, JJKDamageSources.indirectJujutsuAttack(owner, owner, JJKAbilities.COLLAPSE.get()), false);
            }
        }
    }


    @Override
    public Status isTriggerable(LivingEntity owner) {
       // BlockHitResult hit = this.getBlockHit(owner);

        //if (hit == null) {
            //return Status.FAILURE;
      //  }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.J2TSU;
    }
}
