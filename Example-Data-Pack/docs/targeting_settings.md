# Targeting Settings Definition
Defines how variant will target other entities

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/targeting_settings) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/targeting_settings) (Mod Files)

Fields:
- `untamed_targets` - list of entity type ids or tags of entities variant will always try to attack when untamed
- `tamed_targets` - list of entity type ids or tags of entities variant will always try to attack when tamed
- `attacks_owner_target` - boolean, defines whether variant will support you when owner attacks other mob
- `defends_owner` - boolean, defines whether variant will support you when owner is being attacked by other mob
