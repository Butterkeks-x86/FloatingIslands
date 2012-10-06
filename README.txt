FloatingIslands Minecraft Bukkit Server Plugin

This is a plugin that generates a fourth world alongside the Overworld,
the Nether and the End. This world consists solely of tiny islands of dirt
or stone with trees, tall grass, reeds, pumkins and much more...
This plugin is inspired by the SkyblockSurvival, you start with a little set
of items to build a cobble generator first. This allows you to make it over
to other islands and gain their ressources. Use water scaffolding to get
down to the valuable stone islands with redstone and diamond ores.

Installation:
	Simply put FloatingIslands.jar in Bukkit's plugin folder and restart
	your server.

Commands:
	/floatingIslands help - displays a help message.

	/floatingIslands join - join the FloatingIslands realm. Your inventory
	will be toggled for your FloatingIslands inventory. If never joined
	before, you will get a set of start items (see config).
	
	/floatingIslands leave - leave the FloatingIslands realm. Your inventory
	will be toggled for your default inventory.

Configuration:
	level0-min-gen-height and level0-max-gen-height control the generation
	height of the bottom stone islands. level0-gen-probability reflects
	the probability of generating a island in a chunk.
	The same parameters exist for level1, for the main dirt islands.
	generateStructures is unimplemented up to now and unused therefore.
	startItems are the items a player receives upon first join in the
	FloatingIslands realm. Provide them in following manner:
		<type>.<data>:<amount>
	The item type (item id) hast to be provided, the data value and amount
	are optional. Default amount is 1, default data is none. For item and
	data values, refer to the Minecraft Wiki:
		http://www.minecraftwiki.net/wiki/Data_Value
	A set of default start items is provided by the standard conf.yml
	of this plugin.

Permissions:
	floatingIslands.* - Acces to all FloatingIsland commands.
	floatingIslands.join - Allows a player or group to join the
		FloatingIslands relam.
	floatingIslands.leave - Allows a player or group to leave
		the FloatingIslands realm.

2012-10-6 Butterkeks-x86
