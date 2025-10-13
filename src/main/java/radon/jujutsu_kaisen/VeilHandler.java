package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeilHandler {


    private static final Map<ResourceKey<Level>, Map<ChunkPos, Set<VeilRodBlockEntity>>> veilsByChunk = new HashMap<>();


    private static final Map<ResourceKey<Level>, Set<UUID>> domains = new HashMap<>();



    public static void addVeil(VeilRodBlockEntity rod) {
        ResourceKey<Level> dimension = rod.getLevel().dimension();
        ChunkPos chunk = new ChunkPos(rod.getBlockPos());

        veilsByChunk
            .computeIfAbsent(dimension, k -> new HashMap<>())
            .computeIfAbsent(chunk, k -> new HashSet<>())
            .add(rod);
    }

    public static void removeVeil(VeilRodBlockEntity rod) {
        ResourceKey<Level> dimension = rod.getLevel().dimension();
        ChunkPos chunk = new ChunkPos(rod.getBlockPos());

        Map<ChunkPos, Set<VeilRodBlockEntity>> chunkMap = veilsByChunk.get(dimension);
        if (chunkMap != null) {
            Set<VeilRodBlockEntity> rods = chunkMap.get(chunk);
            if (rods != null) {
                rods.remove(rod);
                if (rods.isEmpty()) chunkMap.remove(chunk);
            }
            if (chunkMap.isEmpty()) veilsByChunk.remove(dimension);
        }
    }


    public static void addDomain(ResourceKey<Level> dimension, UUID id) {
        domains.computeIfAbsent(dimension, k -> new HashSet<>()).add(id);
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level) {
        Set<DomainExpansionEntity> result = new HashSet<>();
        Set<UUID> ids = domains.get(level.dimension());
        if (ids == null) return result;

        for (UUID id : ids) {
            if (level.getEntity(id) instanceof DomainExpansionEntity domain) {
                result.add(domain);
            }
        }
        return result;
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, BlockPos pos) {
        Set<DomainExpansionEntity> result = new HashSet<>();
        Set<UUID> ids = domains.get(level.dimension());
        if (ids == null) return result;

        for (UUID id : ids) {
            if (level.getEntity(id) instanceof DomainExpansionEntity domain && domain.isInsideBarrier(pos)) {
                result.add(domain);
            }
        }
        return result;
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, AABB bounds) {
        Set<DomainExpansionEntity> result = new HashSet<>();
        Set<UUID> ids = domains.get(level.dimension());
        if (ids == null) return result;

        for (UUID id : ids) {
            if (level.getEntity(id) instanceof DomainExpansionEntity domain && bounds.intersects(domain.getBounds())) {
                result.add(domain);
            }
        }
        return result;
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Set<UUID> current = domains.get(event.getLevel().dimension());
        if (current != null) {
            current.remove(event.getEntity().getUUID());
            if (current.isEmpty()) domains.remove(event.getLevel().dimension());
        }
    }



    public static boolean canSpawn(Mob mob, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);
        Map<ChunkPos, Set<VeilRodBlockEntity>> chunkMap = veilsByChunk.get(mob.level().dimension());
        if (chunkMap == null) return true;

        ChunkPos chunk = new ChunkPos(target);
        Set<VeilRodBlockEntity> veils = chunkMap.get(chunk);
        if (veils == null) return true;

        for (VeilRodBlockEntity rod : veils) {
            if (!rod.isValid()) continue;
            int radius = rod.getSize();
            if (target.distSqr(rod.getBlockPos()) < radius * radius) return false;
        }
        return true;
    }

    public static boolean canDestroy(LivingEntity entity, Level level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);
        Map<ChunkPos, Set<VeilRodBlockEntity>> chunkMap = veilsByChunk.get(level.dimension());
        if (chunkMap == null) return true;

        ChunkPos chunk = new ChunkPos(target);
        Set<VeilRodBlockEntity> veils = chunkMap.get(chunk);
        if (veils == null) return true;

        for (VeilRodBlockEntity rod : veils) {
            if (!rod.isValid() || target.equals(rod.getBlockPos())) continue;
            int radius = rod.getSize();
            if (target.distSqr(rod.getBlockPos()) >= radius * radius) continue;
            if (entity != null && rod.ownerUUID.equals(entity.getUUID())) continue;

            for (Modifier modifier : rod.modifiers) {
                if (modifier.getAction() == Modifier.Action.DENY &&
                    modifier.getType() == Modifier.Type.GRIEFING) return false;
            }
        }
        return true;
    }

    public static boolean isProtected(Level level, BlockPos target) {
        Map<ChunkPos, Set<VeilRodBlockEntity>> chunkMap = veilsByChunk.get(level.dimension());
        if (chunkMap == null) return false;

        ChunkPos chunk = new ChunkPos(target);
        Set<VeilRodBlockEntity> veils = chunkMap.get(chunk);
        if (veils == null) return false;

        for (VeilRodBlockEntity rod : veils) {
            if (!rod.isValid()) continue;
            int radius = rod.getSize();
            if (target.distSqr(rod.getBlockPos()) < radius * radius) return true;
        }
        return false;
    }



    public static void cleanupChunk(ResourceKey<Level> dimension, ChunkPos chunk) {
        Map<ChunkPos, Set<VeilRodBlockEntity>> chunkMap = veilsByChunk.get(dimension);
        if (chunkMap == null) return;

        Set<VeilRodBlockEntity> veils = chunkMap.get(chunk);
        if (veils == null) return;

        veils.removeIf(rod -> !rod.isValid());

        if (veils.isEmpty()) chunkMap.remove(chunk);
        if (chunkMap.isEmpty()) veilsByChunk.remove(dimension);
    }
}
