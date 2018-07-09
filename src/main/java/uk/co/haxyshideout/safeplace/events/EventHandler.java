package uk.co.haxyshideout.safeplace.events;

import java.util.List;
import java.util.NoSuchElementException;

import com.pixelmonmod.pixelmon.api.events.PixelmonStructureGenerationEvent;
import com.pixelmonmod.pixelmon.api.events.PlayerBattleEndedEvent;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerNotLoadedException;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import uk.co.haxyshideout.safeplace.SafePlace;
import uk.co.haxyshideout.safeplace.config.ConfigStruc;
import uk.co.haxyshideout.safeplace.config.PlayerStruc;
import uk.co.haxyshideout.safeplace.config.SafePlaceConfiguration;
import uk.co.haxyshideout.safeplace.config.SafePlaceStruc;
import uk.co.haxyshideout.safeplace.config.Utilities;

public class EventHandler {

	@SubscribeEvent
	public void onStructureSpawn(PixelmonStructureGenerationEvent event)
	{
		//System.out.println(event.getStructureData().id);
	    if ((SafePlaceConfiguration.addNewCenters) && (event.getStructureData().id.startsWith("center"))) {
			Utilities.addSafePlace(event);
			//System.out.println("A Pokecenter has spawned!");

	  }
	}

	@SubscribeEvent
	public void onPlayerBattleEnd(PlayerBattleEndedEvent event) throws PlayerNotLoadedException
	{
	    try {
	        if (((PlayerStorage)PixelmonStorage.pokeBallManager.getPlayerStorage(event.player).get()).getFirstAblePokemon(event.player.getEntityWorld()) == null) {
	        	teleportPlayerToNearestSafePlace(event.player);
	        }
	      }
	      catch (NoSuchElementException nsee) {
	        nsee.printStackTrace();
	      }
	}

	@SubscribeEvent
    public void onPlayerTicked(PlayerTickEvent e)
    {
        if(e.phase == Phase.END)
        {
        	if(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList() != null)
        	{
	            List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList();
	            for(EntityPlayerMP player: players) {
	                SafePlaceStruc sps = Utilities.getNearestSafePlace(player, true);
	                if (sps != null && sps.SafePlace.distanceTo(player.getPositionVector()) - sps.Radius < 0) {
	                    boolean found = false;
	                    PlayerStruc ps = Utilities.getPlayerData((EntityPlayer) player);
	                    if (!ps.VisitedSafePlaces.contains(sps.Name)) {
	                        ConfigStruc.gcon.PlayerData.remove(ps);
	                        ps.VisitedSafePlaces.add(sps.Name);
	                        ConfigStruc.gcon.PlayerData.add(ps);
	                        SafePlaceConfiguration.saveConfig();
	                        if (SafePlaceConfiguration.requireEntryToSafeplace) {
	                            CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" You have successfully registered the \"" + sps.Name + "\" SafePlace!", "");
	                        }
	                    }
	                }
	            }
            }
        }
    }
	
	/*@SubscribeEvent
    public void onPlayerTicked(PlayerTickEvent e)
    {
        if(e.phase == Phase.END)
        {
            PlayerList p = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

            for(EntityPlayerMP player: p.getPlayerList())
            {
                SafePlaceStruc sps = Utilities.getNearestSafePlace(player, true);
                if(sps != null)
                {
                    if(sps.SafePlace.distanceTo(player.getPositionVector()) - sps.Radius < 0)
                    {
                        boolean found = false;
                        PlayerStruc ps = Utilities.getPlayerData((EntityPlayer)player);
                        for(String s : ps.VisitedSafePlaces)
                        {
                            if(sps.Name.equalsIgnoreCase(s))
                            {
                                found = true;
                            }
                        }
                        if(!found)
                        {
                        	System.out.println("Player has NEVER visited before... adding the data.");
                            ConfigStruc.gcon.PlayerData.remove(ps);
                            ps.VisitedSafePlaces.add(sps.Name);
                            ConfigStruc.gcon.PlayerData.add(ps);
                            SafePlaceConfiguration.saveConfig();
                            if(SafePlaceConfiguration.requireEntryToSafeplace)
                                CommandChatHandler.sendChat(player, "You have successfully registered the "+sps.Name+" SafePlace!", "");
                        }
                    }
                }
            }
        }
    }*/
	
	public void teleportPlayerToNearestSafePlace(EntityPlayerMP player) {
		SafePlaceStruc closestPlace = Utilities.getNearestSafePlace(player, false);
		if(closestPlace != null)
		{
			player.setPositionAndUpdate(closestPlace.SafePlace.xCoord, closestPlace.SafePlace.yCoord, closestPlace.SafePlace.zCoord);
			CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" You blacked out and were teleported to the nearest SafePlace!", "");
		}
	}
}
