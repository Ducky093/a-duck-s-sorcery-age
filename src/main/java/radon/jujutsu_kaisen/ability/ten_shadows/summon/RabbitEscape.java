package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.network.PacketDistributor;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;

import java.util.List;

public class RabbitEscape extends Summon<RabbitEscapeEntity> {
    public RabbitEscape() {
        super(RabbitEscapeEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.tickCount - owner.getLastHurtByMobTimestamp() < 20;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.RABBIT_ESCAPE.get());
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);
         if (!(owner.level() instanceof ServerLevel level)) return;
        owner.addEffect(new MobEffectInstance(JJKEffects.INVISIBILITY.get(), 10 * 10, 0, false, false, false));
       // MobEffectInstance instance = new MobEffectInstance(JJKEffects.INVISIBILITY.get(), 12 * 10, 0, false, false, true);
        // owner.addEffect(new MobEffectInstance(JJKEffects.INVISIBILITY.get(), 120, 0, false, false, false));
       // owner.addEffect(instance);
       //  if (!owner.level().isClientSide) {
       //     PacketDistributor.TRACKING_ENTITY.with(() -> owner).send(new ClientboundUpdateMobEffectPacket(owner.getId(), instance));
      //  }

    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    @Override
    protected RabbitEscapeEntity summon(LivingEntity owner) {
        return new RabbitEscapeEntity(owner, this.isTamed(owner));
    }

    @Override
    public boolean canDie() {
        return true;
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    protected boolean canTame() {
        return true;
    }

    @Override
    public boolean canDisable() {
        return false;
    }

     @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 0.1F : 10.0F;
    }
}
