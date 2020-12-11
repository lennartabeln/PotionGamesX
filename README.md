# PotionGames
> A Minecraft Minigames Plugin for Bukkit Servers!

PotionGames is a mingames plugin that works like SurvivalGames but with Potions and Effects

![](header.png)

## Installation

1. Download the plugin
2. Put the .jar in your plugins folder
3. Download Multiverse-Core
4. Put the .jar in your plugins folder
5. Add `mv.bypass.gamemode.*: true` to your permissions.yml
5. Start your server
6. Change the config.yml in the PotionGames folder like you want it
7. (Optional) Change or add languages to the messages.yml in the PotionGames folder

## Usage

//TODO

### Setup
1. Create lobby `/pg setlobby`
2. Create arena `/pg addarena [name]`
3. Add arena spawns `/pg addspawn [arena]`
4. Add chests to your arena

* Chest-Types
    * End Portal Frame: Normal chest like in SurivalGames
    * Honeycomb Block: Special chest with flame bow and arrows
    * Composter: Shop with potions (Build a Beacon under the Composter to make it shown on the whole map)
    
* Create Stats-Wall
    1. Place 3 Player Heads on a block next to each other
    2. Place 3 Signs at the front of the block
    3. Now use the commands listed below to create a podium
      - Look at the head of the 1(2;3) player on the podium and do: `/pg head1(2;3)`
      - Look at the sign of the 1(2;3) player on the podium and do: `/pg sign1(2;3)`
    
### Commands
`/pg setlobby`
`/pg addarena [name]`
`/pg addspawn [arena]`
`/pg `

### Config


### Permissions


## Release History

* 0.5
    * ADD: Teams
* 0.4
    * ADD: Kits
* 0.3
    * ADD: Shop
* 0.2
    * ADD: ArenaVote-System
* 0.1
    * ADD: SurvivalGames-System with chests giving PotionEffects
