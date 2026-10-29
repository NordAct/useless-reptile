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

## Documentation
- [Variant Definition](./docs/variant.md)
- [Abilities](./docs/abilities.md)
- [Base Attribute Values](./docs/base_attribute_values.md)
- [Dragon Model](./docs/dragon_model.md)
- [Equipment](./docs/equipment.md)
- [Equipment Inject](./docs/equipment_inject.md)
- [Interaction Settings](./docs/interaction_settings.md)
- [Targeting Settings](./docs/targeting_settings.md)
- [Spawn Conditions](./docs/spawn_conditions.md)