package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.IDomain;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Veil extends Ability implements Ability.IToggled {
    public static final double RANGE = 16.0D;
    private static final int RADIUS = 64;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
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
        if (!(owner.level() instanceof ServerLevel level)) return;

        for (ServerPlayer player : level.players()) {
            if (owner instanceof Mob && player.distanceTo(owner) > owner.getAttributeValue(Attributes.FOLLOW_RANGE))
                continue;
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.veil", JujutsuKaisen.MOD_ID), owner.getName().getString()));
        }

        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();

            if (owner.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                if (be.getOwnerUUID() != null && be.getOwnerUUID().equals(owner.getUUID())) {
                    VeilEntity veil = new VeilEntity(owner, pos.getCenter(), be.getSize(), be.getModifiers(), pos);
                    owner.level().addFreshEntity(veil);
                    return;
                }
            }
             Vec3 start = owner.getEyePosition();
            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            Vec3 end = start.add(look.scale(RANGE));
            HitResult result = RotationUtil.getHitResult(owner, start, end);

            Vec3 posV = result.getType() == HitResult.Type.MISS ? end : result.getLocation();

            VeilEntity veil = new VeilEntity(owner, posV, RADIUS, List.of(), pos );
            owner.level().addFreshEntity(veil);
        }

       
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.6F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public MenuType getMenuType() {
        return MenuType.DOMAIN;
    }

    @Override
    public boolean usesHands() {
        return false;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();
        return JJKAbilities.CURSED_ENERGY_FLOW.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(2.0F, 0.0F);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public int getPointsCost() {
        return 10;
    }
}
