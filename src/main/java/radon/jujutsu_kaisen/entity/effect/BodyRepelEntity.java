package radon.jujutsu_kaisen.entity.effect;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BodyRepelEntity extends Projectile implements GeoEntity {

    public static EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(BodyRepelEntity.class, EntityDataSerializers.INT);

    private static final double SPEED = 3D;
    private static final float DAMAGE = 8.0F;
    private static final float EXPLOSIVE_POWER = 2.5F;
    private static final float MAX_EXPLOSION = 10.0F;
    private static final int DURATION = 3 * 20;

    private static final int MAX_SEGMENTS = 24;
    private final BodyRepelSegmentEntity[] segments;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int souls;

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 1);
    }

    public BodyRepelEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.segments = new BodyRepelSegmentEntity[MAX_SEGMENTS];

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i] = new BodyRepelSegmentEntity(this,i);
        }
        this.setId(ENTITY_COUNTER.getAndAdd(this.segments.length + 1) + 1);
    }

    public BodyRepelEntity(LivingEntity pShooter, int souls) {
        this(JJKEntities.BODY_REPEL.get(), pShooter.level());

        this.setOwner(pShooter);

        this.souls = souls;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(pShooter);
        this.moveTo(pShooter.getEyePosition());
        EntityUtil.offset(this, pShooter.getLookAngle(), new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2), pShooter.getZ()).add(look));
        this.setYRot(pShooter.getYHeadRot());
        double speed = SPEED * (1+ (1.5 * this.souls) /10);
        this.setDeltaMovement(look.scale(speed));
    }


    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("time", this.getTime());
        pCompound.putInt("souls", this.souls);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setTime(pCompound.getInt("time"));
        this.souls = pCompound.getInt("souls");
    }

    @Override
    public void setId(int id) {
        super.setId(id);

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].setId(id + i + 1);
        }
    }


    @Override
    public PartEntity<?> @NotNull [] getParts() {
        return this.segments;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);

        if (!this.level().isClientSide) {
            for (BodyRepelSegmentEntity seg : this.segments) {
                seg.kill();
            }
        }
    }

    private void moveSegments() {
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].tick();

            Entity leader = i == 0 ? this : this.segments[i - 1];
            Vec3 follow = i == 0 ? leader.position().add(0.0D, this.getBbHeight() - this.segments[i].getBbHeight(), 0.0D)
                    .add(this.getDeltaMovement()) : leader.position();

            Vec3 diff = new Vec3(this.segments[i].getX() - follow.x, this.segments[i].getY() - follow.y, this.segments[i].getZ() - follow.z)
                    .normalize();

            double f = (leader.getBbWidth() / 2) + (this.segments[i].getBbWidth() / 2);

            double destX = follow.x + f * diff.x;
            double destY = follow.y + f * diff.y;
            double destZ = follow.z + f * diff.z;

            this.segments[i].setPos(destX, destY, destZ);

            double d0 = diff.horizontalDistance();
            this.segments[i].setRot((float) (Math.atan2(diff.z, diff.x) * 180.0D / Math.PI) + 90.0F, (float) (Math.atan2(diff.y, d0) * 180.0D / Math.PI));
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return this.segments != null;
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        Entity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            if (this.getTime() >= DURATION) {
                this.discard();
                return;
            }

            this.moveSegments();

            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

            if (hit.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hit)) {
                this.onHit(hit);
            }

            this.checkInsideBlocks();

            Vec3 movement = this.getDeltaMovement();
            double d0 = this.getX() + movement.x;
            double d1 = this.getY() + movement.y;
            double d2 = this.getZ() + movement.z;
            this.setPos(d0, d1, d2);
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;

        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    public float getRealDamage() {

        ISorcererData ownerCap = this.getOwner().getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return DAMAGE*(1f+(this.souls/10f))* SorcererUtil.getPower(ownerCap.getExperience());
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (entity == owner) return;
        // direct hit damage here and in OnHitBlock
        entity.hurt(this.damageSources().thrown(this, owner), this.getRealDamage());
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        Vec3 location = pResult.getLocation();
        // damage on explosion here
        // this might stack and hit multiple times with on hit entity IDK
        // the damage on the explosion was 1 before so it was entirely unscaled and just to do terrain damage
        ExplosionHandler.spawn(this.level().dimension(), location, Math.min(MAX_EXPLOSION, EXPLOSIVE_POWER * this.souls),
                20, this.getRealDamage()*0.5f, owner, this.damageSources().explosion(this, owner), false);

        this.discard();
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(),
                this.getXRot(), this.getYRot(), this.getType(), i, this.getDeltaMovement(), 0.0D);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        this.moveTo(pPacket.getX(), pPacket.getY(), pPacket.getZ(), pPacket.getYRot(), pPacket.getXRot());
        this.setDeltaMovement(pPacket.getXa(), pPacket.getYa(), pPacket.getZa());
    }
}