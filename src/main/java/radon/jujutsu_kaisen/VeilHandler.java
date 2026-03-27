package radon.jujutsu_kaisen;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.IBarrier;
import radon.jujutsu_kaisen.block.entity.IDomain;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.block.entity.IOpenDomainBarrier;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainBarrierEntity;
import radon.jujutsu_kaisen.block.entity.IVeil;
import radon.jujutsu_kaisen.entity.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.PlayerModifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeilHandler {

    // private static final Map<ResourceKey<Level>, Set<VeilRodBlockEntity>> veilsByDimension = new HashMap<>();
    // private static final Map<ResourceKey<Level>, Set<UUID>> domains = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> barriers = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Map<UUID, IBarrier>> cache = new ConcurrentHashMap<>();
    //private final Map<BlockPos, CompoundTag> placedBlocks = new HashMap<>();

    public static void barrier(ResourceKey<Level> dimension, UUID identifier) {
        barriers.computeIfAbsent(dimension, ignored -> new HashSet<>()).add(identifier);
    }

    @Nullable
    private static IBarrier get(ServerLevel level, UUID identifier) {
        ResourceKey<Level> dimension = level.dimension();

        Map<UUID, IBarrier> slot = cache.computeIfAbsent(dimension, k -> new HashMap<>());

        IBarrier cached = slot.get(identifier);

        if (cached instanceof Entity entity && entity.isAlive()) {
            return cached;
        }

        Entity entity = level.getEntity(identifier);

        if (entity instanceof IBarrier barrier) {
            slot.put(identifier, barrier);
            return barrier;
        }

        slot.remove(identifier);
        return null;
    }

     public static Set<IBarrier> getBarriers(ServerLevel level, BlockPos target) {
        Set<IBarrier> result = new HashSet<>();

        if (!barriers.containsKey(level.dimension())) return result;
                  //System.out.println("domain looped -2");
        for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            //System.out.println(barrier == null);
           // System.out.println(!barrier.isInsideBarrier(target));
            //System.out.println(target);
            //  System.out.println("domain looped -1");
            if (barrier == null || !barrier.isInsideBarrier(target)) continue;
             // System.out.println("domain looped -0.5");
            result.add(barrier);
        }
        return result;
    }

    public static Set<IDomainBarrier> getDomainBarriers(ServerLevel level, AABB bounds) {
        Set<IDomainBarrier> result = new HashSet<>();

        if (!barriers.containsKey(level.dimension())) return result;

        for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier == null || !(barrier instanceof IDomainBarrier barrierEntity) || !bounds.intersects(barrierEntity.getBounds())) continue;
            result.add(barrierEntity);
        }
        return result;
    }


     public static Set<IDomainBarrier> getDomainBarriers(ServerLevel level, BlockPos target) {
        Set<IDomainBarrier> result = new HashSet<>();

        if (!barriers.containsKey(level.dimension())) return result;
                  //System.out.println("domain looped -2a");
        for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            //System.out.println(barrier == null);
              //System.out.println("domain looped -1");
            if (barrier == null ||  !(barrier instanceof IDomainBarrier barrierEntity) || !barrierEntity.isInsideBarrier(target)) continue;
            //  System.out.println("domain looped -0.5");
            result.add(barrierEntity);
        }
        return result;
    }
    

    public static Set<IDomainBarrier> getDomainShellBarriers(ServerLevel level, AABB bounds) {
        Set<IDomainBarrier> result = new HashSet<>();

        if (!barriers.containsKey(level.dimension())) return result;

        for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier == null || barrier instanceof IOpenDomainBarrier || !(barrier instanceof IDomainBarrier barrierEntity) || !bounds.intersects(barrierEntity.getBounds())) continue;
            result.add(barrierEntity);
        }
        return result;
    }


     public static Set<IDomainBarrier> getDomainShellBarriers(ServerLevel level, BlockPos target) {
        Set<IDomainBarrier> result = new HashSet<>();

        if (!barriers.containsKey(level.dimension())) return result;
                //  System.out.println("domain looped -2a");
        for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            //System.out.println(barrier == null);
             // System.out.println("domain looped -1");
            if (barrier == null || barrier instanceof IOpenDomainBarrier || !(barrier instanceof IDomainBarrier barrierEntity) || !barrierEntity.isInsideBarrier(target)) continue;
              //System.out.println("domain looped -0.5");
            result.add(barrierEntity);
        }
        return result;
    }
    

    public static Set<IBarrier> getBarriers(ServerLevel level, AABB bounds) {
        Set<IBarrier> result = new HashSet<>();

        if (!barriers.containsKey(level.dimension())) return result;

        for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier == null || !bounds.intersects(barrier.getBounds())) continue;
            result.add(barrier);
        }
        return result;
    }

      public static Set<IDomain> getDomains(ServerLevel level, BlockPos target) {
        Set<IDomain> result = new HashSet<>();
        //System.out.println("we getting domains");
        for (IBarrier barrier : getBarriers(level, target)) {
                 // System.out.println("domain looped 0");
            if (!(barrier instanceof IDomain domain)) continue;
                //  System.out.println("domain looped 1");
            result.add(domain);
        }
        return result;
    }

    

    public static Set<IDomain> getDomains(ServerLevel level, AABB bounds) {
        Set<IDomain> result = new HashSet<>();

        for (IBarrier barrier : getBarriers(level, bounds)) {
            if (!(barrier instanceof IDomain domain)) continue;

            result.add(domain);
        }
        return result;
    }


      public static Set<IVeil> getVeils(ServerLevel level, BlockPos target) {
        Set<IVeil> result = new HashSet<>();

        for (IBarrier barrier : getBarriers(level, target)) {
            if (!(barrier instanceof IVeil veil)) continue;

            result.add(veil);
        }
        return result;
    }

    public static Set<IVeil> getVeils(ServerLevel level, AABB bounds) {
        Set<IVeil> result = new HashSet<>();

        for (IBarrier barrier : getBarriers(level, bounds)) {
            if (!(barrier instanceof IVeil veil)) continue;

            result.add(veil);
        }
        return result;
    }

    
    @Nullable
    public static IBarrier getSurroundingBarrier(ServerLevel level, BlockPos target) {
        Set<IBarrier> all = getBarriers(level, target);

        if (all.isEmpty()) return null;

        IBarrier innermost = null;
        double bestDepth = -Double.MAX_VALUE;

        for (IBarrier barrier : all) {
            AABB bounds = barrier.getBounds();
            double dx = Math.min(target.getX() - bounds.minX, bounds.maxX - target.getX());
            double dy = Math.min(target.getY() - bounds.minY, bounds.maxY - target.getY());
            double dz = Math.min(target.getZ() - bounds.minZ, bounds.maxZ - target.getZ());

            double depth = Math.min(dx, Math.min(dy, dz));

            if (depth > bestDepth) {
                bestDepth = depth;
                innermost = barrier;
            }
        }

        return innermost;
    }




    public static boolean checkIntersect(Level level, BlockPos center, int radius) {
        if ( !(level instanceof ServerLevel servLevel)) return false;
    Set<IVeil> veils = getVeils(servLevel, center);
    if (veils == null) return false;

    for (IVeil veil : veils) {
        BlockPos otherPos = veil.getVeil().getCenter();

        if (otherPos.equals(center)) continue;

            int otherRadius = veil.getRadius();
            // double distSqr = center.distSqr(otherPos);
            // double minSqr = Math.pow(radius - otherRadius, 2);
            // double maxSqr = Math.pow(radius + otherRadius, 2);
            double dist = Math.sqrt(center.distSqr(otherPos));
            if (dist <= radius + otherRadius && dist >= Math.abs(radius - otherRadius)) {
                return true;
            }

            // if (distSqr <= maxSqr && distSqr >= minSqr) {
            //     return true;
            // }
        // Check if borders intersect
    }

    return false;
}

    // @Nullable
    // private static IBarrier getOwner(ServerLevel level, BlockPos target) {
    //     IBarrier strongest = null;
    //     boolean tie = false;

    //     if (!barriers.containsKey(level.dimension())) return null;

    //     for (UUID identifier : barriers.get(level.dimension())) {
    //         IBarrier current = get(level, identifier);

    //         if (current == null) continue;

    //         AABB bounds = current.getBounds();

    //         if (!bounds.contains(target.getCenter())) continue;

    //         if (!current.isBarrierOrInside(target)) continue;

    //         if (strongest == null) {
    //             strongest = current;
    //             continue;
    //         }

    //         if (strongest instanceof OpenDomainExpansionEntity && current instanceof ClosedDomainExpansionEntity closed) {
    //             if (closed.isBarrier(target)) continue;
    //         } else if (strongest instanceof ClosedDomainExpansionEntity closed && current instanceof OpenDomainExpansionEntity open) {
    //             if (closed.isBarrier(target) && open.isInsideBarrier(target)) {
    //                 strongest = current;
    //                 tie = false;
    //                 continue;
    //             }
    //         }

    //         float strengthA = current.getStrength();
    //         float strengthB = strongest.getStrength();

    //         if (strengthA > strengthB) {
    //             strongest = current;
    //             tie = false;
    //         } else if (strengthA == strengthB) {
    //             tie = true;
    //         }
    //     }

    //     return tie ? null : strongest;
    // }

    // public static boolean isOwnedBy(ServerLevel level, BlockPos target, IBarrier barrier) {
    //     return getOwner(level, target) == barrier;
    // }

    // public static boolean isOwnedNonDomain(ServerLevel level, BlockPos target) {
    //     return !(getOwner(level, target) instanceof IDomain);
    // }

    // public static boolean isInsideBarrier(ServerLevel level, BlockPos target) {
    //     return getOwner(level, target) != null;
    // }
  public static boolean isWhitelisted(@Nullable BlockPos parent, Entity entity) {
        if (entity == null || parent == null || !(entity.level().getBlockEntity(parent) instanceof VeilRodBlockEntity be)) return false;
        if (be.modifiers == null) return false;
        if (entity instanceof Player player) {
            for (Modifier modifier : be.modifiers) {
                if (modifier.getAction() == Modifier.Action.ALLOW &&
                    modifier.getType() == Modifier.Type.OWNER_BYPASS && (entity.getUUID().equals(be.ownerUUID)) ) {
                        return true;
                }
                if (modifier.getAction() != Modifier.Action.ALLOW && modifier.getType() != Modifier.Type.PLAYER)
                    continue;
                if (modifier.getType() == Modifier.Type.PLAYER && (((PlayerModifier)modifier).getUUID() == player.getUUID() || ((PlayerModifier) modifier).getName().equals(player.getDisplayName().getString()))) {
                    return modifier.getAction() == Modifier.Action.ALLOW;
                }
            }
        }

        for (Modifier modifier : be.modifiers) {
            if (modifier.getAction() == Modifier.Action.ALLOW && (modifier.getType() == Modifier.Type.CURSE || modifier.getType() == Modifier.Type.SORCERER)) {
                if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
                ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                return cap.getType() == JujutsuType.CURSE && modifier.getType() == Modifier.Type.CURSE ||
                        cap.getType() != JujutsuType.CURSE && modifier.getType() == Modifier.Type.SORCERER;
            }
        }
        return false;
    }

@Nullable
    public static VeilEntity getVeil(Level pLevel, VeilBlockEntity be) {
        if (((((ServerLevel) pLevel).getEntity(be.getParentUUID()) instanceof VeilEntity veil) && !veil.isRemoved() && veil.isAlive())) {
            return veil;
        }
        return null;
    }
    public static boolean canDestroy(LivingEntity entity, Level level, double x, double y, double z) {
        return canDestroy(entity, level, x, y, z, false);
    }
    public static boolean canDestroy(LivingEntity entity, Level level, double x, double y, double z, boolean manualBreak) {
        if ( !(level instanceof ServerLevel servLevel)) return false;
        BlockPos target = BlockPos.containing(x, y, z);
        Set<IVeil> veils = VeilHandler.getVeils(servLevel, target);
        if (veils == null) return true;
    

        for (IVeil veil : veils) {
            if (manualBreak && entity.level().getBlockState(target).is(JJKBlocks.VEIL_ROD.get())) return true;
            // if (manualBreak && target.equals(veil.getCenter())) {
            //     continue;
            // }
            int radius = veil.getRadius();
            //fix here java.lang.NullPointerException: Cannot invoke "net.minecraft.core.Vec3i.getX()" because "pVector" is null
            if (target.distSqr(veil.getVeil().getCenter() ) >= radius * radius) continue;
            boolean ownerFlag = false;
            boolean destroyFlag = true;
            for (Modifier modifier : veil.getModifiers()) {
                if (modifier.getAction() == Modifier.Action.ALLOW &&
                    modifier.getType() == Modifier.Type.OWNER_BYPASS) {
                    ownerFlag = true;
                }else if (modifier.getAction() == Modifier.Action.DENY &&
                    modifier.getType() == Modifier.Type.GRIEFING) {
                    destroyFlag = false;
                }
            }
            VeilEntity veilObj = veil.getVeil();
         if (ownerFlag == true && entity != null && veilObj.getUUID() != null && veilObj.getUUID().equals(entity.getUUID()) ) continue;
                return destroyFlag;
        }
        return true;
    }

    public static boolean canSpawn(ServerLevel level, Mob mob, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);
        IBarrier owner = getSurroundingBarrier(level, target);

        if (owner instanceof IVeil veil)  {
            boolean curseSpawnFlag = false;
            boolean sorcererSpawnFlag = false;
            for (Modifier modifier : veil.getModifiers()) {
            if (modifier.getAction() == Modifier.Action.ALLOW &&
                modifier.getType() == Modifier.Type.CURSE_SPAWN) {
                curseSpawnFlag = true;
            } else if (modifier.getAction() == Modifier.Action.ALLOW &&
                modifier.getType() == Modifier.Type.SORCERER_SPAWN) {
                sorcererSpawnFlag = true;
            }
            }
            //if (target.distSqr(rod.getBlockPos()) <= radius * radius)  {
            if ((sorcererSpawnFlag && mob instanceof SorcererEntity) || (curseSpawnFlag && mob instanceof CursedSpirit) )  {
                //continue;
            }
            else {
                return false;
            }
            
        }
        else if (owner instanceof IDomainBarrier) {
            return false;
        }

        
        return true;
    }

  
 
    // public static void addVeil(VeilRodBlockEntity rod) {
    //     ResourceKey<Level> dimension = rod.getLevel().dimension();
    //     veilsByDimension.computeIfAbsent(dimension, k -> new HashSet<>()).add(rod);
    // }

    // public static void removeVeil(VeilRodBlockEntity rod) {
    //     ResourceKey<Level> dimension = rod.getLevel().dimension();
    //     Set<VeilRodBlockEntity> rods = veilsByDimension.get(dimension);
    //     if (rods == null) return;

    //     rods.remove(rod);

    //     int radius = rod.getSize();
    //     BlockPos center = rod.getBlockPos();
    //     Level level = rod.getLevel();

    //     for (int x = -radius; x <= radius; x++) {
    //         for (int y = -radius; y <= radius; y++) {
    //             for (int z = -radius; z <= radius; z++) {
    //                 BlockPos pos = center.offset(x, y, z);
    //                 BlockEntity be = level.getBlockEntity(pos);

    //                 if (be instanceof VeilBlockEntity veil) {
    //                     BlockPos parent = veil.getParent();
    //                     if (parent != null && parent.equals(center)) {
    //                         veil.destroy();
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     if (rods.isEmpty()) veilsByDimension.remove(dimension);
    // }

    // public static void addDomain(ResourceKey<Level> dimension, UUID id) {
    //     domains.computeIfAbsent(dimension, k -> new HashSet<>()).add(id);
    // }

    // public static Set<DomainExpansionEntity> getDomains(ServerLevel level) {
    //     Set<DomainExpansionEntity> result = new HashSet<>();
    //     Set<UUID> ids = domains.get(level.dimension());
    //     if (ids == null) return result;

    //     for (UUID id : ids) {
    //         if (level.getEntity(id) instanceof DomainExpansionEntity domain) {
    //             result.add(domain);
    //         }
    //     }
    //     return result;
    // }

    

    // public static Set<DomainExpansionEntity> getDomains(ServerLevel level, BlockPos pos) {
    //     Set<DomainExpansionEntity> result = new HashSet<>();
    //     Set<UUID> ids = domains.get(level.dimension());
    //     if (ids == null) return result;

    //     for (UUID id : ids) {
    //         if (level.getEntity(id) instanceof DomainExpansionEntity domain && domain.isInsideBarrier(pos)) {
    //             result.add(domain);
    //         }
    //     }
    //     return result;
    // }

    // public static Set<DomainExpansionEntity> getDomains(ServerLevel level, AABB bounds) {
    //     Set<DomainExpansionEntity> result = new HashSet<>();
    //     Set<UUID> ids = domains.get(level.dimension());
    //     if (ids == null) return result;

    //     for (UUID id : ids) {
    //         if (level.getEntity(id) instanceof DomainExpansionEntity domain && bounds.intersects(domain.getBounds())) {
    //             result.add(domain);
    //         }
    //     }
    //     return result;
    // }

    // @SubscribeEvent
    // public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
    //     Set<UUID> current = domains.get(event.getLevel().dimension());
    //     if (current != null) {
    //         current.remove(event.getEntity().getUUID());
    //         if (current.isEmpty()) domains.remove(event.getLevel().dimension());
    //     }
    // }
     @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Level level = event.getLevel();
        Entity entity = event.getEntity();

        if (barriers.containsKey(level.dimension())) {
            Set<UUID> identifiers = barriers.get(level.dimension());

            if (!identifiers.remove(entity.getUUID())) return;

            if (identifiers.isEmpty()) {
                barriers.remove(level.dimension());
                cache.remove(level.dimension());
            } else {
                Map<UUID, IBarrier> levelCache = cache.get(level.dimension());
                if (levelCache != null) {
                    levelCache.remove(entity.getUUID());
                    if (levelCache.isEmpty()) cache.remove(level.dimension());
                }
            }
        }
    }

    


    // public static boolean canSpawn(Mob mob, double x, double y, double z) {
    //     BlockPos target = BlockPos.containing(x, y, z);
    //     Set<VeilRodBlockEntity> rods = veilsByDimension.get(mob.level().dimension());
    //     if (rods == null) return true;

    //     for (VeilRodBlockEntity rod : Set.copyOf(rods)) {
    //         if (!rod.isValid()) continue;
    //         int radius = rod.getSize();
    //         boolean curseSpawnFlag = false;
    //         boolean sorcererSpawnFlag = false;
    //         for (Modifier modifier : rod.modifiers) {
    //         if (modifier.getAction() == Modifier.Action.ALLOW &&
    //             modifier.getType() == Modifier.Type.CURSE_SPAWN) {
    //             curseSpawnFlag = true;
    //         } else if (modifier.getAction() == Modifier.Action.ALLOW &&
    //             modifier.getType() == Modifier.Type.SORCERER_SPAWN) {
    //             sorcererSpawnFlag = true;
    //         }
    //         }
    //         if (target.distSqr(rod.getBlockPos()) <= radius * radius)  {
    //             if ((sorcererSpawnFlag && mob instanceof SorcererEntity) || (curseSpawnFlag && mob instanceof CursedSpirit) )  {
    //                 continue;
    //             }
    //             else {
    //                 return false;
    //             }
    //         }
    //     }
    //     return true;
    // }
    
    // public static boolean canSpawn(ServerLevel level, Mob mob, double x, double y, double z) {
    //     BlockPos target = BlockPos.containing(x, y, z);
    //     IBarrier owner = getOwner(level, target);

    //     if (owner instanceof IVeil veil) return veil.isAllowed(mob);

    //     return true;
    // }

 

    public static boolean isProtected(Level level, BlockPos target) {
        if ( !(level instanceof ServerLevel servLevel)) return false;
        Set<IVeil> veils = getVeils(servLevel, target);
        if (veils.isEmpty()) return false;

        for (IVeil veil : veils) {
            //if (!rod.isValid()) continue;
            int radius = veil.getRadius();
            if (target.distSqr(veil.getVeil().getCenter() ) < radius * radius) return true;
        }
        return false;
    }

    public static boolean isTeleportValid(Level level, BlockPos target) {
        
        if ( !(level instanceof ServerLevel servLevel)) return true;
        //System.out.println("made it past first step");
        Set<IVeil> veils = getVeils(servLevel, target);
        if (veils.isEmpty()) return true;

        for (IVeil veil : veils) {
            //if (!rod.isValid() ) continue;
            boolean ownerFlag = false;
             boolean teleportFlag = false;
             for (Modifier modifier : veil.getModifiers()) {
                if (modifier.getAction() == Modifier.Action.ALLOW &&
                    modifier.getType() == Modifier.Type.OWNER_BYPASS) {
                    ownerFlag = true;
                }else if (modifier.getAction() == Modifier.Action.DENY &&
                    modifier.getType() == Modifier.Type.TELEPORT) {
                    teleportFlag = true;
                }
            }
            int radius = veil.getRadius();
            if (( teleportFlag && target.distSqr(veil.getVeil().getCenter()) <= (radius+2) * (radius+2) && !ownerFlag ) ) return false;
        }
        return true;
    }
}
