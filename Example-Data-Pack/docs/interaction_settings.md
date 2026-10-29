# Interaction Settings Definition
Specifies how various interactions with dragon are handled.

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/interaction_settings) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/interaction_settings) (Mod Files)

Fields:
- `base_taming_progress` - base taming progress for variant. Setting this to -1 will make variant untameable. Should be a positive number, as taming progress works by going down each time certain condition is met (i.e. giving dragon right taming item) until it reaches 0
- `taming_items` - list of items that can be used for taming the variant. Technically optional, but not specifying those makes variant technically untameable. List entries can be specified in 2 ways:
    - By specifying entry as a pair of `"key": value`, where `key` is either item tag or item id and value is an array of 2 integer numbers, where 1st number represents minimal possible taming progress decrease and second is maximum possible decrease (`[min, max]`). Note that in this case entry priority cannot be specified manually and thus will be the lowest
    - By specifying entry as object with following fields:
        - `item` - either item tag or item id
        - `taming_progress_increase` - either an array of 2 integer numbers, where 1st number represents minimal possible taming progress decrease and second is maximum possible decrease (`[min, max]`) or an object with following fields:
            - `min` - minimal possible taming progress decrease
            - `max` - maximum possible taming progress decrease
        - `priority` - (optional) defines priority of entry. If not specified, it'll default to lowest possible value. If several entries that match given item are present, one with highest priority value will be picked. If several matching entries have same priority value, random entry will be picked
- `food_items` - list of items that can be used to heal dragon. For some dragons may be used for other activities (i.e. River Pikehorn will seek its food items when fishing). Technically optional, but leaving this empty will make dragon impossible to be healed with food. List entries can be specified in 2 ways:
    - By specifying entry as a pair of `"key": value`, where `key` is either item tag or item id and value is a positive integer number that represents amount of health that will be restored when dragon consumes the food item. Note that in this case entry priority cannot be specified manually and thus will be the lowest
    - By specifying entry as object with following fields:
        - `item` - either item tag or item id
        - `healing_amount` - amount of health that will be restored when dragon consumes the food item
    - `priority` - (optional) defines priority of entry. If not specified, it'll default to lowest possible value. If several entries that match given item are present, one with highest priority value will be picked. If several matching entries have same priority value, random entry will be picked
- `custom_item_interactions` - list of interactions that can execute commands. All interactions are executed at default operator permission level (2). Entry fields:
    - `item` - item id or item tag
    - `count` - optional, required item count for interaction to happen. If not specified, defaults to 1
    - `components` - optional, required non-default item components for interaction to happen. If not specified, any item matching other criteria will be accepted
    - `tame` - boolean, specifies if interaction can happen with only tamed or untamed dragon
    - `owner_only` - boolean, specifies if interaction can happen only if owner initiates it. Does nothing if `tame` set to false
    - `consume` - specifies if interaction should consume specified item count upon successful interaction
    - `commands` - list of commands to be executed. Entry fields:
        - `command` - command string to be executed
        - `executor` - on whom behalf command will be executed. Possible values
            - `player` - executes command as player who triggered interaction
            - `entity` - executes command as dragon with whom interaction has happened
            - `server` - executes command as server
- `effect_immunities` - list of mob effect ids or/and tags to which variant is immune to
- `damage_type_immunities` - list of damage types ids or/and tags to which variant is immune to
