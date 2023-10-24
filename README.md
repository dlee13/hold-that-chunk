# Hold That Chunk

This Fabric loader mod prevents the Minecraft client from immediately unloading chunks when it receives chunk unload packets from the server. Instead, client chunks get periodically cleaned up based on distance away from the player.

This chunk unloading strategy is particularly useful for participating in ice boat racing or elytra course racing on highly active multiplayer servers where servers may struggle to keep up with sending chunk updates to all of the players. Typically, without this mod, a player would start to move into empty chunks when several players on the server are simultaneously traveling at high velocities. The issue is resolved since the cached chunks can be readily redrawn before the fresh chunk data comes in over the network.

You may also increase your view distance by setting the `Ignore Server Render Distance` option in this mod's config.
