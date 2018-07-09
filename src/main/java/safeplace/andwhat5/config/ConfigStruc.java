package safeplace.andwhat5.config;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class ConfigStruc {

	public ConfigStruc()
	{
		SafePlaces = new ArrayList<>();
		PlayerData = new ArrayList<>();
	}
	
	@Expose
	public ArrayList <SafePlaceStruc> SafePlaces;
	
	@Expose
	public ArrayList<PlayerStruc> PlayerData;
	
	public static ConfigStruc gcon = new ConfigStruc();
	
}