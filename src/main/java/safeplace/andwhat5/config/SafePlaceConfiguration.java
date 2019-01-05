package safeplace.andwhat5.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraftforge.common.config.Configuration;
import safeplace.andwhat5.SafePlace;

public class SafePlaceConfiguration {

	Configuration config = new Configuration(new File("config/"+ SafePlace.MODID+".cfg"));
	List<String> spawns = new ArrayList<>();
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static boolean addNewCenters;
	public static boolean requireEntryToSafeplace;
	public static boolean loseMoneyOnBlackout;
	public static int moneyLostOnBlackout;

	public SafePlaceConfiguration() {
		addNewCenters = config.get("main", "Should a SafePlace be generated when a Pokecenter is generated in the wild?", false).getBoolean();
		requireEntryToSafeplace = config.get("main", "Should players be required to visit the SafePlace before teleporting there?", true).getBoolean();
		loseMoneyOnBlackout = config.get("money", "Should players lose PokeDollars when they blackout?", true).getBoolean();
		moneyLostOnBlackout = config.get("money", "Money Lost Multiplier (Formula: (Highest Lvl in Party) x (multiplier)", 5).getInt();
		loadConfig();
		
		config.save();
	}

	public static void loadConfig()
	{
		File file = new File("config/SafePlaceLocations.json");
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileReader r;
		try {
			r = new FileReader(file);
			ConfigStruc.gcon = gson.fromJson(r, ConfigStruc.class);
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		if(ConfigStruc.gcon == null)
			ConfigStruc.gcon = new ConfigStruc();
	}
	
	public static void saveConfig()
	{
		File file = new File("config/SafePlaceLocations.json");
		try {
			if(!file.exists())
				file.createNewFile();
			if(ConfigStruc.gcon == null)//this should not happen.
			{
				ConfigStruc.gcon = new ConfigStruc();
			}
			FileWriter w = new FileWriter(file);
			w.write(gson.toJson(ConfigStruc.gcon));
			w.flush();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
