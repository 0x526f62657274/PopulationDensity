# PopulationDensity

This is a fork of PopulationDensity that adds some features one may or may not find useful. It is not meant to mimic the original plugin.

## Invites
The invite system has been expanded with the following commands.
* `/visit <player name>` will allow visiting players that are offline.
* `/invite <player name>` will notify the invitee that they have been invited, and invites are now persistent. Players can also invite multiple players at a time.
* `/cancelinvite <player name>` will revoke an invite.
* `/invitelist` will show a list of players a player has invited to their home region.

## Animal Teleportation
Animals are no longer teleported into the sky. Instead, when a player teleports, after a short delay, the animal will be teleported to the player, and leashes are preserved. This is mainly for better compatibility with flying animals such as the Allay in 1.19.