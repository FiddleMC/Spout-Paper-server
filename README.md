<div align="center">

  <!--<img src="logo.png" alt="Spout logo" width="21%" align="right">-->
  <h1>
    Spout server / Spoutcraft client<br>(Paper,  Fabric)
  </h1>
  <h3>
    Vanilla-compatible server + client
    <br>
    that automatically sends modded content from server to client
  </h3>

[![Discord](https://img.shields.io/discord/1091830813240348732?color=5865F2&label=discord&style=for-the-badge)](https://discord.gg/EduvcVmKS7)
[![Latest version](https://img.shields.io/badge/Latest_version-26.1.2-4fa31a?style=for-the-badge)](https://github.com/ModernSpout/Spout/releases)

</div>

<table>
  <tr>
    <td>
      <a href="design/fire.png"><img alt="Custom fire colors" src="design/fire_small.png"></a>
    </td>
    <td>
      <a href="design/orange.png"><img alt="Custom maple leaves" src="design/orange_small.png"></a>
    </td>
    <td>
      <a href="design/stone.png"><img alt="Custom stone types" src="design/stone_small.png"></a>
    </td>
  </tr>
  <tr>
    <td>
      <a href="design/lantern.png"><img alt="Custom Chinese paper lamps" src="design/lantern_small.png"></a>
    </td>
    <td>
      <a href="design/bookshelves.png"><img alt="Custom bookshelves" src="design/bookshelves_small.png"></a>
    </td>
    <td>
      <a href="design/concrete.png"><img alt="Slabs and stairs for wool, concrete and terracotta" src="design/concrete_small.png"></a>
    </td>
  </tr>
</table>

# Server

## Introduction

Spout lets you add new blocks and items on a Paper server, fully server-side.\
When players join, the new blocks and items will be added client-side too.

&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Automatically sends custom content to clients with the Spoutcraft mod
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Works with other clients too, including vanilla
(with/without resource pack)
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Support for all Bukkit / Spigot / Paper plugins

A Fabric server mod is planned.
Please let us know on [Discord](https://discord.gg/EduvcVmKS7) if you are interested.

## Downloads

[![Download from GitHub](https://img.shields.io/badge/⬇-GitHub-878787?style=for-the-badge)](https://github.com/ModernSpout/Spout/releases/download/1.13/spout-26.1.2-R1.13.jar)

## Installation

The `.jar` file is a drop-in replacement for the Paper JAR, and you can run it the same:

```sh
java -jar spout-26.1.2-R1.13.jar
```

Please report any issues you encounter.
As always, backup your server regularly.

## Adding custom blocks and items

New blocks and items can be added by Paper plugins that support Spout.

### Spout plugin showcase

* [Quark](https://github.com/ModernSpout/Quark-plugin)
* [Chinese paper lamps](https://hangar.papermc.io/Spout/ChinesePaperLamps)
* [Snowy stone bricks](https://hangar.papermc.io/Spout/SnowyStoneBricks)

### Creating a Spout plugin

It's very simple:
1. Create a regular Paper plugin
2. Add the Spout API as a dependency
3. Define your content with a data and resource pack

See the step-by-step guide on the
<a href="https://github.com/ModernSpout/Spout/wiki/*-Making-a-Spout-plugin">wiki</a>!

## Current known issues

* Vanilla clients cannot see block display entities, falling blocks and stonecutter recipes for custom blocks.

# Client

## Introduction

Spoutcraft works like a per-server modloader: the moment you join a Spout server,
the server's modded content will be transferred and added client-side automatically.

&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Supports non-vanilla block shapes, like vertical slabs
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Supports all properties, including breaking speed, light level and textures
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Auto-completion in commands, such as <code>/give</code>

Because it contains no modded blocks or items of itself, the Spoutcraft client mod is super lightweight.
<br>
It is incredibly fast: a server's modded content is downloaded and added in less than a second.
<br>
The client only accepts a server's text description of block and item types.
No server code is ever transferred or executed.
<br>
All custom content is automatically removed the moment you leave a server.

## Downloads

[![Download from GitHub](https://img.shields.io/badge/⬇-GitHub-878787?style=for-the-badge)](https://github.com/ModernSpout/Spout/releases/download/1.13/spoutcraft-1.13.0.jar)
[![Download from Modrinth](https://img.shields.io/badge/⬇-modrinth-2c9448?style=for-the-badge)](https://modrinth.com/mod/spout-client)
[![Download from CurseForge](https://img.shields.io/badge/⬇-curseforge-ba5c3d?style=for-the-badge)](https://www.curseforge.com/minecraft/mc-mods/spout)

## Installation

Place the `.jar` file into the `mods` folder.

Requires [Fabric API](https://modrinth.com/mod/fabric-api).

Most mods are compatible and integrate with Spoutcraft perfectly.\
For example,  mods with custom statistics, shulker box tooltips, minimaps, dynamic lights, etc.
all support the custom blocks and items.

<details>
    <summary>Expand the list of tested mods</summary>
    <br>
    ✅ <b>Compatible</b>
    <ul>
        <li><a href="https://modrinth.com/mod/3dskinlayers">3D Skin Layers</a></li>
        <li><a href="https://modrinth.com/mod/advancements-reloaded">Advancements Reloaded (AdvancementInfo)</a></li>
        <li><a href="https://modrinth.com/mod/allow-portal-guis">Allow Portal GUIs</a></li>
        <li><a href="https://modrinth.com/mod/alternate-current">Alternate Current</a></li>
        <li><a href="https://modrinth.com/mod/ambientsounds">AmbientSounds</a></li>
        <li><a href="https://modrinth.com/mod/appleskin">AppleSkin</a></li>
        <li><a href="https://modrinth.com/mod/badoptimizations">BadOptimizations</a></li>
        <li><a href="https://modrinth.com/mod/balm">Balm</a></li>
        <li><a href="https://modrinth.com/mod/better-advancements">Better Advancements</a></li>
        <li><a href="https://modrinth.com/mod/betterf3">BetterF3</a></li>
        <li><a href="https://modrinth.com/mod/better-log4j-config">Better Log4j Config</a></li>
        <li><a href="https://modrinth.com/mod/better-saved-hotbars-forked">Better Saved Hotbars Forked</a></li>
        <li><a href="https://modrinth.com/mod/better-stats">Better Statistics Screen</a></li>
        <li><a href="https://modrinth.com/mod/bigsignwriter">Big Sign Writer</a></li>
        <li><a href="https://modrinth.com/mod/blur-plus">Blur+</a></li>
        <li><a href="https://modrinth.com/mod/boat-item-view">Boat Item View</a></li>
        <li><a href="https://modrinth.com/mod/bobby">Bobby</a></li>
        <li><a href="https://modrinth.com/mod/bookshelf-lib">Bookshelf</a></li>
        <li><a href="https://modrinth.com/mod/capes">Capes</a></li>
        <li><a href="https://modrinth.com/mod/chat-heads">Chat Heads</a></li>
        <li><a href="https://modrinth.com/mod/chattytimestamps">Chatty Timestamps</a></li>
        <li><a href="https://modrinth.com/mod/cherished-worlds">Cherished Worlds</a></li>
        <li><a href="https://modrinth.com/mod/chunky">Chunky</a></li>
        <li><a href="https://modrinth.com/mod/cloth-config">Cloth Config API</a></li>
        <li><a href="https://modrinth.com/mod/clumps">Clumps</a></li>
        <li><a href="https://modrinth.com/mod/collective">Collective</a></li>
        <li><a href="https://modrinth.com/mod/commandkeys">Command Keys</a></li>
        <li><a href="https://modrinth.com/mod/c2me-fabric">Concurrent Chunk Management Engine (Fabric)</a></li>
        <li><a href="https://modrinth.com/mod/continuity">Continuity</a></li>
        <li><a href="https://modrinth.com/mod/controlling">Controlling</a></li>
        <li><a href="https://modrinth.com/mod/coolrain">Cool Rain</a></li>
        <li><a href="https://modrinth.com/mod/creativecore">CreativeCore</a></li>
        <li><a href="https://modrinth.com/mod/cubes-without-borders">Cubes Without Borders</a></li>
        <li><a href="https://modrinth.com/mod/debugify">Debugify</a></li>
        <li><a href="https://modrinth.com/mod/discount-disable">Discount Disable</a></li>
        <li><a href="https://modrinth.com/mod/distanthorizons">Distant Horizons</a></li>
        <li><a href="https://modrinth.com/mod/dont-hide-my-items">Don't Hide My Items</a></li>
        <li><a href="https://modrinth.com/mod/dynamic-fps">Dynamic FPS</a></li>
        <li><a href="https://modrinth.com/mod/enchantment-descriptions">Enchantment Descriptions</a></li>
        <li><a href="https://modrinth.com/mod/entity-model-features">[EMF] Entity Model Features</a></li>
        <li><a href="https://modrinth.com/mod/entitytexturefeatures">[ETF] Entity Texture Features</a></li>
        <li><a href="https://modrinth.com/mod/entityculling">Entity Culling</a></li>
        <li><a href="https://modrinth.com/mod/essential">Essential</a></li>
        <li><a href="https://modrinth.com/mod/exordium">Exordium</a></li>
        <li><a href="https://modrinth.com/mod/fabric-api">Fabric API</a></li>
        <li><a href="https://modrinth.com/mod/fabric-language-kotlin">Fabric Language Kotlin</a></li>
        <li><a href="https://modrinth.com/mod/fabrishot">Fabrishot</a></li>
        <li><a href="https://modrinth.com/mod/fancymenu">FancyMenu</a></li>
        <li><a href="https://modrinth.com/mod/fast-ip-ping">Fast IP Ping</a></li>
        <li><a href="https://modrinth.com/mod/ferrite-core">FerriteCore</a></li>
        <li><a href="https://modrinth.com/mod/forge-config-api-port">Forge Config API Port</a></li>
        <li><a href="https://modrinth.com/mod/freedesktop-tea">FreeDesktop Tea</a></li>
        <li><a href="https://modrinth.com/mod/fzzy-config">Fzzy Config</a></li>
        <li><a href="https://modrinth.com/mod/geckolib">Geckolib</a></li>
        <li><a href="https://modrinth.com/mod/held-item-info">Held Item Info</a></li>
        <li><a href="https://modrinth.com/mod/immediatelyfast">ImmediatelyFast</a></li>
        <li><a href="https://modrinth.com/mod/inventory-management">Inventory Management</a></li>
        <li><a href="https://modrinth.com/mod/inventory-profiles-next">Inventory Profiles Next</a></li>
        <li><a href="https://modrinth.com/mod/inventory-sorting">Inventory Sorting</a></li>
        <li><a href="https://modrinth.com/mod/iris">Iris Shaders</a></li>
        <li><a href="https://modrinth.com/mod/jade">Jade</a></li>
        <li><a href="https://modrinth.com/plugin/journeymap">JourneyMap</a></li>
        <li><a href="https://modrinth.com/mod/forcecloseworldloadingscreen">kennytvs-epic-force-close-loading-screen-mod-for-fabric</a></li>
        <li><a href="https://modrinth.com/mod/konkrete">Konkrete</a></li>
        <li><a href="https://modrinth.com/mod/krypton">Krypton</a></li>
        <li><a href="https://modrinth.com/mod/ksyxis">Ksyxis</a></li>
        <li><a href="https://modrinth.com/mod/lambdynamiclights">LambDynamicLights</a></li>
        <li><a href="https://modrinth.com/mod/language-reload">Language Reload</a></li>
        <li><a href="https://modrinth.com/mod/litematica">Litematica</a></li>
        <li><a href="https://modrinth.com/mod/libjf">LibJF</a></li>
        <li><a href="https://modrinth.com/mod/lithium">Lithium</a></li>
        <li><a href="https://modrinth.com/mod/locator-heads">Locator Heads</a></li>
        <li><a href="https://modrinth.com/mod/make_bubbles_pop">Make Bubbles Pop</a></li>
        <li><a href="https://modrinth.com/mod/malilib">MaLiLib</a></li>
        <li><a href="https://modrinth.com/mod/mcqoy">McQoy</a></li>
        <li><a href="https://modrinth.com/mod/melody">Melody</a></li>
        <li><a href="https://modrinth.com/mod/midnightlib">MidnightLib</a></li>
        <li><a href="https://modrinth.com/mod/minihud">MiniHUD</a></li>
        <li><a href="https://modrinth.com/mod/mixintrace-reborn">MixinTrace Reborn</a></li>
        <li><a href="https://modrinth.com/mod/modmenu">Mod Menu</a></li>
        <li><a href="https://modrinth.com/mod/morechathistory">More Chat History</a></li>
        <li><a href="https://modrinth.com/mod/moreculling">More Culling</a></li>
        <li><a href="https://modrinth.com/mod/mouse-tweaks">Mouse Tweaks</a></li>
        <li><a href="https://modrinth.com/mod/nbt-autocomplete">NBT Autocomplete</a></li>
        <li><a href="https://modrinth.com/mod/nbt-copy">NBT Copy</a></li>
        <li><a href="https://modrinth.com/mod/no-chat-reports">No Chat Reports</a></li>
        <li><a href="https://modrinth.com/mod/not-enough-animations">Not Enough Animations</a></li>
        <li><a href="https://modrinth.com/mod/no-telemetry">No Telemetry</a></li>
        <li><a href="https://modrinth.com/mod/nullbar">NullBar</a></li>
        <li><a href="https://modrinth.com/mod/optigui">OptiGUI</a></li>
        <li><a href="https://modrinth.com/mod/owo-lib">oωo (owo-lib)</a></li>
        <li><a href="https://modrinth.com/mod/particle-rain">Particle Rain</a></li>
        <li><a href="https://modrinth.com/mod/ping-view">Ping View</a></li>
        <li><a href="https://modrinth.com/plugin/plasmo-voice">Plasmo Voice</a></li>
        <li><a href="https://modrinth.com/mod/polymer">Polymer</a></li>
        <li><a href="https://modrinth.com/mod/presence-footsteps">Presence Footsteps</a></li>
        <li><a href="https://modrinth.com/mod/puzzles-lib">Puzzles Lib</a></li>
        <li><a href="https://modrinth.com/mod/reeses-sodium-options">Reese's Sodium Options</a></li>
        <li><a href="https://modrinth.com/mod/rrls">Remove Reloading Screen</a></li>
        <li><a href="https://modrinth.com/mod/resourceful-config">Resourceful Config</a></li>
        <li><a href="https://modrinth.com/mod/resourceful-lib">Resourceful Lib</a></li>
        <li><a href="https://modrinth.com/mod/shulkerboxtooltip">Shulker Box Tooltip</a></li>
        <li><a href="https://modrinth.com/plugin/simple-voice-chat">Simple Voice Chat</a></li>
        <li><a href="https://modrinth.com/mod/sodium">Sodium</a></li>
        <li><a href="https://modrinth.com/mod/sodium-extra">Sodium Extra</a></li>
        <li><a href="https://modrinth.com/mod/sound-physics-remastered">Sound Physics Remastered</a></li>
        <li><a href="https://modrinth.com/mod/structure-layout-optimizer">Structure Layout Optimizer</a></li>
        <li><a href="https://modrinth.com/mod/tcdcommons">TCDCommons API</a></li>
        <li><a href="https://modrinth.com/mod/terrablender">TerraBlender</a></li>
        <li><a href="https://modrinth.com/mod/placeholder-api">Text Placeholder API</a></li>
        <li><a href="https://modrinth.com/datapack/veinminer">VeinMiner</a></li>
        <li><a href="https://modrinth.com/mod/veinminer-client">VeinMiner Hotkey</a></li>
        <li><a href="https://modrinth.com/mod/vmp-fabric">Very Many Players (Fabric)</a></li>
        <li><a href="https://modrinth.com/mod/visuality">Visuality</a></li>
        <li><a href="https://modrinth.com/mod/voxy">Voxy</a></li>
        <li><a href="https://modrinth.com/mod/xaeros-minimap">Xaero's Minimap</a></li>
        <li><a href="https://modrinth.com/mod/xaeros-world-map">Xaero's World Map</a></li>
        <li><a href="https://modrinth.com/mod/yacl">YetAnotherConfigLib (YACL)</a></li>
        <li><a href="https://modrinth.com/mod/zoomify">Zoomify (Zoom)</a></li>
    </ul>
</details>

<!--
## Next

The next goals of the project are:

* More ways to serve the resource pack
* More types of blocks and items

Afterward, goals of the project are:

* Custom block types, item types, block entities and entities
* Using display entities to display custom blocks to vanilla clients

Don't hesitate to suggest ideas, send in PRs (we will take a serious look at every PR, even if it is only a draft),
or ask to join the project as a developer.
-->

# Acknowledgements

This project is heavily inspired by the original
[Spoutcraft / BukkitContrib](https://github.com/spoutcraft) project.
This project would not exist without the ideas and work that those who worked on it put forward.
Additionally, this project builds on top of the work of the contributors to
[Paper](https://github.com/PaperMC/Paper) and [Spigot](https://www.spigotmc.org/), and
[Fabric](https://fabricmc.net/) and [Sponge](https://spongepowered.org/).

Also, thanks go out to
[Alvinn8](https://github.com/Alvinn8/),
[SoSeDiK](https://github.com/SoSeDiK) and
[zoumath19](https://github.com/zoumath19)
for their contributions to this project.
