# Variant Definition
Variant definition file is main file of dragon's variant, which contains all necessary information for it.

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/variant) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/variant) (Mod Files)

## Common fields
All fields must be specified unless stated otherwise
- `type` - variant type. Each type is an identifier that matches dragon's entity id. Possible values: 
  - `uselessreptile:wyvern`
  - `uselessreptile:moleclaw`
  - `uselessreptile:river_pikehorn`
  - `uselessreptile:lightning_chaser`
  - `uselessreptile:magmamuncher`
- `name` - dragon's variant name as it'll be stored in entity NBT data. Generally recommended to use only lowercase letters with no spaces for it to avoid any possible issues, but on practice all characters are accepted. Each name must be unique for specified type to avoid any possible collision
- `variant_name_key` - translation key for variant name. Used to display variant name in Jade integration and Variant Changing Orb item
- `display_name_key` - optional, translation key for dragon's display name. Replaces default displayed entity name if present
- `dragon_model` - location of `dragon_model` file. Defines variant's appearance. [See documentation](./dragon_model.md)
- `equipment` - location of `equipment` file. Defines what equipment variant can have. [See documentation](./equipment.md)
- `spawn_conditions` - location of `spawn_conditions` file. Defines spawn conditions of the variant. [See documentation](./spawn_conditions.md)
- `base_attribute_values` - location of `base_attribute_values` file. Defines base attribute values of the variant. [See documentation](./base_attribute_values.md)
- `abilities` - location of `abilities` file. Defines what abilities variant can use and their settings. [See documentation](./abilities.md)
- `interaction_settings` - location of `interaction_settings` file. Specifies taming conditions, food items, effect and damage immunities and other direct interactions with the variant. [See documentation](./interaction_settings.md)
- `targeting_settings` - location of `targeting_settings` file. Defines combat behavior of the variant. [See documentation](./targeting_settings.md)

## `uselessreptile:river_pikehorn` specific fields
The following fields are unique only for type `uselessreptile:river_pikehorn`
- `hunt_targets` - list of entity type ids and entity type tags. Specifies which entities this River Pikehorn variant will try to kill during hunt