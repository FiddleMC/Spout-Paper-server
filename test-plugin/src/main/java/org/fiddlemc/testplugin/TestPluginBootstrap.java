package org.fiddlemc.testplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Instrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Slab;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.fiddlemc.fiddle.api.FiddleEvents;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingUtilities;
import org.fiddlemc.testplugin.data.PluginBlockTypes;
import org.fiddlemc.testplugin.data.PluginItemTypes;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class TestPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        loadIncludedDataPack(context);
        setBlockMappings(context);
        setItemMappings(context);
        setTranslations(context);
        configureResourcePack(context);
    }

    /**
     * Makes sure the included data pack is loaded.
     * It contains crafting recipes for the custom items we add.
     */
    private void loadIncludedDataPack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            try {
                event.registrar().discoverPack(Objects.requireNonNull(this.getClass().getResource("/data_pack")).toURI(), "provided");
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static NoteBlock getAzaleaPlanksNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.sharp(1, Note.Tone.G));
        return state;
    }

    private static NoteBlock getBirchBookshelfNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.natural(1, Note.Tone.G));
        return state;
    }

    private static NoteBlock getDioriteBricksNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.natural(0, Note.Tone.G));
        return state;
    }

    /**
     * Configures the server-to-client mappings for blocks.
     */
    private void setBlockMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_MAPPING, event -> {

            // Glass slab
            event.registerStateToState(
                ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(),
                PluginBlockTypes.GLASS_SLAB.get(),
                BlockType.QUARTZ_SLAB
            );
            // event.register(builder -> {
            //     builder.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks());
            //     builder.from(PluginBlockTypes.GLASS_SLAB.get().createBlockDataStates().stream().filter(data -> ((Slab) data).getType() == Slab.Type.DOUBLE).toList());
            //     builder.toDefaultStateOf(BlockType.GLASS);
            // });

            // Glass stairs
            event.registerStateToState(
                ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(),
                PluginBlockTypes.GLASS_STAIRS.get(),
                BlockType.QUARTZ_STAIRS
            );

            // Stone brick bevel
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks());
                builder.fromEveryStateOf(PluginBlockTypes.STONE_BRICK_BEVEL.get());
                builder.toDefaultStateOf(BlockType.TEST_BLOCK);
            });

            // Azalea planks
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.AZALEA_PLANKS.get(),
                BlockType.WARPED_PLANKS
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.AZALEA_PLANKS.get());
                builder.to(getAzaleaPlanksNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getAzaleaPlanksNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });

            // Birch bookshelf
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.BIRCH_BOOKSHELF.get(),
                BlockType.BOOKSHELF
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.BIRCH_BOOKSHELF.get());
                builder.to(getBirchBookshelfNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getBirchBookshelfNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });

            // Diorite brick slab
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICK_SLAB.get(),
                BlockType.POLISHED_DIORITE_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIORITE_BRICK_SLAB.get(),
                BlockType.WAXED_CUT_COPPER_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                BlockType.WAXED_CUT_COPPER_SLAB,
                BlockType.CUT_COPPER_SLAB
            );

            // Diorite brick stairs
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICK_STAIRS.get(),
                BlockType.POLISHED_DIORITE_STAIRS
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIORITE_BRICK_STAIRS.get(),
                BlockType.WAXED_CUT_COPPER_STAIRS
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                BlockType.WAXED_CUT_COPPER_STAIRS,
                BlockType.CUT_COPPER_STAIRS
            );

            // Diorite bricks
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICKS.get(),
                BlockType.POLISHED_DIORITE
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.DIORITE_BRICKS.get());
                builder.to(getDioriteBricksNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getDioriteBricksNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });

        });
    }

    /**
     * Configures the server-to-client mappings for items.
     */
    private void setItemMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {

            // Glass slab
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.GLASS_SLAB.get());
                builder.to(ItemType.QUARTZ_SLAB);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.GLASS_SLAB.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.QUARTZ_SLAB);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("fiddle_more_shapes:glass_slab")));
                });
            });

            // Glass stairs
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.GLASS_STAIRS.get());
                builder.to(ItemType.QUARTZ_STAIRS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.GLASS_STAIRS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.QUARTZ_STAIRS);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("fiddle_more_shapes:glass_stairs")));
                });
            });

            // Stone brick bevel
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.STONE_BRICK_BEVEL.get());
                builder.to(ItemType.TEST_BLOCK);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.STONE_BRICK_BEVEL.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.TEST_BLOCK);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("fiddle_more_shapes:stone_brick_bevel")));
                });
            });

            // Azalea planks
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.AZALEA_PLANKS.get());
                builder.to(ItemType.WARPED_PLANKS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.AZALEA_PLANKS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("quark:azalea_planks"));
                        ((BlockDataMeta) meta).setBlockData(getAzaleaPlanksNoteBlockState());
                    });
                });
            });

            // Birch bookshelf
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.BIRCH_BOOKSHELF.get());
                builder.to(ItemType.BOOKSHELF);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.BIRCH_BOOKSHELF.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("quark:birch_bookshelf"));
                        ((BlockDataMeta) meta).setBlockData(getBirchBookshelfNoteBlockState());
                    });
                });
            });

            // Diorite brick slab
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIORITE_BRICK_SLAB.get());
                builder.to(ItemType.POLISHED_DIORITE_SLAB);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIORITE_BRICK_SLAB.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.WAXED_CUT_COPPER_SLAB);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("quark:diorite_brick_slab")));
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(ItemType.WAXED_CUT_COPPER_SLAB);
                builder.to(ItemType.CUT_COPPER_SLAB);
            });

            // Diorite brick stairs
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIORITE_BRICK_STAIRS.get());
                builder.to(ItemType.POLISHED_DIORITE_STAIRS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIORITE_BRICK_STAIRS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.WAXED_CUT_COPPER_STAIRS);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("quark:diorite_brick_stairs")));
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(ItemType.WAXED_CUT_COPPER_STAIRS);
                builder.to(ItemType.CUT_COPPER_STAIRS);
            });

            // Diorite bricks
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIORITE_BRICKS.get());
                builder.to(ItemType.POLISHED_DIORITE);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIORITE_BRICKS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("quark:diorite_bricks"));
                        ((BlockDataMeta) meta).setBlockData(getDioriteBricksNoteBlockState());
                    });
                });
            });

            // Glass shard
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.GLASS_SHARD.get());
                builder.to(ItemType.PRISMARINE_SHARD);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.GLASS_SHARD.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.PRISMARINE_SHARD);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("quark:glass_shard")));
                });
            });

        });
    }

    /**
     * Configures the translations of names of blocks and items.
     */
    private void setTranslations(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.SERVER_SIDE_TRANSLATION, event -> {

            event.register(PluginBlockTypes.GLASS_SLAB.get().translationKey(), "Glass Slab");
            event.register(PluginBlockTypes.GLASS_SLAB.get().translationKey(), "ガラスのハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.GLASS_STAIRS.get().translationKey(), "Glass Stairs");
            event.register(PluginBlockTypes.GLASS_STAIRS.get().translationKey(), "ガラスの階段", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.STONE_BRICK_BEVEL.get().translationKey(), "Stone Brick Mini");
            event.register(PluginBlockTypes.STONE_BRICK_BEVEL.get().translationKey(), "石レンガのミニ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.AZALEA_PLANKS.get().translationKey(), "Azalea Planks");
            event.register(PluginBlockTypes.AZALEA_PLANKS.get().translationKey(), "ツツジの板材", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "Birch Bookshelf");
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "シラカバの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICK_SLAB.get().translationKey(), "Diorite Brick Slab");
            event.register(PluginBlockTypes.DIORITE_BRICK_SLAB.get().translationKey(), "閃緑岩レンガのハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICK_STAIRS.get().translationKey(), "Diorite Brick Stairs");
            event.register(PluginBlockTypes.DIORITE_BRICK_STAIRS.get().translationKey(), "閃緑岩レンガの階段", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "Diorite Bricks");
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "閃緑岩レンガ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginItemTypes.GLASS_SHARD.get().translationKey(), "Glass Shard");
            event.register(PluginItemTypes.GLASS_SHARD.get().translationKey(), "ガラスの破片", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

            event.register(BlockType.BOOKSHELF.translationKey(), "Oak Bookshelf");
            event.register(BlockType.BOOKSHELF.translationKey(), "オークの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

        });
    }

    private void configureResourcePack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.RESOURCE_PACK_CONSTRUCT, event -> {
            try {
                event.copyPluginResources(context, List.of(ClientView.AwarenessLevel.RESOURCE_PACK, ClientView.AwarenessLevel.CLIENT_MOD), "resource_pack_direct", "");
                event.asset(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.NOTE_BLOCK.getKey(), "json").asJsonObject().setParsedFromString("""
                    {
                      "variants": {
                           "instrument=bell,note=14,powered=false": { "model": "quark:block/azalea_planks" },
                           "instrument=bell,note=13,powered=false": { "model": "quark:block/birch_bookshelf" },
                           "instrument=bell,note=1,powered=false": { "model": "quark:block/diorite_bricks" }
                         }
                    }
                    """);
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/quark/blockstates/diorite_brick_slab.json", "assets/minecraft/blockstates/waxed_cut_copper_slab.json");
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/quark/blockstates/diorite_brick_stairs.json", "assets/minecraft/blockstates/waxed_cut_copper_stairs.json");
                event.copyPluginResources(context, ClientView.AwarenessLevel.CLIENT_MOD, "resource_pack_indirect", "");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
