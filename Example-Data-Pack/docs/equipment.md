# Equipment Definition
Lists models for items that are going to be used when the latter are used as equipment.

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/equipment) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/equipment) (Mod Files)

Fields:
- `parent` - (optional) id of parent equipment list file. If specified, will try to poll models for items that are not specified within current list. It's generally recommended to inherit base lists for each dragon species if you want to change some models for your custom variant.
- `equipment` - list of models to be used for specified items. Specified as a pair of key-object, where key is item id and object is equipment data
    - `hid_bones` - (optional) list of bone names that gets hidden on dragon model when this item is equipped
    - `slot` - slot in which specified item goes. Can be either `helmet`, `chestplate`, `tail_armor` or `saddle`
    - `model_data` - a single object holding all info on used assets for model:
        - `texture` -  resource location of texture file, located in `textures` within assets
        - `model` -  resource location of model file, located in `biscuit_roll/models` within assets
        - `animation` -  resource location of model file, located in `biscuit_roll/animations` within assets. If you wish for your equipment to not have any animations, specify build-in empty animation file (`uselessreptile:biscuit_roll/animations/entity/empty.animation.json`)
        - `translucent` - (optional) specifies if model can have translucent pixels. If not specified, defaults to `false`
    - `max_passengers` - (optional) specifies max amount of passengers for saddle

**Note for saddles**: to display banner correctly your saddle model _must_ have locators named `banner`. To display any passengers, model must have locators `passengerN` where `N` is an ordinal number of passenger. I.e. if saddle can support 3 passengers, it must have locators called `passenger0` for primary rider (first passenger), `passenger1` for second passenger and `passenger2` for third passenger. For reference, refer to Swamp Wyvern's Dual Saddle model

**Note for Moleclaw helmets**: if you wish your helmet for Moleclaw to protect it from light, you must add item in `uselessreptile:protects_moleclaw_from_light` item tag
