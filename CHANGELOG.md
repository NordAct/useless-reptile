- 1.21.5 port

- Changed the way inventory is stored. **REMOVE ALL ITEMS OUT OF DRAGON INVENTORY BEFORE UPDATING YOUR WORLD OR ALL OF THEM WILL BE LOST**

- New variant system. Packs made for previous versions will no longer work
  - Docs and examples: [Example Data Pack](https://github.com/NordAct/useless-reptile/tree/1.21.5/Example-Data-Pack), [Example Resource Pack](https://github.com/NordAct/useless-reptile/tree/1.21.5/Example-Resource-Pack)
  - To make making variant more organised process and to deal with some limitations of previous approach, all information about variants is now defined in data pack
  - Actual variants and ones that available via custom name are now defined separately
  - You now can define display name for variant via providing localisation key
  - It's now possible to add new sound keyframes to animations of dragons and make them play sound you want
  - Dragons now can have attribute modifiers based on their variant
  - Other features for new system are still yet to come

- Dragons will no longer stand up in some cases if you try to access inventory while not riding

- You now can access dragon's inventory even if both of your hands are full of items (as long as none of them are interactable ones)

- Updated spawn egg item icons ~~because spawn egg template is no longer a thing *cries*~~