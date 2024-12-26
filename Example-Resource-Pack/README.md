Example resource pack.

Swamp Wyvern:
- Barren Orange (`barren_orange`)
- Barren Brown (`barren_brown`)
- Ender (`ender`)

Moleclaw:
- Battleworn (`battleworn`) - available via nametag

River Pikehorn
- Striped (`striped`)

Showcases how to add custom variants and override equipment models per variant and also how to add glowing layer.
File name - name of the variant. Variant file must be placed in folder named after dragon's id, which must be in `dragon_model_data` folder.
Swamp Wyvern and Moleclaw variants showcase how to use `equipment_model_overrides` and add glowing layer to the model of both equipment and dragon itself. River Pikehorn variant shows how to add variant that only has different texture from default ones.

Equipment model data must go into `dragon_equipment_model_data` folder if it's defined for all variants. File should be named after dragon's id (i.e. `moleclaw.json` for Moleclaw).
Moleclaw shows how to add equipment models for other items than standard equipment items (aka saddle and armor). But in order for that to work correctly you have to install example data pack too since by default you can't equip those items on Moleclaw.

Fields for dragon model data:
- `model_data` - specifies location of animation, texture and model file for the model
  - `texture` - location of texture file. Must be placed somewhere in textures folder in order to work
  - `model` - location of geo model file. Must be placed in geo folder
  - `animation` - location of animation file. Must be placed in animations folder
  - `cull` - enables culling on model. If not specified, defaults to true
  - `translucent` - allows transparent pixels on model. If not specified, defaults to false

- `nametag_accessible` - defines if variant is accessible via nametag. If not specified, defaults to true

- `equipment_model_overrides` - overrides for equipment models
  - `item` - item id for which model is defined
  - `model_data` - model data of equipment

Fields for equipment model data:
- `item` - item id for which model is defined
- `model_data` - model data of equipment

If equipment data is defined in equipment model data, it'll be available for all dragons independently of their variant. If it's defined as equipment model override, it'll be available only for this variant. If model data for specific item is defined in equipment model data and override, override one will be used.

To add glowing layer to either dragon or equipment, add texture named after texture used by the model with `_glowing` suffix alongside said texture. I.e. if texture named `end.png`, to add glowing layer you have to add texture named `end_glowing.png` in the same folder where `end.png` is.

