# Abilities Definition
Abilities definition specifies list of dragon abilities that it can use, ability use conditions and animations it can play when ability is activated.

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/abilities) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/abilities) (Mod Files)

## Common fields
- `type` - id of ability type. Build in ability types: 
  - `uselessreptile:melee_attack`
  - `uselessreptile:block_breaking_melee_attack`
  - `uselessreptile:shockwave_attack`
  - `uselessreptile:lightning_breath_attack`
  - `uselessreptile:shot_attack`
  - `uselessreptile:noop`
- `cooldown_time_seconds` - ability cooldown time in seconds (1 second = 20 ticks)
- `block_other_abilities_if_active` - boolean, defines if ability prevents other abilities from being used/activated is this ability is active
- `animations` - list of conditioned animations that can play when this ability is used. When attempting to play animations, all animations with matching conditions will be played. Entry fields:
  - `controller` - animation controller on which animation will play. Possible values:
    - `main` - main animation controller, where all movement animations are played. Has transition time of 0.5 second, can play only one animation at time
    - `attack` - attack animation controller. Used primarly for ability animations. Has no transition time, can play multiple animations at the same time
    - `blink` - blink animation controller, used to play eye blink animation. Used to play eye blinking animation. Has no transition time. Can play only one animation at a time
    
  - `animations` - list of animation names that can play. Only one animation from the list will be played and all of them have equal chance of being played.
  - `conditions` - list of use conditions for animation to play. For specifications for entries, see "Use Condition Fields" section below.
  - `can_interrupt_playing_ability_animation` - boolean, if set to false, checks if any other ability animations are currently playing on specified controller before trying to play.
  
- `conditions` - list of use conditions for ability being usable by dragon. For specifications for entries, see "Use Condition Fields" section below.
- `ability_type` - optional, used for specifying to which ability button ability will be assigned to for rideable dragons when they're controlled by rider. If several abilities match conditions and have same ability type specified, one will be picked at random. If this field is not specified, rider cannot use this ability while riding the dragon. Fields:
  - `primary` - assigns ability to primary ability key
  - `secondary` - assigns ability to secondary ability key
- `icon` - optional, location of icon texture sprite (relative to `textures/gui/sprite`) that will be displayed in HUD to rider when ability can be used by rider

## Use Condition fields
Each use condition type has it's own fields besides. `uselessreptile:noop`. Noop is a placeholder type that does nothing. Below is presented list of built-in use condition types with their fields

For simplicity, some dragons with specific traits will be referred by groups:
- Flying:
  - River Pikehorn
  - Swamp Wyvern
  - Lightning Chaser

- Rideable:
  - Swamp Wyvern
  - Lightning Chaser
  - Moleclaw

- Head Mount:
  - Magmamuncher
  - River Pikehorn

### Common fields
- `type` - use condition type

### Condition type specific fields
- `uselessreptile:moving`
  - `moving` - boolean, checks if dragon is moving on XZ axis
  - `backwards` - boolean, checks if dragon is moving backwards

- `uselessreptile:turning_state`
  - `turning_state` - current turn direction of dragon. Possible values: `none`, `left`, `right`

- `uselessreptile:flying`
  - `flying` - boolean, checks if dragon is currently flying. Applicable only to Flying dragons

- `uselessreptile:tilt_state`
  - `tilt_state` - current horizontal movement direction of dragon. Possible values: `none`, `up`, `down`. Applicable only to Flying dragons

- `uselessreptile:rideable`
  - `controlled_by_rider` - boolean, checks if dragon is currently controlled by rider. Applicable only to Rideable dragons

- `uselessreptile:head_mount`
  - `is_riding_player` - boolean, checks if dragon is currently on player's head. Applicable only to Head Mount dragons

- `uselessreptile:moleclaw`
  - `is_panicking` - boolean, checks if Moleclaw is currently panicking. Applicable only to Moleclaw

- `uselessreptile:has_ability_active`
  - `active_ability_type` - id of ability type, checks if dragon has any active ability with specified type


## `uselessreptile:melee_attack` and `uselessreptile:block_breaking_melee_attack` specific fields
Melee attack. Damages one or several entities within specified area. Difference between `uselessreptile:melee_attack` and `uselessreptile:block_breaking_melee_attack` is that last one will attempt to break all blocks in area if dragon's mining level allows it.

- `trigger_time_seconds` - time in seconds when ability sets off its effect
- `active_time_seconds` - for how long ability is considered active since moment it's activated
- `aoe` - boolean. If true, attack will damage all entities in area, if false, it'll damage one closest to dragon
- `attack_effects` - list of mob effects that will be applied to damaged target or targets. Entry fields:
  - `id` - mob effect id
  - `amplifier` - optional, effect level minus 1 level (amplifier of 0 equals level 1 of effect). If not present, defaults to 0
  - `duration` - optional, effect duration in ticks. If not present, defaults to 0
  - `ambient` - optional, boolean, shows different background on icon. That's really all what it does. No, I didn't make it, ask Mojang. If not present, defaults to false.
  - `show_particles` - optional, boolean, if effect should show particles. If not present, defaults to true
  - `show_icon` - optional, boolean, if effect icon should be displayed in HUD. If not present, defaults to true
- `set_on_fire` - boolean, if attack should set target on fire
- `attack_box` - list of conditioned boxes that represent attack area. If several boxes match conditions, one will be picked at random. Entry fields:
  - `width` - box width in blocks
  - `height` - box height in blocks
  - `center_offset` - XYZ vector, box position offset from entity's center
  - `vertical_box_movement` - defines how box depends on entity's pitch. Possible values:
    - `none` - box will not move with pitch change
    - `snapped` - if entity's pitch is greater or lesser than 25 degrees, box will move down or up by 1/4 of its height
    - `smooth` - box Y coordinate directly depends on entity's pitch
  - `conditions` - use conditions for this attack box. For specifications for entries, see "Use Condition Fields" section above.

## `uselessreptile:shot_attack` specific fields
Ranged attack. Shoots specified amount of projectiles from specified point. 

- `trigger_time_seconds` - time in seconds when ability sets off its effect
- `active_time_seconds` - for how long ability is considered active since moment it's activated
- `projectile_entity_type` - entity type id of projectile. It must be considered projectile by game. No, you cannot use it to shoot pigs or armor stands
- `projectile_entity_nbt` - entity NBT to be applied to spawned projectiles. Useful for preconfiguring
- `anchor_point` - point will be considered the center for shooting position. Possible values:
  - `eyes` - center of entity with Y at eye level
  - `entity_pos` - center of entity with Y at its feet
  - `multipart_box` - position of entity part with name specified in `multipart_box_name` with Y at its bottom. Can be used only by multipart dragons
- `multipart_box_name` - optional, needs to be specified if `multipart_box` is chosen for `anchor_point`. Represents name of the entity part of multipart entity. Currently only dragons with following part names have them:
  - Swamp Wyvern: `head`, `neck1`, `neck2`, `neck3`, `neck4`, `neck5`, `front`, `back`, `tail1`, `tail2`, `tail3`, `tail4`, `tail5`, `wing_left`, `wing_right`, `shoulder_arm_left`, `shoulder_arm_right`
  - Lightning Chaser: `head`, `neck1`, `neck2`, `neck3`, `front`, `back`, `tail1`, `tail2`, `tail3`, `tail4`, `wing_left`, `wing_right`, `shoulder_arm_left`, `shoulder_arm_right`, `fingers_left`, `fingers_right`
- `anchor_point_offset` - XYZ vector, representing offset from anchor point
- `count` - amount of projectiles to be shot
- `speed` - shot projectiles speed
- `spread` - accuracy of shot
- `ai_attack_distance` - maximum attack range used by combat AI

## `uselessreptile:lightning_breath` specific fields
Ranged attack. Shoots lightning bolt in direction where dragon is looking. Note: for correct rendering it's recommended that dragon model has `breath` locator, since renderer will try to use it as reference for starting point

- `trigger_time_seconds` - time in seconds when ability sets off its effect
- `active_time_seconds` - for how long ability is considered active since moment it's activated
- `anchor_point` - point will be considered the center for shooting position. Possible values:
    - `eyes` - center of entity with Y at eye level
    - `entity_pos` - center of entity with Y at its feet
    - `multipart_box` - position of entity part with name specified in `multipart_box_name` with Y at its bottom. Can be used only by multipart dragons
- `multipart_box_name` - optional, needs to be specified if `multipart_box` is chosen for `anchor_point`. Represents name of the entity part of multipart entity. Currently only dragons with following part names have them:
    - Swamp Wyvern: `head`, `neck1`, `neck2`, `neck3`, `neck4`, `neck5`, `front`, `back`, `tail1`, `tail2`, `tail3`, `tail4`, `tail5`, `wing_left`, `wing_right`, `shoulder_arm_left`, `shoulder_arm_right`
    - Lightning Chaser: `head`, `neck1`, `neck2`, `neck3`, `front`, `back`, `tail1`, `tail2`, `tail3`, `tail4`, `wing_left`, `wing_right`, `shoulder_arm_left`, `shoulder_arm_right`, `fingers_left`, `fingers_right`
- `anchor_point_offset` - XYZ vector, representing offset from anchor point
- `damage_scaling` - damage multiplier
- `color` - lightning color
- `max_length` - max lightning bolt length
- `max_age` - max lifespan of lightning in ticks
- `block_break_power` - power of ability of lightning breath to break blocks

## `uselessreptile:shockwave` specific fields
Defensive ability. Creates electric blob around dragon that gradually expands and pushes back all projectiles and entities and stuns living entities

- `trigger_time_seconds` - time in seconds when ability sets off its effect
- `active_time_seconds` - for how long ability is considered active since moment it's activated
- `projectile_entity_nbt` - entity NBT to be applied to spawned projectiles. Useful for preconfiguring
- `damage_scaling` - damage multiplier
- `color` - shockwave color
- `power` - shockwave's push back strength
- `max_radius` - max shockwave radius
- `radius_change_speed` - shockwave expansion speed per tick