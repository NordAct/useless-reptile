Example datapack.\
Adds spawns for custom variants of Swamp Wyvern and River Pikehorn and custom equipment in forms of helmets for Moleclaw.

Swamp Wyvern:
- Barren Orange (`barren_orange`) and Barren Brown (`barren_brown`) - can be found in badlands, orange spawns below Y 80, brown - above Y 80. Brown one also spawns in savanna with no altitude restrictions
- Ender (`ender`) - spawns (drum roll) in the End

River Pikehorn:
- Striped (`striped`) - can be found anywhere where default variants of River Pikehorn can spawn

Besides how to just add spawn entries, Swamp Wyvern also has an example of how to add several entries for same variant (`barren_brown`) with different conditions. 

Example pack also got edited tags for Moleclaw equipment items to show how to add equipment for dragons in specific armor slots. Note that `protects_moleclaw_from_light` tag is part of `moleclaw_helmets` by default. For other equipment tags, please refer to either GitHub repo or mod's .jar file.

Fields:
- `name` - name of the variant
- `weight` - spawn weight of the variant. Defines how often variant will appear if several variants can spawn on the same spot. Higher the number - higher the chance. Does not affect spawn rates of dragon itself
- `allowed_biomes` - whitelist of biomes where variant can appear. If not specified, variant can appear in all biomes except ones listed in blacklist tag (and you probably don't want that)
- `banned_biomes` - blacklist of biomes where variant can't appear. If not specified, variant can appear anywhere where it can appear
- `allowed_blocks` - whitelist of blocks where variant can appear. If not specified, variant can appear on any block
- `banned_blocks` - blacklist of blocks where variant can't appear. If not specified, variant can appear anywhere where it can appear
- `altitude` - Y coordinate range where variant can appear
  - `min` - minimal world height for variant to appear. If not specified, no limit on min Y
  - `max` - maximum world height up to which variant can appear. If not specified, no limit on min Y

To add spawn entry, you have to at least specify `name` and `weight`. You probably also should add `allowed_biomes` if you don't want to end up with your custom variant spawning absolutely everywhere (although this field is optional).
File names for spawn entries don't matter. You also can create subfolders within folder of the dragon for which you are adding spawns.
Note: if game is unable to find any variants that can spawn at specific spot, game will not spawn the dragon at all.

Tag list for dragon equipment:

|      Dragon      |                   Item tag                    |        Equipment piece         |
|:----------------:|:---------------------------------------------:|:------------------------------:|
|     Moleclaw     |       `uselessreptile:moleclaw_helmets`       |             Helmet             |
|     Moleclaw     |     `uselessreptile:moleclaw_chestplates`     |           Chestplate           |
|     Moleclaw     |     `uselessreptile:moleclaw_tail_armor`      |           Tail Armor           |
|     Moleclaw     | `uselessreptile:protects_moleclaw_from_light` | Helmet (With light protection) |
|     Moleclaw     |       `uselessreptile:moleclaw_saddles`       |             Saddle             |
| Lightning Chaser |   `uselessreptile:lightning_chaser_helmets`   |             Helmet             |
| Lightning Chaser | `uselessreptile:lightning_chaser_chestplates` |           Chestplate           |
| Lightning Chaser | `uselessreptile:lightning_chaser_tail_armor`  |           Tail Armor           |
| Lightning Chaser |   `uselessreptile:lightning_chaser_saddles`   |             Saddle             |
|   Swamp Wyvern   |        `uselessreptile:wyvern_saddles`        |             Saddle             |