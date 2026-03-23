# Example data pack

## General information
Showcases how to add custom variant and equipment. All directories added by Useless Reptile have to go in `uselessreptile` folder right after namespace folder.
List of directories and what they do:
- `variant` - stores information on dragon variants that can spawn naturally
- `attribute_modifiers` - stores files with lists of modifiers for variants
- `dragon_model` - stores information about dragon model (used assets, sounds)
- `equipment` - stores lists with information on equipment models for specific items
- `equipment_inject` - allows to inject extra models to specified equipment model lists
- `spawn_conditions` - lists with spawn conditions for variants

Each directory represents dynamic registry. Thus stuff in it can only be added or changed in them only when world is loaded (you have to restart it for changes to be applied). 

Example data pack adds following:
- Variants (by actual names):
  - Swamp Wyvern: `Barren Brown`, `Barren Orange`, `Ender`
  - River Pikehorn: `striped`
  - Moleclaw: `Battleworn`
- Dirt and stone blocks as equipment for helmet slot for Moleclaw. Stone block also protects Moleclaw from light.
- `Barren Brown`, `Barren Orange` and `Battleworn` have custom models for some pieces of equipment
- `Barren Brown`, `Barren Orange`, `Ender` have some sounds altered, `striped` and `Battleworn` have different pitch and volume to default sounds
- `Barren Brown` and `Barren Orange` can spawn in badlands biomes on different altitudes. `Barren Brown` also can spawn in savannas. `Ender` spawns in End biomes. `striped` spawns alongside normal Pikehorns
- `striped` has attribute modifiers that decrease its size and health
- `Barren Brown` and `Barren Orange` showcase different ways of declaring taming and food items

To summon specific variant, you can use following command `/summon uselessreptile:dragon_id ~ ~ ~ {Variant:"VARIANT NAME"}`, where `uselessreptile:dragon_id` - entity id of the dragon, `VARIANT NAME` - name of the variant.
Alternatively, you can give yourself **Variant Changing Orb** and use it to change dragon's variant without using commands.

## `variant` file structure
Allows to add new variants to dragon species
Fields:
- `type` - full entity id of the dragon for which this variant can be applied to
- `name` - name of the variant. Does not have any restrictions *technically* on what characters can be used for them, but in general I would recommend to use lowercase english alphabet letters and avoid spaces to avoid any potential issues.
- `equipment` - id of equipment list file, located in `uselessreptile/equipment` within data. To be equipable by variant, item must be on the list
- `dragon_model` - id of dragon model file, located in `uselessreptile/dragon_model` within data
- `spawn_conditions` - (optional) id of spawn conditions file, located in `uselessreptile/spawn_conditions` within data
- `attribute_modifiers` - (optional) id of attribute modifiers file, located in `uselessreptile/attribute_modifiers` within data
- `display_name_key` - (optional) localisation key for display name of the variant. Note that it'll only be visible if entity does not have custom name
- `variant_name_key` - localisation key for name of the variant. Used to display variant name in Jade tooltip
- `base_taming_progress` - base taming progress for variant. Setting this to -1 will make variant untameable. Should be a positive number, as taming progress works by going down each time certain condition is met (i.e. giving dragon right taming item) until it reaches 0
- `taming_items` - list of items that can be used for taming the variant. Technically optional, but not specifying those makes variant technically untameable. List entries can be specified in 2 ways:
  - By specifying entry as a pair of `"key": value`, where `key` is either item tag or item id and value is an array of 2 integer numbers, where 1st number represents minimal possible taming progress decrease and second is maximum possible decrease (`[min, max]`). Note that in this case entry priority cannot be specified manually and thus will be the lowest
  - By specifying entry as object with following fields:
    - `item` - either item tag or item id
    - `taming_progress_increase` - either an array of 2 integer numbers, where 1st number represents minimal possible taming progress decrease and second is maximum possible decrease (`[min, max]`) or an object with following fields:
      - `min` - minimal possible taming progress decrease
      - `max` - maximum possible taming progress decrease
    - `priority` - (optional) defines priority of entry. If not specified, it'll default to lowest possible value. If several entries that match given item are present, one with highest priority value will be picked. If several matching entries have same priority value, random entry will be picked
- `food_items` - list of items that can be used to heal dragon. For some dragons may be used for other activities (i.e. River Pikehorn will seek its food items when fishing). Technically optional, but leaving this empty will make dragon impossible to be healed with food. List entries can be specified in 2 ways:
  - By specifying entry as a pair of `"key": value`, where `key` is either item tag or item id and value is a positive integer number that represents amount of health that will be restored when dragon consumes the food item. Note that in this case entry priority cannot be specified manually and thus will be the lowest
  - By specifying entry as object with following fields:
    - `item` - either item tag or item id 
    - `healing_amount` - amount of health that will be restored when dragon consumes the food item
  - `priority` - (optional) defines priority of entry. If not specified, it'll default to lowest possible value. If several entries that match given item are present, one with highest priority value will be picked. If several matching entries have same priority value, random entry will be picked

## `attribute_modifiers` file structure
Lists modifiers for attributes that will be applied for variant. Note that for each attribute modifier from this list can be applied only once.
Fields:
- `id` - attribute id
- `amount` - amount at which attribute value will be changed depending on operation
- `operation` - defines how exactly modifier will affect attribute. Allowed values: `add_value` - adds amount to base value; `add_multiplied_base` - multiplies base value of attribute on (1 + amount); `add_multiplied_total` - multiplies total value of attribute on (1 + amount)

## `dragon_model` file structure
Contains information about used model, texture, animation and information for sound keys within animation.
Fields:
- `model_data` - a single object holding all info on used assets for model:
  - `texture` -  resource location of texture file, located in `textures` within assets
  - `model` - resource location of model file, located in `biscuit_roll/models` within assets
  - `animation` -  resource location of model file, located in `biscuit_roll/animations` within assets
  - `cull` - (optional) specifies if model should cull its faces. If not specified, defaults to `true`
  - `translucent` - (optional) specifies if model can have translucent pixels. If not specified, defaults to `false`. If set to `true`, culling will be disabled regardless of what's specified in `cull`
- `sounds` - list of sound keys and information on them:
  - `id` - id of sound to be played when key is triggered
  - `step` - name of sound key in animation
  - `pitch` - (optional) sound pitch, if not specified, defaults to 1
  - `volume` - (optional) sound volume, if not specified, defaults to 1

Due to some technical limitations some sound key names are hardcoded and have to be specified for each model individually to play correctly

|      Dragon      |                      Key names                      |                                                                                 Comment                                                                                  |
|:----------------:|:---------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|     Moleclaw     |          `idle`, `hurt`, `death`, `panic`           |                                      `panic` is played when dragon is panicking due being in too bright spot without any protection                                      |
|  River Pikehorn  |               `idle`, `hurt`, `death`               |                                                                                    -                                                                                     |
|   Swamp Wyvern   |           `idle`, `hurt`, `death`, `bite`           |                                                         `bite` is played when dragon uses melee attack in flight                                                         |
| Lightning Chaser | `idle`, `hurt`, `death`, `roar`, `accept_challenge` | `roar` is played when dragon appears during thunderstorm and sometimes during it, `accept_challenge` is played when you manage to provoke an attack of challenger dragon |

`idle` - sound that dragon emits when idle\
`hurt` - played when dragon is hurt\
`death` - played when dragon is dying

**Note on Lightning Chaser**: it _must_ have locator `breath` on the model as it's used for correct renderer of lightning breath attack

## `equipment` file structure
Lists models for items that are going to be used when the latter are used as equipment.
Fields:
- `parent` - (optional) id of parent equipment list file. If specified, will try to poll models for items that are not specified within current list. It's generally recommended to inherit base lists for each dragon species if you want to change some models for your custom variant.
- `equipment` - list of models to be used for specified items. Specified as a pair of key-object, where key is item id and object is equipment data
  - `hid_bones` - (optional) list of bone names that gets hidden on dragon model when this item is equipped
  - `slot` - slot in which specified item goes. Can be either `helmet`, `chestplate`, `tail_armor` or `saddle`
  - `model_data` - a single object holding all info on used assets for model:
      - `texture` -  resource location of texture file, located in `textures` within assets
      - `model` -  resource location of model file, located in `biscuit_roll/models` within assets
      - `animation` -  resource location of model file, located in `biscuit_roll/animations` within assets. If you wish for your equipment to not have any animations, specify build-in empty animation file for this dragon (`uselessreptile:biscuit_roll/animations/entity/<dragon_id>/empty.animation.json`)
      - `translucent` - (optional) specifies if model can have translucent pixels. If not specified, defaults to `false`
  - `passenger_positions` - (optional) controls entity offset for a passenger (where actual entity gets placed relative to ridden dragon) for items that go into `saddle` slot. Represented as list of 3-sized arrays of floats, where each number represents offset on x, y and z axis respectively. Amount of entries also controls max amount of passengers supported.

**Note for saddles**: to display banner correctly your saddle model _must_ have locators named `banner`. To display any passengers, model must have locators `passengerN` where `N` is an ordinal number of passenger. I.e. if saddle can support 3 passengers, it must have locators called `passenger0` for primary rider (first passenger), `passenger1` for second passenger and `passenger2` for third passenger. For reference, refer to Swamp Wyvern's Dual Saddle model

**Note for Moleclaw helmets**: if you wish your helmet for Moleclaw to protect it from light, you must add item in `uselessreptile:protects_moleclaw_from_light` item tag

## `equipment_inject` file structure
Allows to inject extra equipment entries into specified equipment list without needing to override entire file.
Fields:
- `parent` - id of equipment list file, located in `uselessreptile/equipment` within data, in which listed below equipment will be injected
- `equipment` - list of models to be used for specified items. Specified as a pair of key-object, where key is item id and object is equipment data
  - `hid_bones` - (optional) list of bone names that gets hidden on dragon model when this item is equipped
  - `slot` - slot in which specified item goes. Can be either `helmet`, `chestplate`, `tail_armor` or `saddle`
  - `model_data` - a single object holding all info on used assets for model:
    - `texture` -  resource location of texture file, located in `textures` within assets
    - `model` -  resource location of model file, located in `biscuit_roll/models` within assets
    - `animation` -  resource location of model file, located in `biscuit_roll/animations` within assets. If you wish for your equipment to not have any animations, specify build-in empty animation file for this dragon (`uselessreptile:biscuit_roll/animations/entity/<dragon_id>/empty.animation.json`)
    - `translucent` - (optional) specifies if model can have translucent pixels. If not specified, defaults to `false`
  - `passenger_positions` - (optional) controls entity offset for a passenger (where actual entity gets placed relative to ridden dragon) for items that go into `saddle` slot. Represented as list of 3-sized arrays of floats, where each number represents offset on x, y and z axis respectively. Amount of entries also controls max amount of passengers supported.

## `spawn_conditions` file structure
List of entries with spawn conditions.
Fields:
- `weight` - spawn weight of the variant. Defines how often variant will appear if several variants can spawn on the same spot. Higher the number - higher the chance. Does not affect spawn rates of dragon itself
- `allowed_biomes` - (optional) whitelist of biomes where variant can appear. If not specified, variant can appear in all biomes except ones listed in blacklist tag (and you probably don't want that)
- `banned_biomes` - (optional) blacklist of biomes where variant can't appear. If not specified, variant can appear anywhere where it can appear
- `allowed_blocks` - (optional) whitelist of blocks where variant can appear. If not specified, variant can appear on any block
- `banned_blocks` - (optional) blacklist of blocks where variant can't appear. If not specified, variant can appear anywhere where it can appear
- `altitude` - (optional) Y coordinate range where variant can appear. Can be defined in 2 ways:
  - As an integer array of size of 2, where first number represents minimal world height and second represents maximum world height (`[min, max]`)
  - As object:
    - `min` - (optional) minimal world height for variant to appear. If not specified, no limit on min Y
    - `max` - (optional) maximum world height up to which variant can appear. If not specified, no limit on max Y
- `light_level` - (optional) required light level for dragon to spawn. Can be specified as block and sky light level ranges:
  - `block` - (optional) block light level range. Can be defined in 2 ways:
    - As an integer array of size of 2, where first number represents minimal light level and second represents maximum light level (`[min, max]`)
    - As object:
      - `min` - (optional) minimal light level for variant to appear. If not specified, no limit on min light level
      - `max` - (optional) maximum light level up to which variant can appear. If not specified, no limit on max light level
  - `sky` - (optional) sky light level range. Note that sky light level does not change depending on time of day. Can be defined in 2 ways:
    - As an integer array of size of 2, where first number represents minimal light level and second represents maximum light level (`[min, max]`)
    - As object:
      - `min` - (optional) minimal light level for variant to appear. If not specified, no limit on min light level
      - `max` - (optional) maximum light level up to which variant can appear. If not specified, no limit on max light level
- `spacing` - (optional) defines how much dragons with same id can be within zone of specified size in order to spawn the variant. Note: for convenience, spacing check is ignored when trying to spawn dragon from command, spawner or spawn egg
  - `range` - controls check zone size. Check zone is represented by cube with each side size equaling `range * 2` with center at spawn position
  - `max_entity_count` - max amount of dragons with same id that is allowed to be within check radius for it to pass

To add spawn entry, you have to at least specify `weight`. You probably also should add `allowed_biomes` if you don't want to end up with your custom variant spawning absolutely everywhere (although this field is optional).
File names for spawn entries don't matter. You also can create subfolders within folder of the dragon for which you are adding spawns.
Note: if game is unable to find any variants that can spawn at specific spot, game will not spawn the dragon at all.