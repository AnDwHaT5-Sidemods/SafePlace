package uk.co.haxyshideout.safeplace.config;

import com.pixelmonmod.pixelmon.api.events.PixelmonStructureGenerationEvent;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

public class Utilities {

	//Adds an automatically generated SafePlace based on pixelmons world gen event
	public static void addSafePlace(PixelmonStructureGenerationEvent event)
	{
		double x = event.getStructureScattered().getBoundingBoxCenter().getX();
		double y = event.getStructureScattered().getBoundingBoxCenter().getY();
		double z = event.getStructureScattered().getBoundingBoxCenter().getZ();
		duplicationCheck((int)x,(int)z);
		SafePlaceStruc sps = new SafePlaceStruc();
		sps.Name = "G_Pokecenter("+(int)x+","+(int)y+","+(int)z+")";
		sps.Radius = 7;
		sps.SafePlace = new Vec3d(x, y, z);
		
		ConfigStruc.gcon.SafePlaces.add(sps);
		SafePlaceConfiguration.saveConfig();
		//System.out.println("ADDED SAFEPLACE");
	}

	//New method for adding a SafePlace
	public static void addSafePlace(EntityPlayer player, String name, int radius)
	{
		if(player == null)
			return;
		for(SafePlaceStruc s : ConfigStruc.gcon.SafePlaces)
		{
			if(s.Name.equalsIgnoreCase(name))
			{
				CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" A SafePlace under this name already exists!", "");
				return;
			}
		}
		SafePlaceStruc sps = new SafePlaceStruc();
		sps.Name = name;
		sps.Radius = radius;
		sps.SafePlace = player.getPositionVector();
		ConfigStruc.gcon.SafePlaces.add(sps);
		CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" Added SafePlace place at DIM: "+player.dimension+" X: "+((int)player.posX)+" Y: "+((int)player.posY)+" Z: "+((int)player.posZ), "");
		SafePlaceConfiguration.saveConfig();
	}

	//Gets a players information. Mainly for seeing what SafePlaces the player has registered
	public static PlayerStruc getPlayerData(EntityPlayer player)
	{
		for(PlayerStruc s : ConfigStruc.gcon.PlayerData)
		{
			if(s.UUID.equals(player.getUniqueID().toString()))
				return s;		
		}
		PlayerStruc p = new PlayerStruc();
		p.UUID = player.getUniqueID().toString();
		ConfigStruc.gcon.PlayerData.add(p);
		SafePlaceConfiguration.saveConfig();
		return p;

	}
	
	//There seems to be a bug with the pixelmon world gen event. This deletes the initial incorrect gen event.
	public static boolean duplicationCheck(int x, int z)
	{
		for(SafePlaceStruc s : ConfigStruc.gcon.SafePlaces)
		{
			if((int)s.SafePlace.xCoord == x)
				if((int)s.SafePlace.zCoord == z)
				{
					
					ConfigStruc.gcon.SafePlaces.remove(s);
					return false;

				}
		}
		return true;
	}
	
	//Gets the nearest SafePlace to the player. If ignoreConfig = true, It ignores the config value
	//And just gets the literal closest SafePlace. Used for internal purposes.
	public static SafePlaceStruc getNearestSafePlace(EntityPlayer player, boolean ignoreConfig)
	{
		double distance = -1;
		SafePlaceStruc safeplace = null;
		if(SafePlaceConfiguration.requireEntryToSafeplace)
		{
			for(SafePlaceStruc s : ConfigStruc.gcon.SafePlaces)
			{
				if((distance == -1 && (ignoreConfig || getPlayerData(player).VisitedSafePlaces.contains(s.Name))))
				{
					distance = player.getPositionVector().distanceTo(s.SafePlace);
					safeplace = s;
				} 
				else 
				if (player.getPositionVector().distanceTo(s.SafePlace) < distance && (ignoreConfig || getPlayerData(player).VisitedSafePlaces.contains(s.Name)))
				{
					distance = player.getPositionVector().distanceTo(s.SafePlace);
					safeplace = s;
				}
			}
			if(safeplace != null)
			{
				return safeplace;
			}
			else
				return null;
		}
		else
		{
			for(SafePlaceStruc s : ConfigStruc.gcon.SafePlaces)
			{
				if(distance == -1 )
				{
					distance = player.getPositionVector().distanceTo(s.SafePlace);
					safeplace = s;
				}
				else
				{
					if(player.getPositionVector().distanceTo(s.SafePlace) < distance)
					{
						distance = player.getPositionVector().distanceTo(s.SafePlace);
						safeplace = s;
					}
				}
				if(safeplace != null)
				{
					return safeplace;
				}
				else
					return null;
			}
		}
		return null;
	}
	
	//Some old code where we were trying to improve on efficiency 
	
	/*public static SafePlaceStruc getNearestSafePlace(EntityPlayer player, boolean useConfig) {
		Vec3d loc = player.getPositionVector();
        final List<SafePlaceStruc> allSP = ConfigStruc.gcon.SafePlaces;
        allSP.sort((s1, s2) -> loc.distanceTo(s1.SafePlace) < loc.distanceTo(s2.SafePlace) ? -1 : 1);
        if (useConfig && SafePlaceConfiguration.requireEntryToSafeplace) {
            List<String> usableSP = getPlayerData(player).VisitedSafePlaces;
            for (SafePlaceStruc sp : allSP) {
                if (usableSP.contains(sp.Name)) {
                    return sp;
                }
            }
        } else if (allSP.size() > 0) {
            return ConfigStruc.gcon.SafePlaces.get(0);
        }
        return null;
    }
*/
	//Gets all the SafePlaces and lists it to the user.
	public static void listSafePlaces(EntityPlayer player) {
		String message = "";
		if(ConfigStruc.gcon.SafePlaces.size() == 1)
			message = ConfigStruc.gcon.SafePlaces.get(0).Name;
		else
			for(int i = 0; i < ConfigStruc.gcon.SafePlaces.size(); i++)
			{
				if(i == ConfigStruc.gcon.SafePlaces.size()-1)
				{
					message += "and "+ConfigStruc.gcon.SafePlaces.get(i).Name;
				}
				else
				{
					message += ConfigStruc.gcon.SafePlaces.get(i).Name+", ";
				}
			}
		CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" Current SafePlaces: "+message, "");
	}

	//Deletes a SafePlace within 5 blocks of the player
	public static void deleteSafePlace(EntityPlayer player) {
		
		SafePlaceStruc sps = getNearestSafePlace(player, true);
		if(sps == null)
		{
			CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" There are no SafePlaces to delete!", "");
			return;
		}
		if(sps.SafePlace.distanceTo(player.getPositionVector()) > sps.Radius)
		{
			CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" Please stand within the SafePlace that you would like to delete.", "");
			return;
		}
		
		ConfigStruc.gcon.SafePlaces.remove(sps);
		SafePlaceConfiguration.saveConfig();
		CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" Successfully deleted the SafePlace \""+sps.Name+"\"!", "");
	}
	
}
