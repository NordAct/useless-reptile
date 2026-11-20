# Equipment Inject Definition
Allows to inject extra equipment entries into specified equipment list without needing to override entire file.

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/equipment_inject) (Example Pack)

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
    - `max_passengers` - (optional) specifies max amount of passengers for saddle
