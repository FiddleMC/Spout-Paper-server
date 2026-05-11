<div align="center">

  <!--<img src="logo.png" alt="Spout logo" width="21%" align="right">-->
  <h1>
    Spout<br>(for Paper & Bukkit)
  </h1>
  <h3>
    Server software extension<br>letting you add new blocks and items
    <br>
    that are automatically installed on clients
  </h3>

[![Discord](https://img.shields.io/discord/1091830813240348732?color=5865F2&label=discord&style=for-the-badge)](https://discord.gg/EduvcVmKS7)
[![Download latest version](https://img.shields.io/badge/Latest_version-26.1.2-4fa31a?style=for-the-badge)](https://github.com/ModernSpout/Spout/releases/download/26.1.2-R1.0/spout-26.1.2-R1.0.jar)

</div>

<table>
  <tr>
    <td>
      <a href="design/fire.png"><img src="design/fire_small.png"></a>
    </td>
    <td>
      <a href="design/orange.png"><img src="design/orange_small.png"></a>
    </td>
    <td>
      <a href="design/stone.png"><img src="design/stone_small.png"></a>
    </td>
  </tr>
  <tr>
    <td>
      <a href="design/lantern.png"><img src="design/lantern_small.png"></a>
    </td>
    <td>
      <a href="design/bookshelves.png"><img src="design/bookshelves_small.png"></a>
    </td>
    <td>
      <a href="design/concrete.png"><img src="design/concrete_small.png"></a>
    </td>
  </tr>
</table>

## Introduction

Spout is a Paper server extension that lets Bukkit plugins add new blocks and items server-side.
When players join, the new blocks and items will be sent to their client and added client-side.

Spout requires a Paper server.
<!--A Fabric server mod is also in development.-->

&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Support for all Bukkit / Spigot / Paper plugins
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Spout plugins are just Paper plugins
that can add blocks and items on Spout servers
<br>
&nbsp;&nbsp;&nbsp;&nbsp;✓&nbsp;&nbsp;Works with all clients,
with special support for clients with the corresponding
<a href="https://github.com/ModernSpout/Spoutcraft">Fabric mod</a>
<br>

## Spout plugin showcase

* [Quark](https://github.com/ModernSpout/Quark-plugin)
* [Chinese paper lamps](https://github.com/ModernSpout/ChinesePaperLamps-plugin)
* [Snowy stone bricks](https://github.com/ModernSpout/SnowyStoneBricks-plugin)

## Downloads

&nbsp;&nbsp;&nbsp;&nbsp;➞&nbsp;&nbsp;**[Latest version: 26.1.2 (version 1.0)](https://github.com/ModernSpout/Spout/releases/download/26.1.2-R1.0/spout-26.1.2-R1.0.jar)**

* Development versions: download from
  [Actions](https://github.com/ModernSpout/Spout/actions/workflows/build-server.yml),
  under **Artifacts**
* [Older releases](https://github.com/ModernSpout/Spout/releases)

## Installation

The `.jar` file is a drop-in replacement for the Paper JAR, and you can run it the same:

```sh
java -jar spout-26.1.2-R1.0.jar
```

<!--You can place Bukkit / Paper / Spout plugins in the `plugins` folder.-->

<div align="center">
  <table>
    <tr>
      <td valign="center">
        <h1>🔨</h1>
      </td>
      <td valign="center">
        Spout provides some non-trivial features.
        <br>
        It has been tested, but please report any issues you encounter.
        <br>
        Keep backups, and always proceed with care.
      </td>
    </tr>
  </table>
</div>

## Creating a Spout plugin

It's very simple:
1. Create a regular Paper plugin
2. Add the Spout API as a dependency
3. Define your content with a data and resource pack

See the step-by-step guide on the
<a href="https://github.com/ModernSpout/Spout/wiki">wiki</a>!

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
## Current known issues

* Vanilla clients cannot see block display entities, falling blocks and stonecutter recipes for custom blocks.

## Acknowledgements

This project is heavily inspired by the original
[Spoutcraft / BukkitContrib](https://github.com/spoutcraft) project.
This project would not exist without the ideas and work that those who worked on it put forward.
Additionally, this project builds on top of the work of the contributors to
[Paper](https://github.com/PaperMC/Paper) and [Spigot](https://www.spigotmc.org/), and
[Fabric](https://fabricmc.net/) and [Sponge](https://spongepowered.org/).

Also, thanks go out to [Alvinn8](https://github.com/Alvinn8/) and [SoSeDiK](https://github.com/SoSeDiK)
for their significant contributions to this project.
