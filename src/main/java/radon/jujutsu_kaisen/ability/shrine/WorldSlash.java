package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

import org.jetbrains.annotations.Nullable;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.entity.projectile.WorldSlashProjectile;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WorldSlash extends Ability {
    public static final float SPEED = 5.0F;
    private static final double RANGE = 64.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return true;
    }

     @Override
    public boolean isChantable() {
        return true;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;

        if (owner instanceof MahoragaEntity) return HelperMethods.RANDOM.nextInt(20) == 0;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        return cap.getType() == JujutsuType.CURSE || JJKAbilities.RCT1.get().isUnlocked(owner) ? owner.getHealth() / owner.getMaxHealth() < 0.9F :
                owner.getHealth() / owner.getMaxHealth() < 0.8F || target.getHealth() > owner.getHealth() * 2;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
      ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        // if (!(owner instanceof MahoragaEntity) && (!(owner instanceof Player player) || !player.getAbilities().instabuild)  ) {

        //     if (!cap.hasTechnique(CursedTechnique.SHRINE)) return false;
        // }
        //  if (!cap.hasTechnique(CursedTechnique.SHRINE)) return false;
        if (!(owner instanceof MahoragaEntity) && (!cap.hasTechnique(CursedTechnique.SHRINE))) {
            return false;
        }
        else {
             return super.isValid(owner);
        }
    }
     @Override
    public boolean isUnlockable() {
        return true;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (owner instanceof MahoragaEntity ) {
            ITenShadowsData cap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.getAdaptation(JJKAbilities.INFINITY.get()) > 1) return true;
        }
        else if (((owner instanceof Player player) && player.getAbilities().instabuild)   ) {
            return true;
        }
        return super.isUnlocked(owner);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
    
        WorldSlashProjectile slash = new WorldSlashProjectile(owner, cap.getOutput(), (owner.isShiftKeyDown() ? 90.0F : 0.0F) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F);
        slash.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
        owner.level().addFreshEntity(slash);
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }


    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.J2TSU;
    }

     @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            Ability ability = event.getAbility();

            if (ability != JJKAbilities.WORLD_SLASH.get()) return;

            LivingEntity owner = event.getEntity();

            for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE))) {
                if (!(entity instanceof LivingEntity living) || !living.hasLineOfSight(owner)) continue;

                 ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();


                if (JJKAbilities.WORLD_SLASH.get().isUnlocked(owner)) continue;

                cap.unlock(JJKAbilities.WORLD_SLASH.get());

                if (entity instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            }
        }
    }
}
