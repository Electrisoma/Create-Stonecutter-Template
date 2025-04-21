package com.example.template.neoforge;

import com.example.template.TemplateModCommon;

import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.MavenVersionAdapter;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.List;


@SuppressWarnings("unused")
@Mod(TemplateModCommon.MOD_ID)
public class TemplateModNeoForge {

	static IEventBus eventBus;
	static IEventBus neoforgeBus;

	public TemplateModNeoForge() {
		eventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		neoforgeBus = NeoForge.EVENT_BUS;

		TemplateModCommon.init();

		neoforgeBus.addListener(this::onServerStarting);
	}

	// Finds the version for NeoForge
	public static String findVersion() {
		String versionString = "UNKNOWN";

		List<IModInfo> infoList = ModList.get().getModFileById(TemplateModCommon.MOD_ID).getMods();
		if (infoList.size() > 1) {
			TemplateModCommon.LOGGER.error("Multiple mods for MOD_ID: " + TemplateModCommon.MOD_ID);
		}
		for (IModInfo info : infoList) {
			if (info.getModId().equals(TemplateModCommon.MOD_ID)) {
				versionString = String.valueOf(MavenVersionAdapter.createFromVersionSpec(String.valueOf(info.getVersion())));
				break;
			}
		}
		return versionString;
	}

	public void onServerStarting(ServerStartedEvent event) {
		TemplateModCommon.LOGGER.info(TemplateModCommon.SERVER_START);
	}
}
