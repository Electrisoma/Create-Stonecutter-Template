package com.example.template.forge;

import com.example.template.TemplateMod;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;


@SuppressWarnings("unused")
@Mod(TemplateMod.MOD_ID)
public class TemplateModImpl {

	static IEventBus eventBus;
	static IEventBus forgeBus;


	public TemplateModImpl() {
		eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		forgeBus = MinecraftForge.EVENT_BUS;

		TemplateMod.init();

		forgeBus.addListener(this::onServerStarting);
	}

	// Finds the version for Forge
	public static String findVersion() {
		String versionString = "UNKNOWN";

		List<IModInfo> infoList = ModList.get().getModFileById(TemplateMod.MOD_ID).getMods();
		if (infoList.size() > 1) {
			TemplateMod.LOGGER.error("Multiple mods for MOD_ID: " + TemplateMod.MOD_ID);
		}
		for (IModInfo info : infoList) {
			if (info.getModId().equals(TemplateMod.MOD_ID)) {
				versionString = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
				break;
			}
		}
		return versionString;
	}

	public void onServerStarting(ServerStartedEvent event) {
		TemplateMod.LOGGER.info(TemplateMod.SERVER_START);
	}
}
