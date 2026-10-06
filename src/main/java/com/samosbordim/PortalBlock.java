package com.samosbordim;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class PortalBlock extends Block implements PolymerBlock {

    private static final ResourceKey<Level> GIGAKHRUSHCHEVKA_KEY = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(SamosborDim.MOD_ID, "gigakhrushchevka"));

    public PortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Block getPolymerReplacement(ServerPlayer player) {
        return Blocks.IRON_DOOR; // Visual placeholder for the portal
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.IRON_DOOR.defaultBlockState();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ServerLevel targetLevel;
            if (level.dimension() == GIGAKHRUSHCHEVKA_KEY) {
                targetLevel = serverPlayer.server.getLevel(Level.OVERWORLD);
            } else {
                targetLevel = serverPlayer.server.getLevel(GIGAKHRUSHCHEVKA_KEY);
            }

            if (targetLevel != null) {
                // Find a safe spot in the new dimension (simplistic for now)
                BlockPos targetPos = new BlockPos(blockPos.getX(), 50, blockPos.getZ());
                if (targetLevel.dimension() == GIGAKHRUSHCHEVKA_KEY) {
                    // Try to find a floor block
                    targetPos = new BlockPos(blockPos.getX(), 5, blockPos.getZ());
                } else {
                    targetPos = targetLevel.getSharedSpawnPos();
                }

                serverPlayer.teleportTo(targetLevel, targetPos.getX(), targetPos.getY(), targetPos.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
