package radon.jujutsu_kaisen.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.entity.IBarrier;
import radon.jujutsu_kaisen.block.entity.IDomain;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainBarrierEntity;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class DomainExpansionEntity extends Entity implements IDomain {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(DomainExpansionEntity.class, EntityDataSerializers.INT);

    public static final int OFFSET = 5;
    public static final int INITIAL_COST = 1000;
    public boolean first;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public DomainExpansion ability;

    //protected boolean first = true;

    //private float scale;
    protected boolean instant;
    protected boolean sureHitToggled;
    protected boolean shellBalance;
    protected boolean sureHitAllies;

    protected DomainExpansionEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability) {
        super(pType, owner.level());

        this.setOwner(owner);
        this.instant = false;
        this.first = false;
        this.ability = ability;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);

        if (cap == null) return;
        this.shellBalance = cap.getShellBalance();
        this.sureHitToggled = cap.getToggleSureHit();
        this.sureHitAllies = cap.getAlliedSureHit();
        //     this.scale = cap.getDomainSize();
    }

    //  public boolean checkVeil(BlockPos pos, LivingEntity domainOwner) {
    //         BlockEntity be = this.level().getBlockEntity(pos);
    //         if (!(be instanceof VeilBlockEntity veilBe)) return false;

    //         BlockPos parentPos = veilBe.getParent();
    //         if (parentPos == null) return false;

    //         BlockEntity parentBE = this.level().getBlockEntity(parentPos);
    //         if (!(parentBE instanceof VeilRodBlockEntity rodBE)) return false;
          

    //         ISorcererData domainCap = domainOwner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            

    //         if (domainCap == null) return false;

  
    //         if (domainCap.getExperience() >= rodBE.getExperience() ) {
    //             this.level().destroyBlock(rodBE.getBlockPos(), true);
    //             return true;
    //         }
    //         return false;
    // }
    
    @Override
    public boolean ignoreExplosion() {
        return true;
    }

//     protected ClosedDomainExpansionEntity getClosestBarrierAny(AABB pos) {
//         Set<IDomain> domains = VeilHandler.getDomains((ServerLevel) this.level(), pos);
// //get domains not functioning
//         ClosedDomainExpansionEntity closest = null;
//         double closestDistSqr = Double.MAX_VALUE;

//         Vec3 selfPos = this.position(); 
//       System.out.println("looapy");
//         for (IDomain domain : domains) {
//             System.out.println("loopy");
//         //     if (domain.getDomain() instanceof ClosedDomainExpansionEntity barrier) {
//         //     if (barrier != null) {
//         //     System.out.println(barrier.primary);
//         //     }
//         // }
//             if (domain.getDomain() instanceof ClosedDomainExpansionEntity barrier ) {
//                 double distSqr = selfPos.distanceToSqr(barrier.position());
//                 if (distSqr < closestDistSqr) {
//                     closestDistSqr = distSqr;
//                     closest = barrier;
//                 }
//             }
//         }

//         return closest;
//     }

    protected DomainBarrierEntity getClosestDomainBarrier(BlockPos pos) {
        Set<IBarrier> domains = VeilHandler.getBarriers((ServerLevel) this.level(), pos);

        DomainBarrierEntity closest = null;
        double closestDistSqr = Double.MAX_VALUE;

        Vec3 selfPos = this.position(); 

        for (IBarrier domain : domains) {
            if (domain instanceof DomainBarrierEntity barrier) {
                double distSqr = selfPos.distanceToSqr(barrier.position());
                if (distSqr < closestDistSqr) {
                    closestDistSqr = distSqr;
                    closest = barrier;
                }
            }
        }

        return closest;
    }


    protected boolean isOccupiedByBarrier(BlockPos pos) {
        Set<IDomain> domains = VeilHandler.getDomains((ServerLevel) this.level(), pos);
        for (IDomain domain : domains) {
            if (domain.getDomain() instanceof ClosedDomainExpansionEntity barrier && domain != this ) return true;
        }
        return false;
    }


        
   
    
    // @Override
    // public void onAddedToWorld() {
    //     super.onAddedToWorld();

    //     if (!this.level().isClientSide) {
    //         VeilHandler.addDomain(this.level().dimension(), this.getUUID());

    //     //     LivingEntity owner = this.getOwner();

    //     // if (owner == null) return;

    //     // for (LivingEntity entity : this.getAffected()) {
    //     //     MinecraftForge.EVENT_BUS.post(new LivingHitByDomainEvent(entity, this.ability, owner));
    //     // }


    //     }
    // }

    // @Override
    // public void onAddedToWorld() {
    //     super.onAddedToWorld();

    //     if (this.level().isClientSide) return;

    //     VeilHandler.barrier(this.level().dimension(), this.getUUID());

    //     LivingEntity owner = this.getOwner();

    //     if (owner == null) return;

    //     for (LivingEntity entity : this.getInside()) {
    //         MinecraftForge.EVENT_BUS.post(new LivingHitByDomainEvent(entity, this.ability, owner));
    //     }
    // }

    
    @Override
    public void doSureHitEffect() {
        LivingEntity owner = this.getOwner();
        for (LivingEntity entity : this.getAffected()) {
            if (JJKAbilities.hasTrait(entity, Trait.HEAVENLY_RESTRICTION)) {
                this.ability.onHitBlock(this, owner, entity.blockPosition());
            } else {
                this.ability.onHitEntity(this, owner, entity, false);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }


    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public boolean isInstant() {
        return this.instant;
    }

    public void setInstant(boolean yn) {
        this.instant = true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putString("ability", JJKAbilities.getKey(this.ability).toString());
        pCompound.putBoolean("first", this.first);
        pCompound.putInt("time", this.getTime());
        pCompound.putBoolean("instant", this.instant);
        //pCompound.putFloat("scale", this.scale);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.ability = (DomainExpansion) JJKAbilities.getValue(new ResourceLocation(pCompound.getString("ability")));
        this.first = pCompound.getBoolean("first");
        this.setTime(pCompound.getInt("time"));
            this.instant = pCompound.getBoolean("instant");
        //this.scale = pCompound.getFloat("scale");
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    public List<LivingEntity> getAffected() {
        return this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(), this::isAffected);
    }

    // public List<LivingEntity> getInside() {
    //     return this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(),
    //             entity -> this.isInsideBarrier(entity.blockPosition())
    //     ); //restructure the interfaces
    // }

    public boolean hasSureHitEffect() {
        return true;
    }

    public abstract DomainExpansionEntity checkSureHitEffect();

    public Ability getAbility() {
        return this.ability;
    }

    public DomainExpansion getDomainAbility() {
        return this.ability;
    }
    
    

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    public DomainExpansionEntity getDomain() {
        return this;
    }

    @Nullable
    public DomainExpansionCenterEntity getDomainCenter() {
        List<DomainExpansionCenterEntity> collisions = this.level().getEntitiesOfClass(DomainExpansionCenterEntity.class, this.getBounds());

        for (DomainExpansionCenterEntity collision : collisions) {
            if (collision.getDomain() == this) {
                return collision;
            }
        }
        return null;
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

   // public abstract AABB getBounds();

    public abstract boolean isInsideBarrier(BlockPos pos);


    public boolean getSureHitToggled() {
        return this.sureHitToggled;
    }

    public boolean getAlliedSureHit() {
        return this.sureHitAllies;
    }

    public boolean getShellBalance() {
        return this.shellBalance;
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        LivingEntity owner = this.getOwner();
        if (!this.level().isClientSide && owner != null && ( !(owner instanceof Player player) || !player.getAbilities().instabuild) && this.getTime() == 1 &&  !this.instant ) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            float cost = this.getAbility().getRealCost(owner, INITIAL_COST);
            //if (this.instant) cost /= 4;
            if (cap == null || cap.getEnergy() - cost < 0 ) {
                this.discard();
                return;
            }
            else {
                cap.useEnergy(cost);
                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            }
        }
        

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() )) {
            this.discard();
            return;
        }
        super.tick();
    }

    public boolean isAffected(BlockPos pos) {
        return this.isInsideBarrier(pos);
    }

    public boolean isAffected(LivingEntity victim) {
        LivingEntity owner = this.getOwner();

        if (owner == null || victim == owner) {
            return false;
        }

        if (victim instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == owner) return false;

        if (!owner.canAttack(victim)) return false;

        if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ITenShadowsData victimTenShadowsCap = victim.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElse(null);
            ISorcererData victimSorcererCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if ((victimTenShadowsCap != null && victim instanceof MahoragaEntity && victimTenShadowsCap.isAdaptedTo(this.ability))) return false;

            if (victimSorcererCap.hasToggled(JJKAbilities.HOLLOW_WICKER_BASKET.get())) {
                return false;       
            }

            if (victimSorcererCap.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
                SimpleDomainEntity simple = victimSorcererCap.getSummonByClass(SimpleDomainEntity.class);

                if (simple != null) {
                    ISorcererData ownerSorcererCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    simple.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, this.ability), ownerSorcererCap.getAbilityPower() * 10.0F);
                    return false;
                }
            }

            for (SimpleDomainEntity simple : this.level().getEntitiesOfClass(SimpleDomainEntity.class, AABB.ofSize(victim.position(), SimpleDomainEntity.MAX_RADIUS * 3, SimpleDomainEntity.MAX_RADIUS * 3, SimpleDomainEntity.MAX_RADIUS * 3))) {
                //if (victim.distanceTo(simple) < simple.getRadius()) return false;
                if (simple.isInsideBarrier(victim.blockPosition())) return false;
            }
        }
        return this.isAffected(victim.blockPosition());
    }

    
    protected boolean isReadyToCollapse() {
        return true;
    }


    public boolean shouldCollapse(float strength) {
        return this.isReadyToCollapse() && (strength / this.getStrength()) > 2.5F;
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
    // public float getStrength() {
    //     LivingEntity owner = this.getOwner();
    //     if (owner == null) return 0.0F;
    //     ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
    //     float domainMod = 1.0F;
    //     if (JJKAbilities.CHIMERA_SHADOW_GARDEN.get() == this.ability ) {
    //         domainMod *= 0.66;
    //     }
    //     return (cap.getAbilityPower() * (owner.getHealth() / owner.getMaxHealth())) * domainMod ;
    // }
    
    // @Override
    // public float getStrength() {
    //     return this.getStrength();
    // }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity owner = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
        }
    }
}


