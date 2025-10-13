package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);

    private int total;
    private final Map<UUID, Vec3> positions = new HashMap<>();
    private final Map<BlockPos, Boolean> domainBlocks = new HashMap<>();

    public ClosedDomainExpansionEntity(EntityType<? > pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability, int radius) {
        this(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, ability, radius);
    }

   public ClosedDomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability, int radius) {
    super(pType, owner, ability);

    // Get direction but flatten Y so pitch never affects vertical placement
    Vec3 rawDir = RotationUtil.getTargetAdjustedLookAngle(owner);
    Vec3 horizontal = new Vec3(rawDir.x, 0.0D, rawDir.z).normalize();

    // Place domain origin at owner's feet, offset horizontally
    Vec3 base = owner.position().add(horizontal.scale(radius - OFFSET));

    // Apply the fixed downward offset for the base -> center relationship
    Vec3 finalPos = base.subtract(0.0D, radius, 0.0D);

    this.moveTo(
            finalPos.x, finalPos.y, finalPos.z,
            RotationUtil.getTargetAdjustedYRot(owner),
            RotationUtil.getTargetAdjustedXRot(owner)
    );

    this.entityData.set(DATA_RADIUS, radius);
}
    @Override
    public boolean shouldCollapse(float strength) {
        int radius = this.getRadius();
        boolean completed = this.getTime() >= radius * 2;
        return completed && super.shouldCollapse(strength);
    }

    @Override
    public AABB getBounds() {
        return this.getBoundingBox();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_RADIUS, 0);
    }

   @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(DATA_RADIUS, pCompound.getInt("radius"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("radius", this.getRadius());
    }

    public int getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

      @Override
    public boolean isInsideBarrier(BlockPos pos) {
        int radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < (radius - 1) * (radius - 1);
    }

    

    private void createBlock(int delay, BlockPos pos, int radius, double distance) {
        if (distance > radius) return;

        if (!this.level().isInWorldBounds(pos)) return;

        BlockState state = this.level().getBlockState(pos);

        if (state.is(Blocks.BEDROCK)) return;

        if (this.isRemoved()) return;

       

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

     BlockEntity existing = this.level().getBlockEntity(pos) ;
      CompoundTag saved = null;
         if (existing instanceof VeilBlockEntity be) {
                be.destroy();
                state = this.level().getBlockState(pos);
            } else if (existing instanceof DomainBlockEntity be) {
                BlockState original = be.getOriginal();
                state = original;
                saved = be.getSaved();
            }
            else if (existing != null) {
                saved = existing.saveWithFullMetadata();
            }
            
            DomainExpansion.IClosedDomain domain = ((DomainExpansion.IClosedDomain) this.ability);
            List<Block> blocks = domain.getBlocks();
            List<Block> fill = domain.getFillBlocks();
            List<Block> floor = domain.getFloorBlocks();
            List<Block> decoration = domain.getDecorationBlocks();

            Block block = null;

            // if (distance >= radius - 1) {
            //     block = JJKBlocks.DOMAIN.get();
            // } else if (!state.getFluidState().isEmpty()) {
            //     block = distance >= radius - 2 ? blocks.get(this.random.nextInt(blocks.size())) : JJKBlocks.DOMAIN_AIR.get();
            // } else {
            //     if (distance >= radius - 2) {
            //         block = blocks.get(this.random.nextInt(blocks.size()));
            //     } else if (!state.isAir()) {
            //         if (!floor.isEmpty() && domain.canPlaceFloor(this, pos)) {
            //             block = floor.get(this.random.nextInt(floor.size()));
            //         } else {
            //             block = fill.get(this.random.nextInt(fill.size()));
            //         }
            //     } else {
            //         if (!decoration.isEmpty() && domain.canPlaceDecoration(this, pos)) {
            //             block = decoration.get(this.random.nextInt(decoration.size()));
            //         }
            //     }
            // }
            BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
            if (distance >= radius - 1) {
                 block = JJKBlocks.DOMAIN.get();
            } else {
                if (distance >= radius - 2) {
                    block = blocks.get(this.random.nextInt(blocks.size()));
                } else if (pos.getY() < center.getY()) {
                    block = floor.isEmpty() ? fill.get(this.random.nextInt(fill.size())) : floor.get(this.random.nextInt(floor.size()));
                } else if (!decoration.isEmpty() && pos.getY() == center.getY()) {
                    block = decoration.get(this.random.nextInt(decoration.size()));
                } else {
                    block = JJKBlocks.DOMAIN_AIR.get();
                }
            }
            
             if (existing instanceof DomainBlockEntity be) {
                 UUID identifier = be.getIdentifier();

                 if (identifier != null && ((ServerLevel) this.level()).getEntity(identifier) instanceof DomainExpansionEntity) {
                     if (block == JJKBlocks.DOMAIN_AIR.get()) return;
                 }
             }


             owner.level().removeBlockEntity(pos);
            //if (block == null) return;

              boolean success = owner.level().setBlock(pos, block.defaultBlockState(),
                Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
            if (success) {
                this.domainBlocks.put(pos.immutable(), true);
            }
            // boolean success = owner.level().setBlock(pos, block.defaultBlockState(),
            //         Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);

            if (distance >= radius - 1 && success) this.total++;

            if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
                be.create(this.uuid, delay, state, saved);
            }
        }
    
    private void applyPreStun() {
        if (this.level().isClientSide) return;

        LivingEntity owner = this.getOwner();
        if (owner == null) return;
         for (LivingEntity entity : this.getAffected()) {
      
            entity.addEffect(new MobEffectInstance(
                JJKEffects.DOMAINSTUN.get(),
                30,
                1,
                false,
                false,
                false
            ));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30,
                6, false, false, false));
         }


      

        
    }

    private void createBarrier(boolean instant) {
        this.total = 0;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        int radius = this.getRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        Vec3 direction = this.getLookAngle();
        Vec3 behind = this.position().subtract(direction.scale(radius - OFFSET)).add(0.0D, radius, 0.0D);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance > radius) continue;

                    BlockPos pos = center.offset(x, y, z);

                    int delay = (int) Math.round(pos.getCenter().distanceTo(behind)) / 2 + 1;

                    ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (instant) {
                        this.createBlock(radius - delay, pos, radius, distance);
                    } else {
                        cap.delayTickEvent(() -> this.createBlock(radius - delay, pos, radius, distance), delay);
                    }
                }
            }
        }
    }

    private void doSureHitEffect(@NotNull LivingEntity owner) {
        for (LivingEntity entity : this.getAffected()) {
            if (JJKAbilities.hasTrait(entity, Trait.HEAVENLY_RESTRICTION)) {
                this.ability.onHitBlock(this, owner, entity.blockPosition());
            } else {
                this.ability.onHitEntity(this, owner, entity, false);
            }
        }

      // int radius = this.getRadius();
     // BlockPos center = this.blockPosition().offset(0, radius / 2, 0);

        // for (int x = -radius; x <= radius; x++) {
        //     for (int y = -radius; y <= radius; y++) {
        //         for (int z = -radius; z <= radius; z++) {
        //             double distance = Math.sqrt(x * x + y * y + z * z);

        //             if (distance < radius - 1) {
        //                 BlockPos pos = center.offset(x, y, z);
        //                 this.ability.onHitBlock(this, owner, pos);
        //             }
        //         }
        //     }
        // }
    }

    @Override
    public boolean checkSureHitEffect() {
        int radius = this.getRadius();
        boolean completed = this.getTime() >= radius * 2;

        if (!completed) return false;

        Set<DomainExpansionEntity> domains = VeilHandler.getDomains((ServerLevel) this.level(), this.getBounds());

        for (DomainExpansionEntity domain : domains) {
            if (domain == this) continue;

            if (this.shouldCollapse(domain.getStrength())) {
                this.discard();
            }
            return false;
        }
        return true;
    }

        @Override
        public void onRemovedFromWorld() {
            super.onRemovedFromWorld();

            if (!this.level().isClientSide) {
                LivingEntity owner = this.getOwner();

                if (owner != null) {
                    owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        cap.setBurnout(DomainExpansion.BURNOUT);
                        cap.resetSpeedStacks();

                        if (owner instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                        }
                    });
                }
            }
        }

        
    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.refreshDimensions();
    }

    private void check() {
        //int radius = this.getRadius();

        //Vec3 behind = this.position().add(0.0D, radius, 0.0D);
        //BlockPos center = BlockPos.containing(behind);

        int count = 0;

        for (Map.Entry<BlockPos, Boolean> entry : this.domainBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockEntity be = this.level().getBlockEntity(pos);
            if (be instanceof DomainBlockEntity domainBe && this.getUUID().equals(domainBe.getIdentifier())) {
                count++;
                this.domainBlocks.put(pos, true);
            } else {
                this.domainBlocks.put(pos, false);
            }
        }
        // for (int x = -radius; x <= radius; x++) {
        //     for (int y = -radius; y <= radius; y++) {
        //         for (int z = -radius; z <= radius; z++) {
        //             double distance = Math.sqrt(x * x + y * y + z * z);

        //             if (distance < radius && distance >= radius - 1) {
        //                 BlockPos pos = center.offset(x, y, z);

        //                 if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be && be.getIdentifier() != null && be.getIdentifier().equals(this.getUUID())) count++;
        //             }
        //         }
        //     }
        // }
         float ratio = (float) count / total;
        if (ratio < 0.75F) {
            this.discard();
        }
        // if ((float) count / this.total < 0.75F) {
        //     this.discard();
        // }
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        Set<DomainExpansionEntity> domains = VeilHandler.getDomains((ServerLevel) this.level(), this.getBounds());

        for (DomainExpansionEntity domain : domains) {
            if (domain == this || !(domain instanceof ClosedDomainExpansionEntity closed)) continue;

            closed.createBarrier(true);
        }

        
        for (Map.Entry<UUID, Vec3> entry : this.positions.entrySet()) {
            UUID identifier = entry.getKey();

            Entity entity = ((ServerLevel) this.level()).getEntity(identifier);

            if (entity == null) continue;

            if (!this.isInsideBarrier(entity.blockPosition())) continue;

            Vec3 pos = entry.getValue();

            entity.teleportTo(pos.x, pos.y, pos.z);
        }
        this.domainBlocks.clear();
        this.positions.clear();
    }

    
    @Override
    public void tick() {
        super.tick();

        //this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (this.level().isClientSide) return;

        int radius = this.getRadius();
        boolean completed = this.getTime() >= radius * 2;

        // if (this.getTime() <= radius * 2) {
        //     BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        //     for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(),
        //             entity -> this.isInsideBarrier(entity.blockPosition()))) {
        //         if (!this.positions.containsKey(entity.getUUID())) {
        //             this.positions.put(entity.getUUID(), entity.position());
        //         }
        //         if (entity.getY() < center.getY()) {
        //             entity.teleportTo(entity.getX(), center.getY(), entity.getZ());
        //         }
        //     }
        // }

        if (this.checkSureHitEffect()) {
            this.doSureHitEffect(owner);
        }

        if (completed) {
            if (this.getTime() % 20 == 0) {
                this.check();
            }
        }

        ParticleOptions particle = ((DomainExpansion.IClosedDomain) this.ability).getEnvironmentParticle();

        if (particle != null) {
            AABB bounds = this.getBounds();

            for (BlockPos pos : BlockPos.randomBetweenClosed(this.random, 16, (int) bounds.minX, (int) bounds.minY, (int) bounds.minZ,
                    (int) bounds.maxX, (int) bounds.maxY, (int) bounds.maxZ)) {
                if (!this.isInsideBarrier(pos)) continue;
                Vec3 center = pos.getCenter();
                ((ServerLevel) this.level()).sendParticles(particle, center.x, center.y, center.z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        if (this.getTime() - 1 == 0) {
            this.applyPreStun();
            this.createBarrier(false);
        } else if (completed && !this.isInsideBarrier(owner.blockPosition())) {
            this.discard();
        }
    }
}
