## Warning: this update will break your data packs that add dragon variants if you have any

Also in general this update is very big from side of reworking mod's internals, plenty of which are needed for implementing next dragon coming to the mod. And as nice bonus, a lot of things got moved to being defined in data packs instead of being hardcoded. Updated documentation can be found here: https://github.com/NordAct/useless-reptile/blob/26.1/Example-Data-Pack/README.md

**Please report any bugs if you find any. You can use either GitHub issue tracker or mod's Discord server for that. Sooner you report, sooner it'll get fixed!**

- Severely overhauled Dragon Variant Definition
  - Added Interaction Settings
     - All food items and taming items definitions were moved here along with taming progress
     - Variants now can have effect immunities and damage type immunities
     - New Custom Item Interactions - allows command execution upon interacting with item stack that matches specified conditions (before you ask, no, you can't ban people with that)
    
  - Added Targeting Settings
     - You can specify which entity types variant will persistently target when dragon is tamed or untamed
     - You now can also change for a variant if dragon should defend you or support your offense
    
  - Attribute Modifiers were removed in favor of Base Attribute Values
     - Base Attribute Values allows you directly change base values of dragon's variant attributes
     - Attributes apply each time dragon variant is set, meaning it also applies each time dragon is loaded into the world

  - Spawn Conditions now allow specifying Allowed Spawn Reasons and Banned Spawn Reasons. So if you ever wanted a dragon that spawns only in structure, you can do that now
  - Small adjustments to Equipment list entries regarding saddles to accompany changes with how riding is now handled
  - Variant definition was reworked to make it easier to change things about existing variants and making new ones more organized
  - River Pikehorn hunt targets now can be specified in variant. They're separate from tamed/untamed targets

- Reworked handling of dragon turning behavior
  - Dragons heads now directed at direction where they're looking at instead of playing different animations
  - Rideable dragons now follow rider's look direction, making it possible to control exactly where they're looking
  - Flying dragons will tilt their bodies according to movement direction when they're flying
  - Dragons tails now turn only when they're actually turn their body instead turning them with their head

- Reworked multipart hitbox handling
  - While hitboxes themselves are still hardcoded, they're now a lot friendlier to model and animation changes, since they're now follow locators on the model instead of being hardcoded
  - This change affects Swamp Wyvern and Lightning Chaser
  - This change also adds a requirement of specifically named locators to be presented on the model. More information can be found in docs

- Added server authoritative animation and model processing for rideable dragons
  - This change affects Swamp Wyvern, Lightning Chaser and Moleclaw
  - Animations dragons with server authoritative processing are synchronized with client and generally should be same for all players
  - Passenger entity positions for rideable dragons are now controlled by positions of their respective locators on saddle, meaning they're controlled by animation as well
  - Due to this change, those dragons models and animations are now part of data instead of assets, and for reason of keeping server-client synchronization consistent, their models and animations can be changed only via datapack, including their equipment
  - Camera will also tilt depending on roll of bone passenger position locator belongs to. This behavior can be disabled in client config

- Added griefing config options for all dragons
  - Removed "Dragon Madness" config options since it's functionality is now redundant. You can make dragons target players using datapack instead
  - Removed most of the mob attribute config options since their functionality is now redundant. You can change base attribute values of dragons with datapack instead
  - Updated animations and models for all dragons and saddles if they got them

- River Pikehorns no longer will hunt Pufferfish by default. They still may pick it up and eat if it's lying around as item. Foolish creatures
- Untamed Swamp Wyverns will now head straight towards food item lying on ground if they spot any. No more completely pointless chicken killing
  - You also can use this to make Swamp Wyvern land if you're done waiting for it to land