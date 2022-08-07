/*
    PopulationDensity Server Plugin for Minecraft
    Copyright (C) 2011 Ryan Hamshire

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.ryanhamshire.PopulationDensity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

//teleports a player.  useful as scheduled task so that a joining player may be teleported (otherwise error)
class TeleportPlayerTask extends BukkitRunnable
{
    private PopulationDensity instance;
    private Player player;
    private Location destination;
    private boolean makeFallDamageImmune;
    DropShipTeleporter dropShipTeleporter;

    public TeleportPlayerTask(Player player, Location destination, boolean makeFallDamageImmune, PopulationDensity plugin, DropShipTeleporter dropShipTeleporter)
    {
        this.player = player;
        this.destination = destination;
        this.makeFallDamageImmune = makeFallDamageImmune;
        this.instance = plugin;
        this.dropShipTeleporter = dropShipTeleporter;
    }

    public TeleportPlayerTask(Player player, Location destination, boolean makeFallDamageImmune, PopulationDensity plugin)
    {
        this(player, destination, makeFallDamageImmune, plugin, null);
    }

    @Override
    public void run()
    {
        ArrayList<Entity> entitiesToTeleport = new ArrayList<Entity>();

        List<Entity> nearbyEntities = player.getNearbyEntities(5, this.player.getWorld().getMaxHeight(), 5);
        for (Entity entity : nearbyEntities)
        {
            if (entity instanceof Tameable && !(entity instanceof AbstractHorse))
            {
                Tameable tameable = (Tameable)entity;
                if (tameable.isTamed())
                {
                    AnimalTamer tamer = tameable.getOwner();
                    if (tamer != null && player.getUniqueId().equals(tamer.getUniqueId()))
                    {
                        EntityType type = entity.getType();
                        if (type == EntityType.WOLF)
                        {
                            Wolf dog = (Wolf)entity;
                            if (dog.isSitting())
                            {
                                continue;
                            }
                        }
                        else if (type == EntityType.CAT)
                        {
                            Cat cat = (Cat)entity;
                            if (cat.isSitting())
                            {
                                continue;
                            }
                        }

                        entitiesToTeleport.add(entity);
                    }
                }
            } else if (entity instanceof Animals && !(entity instanceof AbstractHorse))
            {
                entitiesToTeleport.add(entity);
            }
            if (entity instanceof Allay) {
                entitiesToTeleport.add(entity);
            }

            if (entity instanceof LivingEntity)
            {
                LivingEntity creature = (LivingEntity)entity;
                if ((creature.isLeashed() && player.equals(creature.getLeashHolder())) || player.equals(creature.getPassenger()))
                {
                    entitiesToTeleport.add(creature);
                }
            }
        }

        player.teleport(destination, TeleportCause.PLUGIN);
        if (this.makeFallDamageImmune)
        {
            dropShipTeleporter.makeEntityFallDamageImmune(player);
        }

        //sound effect
        player.playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

        if (PopulationDensity.instance.config_teleportAnimals)
        {

            for (Entity entity : entitiesToTeleport)
            {
                if (!(entity instanceof LivingEntity)) continue;
                LivingEntity livingEntity = (LivingEntity)entity;
                if(livingEntity instanceof Player) {
                    if (this.makeFallDamageImmune)
                        dropShipTeleporter.makeEntityFallDamageImmune(livingEntity);
                    entity.teleport(destination, TeleportCause.PLUGIN);
                }
                else {
                    boolean onLeash = livingEntity.isLeashed() && livingEntity.getLeashHolder().equals(player);
                    if(onLeash) {
                        livingEntity.setLeashHolder(null);
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                        if(player.isOnline()) {
                            entity.teleport(player.getLocation(), TeleportCause.PLUGIN);
                            if(onLeash) {
                                livingEntity.setLeashHolder(player);
                            }
                        }
                    }, 20 * 8);
                }
            }
        }
    }
}
