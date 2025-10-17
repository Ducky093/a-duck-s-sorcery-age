package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeilHandler {

    private static final Map<ResourceKey<Level>, Set<VeilRodBlockEntity>> veilsByDimension = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> domains = new HashMap<>();
    //private final Map<BlockPos, CompoundTag> placedBlocks = new HashMap<>();

    public static boolean checkIntersect(Level level, BlockPos center, int radius) {
    Set<VeilRodBlockEntity> rods = veilsByDimension.get(level.dimension());
    if (rods == null) return false;

    for (VeilRodBlockEntity rod : rods) {
        if (!rod.isValid() || rod.getBlockPos().equals(center)) continue;

        BlockPos otherPos = rod.getBlockPos();
        int otherRadius = rod.getSize();
        double distSqr = center.distSqr(otherPos);

        double combined = radius + otherRadius;
        if (distSqr <= combined * combined) {
            return true;
        }
    }
    return false;
}



    public static void addVeil(VeilRodBlockEntity rod) {
        ResourceKey<Level> dimension = rod.getLevel().dimension();
        veilsByDimension.computeIfAbsent(dimension, k -> new HashSet<>()).add(rod);
    }

    public static void removeVeil(VeilRodBlockEntity rod) {
        ResourceKey<Level> dimension = rod.getLevel().dimension();
        Set<VeilRodBlockEntity> rods = veilsByDimension.get(dimension);
        if (rods == null) return;

        rods.remove(rod);

        int radius = rod.getSize();
        BlockPos center = rod.getBlockPos();
        Level level = rod.getLevel();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(pos);

                    if (be instanceof VeilBlockEntity veil) {
                        BlockPos parent = veil.getParent();
                        if (parent != null && parent.equals(center)) {
                            veil.destroy();
                        }
                    }
                }
            }
        }

        if (rods.isEmpty()) veilsByDimension.remove(dimension);
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
        Set<VeilRodBlockEntity> rods = veilsByDimension.get(mob.level().dimension());
        if (rods == null) return true;

        for (VeilRodBlockEntity rod : rods) {
            if (!rod.isValid()) continue;
            int radius = rod.getSize();
            if (target.distSqr(rod.getBlockPos()) < radius * radius) return false;
        }
        return true;
    }

    public static boolean canDestroy(LivingEntity entity, Level level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);
        Set<VeilRodBlockEntity> rods = veilsByDimension.get(level.dimension());
        if (rods == null) return true;

        for (VeilRodBlockEntity rod : rods) {
            if (!rod.isValid() || target.equals(rod.getBlockPos())) continue;

            int radius = rod.getSize();
            if (target.distSqr(rod.getBlockPos()) >= radius * radius) continue;

            if (entity != null && rod.ownerUUID != null && rod.ownerUUID.equals(entity.getUUID())) continue;

            for (Modifier modifier : rod.modifiers) {
                if (modifier.getAction() == Modifier.Action.DENY &&
                    modifier.getType() == Modifier.Type.GRIEFING) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isProtected(Level level, BlockPos target) {
        Set<VeilRodBlockEntity> rods = veilsByDimension.get(level.dimension());
        if (rods == null) return false;

        for (VeilRodBlockEntity rod : rods) {
            if (!rod.isValid()) continue;
            int radius = rod.getSize();
            if (target.distSqr(rod.getBlockPos()) < radius * radius) return true;
        }
        return false;
    }

    public static void cleanupDimension(ResourceKey<Level> dimension) {
        Set<VeilRodBlockEntity> rods = veilsByDimension.get(dimension);
        if (rods == null) return;

        rods.removeIf(rod -> !rod.isValid());
        if (rods.isEmpty()) veilsByDimension.remove(dimension);
    }
}
