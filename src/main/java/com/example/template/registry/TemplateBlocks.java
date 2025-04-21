package com.example.template.registry;

import com.example.template.TemplateMod;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;

import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


public class TemplateBlocks {

    // Lets the file use registrate formatting by using the previously established registrate
    private static final CreateRegistrate REGISTRATE =
            TemplateMod.registrate();

    public static void register() {
        // Loads the class and registers everything
        TemplateMod.LOGGER.info("Registering blocks for " + TemplateMod.NAME);
    }

    public static final BlockEntry<Block> EXAMPLE_BLOCK =
            REGISTRATE.block("example_block", Block::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p
                            /** Mess around with these however you want,
                            you can start by typing "." after the "p" to see possible properties
                             */
                            // Simplified color on a map
                            .mapColor(MapColor.COLOR_GRAY)
                            // The sound properties when you place and break it
                            .sound(SoundType.NETHERITE_BLOCK)
                            // The block's tendency to not want to break
                            .strength(1.0F,3600000.0F))
                    .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
                    .transform(pickaxeOnly()) // Breakable with only a pickaxe
                    .lang("Example Block") // The item/block name visible in game
                    .simpleItem() // Make an item
                    .register()
            ;
}
