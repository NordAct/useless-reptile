# Base Attribute Valise Definition
Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/base_attribute_values) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/base_attribute_values) (Mod Files)

Represents itself as map, where key - id of attribute, and value is attribute value. I.e.
```json
{
  "minecraft:armor": 4.0,
  "minecraft:attack_damage": 9.0,
  "minecraft:flying_speed": 0.7,
  "uselessreptile:dragon.mining_level": 1.0
}
```
Meaning this dragon variant will have 4 armor, 9 damage, 7 flying speed and mining level of 1. Unspecified attributes, if dragon has them, will be defaulted to whatever default value in code is.\
Supports attributes added by other mods.