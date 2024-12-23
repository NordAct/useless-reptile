- Added Vortex Horn
  - Vortex Horn is an upgraded version of normal Goat Horn that besides usual functional can store dragons inside and has no cooldown on use, so if you don't have dragons you can just annoy your friends if you have any
  - When dragon is stored inside the horn, it'll take up some capacity. If horn is full, it won't be able to store more dragons
  - Different dragons take up different amount of capacity. Small dragons take only 1, big dragons take up 3
  - To store dragon inside the horn just use it on __your__ dragon, to release the dragon click on the block. When released, last added dragon will always be released first
  - You also can mass capture/release your dragons when crouching
  - When attempting to dp this, sitting dragons will be skipped. You still can capture them in horn individually
  - When released, dragon will automatically be bound to the sound of Vortex Horn in which it was stored in
  - Attempting to release not your dragon may result in an injury
  - Vortex Horn has 5 tiers: normal, iron, gold, diamond and netherite. Each tier increases capacity of the horn
  - Normal Vortex Horn can be crafted combining Breeze Rods with Goat Horn, and Vortex Horn will inherit horn's sound (or any other instrument if you happen to change the recipe). Upgrading it will also require respective to its tier materials and horn of previous tier

- Added `#uselessreptile:moleclaw_saddles`, `#uselessreptile:lightning_chaser_saddles`, `#uselessreptile:lightning_chaser_saddles`, `#uselessreptile:wyvern_saddles` and `#uselessreptile:dragon_saddles` item tags
  - `#uselessreptile:dragon_saddles` is required for correct rendering when saddle is equipped
  - `#uselessreptile:moleclaw_saddles`, `#uselessreptile:lightning_chaser_saddles`, `#uselessreptile:lightning_chaser_saddles` and `#uselessreptile:wyvern_saddles` are tags for items that can be equipped and used as a saddle on dragon. Each tag is responsible for respective dragon. All those tags are by default in `#uselessreptile:dragon_saddles`

- Added Netherite Dragon Armor
  - It can be crafted from Diamond Dragon Armor by applying Netherite Upgrade Template and Netherite ingot in Smiting Table to piece you want to upgrade to netherite
  - Netherite variant of armor not only got better bonuses, but also got different appearance on dragons relative to their normal armor

- Improved logging in case of missing assets specified in model data files

- Fixed z-fighting on Swamp Wyvern's saddle model

- Added `DragonOnItemConsumedEvent` event that fires whenever dragon may consume an item

- Added `#uselessreptile:wyvern_taming_item`, `#uselessreptile:moleclaw_taming_item`, `#uselessreptile:river_pikehorn_taming_item`, `#uselessreptile:wyvern_food`, `#uselessreptile:moleclaw_food`, `#uselessreptile:river_pikehorn_food` and `#uselessreptile:lightning_chaser_food` item tags
  - Food items tags represent items that can be used by or on dragon to heal it
  - In case of River Pikehorn, those are also items that it'll seek during hunt
  - Taming items tags represent items that can be used to tame certain dragons. Note that since Lightning Chaser is tamed in different way, there's no taming item for it, but you still can use its food to instantly tame it

- Remove all attribute multipliers except speed ones, since there's no practical reason to keep those

- Walking animation speed will now account for speed multiplier and changes in base ground speed

- Fix River Pikehorn always having incorrect flying speed and inability to affect those via configs

- Fix River Pikehorn occasionally suffocating in walls

- Dragons now can teleport to owner if they attempt to follow when happen to be too far or stuck. If you don't like this behaviour, you can disable it in config

- `pikehornMinGroupSize` and `pikehornMaxGroupSize` config options were renamed to `riverPikehornMinGroupSize` and `riverPikehornMaxGroupSize` respectively for consistency

- Replaced spawn weight config options with boolean fields that define if dragon can spawn naturally. This change was made since changing spawn weight wasn't actually changing much in terms of spawning and at best could only disable it

- Added client config option to hide info about which dragons can equip specific item