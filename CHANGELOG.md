## Warning: this update contains breaking changes

- New dragon: Magmamuncher
  - Spawns in Nether near lava lakes
  - Comes in 2 variants: Magma and Netherrack
  - Can be tamed with Blaze Rods
  - Has small inventory space
  - Can be put on head
  - When on head and has furnace fuel items in inventory, dragon will attempt to consume those items to apply Fire Resistance effect on owner in case if they catch on fire. Duration of the effect depends on fuel item efficiency (can be adjusted via config or completely turned off)
  - Hates Magma Cubes
  - Can be fed either Magma Cream or Magma Blocks to restore health
  - Sometimes can dig up Magma Blocks, turning those into Depleted Magma. May dig up Coal with a small chance (controlled by loot table)

- New block: Depleted Magma
  - Can be created by Magmamuncher when digging up Magma Block
  - Visually looks like normal Netherrack block and (mostly) behaves the same
  - When destroyed drops Netherrack even with Silk Touch
  - Can turn back into Magma Block over time if it's either in The Nether or near lava

- Added new fields to variant data file:
  - `base_taming_progress` - defines starting taming progress for variant. Can be set to -1 to make variant untameable. **This field is mandatory for all variants**
  - `taming_items` - defines items with which variant can be tamed. Allows specifying taming progress change for item id or tag. Note: for Lightning Chaser by default those lists are empty, but you can fill them yourself to make dragon tameable by giving it something instead of fighting it
  - `food_items` - defines items with which variant can be healed. Allows specifying amount of health that is going to be restored for item id or tag
  - For more info check out example data pack [README](https://github.com/NordAct/useless-reptile/tree/1.21.8/Example-Data-Pack/README.md)

- Removed taming item and food item tags for all dragons since those have been moved to variant data
- Removed `uselessreptile:dragon.regeneration_from_food` attribute and all respective config options since its functionality has been moved to variant data
- Altitude restriction in spawn conditions now can be specified as 2-sized integer array instead on an object
- Sounds in dragon model now have a field `pitch_deviation` that allows to specify range of how much played sound pitch can change. If not specified, default value will be 0.125. Example: if sound pitch is 1 and range is 0.125, played sound pitch can vary between 0.9375 and 1.0625
- Major pathfinding improvements for all dragons. Now poor Pikehorns should get stuck underwater much less often, as well as others in other not convenient places
- Rideable dragons no longer can be leashed due some unintended behaviour that came with 1.21.8 leash rework
- Now attempting to lift off the ground on flying dragon is done by sending request packet from client side instead of checking jump window it from server side. This is done because playing on servers with high ping makes problematic to hit this window
- When bailing out, Lightning Chaser no longer should attempt to pathfind into unloaded chunks. This should fix some scenarios where this behaviour caused too much lag
- Maximum capacity of Vortex Horns is no longer hardcoded and controlled by component `uselessreptile:vortex_horn_capacity`. Note: since this component is also used to track current occupied capacity, due it being changed when updating world to newer mod version you may see that your already existing Vortex horn current occupied capacity was reset to 0. To fix this, simply pick up any dragon in horn or put any dragon out of the horn. After this, horn will update information on current capacity correctly
- Added several new advancements to make it easier to get familiar with some features of the mod that involve interaction with dragons
- Added 2 new modes for Flute: Sit Down and Stand Up. Each mode sits down or stands up all River Pikehorns in flute work radius respectively
- Fixed dragons eating buckets, bowls, bottles and other things that should not be eaten and left out from food items when eating from their inventories
- When giving either food item or taming item to dragon, its remnant (such as bucket from bucket with fish) will correctly be placed in current slot in case if it was last item in the stack instead of slipping in other first available slot in inventory
- Fixed Moleclaw strong attack box block breaking area being inconsistent
- Moleclaw's strong attack box Y coordinate change is now clamped and changes on either +1 or -1 if you're either looking up or down and pass certain threshold in pitch angle when riding it
- Some internal rework has been done for inventory system in order to fix some bugs. This may cause items to end up in wrong slots or being completely lost. **Please remove any items from your dragon inventories before updating**