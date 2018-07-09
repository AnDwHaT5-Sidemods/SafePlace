package safeplace.andwhat5;

import com.pixelmonmod.pixelmon.Pixelmon;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import safeplace.andwhat5.commands.SafePlaceCommand;
import safeplace.andwhat5.config.SafePlaceConfiguration;
import safeplace.andwhat5.events.EventHandler;

@Mod(modid = SafePlace.MODID, version = SafePlace.VERSION, name = SafePlace.NAME, acceptableRemoteVersions = "*",  dependencies = "after:pixelmon")
public class SafePlace {

	public static final String MODID = "safeplace";
	public static final String NAME = "SafePlace";
	public static final String VERSION = "2.3.0";

	@Mod.Instance(SafePlace.MODID)
	public static SafePlace instance;

	public static SafePlaceConfiguration config;
	public static EventHandler eventHandler;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
			eventHandler = new EventHandler();
			Pixelmon.EVENT_BUS.register(eventHandler);
			MinecraftForge.EVENT_BUS.register(eventHandler);
			config = new SafePlaceConfiguration();
			FMLLog.info("initalizating SafePlace v"+VERSION);
	}

	@Mod.EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
			event.registerServerCommand(new SafePlaceCommand());
	}

}
