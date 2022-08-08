package me.ryanhamshire.PopulationDensity;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Invites {

    private final static String dataLayerFolderPath = "plugins" + File.separator + "PopulationDensityData";
    private final static String invitesPath = dataLayerFolderPath + File.separator + "playerinvites.yml";

    private File invitesDataFile;

    private YamlConfiguration invitesData;

    public Invites() {
        invitesDataFile = new File(invitesPath);
        if(!invitesDataFile.exists()) {
            try {
                invitesDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        invitesData = YamlConfiguration.loadConfiguration(invitesDataFile);
    }

    public List<String> getInviteUUIDList(UUID player) {
        if(!invitesData.contains(player.toString())) {
            invitesData.set(player.toString(), new ArrayList<>());
            try {
                invitesData.save(invitesDataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (List<String>) invitesData.getList(player.toString());
    }

    public List<OfflinePlayer> getInvitesForPlayer(UUID player) {
        List<OfflinePlayer> players = new ArrayList<>();
        for(String invite : this.getInviteUUIDList(player)) {
            OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(UUID.fromString(invite));
            if(p.getName() != null) {
                players.add(p);
            }
        }
        return players;
    }

    public void addInvite(UUID inviter, UUID invitee) throws IllegalArgumentException {
        List<String> invites = (List<String>) this.getInviteUUIDList(inviter);
        if(invites.contains(invitee.toString())) {
            throw new IllegalArgumentException("You have already invited this player.");
        }
        invites.add(invitee.toString());
        invitesData.set(inviter.toString(), invites);
        try {
            invitesData.save(invitesDataFile);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Could not add this invite due to a server error.");
        }

    }

    public void removeInvite(UUID inviter, UUID invitee) throws IllegalArgumentException {
        List<String> invites = (List<String>) this.getInviteUUIDList(inviter);
        if(!invites.contains(invitee.toString())) {
            throw new IllegalArgumentException("This player is not on your invites list.");
        }
        invites.remove(invitee.toString());
        invitesData.set(inviter.toString(), invites);
        try {
            invitesData.save(invitesDataFile);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Could not add this invite due to a server error.");
        }
    }
}
