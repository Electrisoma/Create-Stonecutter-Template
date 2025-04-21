package com.example.template.fabric;

import com.example.template.TemplateMod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;


@SuppressWarnings("unused")
public class TemplateModImpl implements ModInitializer {

	@Override
	public void onInitialize() {
		TemplateMod.init();

		onServerStarting();
	}

	// Finds the version for Fabric
	public static String findVersion() {
		return FabricLoader.getInstance()
				.getModContainer(TemplateMod.MOD_ID)
				.orElseThrow()
				.getMetadata()
				.getVersion()
				.getFriendlyString();
	}

	public void onServerStarting(){
		ServerLifecycleEvents.SERVER_STARTED.register(server ->
				TemplateMod.LOGGER.info(TemplateMod.SERVER_START)
		);
	}
}
