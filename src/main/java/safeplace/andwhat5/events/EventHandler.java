package safeplace.andwhat5.events;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.PixelmonStructureGenerationEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;

import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import safeplace.andwhat5.config.ConfigStruc;
import safeplace.andwhat5.config.PlayerStruc;
import safeplace.andwhat5.config.SafePlaceConfiguration;
import safeplace.andwhat5.config.SafePlaceStruc;
import safeplace.andwhat5.config.Utilities;

public class EventHandler {

	@SubscribeEvent
	public void onStructureSpawn(PixelmonStructureGenerationEvent event)
	{
		//System.out.println(event.getStructureData().id);
		if(!event.getStructureData().id.contains("gym"))
			System.out.println("HI " + event.getStructureData().id);
	    if ((SafePlaceConfiguration.addNewCenters) && (event.getStructureData().id.startsWith("center"))) {
			Utilities.addSafePlace(event);
			//System.out.println("A Pokecenter has spawned!");

	  }
	}

	@SubscribeEvent
	public void onPlayerBattleEnd(BattleEndEvent event)
	{
		for(BattleParticipant participant: event.results.keySet()){
			if(participant instanceof PlayerParticipant){
				if(((PlayerParticipant)participant).getNextPokemonUUID()==null){
					teleportPlayerToNearestSafePlace(((PlayerParticipant) participant).player);
					PlayerPartyStorage party=Pixelmon.storageManager.getParty(((PlayerParticipant) participant).player);
					party.heal();
					Utilities.removeMoney(((PlayerParticipant) participant).player, party.getHighestLevel());
				}
			}
		}
	    try {
			for(BattleParticipant participant: event.results.keySet()){
				if(participant instanceof PlayerParticipant){
					if (((PlayerParticipant)participant).getNextPokemonUUID()==null) {
						Timer t = new Timer();
						t.schedule(new TimerTask() {

							@Override
							public void run() {
								Pixelmon.storageManager.getParty(((PlayerParticipant) participant).player).updatePartyCache();

							}}, 500);

					}
				}
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
        	if(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers() != null)
        	{
	            List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
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
	
	public void teleportPlayerToNearestSafePlace(EntityPlayerMP player) {
		SafePlaceStruc closestPlace = Utilities.getNearestSafePlace(player, false);
		if(closestPlace != null)
		{
			player.setPositionAndUpdate(closestPlace.SafePlace.x, closestPlace.SafePlace.y, closestPlace.SafePlace.z);
			CommandChatHandler.sendChat(player, TextFormatting.RED+"[SafePlace]"+ TextFormatting.BLUE+" You blacked out and were teleported to the nearest SafePlace!", "");
		}
	}
}
