## Description
Useless Reptile is a Fabric mod that aimed to bring some dragons into your world that you can tame (or kill).
**Warning:** mod is Work-in-Progress and a lot of stuff may change in the future updates.

[Discord Server](https://discord.gg/JjYE4vEf3s)\
Currently supported game versions: 1.21+

## Showcase
<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/x5ysfiDE4rk" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

## Dependencies
[Geckolib](https://modrinth.com/mod/geckolib), [YetAnotherConfigLib](https://modrinth.com/mod/yacl) and [Fabric API](https://modrinth.com/mod/fabric-api) are **required**.
For mod versions below 0.3.0 [Cloth Config API](https://modrinth.com/mod/cloth-config) is required instead of YetAnotherConfigLib.
Some mod versions for 1.19.2 may require [AzureLib](https://modrinth.com/mod/azurelib) instead of Geckolib

## Features
Note: older versions may not contain all features listed.

### Mobs
- **Swamp Wyvern** - can be found on swamps and tamed with 1-2 stacks of raw chicken. You can ride tamed Swamp Wyverns with use of a saddle. You can also get acid from it by right-clicking with empty bottle and use the acid later as any other potion. Cannot equip armor. \
  Can shoot acid blasts with mediocre accuracy and bite.


- **Moleclaw** - can be found in caves and tamed with 0.5-1 stacks of beetroot. Needs saddle to be ridden, useful for digging. Afraid of light, but can ignore it if Moleclaw Helmet is equipped. \
  By default can only destroy blocks that wooden pickaxe can break. You can use strength or weakness potions to break blocks requiring stronger tool or to completely prevent block breaking.\
  Has only melee AoE attacks.


- **River Pikehorn** - can be found near non-frozen rivers and on beaches. You can tame it with bucket of tropical fish and use flute for extra level of control over your Pikehorns, like sending them to fish for you or attack some mob. Oh, and also you can put one on your head (you have to crouch to put dragon on and off). \
  Has only melee attacks.


- **Lightning Chaser** - can appear during thunderstorms near any player. To tame it you have to defeat it in a fight by taking down to low HP and successfully hitting it with lightning at least 3 times (you can use Trident with Channelling enchantment for that). Can be ridden with saddle on. \
  Lightning breath attacks can trigger same effect as normal lightning strike (i.e. transforming Villagers into Witches, creating charged Creepers, e.t.c.), but without setting target on fire. And also destroys blocks. Shockwave attack can be used to deflect projectiles and push back mobs. Shockwave can be used only if dragon is flying, otherwise dragon can use bite. \
  Both shockwave and lightning breath inflict shock effect that decreases attack and movement speed and obscures vision. \
  __**Note: some pre 0.8.0 mod versions (like versions for Minecraft 1.20.1) have this dragon, but it cannot appear naturally and lacks some parts of AI**__

### Items
- **Vortex Horn** - upgraded version of Goat Horn that can be used to store your dragons inside it
- **Flute** - special item, that allows you more versatile control over your River Pikehorns
- **Wyvern Skin** - initially supposed to be a crafting material for *something*, but currently can only be used to get 2 Leather

### Interaction with dragons
- You can use goat horns (or anything that is considered an instrument by game) to call up dragons even if they're sitting. For that, you have first to "bind" instrument sound to the dragon by right-clicking with the instrument item on the dragon. After that, dragon will respond exclusively on this specific instrument sound.
- To sit down/up your dragon, use stick or goat horn while crouching.
- You can give normal potions to your dragon while crouching.
- To open dragon's inventory, crouch and right-click on dragon with something that is not stick, potion or goat horn and at least one empty hand. Note: some dragons don't have it.
- Rideable dragons can equip **banners** and display them as long as saddle is presented.

### Controls
- W/S - forward/backwards
- Left ctrl - fly down (can be rebinded)
- G - primary attack/ability (can be rebinded)
- R - secondary attack/ability (can be rebinded)
  Rideable dragons cannot move sideways

### Custom dragon variants, equipment and spawns
You can add custom dragon variants and dragon equipment via power of data and resource packs, that Mojang gladly made very useful for mod makers too.\
You can read more about those features and see examples here:
- [Example Data Pack](https://github.com/NordAct/useless-reptile/tree/1.21/Example-Data-Pack)
- [Example Resource Pack](https://github.com/NordAct/useless-reptile/tree/1.21/Example-Resource-Pack)

## FAQ
**Q:** I found a bug! How can I report it?\
**A:** If you found a bug, please report it in #mod-feedback-bug-reports at the Discord server, following the instructions.

**Q:** I have some ideas for this mod, where I can leave one?\
**A:** Leave your ideas at the Discord server. Can't say that I'll listen to all of them, but who knows me.

**Q:** I want to share my resource/data pack that I made for this mod, where I can do so?\
**A:** You can promote your packs at the Discord server in #mod-user-generated-content as well as upload to other platforms.

**Q:** Can you backport mod to 1.18/1.16/1.7.10/Beta 1.0/*insert-some-old-version-of-your-choice-here*?\
**A:** No. They are old, lack performance and features that latest versions have

**Q:** Can you port this mod to Forge/other mod loader?\
**A:** I'm not going to do it myself because this is too much for me, and I'm personally not a big fan of Forge bloatness (kinda includes NeoForge too). But I'm open for anyone who is wishing to make port themselves.
But if you wish to use this mod with **NeoForge** here and now, you can use [Sinytra Connector](https://modrinth.com/mod/connector). **Warning: no complaints about incorrect work of the mod in this case will be reviewed. I'll accept bug reports only if you were using mod on Fabric/Quilt**

**Q:** When is update?\
**A:** I can't give any ETA on any update. I have real life stuff to deal with (more specifically my study at university) and I never know how it'll turn out and how much of free time I'll have. Also keep in mind there's only one person (me, the owner) working on this project.

## Usage of the mod and the source code
You are allowed:
- Make monetized videos and streams with the mod
- Use mod for modpacks (excluding ones distributed exclusively in pay-2-play model (i.e. requiring user to buy Patreon subscription in order to play the modpack), **I'll count that as attempt to profit of my work**, not talking about that it's also technically against Minecraft EULA)
- Make submods, addons for the mod or any other project that relies on this mod and publish them, as long as this project is not a modified version of this mod
- Redistribute modified versions of assets within resource packs for this mod
- Partially use the code for your project from this one (by this I mean just don't rip off the
  whole thing, at least mind what you copy)
- Make modified versions of the mod for **private use** (don't publicly distribute those)

## Translations
Mod is currently translated into the following languages:
- English - native support
- Russian - native support
- Portuguese - translation by rauanrochabarbosa and giotm0833 (last updated 0.4.0)
- Turkish - translation by double_axe (last updated 0.1.11)
- Spanish - translation by kota_araya (last updated 0.4.0)
- Polish - translation by karolofgutovo (last updated 0.3.0)
- French - translation by thefiredragon, carputelechat and daelyx (last updated 0.6.0)
- Japanese - translation by mash1133 (last updated 0.6.3)
- Vietnamese by godkyo98 (last updated 0.8.0)
- Hungarian by bombadil6870 (last updated 0.7.4)

If you want to contribute into translating the feel free to suggest your help at [Discord Server](https://discord.gg/JjYE4vEf3s) :)
