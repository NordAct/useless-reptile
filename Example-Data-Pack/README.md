# Example data pack

## General information
Showcases how to add custom variant and equipment. All directories added by Useless Reptile have to go in `uselessreptile` folder right after namespace folder.
List of directories and what they do:
- `variant` - stores information on dragon variants
- `abilities` - stores lists of dragon abilities with their settings
- `base_attribute_values` - stores files with lists of base attribute values for variants
- `dragon_model` - stores information about dragon model (used assets, sounds)
- `equipment` - stores lists with information on equipment models for specific items
- `equipment_inject` - allows to inject extra models to specified equipment model lists
- `spawn_conditions` - lists with spawn conditions for variants
- `interaction_settings` - specifies how some interactions with variant will be handled
- `targeting_settings` - specifies how variant will target other entities

Each directory represents dynamic registry. Thus stuff in it can only be added or changed in them only when world is loaded (you have to restart it for changes to be applied). 

Example data pack adds following:
- Variants (by actual names):
  - Swamp Wyvern: `Barren Brown`, `Barren Orange`, `Ender`
  - River Pikehorn: `striped`
  - Moleclaw: `Battleworn`
- Dirt and stone blocks as equipment for helmet slot for Moleclaw. Stone block also protects Moleclaw from light.

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