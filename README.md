# MCRGB Mod for Minecraft Fabric 1.20.1+

- [Modrinth](https://modrinth.com/mod/mcrgb)

- [Curseforge](https://legacy.curseforge.com/minecraft/mc-mods/mcrgb-a-colour-picker-for-minecraft-blocks)

- [Discord](https://discord.gg/883ybFjSEf)

- Trailer:

[![Youtube Trailer](https://github.com/bacco-bacco/MCRGB/assets/22712685/83dd58c1-7452-449e-bb7d-36e930dd1182)]([https://www.youtube.com/watch?v=YOUTUBE_VIDEO_ID_HERE](https://www.youtube.com/watch?v=MNBBPMVZp5g))

## Requirements:
- Minecraft 1.20.1+
- Fabric
- Fabric API
## Reccomended: 
- Cloth Config

## Features:
### A Colour Picker for Minecraft Blocks!
MCRGB is a client-side Minecraft Mod designed to assist building. It allows you to search for blocks based on the dominant colours in their textures. 
Press the colour picker key (I by default) to open the colour picker GUI.
![Colour Picker GUI](https://github.com/bacco-bacco/MCRGB/assets/22712685/2cb6bf2f-5c4c-4ee9-865a-78af099a416c)

### HSV, HSL, RGB and Hex Support!
Input your colours using your favourite colour space, and the mod will suggest blocks that closely match that colour.
If you are in Creative Mode and have Operator Level Permissions, you can click blocks to give them to yourself. 

You can also see the breakdown of a block's colour info by hovering over it. This tells you the percentage each colour takes up in each texture, and which blockstates it corresponds to.
Optional: You can also view this info in regular inventories by changing the config.
![Tooltip for Pink Concrete Powder with Colour Info](https://github.com/bacco-bacco/MCRGB/assets/22712685/1faa3ce7-027a-4a42-99b8-eead07c98fba)

![Tooltip for Birch Log with Colour Info](https://github.com/bacco-bacco/MCRGB/assets/22712685/6cb37ba7-db54-4b4e-8a6c-2755b333b98b)

![Tooltip for Redstone Lamp with Colour Info](https://github.com/bacco-bacco/MCRGB/assets/22712685/c4dcae23-e560-4606-b493-83e4201acb11)

### Build using Colour Theory
![Funky House Built with Complementary Colours](https://github.com/bacco-bacco/MCRGB/assets/22712685/82634e97-aad7-4a53-b664-1bb6d5ccfa24)
![Cottage Built with an Adobe Colour Palette](https://github.com/bacco-bacco/MCRGB/assets/22712685/419a3c95-0197-4b8d-a014-37a91d647b4f)


### Easily /give Yourself Dyed Leather Armour

![Colour Picker UI with the hex colour "#CEBBED" selected. A preview is shown for leather armour and horse armour](https://github.com/bacco-bacco/MCRGB/assets/22712685/67c68653-350b-4aa7-8da0-779bf8f06ee0)

![A Player wearing leather armour of the hex colour "#CEBBED"](https://github.com/bacco-bacco/MCRGB/assets/22712685/b79150e5-5ec7-4820-9935-21795273d3ae)


## How it works:

On first launch, MCRGB will generate a file, located in `.minecraft/mcrgb_colours/file.json`
MCRGB attempts to scan every block texture in the game and calculates the dominant colours by grouping together similar pixels based on their euclidean distance in sRGB space, and calculating the mean average of each group. The results are saved in this file. If you ever need to regenerate the file (if you've changed resource packs or added new mods which add more blocks), click the "Refresh" button in the colour picker UI.

When you input a colour to the colour picker, the list of blocks is sorted by the euclidean distance to each of the dominant colours in each texture. Each texture is weighted according to how much of that colour takes up in the texture. 
