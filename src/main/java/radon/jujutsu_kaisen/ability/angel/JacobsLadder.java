package radon.jujutsu_kaisen.ability.angel;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.effect.JacobsLadderEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class JacobsLadder extends Ability {
    public static final double RANGE = 50.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            if (!owner.canAttack(target)) return null;
            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return;
        }

        JacobsLadderEntity strike = new JacobsLadderEntity(owner, this.getPower(owner), target.position());
        owner.level().addFreshEntity(strike);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
       LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.J2TSU;
    }
}
