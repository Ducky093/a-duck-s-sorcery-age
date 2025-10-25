package radon.jujutsu_kaisen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.*;

public class HelperMethods {
    public static final RandomSource RANDOM = RandomSource.createThreadSafe();

    // TODO: move this goofy shit to config
    private static final String[] WORDS = {"blossoms", "thorns", "roots", "petals", "embers", "ashes", "seeds", "vines", "branches", "stones", "crystals", "mist", "bloom", "dew", "tide", "drift", "flame", "soil", 
    "omens", "sigils", "wards", "bindings", "seals", "echoes", "spirits", "whispers", "truths", "mirrors", "phantoms", "visions", "relics", "runes", "shadows", "dreams", "grace", "essence", "fate", "threshold", 
    "judgments", "chains", "ropes", "knives", "crowns", "blades", "scars", "brands", "vows", "marks", "wounds", "oaths", "banners", "arrows", "rings", "keys", "grips", "callings", "rifts", "trials", 
    "silence", "memory", "echo", "stillness", "pulse", "void", "origin", "fracture", "balance", "cycle", "breath", "motion", "reflection", "spark", "moment", "hollow", "weight", "current", "name", "end", 
    "rot", "decay", "curse", "wither", "lament", "sin", "hunger", "feast", "shroud", "wrath", "abyss", "void", "grief", "scar", "torment", "ruin", "dread", "flesh", "night"
    };

    public static boolean isMelee(DamageSource source) {
        return !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SPLIT_SOUL_KATANA)) ||
                source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && jujutsu.getAbility() != null && jujutsu.getAbility().isMelee();
    }

    private static BlockPos getTopNonCollidingPos(LevelReader level, EntityType<?> type, int x, int z) {
        int i = level.getHeight(SpawnPlacements.getHeightmapType(type), x, z);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, i, z);

        if (level.dimensionType().hasCeiling()) {
            do {
                pos.move(Direction.DOWN);
            } while(!level.getBlockState(pos).isAir());

            do {
                pos.move(Direction.DOWN);
            } while (level.getBlockState(pos).isAir() && pos.getY() > level.getMinBuildHeight());
        }

        if (SpawnPlacements.getPlacementType(type) == SpawnPlacements.Type.ON_GROUND) {
            BlockPos below = pos.below();

            if (level.getBlockState(below).isPathfindable(level, below, PathComputationType.LAND)) {
                return below;
            }
        }
        return pos.immutable();
    }

    public static BlockPos findSafePos(ServerLevel level, LivingEntity entity) {
        BlockPos.MutableBlockPos pos = entity.blockPosition().mutable();

        level.getPoiManager().ensureLoadedAndValid(level, pos, 16);

        double minX = level.getWorldBorder().getMinX();
        double maxX = level.getWorldBorder().getMaxX();
        double minZ = level.getWorldBorder().getMinZ();
        double maxZ = level.getWorldBorder().getMaxZ();

        EntityType<?> type = entity.getType();

        while (!NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(type), level, pos, type)) {
            pos.set(minX + RANDOM.nextDouble() * (maxX - minX), 0, minZ + RANDOM.nextDouble() * (maxZ - minZ));

            level.getPoiManager().ensureLoadedAndValid(level, pos, 16);

            pos.set(getTopNonCollidingPos(level, type, pos.getX(), pos.getZ()));
        }

        return pos.immutable();
    }



    public static int getRGB24(Vector3f rgb) {
        return FastColor.ARGB32.color(255, Math.round(rgb.x * 255.0F), Math.round(rgb.y * 255.0F), Math.round(rgb.z * 255.0F));
    }

    public static boolean isDestroyable(BlockGetter getter, @Nullable LivingEntity source, BlockPos pos) {
        if (!ConfigHolder.SERVER.destruction.get() ) return false;

        if (source != null && !(source instanceof Player) && !source.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)  ) return false;
        if (source != null && VeilHandler.canDestroy(source, source.level(),(double) pos.getX(),(double) pos.getY(), (double)pos.getZ()) == false) return false;
        BlockState state = getter.getBlockState(pos);
        boolean destroyable = !state.isAir() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE;

        if (!destroyable && source != null && source.level() instanceof ServerLevel level && getter.getBlockEntity(pos) instanceof DomainBlockEntity be) {
            UUID identifier = be.getIdentifier();
            destroyable = identifier == null || !(level.getEntity(identifier) instanceof DomainExpansionEntity domain) ||
                    !domain.isInsideBarrier(source.blockPosition());
        }
        return destroyable;
    }

    public static Set<String> getRandomWordCombo(int count) {
        if (count > WORDS.length)
            throw new IllegalArgumentException("Number of words requested exceeds the available word list.");

        Set<String> combo = new HashSet<>();

        while (combo.size() < count) {
            combo.add(WORDS[RANDOM.nextInt(WORDS.length)]);
        }
        return combo;
    }

    public static <E> E getWeightedRandom(Map<E, Double> weights, RandomSource random) {
        E result = null;
        double bestValue = Double.MAX_VALUE;

        for (E element : weights.keySet()) {
            double value = -Math.log(random.nextDouble()) / weights.get(element);

            if (value < bestValue) {
                bestValue = value;
                result = element;
            }
        }

        return result;
    }

    public static int getLevenshteinDistance(String x, String y) {
        int m = x.length();
        int n = y.length();

        int[][] T = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            T[i][0] = i;
        }

        for (int j = 1; j <= n; j++) {
            T[0][j] = j;
        }

        int cost;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                cost = x.charAt(i - 1) == y.charAt(j - 1) ? 0: 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }
        return T[m][n];
    }

    public static float strcmp(String x, String y) {
        float max = Float.max(x.length(), y.length());

        if (max > 0) {
            return 1.0F - ((max - getLevenshteinDistance(x, y)) / max);
        }
        return 0.0F;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[RANDOM.nextInt(clazz.getEnumConstants().length)];
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> clazz, Set<T> excluded) {
        if (!excluded.isEmpty()) {
            EnumSet<T> available = EnumSet.complementOf(EnumSet.copyOf(excluded));

            if (!available.isEmpty()) {
                return (T) available.toArray()[RANDOM.nextInt(available.size())];
            }
        }
        return clazz.getEnumConstants()[RANDOM.nextInt(clazz.getEnumConstants().length)];
    }

    public static int toRGB24(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF));
    }
}
