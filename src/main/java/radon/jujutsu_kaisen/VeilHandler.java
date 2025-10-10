package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeilHandler {

    // Map dimension -> chunk -> set of veil centers
    private static final Map<ResourceKey<Level>, Map<ChunkPos, Set<BlockPos>>> veilsByChunk = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> domains = new HashSetMap<>();

    private static int cleanupCounter = 0;

    /** Register a new veil */
    public static void veil(ResourceKey<Level> dimension, BlockPos pos) {
        Map<ChunkPos, Set<BlockPos>> chunkMap = veilsByChunk.computeIfAbsent(dimension, k -> new HashMap<>());
        ChunkPos chunk = new ChunkPos(pos);
        chunkMap.computeIfAbsent(chunk, k -> new HashSet<>()).add(pos);
    }

    /** Register a domain entity */
    public static void domain(ResourceKey<Level> dimension, UUID identifier) {
        domains.computeIfAbsent(dimension, k -> new HashSet<>()).add(identifier);
    }

    /** Get all domains in a level */
    public static Set<DomainExpansionEntity> getDomains(ServerLevel level) {
        Set<DomainExpansionEntity> result = new HashSet<>();
        Set<UUID> ids = domains.get(level.dimension());
        if (ids == null) return result;

        for (UUID id : ids) {
            Entity e = level.getEntity(id);
            if (e instanceof DomainExpansionEntity domain) result.add(domain);
        }
        return result;
    }

    /** Get all domains intersecting a position */
    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, BlockPos pos) {
        Set<DomainExpansionEntity> result = new HashSet<>();
        Set<UUID> ids = domains.get(level.dimension());
        if (ids == null) return result;

        for (UUID id : ids) {
            Entity e = level.getEntity(id);
            if (e instanceof DomainExpansionEntity domain && domain.isInsideBarrier(pos)) {
                result.add(domain);
            }
        }
        return result;
    }

    /** Get all domains intersecting a bounding box */
    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, AABB bounds) {
        Set<DomainExpansionEntity> result = new HashSet<>();
        Set<UUID> ids = domains.get(level.dimension());
        if (ids == null) return result;

        for (UUID id : ids) {
            Entity e = level.getEntity(id);
            if (e instanceof DomainExpansionEntity domain && bounds.intersects(domain.getBounds())) {
                result.add(domain);
            }
        }
        return result;
    }

    /** Can this mob spawn at the given position? */
    public static boolean canSpawn(Mob mob, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);
        Map<ChunkPos, Set<BlockPos>> chunkMap = veilsByChunk.get(mob.level().dimension());
        if (chunkMap == null) return true;

        ChunkPos chunk = new ChunkPos(target);
        Set<BlockPos> veils = chunkMap.get(chunk);
        if (veils == null) return true;

        for (BlockPos pos : veils) {
            if (!(mob.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be)) continue;
            int radius = be.getSize();
            if (target.distSqr(pos) < radius * radius) {
                return false; //VeilBlockEntity.isAllowed(pos, mob);
            }
        }
        return true;
    }

    
                  

 
    public static boolean canDestroy(@Nullable LivingEntity entity, Level level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);
        Map<ChunkPos, Set<BlockPos>> chunkMap = veilsByChunk.get(level.dimension());
        if (chunkMap == null) return true;

        ChunkPos chunk = new ChunkPos(target);
        Set<BlockPos> veils = chunkMap.get(chunk);
        if (veils == null) return true;

        for (BlockPos pos : veils) {
            if (target.equals(pos)) continue;
             // So that veil rods can still be broken
            if (!(level.getBlockEntity(pos) instanceof VeilRodBlockEntity be)) continue;

            int radius = be.getSize();
            if (target.distSqr(pos) >= radius * radius) continue;
            if (entity != null && be.ownerUUID == entity.getUUID()) continue;

            for (Modifier modifier : be.modifiers) {
                if (modifier.getAction() == Modifier.Action.DENY && modifier.getType() == Modifier.Type.GRIEFING) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isProtected(Level level, BlockPos target) {
        Map<ChunkPos, Set<BlockPos>> chunkMap = veilsByChunk.get(level.dimension());
        if (chunkMap == null) return false;

        ChunkPos chunk = new ChunkPos(target);
        Set<BlockPos> veils = chunkMap.get(chunk);
        if (veils == null) return false;

        for (BlockPos pos : veils) {
            if (!(level.getBlockEntity(pos) instanceof VeilRodBlockEntity be)) continue;
            int radius = be.getSize();
            if (target.distSqr(pos) < radius * radius) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Set<UUID> current = domains.get(event.getLevel().dimension());
        if (current != null) {
            current.remove(event.getEntity().getUUID());
            if (current.isEmpty()) domains.remove(event.getLevel().dimension());
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.CLIENT || event.type != TickEvent.Type.LEVEL || event.phase != TickEvent.Phase.END || event.level.isClientSide)
            return;

        cleanupCounter++;
        if (cleanupCounter < 20) return;
        cleanupCounter = 0;

        for (Map.Entry<ResourceKey<Level>, Map<ChunkPos, Set<BlockPos>>> entry : veilsByChunk.entrySet()) {
            Map<ChunkPos, Set<BlockPos>> chunkMap = entry.getValue();
            for (Map.Entry<ChunkPos, Set<BlockPos>> chunkEntry : chunkMap.entrySet()) {
                chunkEntry.getValue().removeIf(pos -> {
                    var be = event.level.getBlockEntity(pos);
                    return !(be instanceof VeilRodBlockEntity rod) || !rod.isValid();
                });
            }
        }
    }

    private static class HashSetMap<K, V> extends HashMap<K, Set<V>> {
        @Override
        public Set<V> computeIfAbsent(K key, java.util.function.Function<? super K, ? extends Set<V>> mappingFunction) {
            return super.computeIfAbsent(key, mappingFunction);
        }
    }
}
