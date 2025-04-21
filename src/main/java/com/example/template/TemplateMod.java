package com.example.template;

import com.example.template.multiloader.Loader;

import com.simibubi.create.CreateBuildInfo;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.createmod.catnip.lang.FontHelper;

import dev.architectury.injectables.annotations.ExpectPlatform;

import net.minecraft.resources.ResourceLocation;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;


public class TemplateMod {

	public static final String MOD_ID = "template";
	public static final String NAME = "Template";
	public static final String SERVER_START = "waaa";
	public static final String VERSION = findVersion();

	public static final Logger LOGGER = LogUtils.getLogger();

	private static final CreateRegistrate REGISTRATE =
			CreateRegistrate.create(MOD_ID);

	// Lets you use fancy create tooltips throughout your mod, just set it up in the lang file for any item
	static {
		REGISTRATE.setTooltipModifierFactory(item -> new
				ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item))));
	}

	public static void init() {
		LOGGER.info("{} {} initializing! Create version: {} on platform: {}",
				NAME, VERSION, CreateBuildInfo.VERSION, Loader.getCurrent());

		ModSetup.register();
	}

	public static CreateRegistrate registrate() {
		return REGISTRATE;
	}

	// Sets up forge and fabric to be able to find the version
	@ExpectPlatform
	public static String findVersion() {
		throw new AssertionError();
	}

	public static ResourceLocation asResource(String path) {
		// if statement for 1.21, if it isn't 1.21 then it's 1.20.1
		//? if >=1.21 {
		/*return ResourceLocation.fromNamespaceAndPath(MOD_ID, path); // the code on 1.21
		*///?} else {
		return new ResourceLocation(MOD_ID, path); // the code on 1.20.1
		//?}
	}
}