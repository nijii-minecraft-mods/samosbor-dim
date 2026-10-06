package com.samosbordim;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModBlocks {

    public static final Block RUSTY_CONCRETE = registerBlock("rusty_concrete", new SamosborBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRAY_CONCRETE)));
    public static final Block PEELING_WALL = registerBlock("peeling_wall", new SamosborBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    public static final Block INDUSTRIAL_FLOOR = registerBlock("industrial_floor", new SamosborBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE)));
    public static final Block FOG_WINDOW = registerBlock("fog_window", new SamosborBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPLE_STAINED_GLASS).lightLevel(state -> 5)));
    public static final Block PORTAL_DOOR = registerBlock("portal_door", new PortalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(SamosborDim.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(SamosborDim.MOD_ID, name),
                new PolymerBlockItem(block, new Item.Properties()));
    }

    public static void registerModBlocks() {
        SamosborDim.LOGGER.info("Registering Mod Blocks for " + SamosborDim.MOD_ID);
    }

    public static class SamosborBlock extends Block implements PolymerBlock {
        public SamosborBlock(Properties properties) {
            super(properties);
        }

        @Override
        public Block getPolymerReplacement(ServerPlayer player) {
            return Blocks.STONE;
        }

        @Override
        public BlockState getPolymerBlockState(BlockState state) {
            return Blocks.STONE.defaultBlockState();
        }
    }

    public static class PolymerBlockItem extends BlockItem implements PolymerItem {
        public PolymerBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public Item getPolymerItem(ItemStack itemStack, ServerPlayer player) {
            return Items.STONE;
        }

        @Override
        public Item getPolymerReplacement(ServerPlayer player) {
            return Items.STONE;
        }
    }
}
