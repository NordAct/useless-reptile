# Dragon Model Definition
Contains information about used model, texture, animation and information for sound keys within animation.

Examples can be found [here](/Example-Data-Pack/data/example/uselessreptile/dragon_model) (Example Pack) and [here](/src/main/generated/data/uselessreptile/uselessreptile/dragon_model) (Mod Files)

Fields:
- `model_data` - a single object holding all info on used assets for model:
    - `texture` -  resource location of texture file, located in `textures` within assets
    - `model` - resource location of model file, located in `biscuit_roll/models` within assets
    - `animation` -  resource location of model file, located in `biscuit_roll/animations` within assets
    - `cull` - (optional) specifies if model should cull its faces. If not specified, defaults to `true`
    - `translucent` - (optional) specifies if model can have translucent pixels. If not specified, defaults to `false`. If set to `true`, culling will be disabled regardless of what's specified in `cull`
- `sounds` - list of sound keys and information on them:
    - `id` - id of sound to be played when key is triggered
    - `step` - name of sound key in animation
    - `pitch` - (optional) sound pitch, if not specified, defaults to 1
    - `volume` - (optional) sound volume, if not specified, defaults to 1

Due to some technical limitations some sound key names are hardcoded and have to be specified for each model individually to play correctly

|      Dragon      |                      Key names                      |                                                                                 Comment                                                                                  |
|:----------------:|:---------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|     Moleclaw     |          `idle`, `hurt`, `death`, `panic`           |                                      `panic` is played when dragon is panicking due being in too bright spot without any protection                                      |
|  River Pikehorn  |               `idle`, `hurt`, `death`               |                                                                                    -                                                                                     |
|   Swamp Wyvern   |               `idle`, `hurt`, `death`               |                                                                                    -                                                                                     |
| Lightning Chaser | `idle`, `hurt`, `death`, `roar`, `accept_challenge` | `roar` is played when dragon appears during thunderstorm and sometimes during it, `accept_challenge` is played when you manage to provoke an attack of challenger dragon |

`idle` - sound that dragon emits when idle\
`hurt` - played when dragon is hurt\
`death` - played when dragon is dying

Note: if dragon has `uselessreptile:lightning_breath` attack ability, consider adding `breath` locator for better rendering handling

Also, because multipart entity hitboxes are still hardcoded, dragons that use them **must** have locators with specified names:
- Swamp Wyvern: `head`, `neck1`, `neck2`, `neck3`, `neck4`, `neck5`, `front`, `back`, `tail1`, `tail2`, `tail3`, `tail4`, `tail5`, `wing_left`, `wing_right`, `shoulder_arm_left`, `shoulder_arm_right`
- Lightning Chaser: `head`, `neck1`, `neck2`, `neck3`, `front`, `back`, `tail1`, `tail2`, `tail3`, `tail4`, `wing_left`, `wing_right`, `shoulder_arm_left`, `shoulder_arm_right`, `fingers_left`, `fingers_right`

Following dragons have server side processing logic of models, and thus require model and animation files for dragon and its equipment to be located in data instead of assets:
- Swamp Wyvern
- Moleclaw
- Lightning Chaser