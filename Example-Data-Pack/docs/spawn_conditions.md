# Spawn Conditions Definition
List of entries with spawn conditions. If variant shouldn't spawn, use built-in `uselessreptile:cannot_spawn` spawn conditions

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/spawn_conditions) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/spawn_conditions) (Mod Files)

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
- `allowed_spawn_reasons` - optional, specifies which spawn reasons are allowed for entry. Acts as whitelist. Cannot be specified with `banned_spawn_reasons` at the same time
- `banned_spawn_reasons` - optional, specifies which spawn reasons are disallowed for entry. Acts as blacklist. Cannot be specified with `allowed_spawn_reasons` at the same time 

To add spawn entry, you have to at least specify `weight`. You probably also should add `allowed_biomes` if you don't want to end up with your custom variant spawning absolutely everywhere (although this field is optional).
File names for spawn entries don't matter. You also can create subfolders within folder of the dragon for which you are adding spawns.
Note: if game is unable to find any variants that can spawn at specific spot, game will not spawn the dragon at all.

List of vanilla spawn reasons for `allowed_spawn_reasons` and `banned_spawn_reasons`: 
- `NATURAL`
- `CHUNK_GENERATION`
- `SPAWNER`
- `STRUCTURE`
- `BREEDING`
- `MOB_SUMMONED`
- `JOCKEY`
- `EVENT`
- `CONVERSION`
- `REINFORCEMENT`
- `TRIGGERED`
- `BUCKET`
- `SPAWN_ITEM_USE`
- `COMMAND`
- `DISPENSER`
- `PATROL`
- `TRIAL_SPAWNER`
- `LOAD`
- `DIMENSION_TRAVEL`