package com.samosbordim;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class GigakhrushchevkaChunkGenerator extends ChunkGenerator {

    public static final MapCodec<GigakhrushchevkaChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(GigakhrushchevkaChunkGenerator::new)));

    public GigakhrushchevkaChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getGenDepth() {
        return 1024;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        BlockState floorState = ModBlocks.INDUSTRIAL_FLOOR.defaultBlockState();
        BlockState wallState = ModBlocks.PEELING_WALL.defaultBlockState();
        BlockState rustyState = ModBlocks.RUSTY_CONCRETE.defaultBlockState();
        BlockState fogState = ModBlocks.FOG_WINDOW.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState ironBars = Blocks.IRON_BARS.defaultBlockState();

        Random random = new Random(chunkPos.toLong());

        int roomSize = 16;
        int corridorWidth = 4;
        int floorHeight = 5;

        for (int y = 0; y < getGenDepth(); y++) {
            boolean isFloor = (y % floorHeight == 0);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = minX + x;
                    int worldZ = minZ + z;

                    BlockState state = air;

                    // Main layout: Grid of rooms with a large central corridor
                    boolean inCorridorX = Math.abs(worldX % 64) < corridorWidth;
                    boolean inCorridorZ = Math.abs(worldZ % 64) < corridorWidth;

                    boolean isStairwell = (worldX % 32 >= 12 && worldX % 32 <= 20) && (worldZ % 32 >= 12 && worldZ % 32 <= 20);

                    if (isFloor) {
                        if (isStairwell) {
                            // Hole for stairs, with iron bars around
                            if (worldX % 32 == 12 || worldX % 32 == 20 || worldZ % 32 == 12 || worldZ % 32 == 20) {
                                state = ironBars;
                            } else {
                                // Empty space for falling/stairs
                            }
                        } else {
                            state = (random.nextFloat() > 0.9) ? rustyState : floorState;
                        }
                    } else {
                        // Walls
                        boolean isWallX = (worldX % roomSize == 0);
                        boolean isWallZ = (worldZ % roomSize == 0);

                        if (isStairwell) {
                            if (worldX % 32 == 12 || worldX % 32 == 20 || worldZ % 32 == 12 || worldZ % 32 == 20) {
                                // Add some fences or walls around the stairwell
                                if (y % floorHeight == 1) state = ironBars;
                            }
                        } else if (isWallX || isWallZ) {
                            // Make some exterior walls into fog windows
                            boolean isExterior = (worldX % 128 == 0 || worldZ % 128 == 0);

                            if (isExterior && (y % floorHeight == 2 || y % floorHeight == 3)) {
                                state = fogState;
                            } else {
                                // Internal walls
                                // Leave doors
                                boolean isDoorX = isWallX && (worldZ % roomSize == roomSize / 2 || worldZ % roomSize == roomSize / 2 + 1);
                                boolean isDoorZ = isWallZ && (worldX % roomSize == roomSize / 2 || worldX % roomSize == roomSize / 2 + 1);

                                if (!(isDoorX || isDoorZ) || y % floorHeight >= 3) {
                                    state = (random.nextFloat() > 0.8) ? rustyState : wallState;
                                }
                            }
                        }
                    }

                    chunk.setBlockState(new BlockPos(worldX, y, worldZ), state, false);
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return 1024;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {
    }
}
