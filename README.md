# MCRGB Mod for Minecraft Fabric 1.20.1

-[Modrinth](https://modrinth.com/mod/mcrgb)

-[Curseforge](https://legacy.curseforge.com/minecraft/mc-mods/mcrgb-a-colour-picker-for-minecraft-blocks)

## Requirements:
- Minecraft 1.20.1
- Fabric
- Fabric API
## Features:
MCRGB is a client-side Minecraft Mod designed to assist building. It allows you to search for blocks based on the dominant colours in their textures. 
Press the colour picker key (I by default) to open the colour picker GUI.

![Colour Picker GUI](https://github.com/JacobHerd/MCRGB/blob/main/readme_images/colour_picker_gui.PNG?raw=true)

Use the RGB sliders or HEX code input to select a colour, and the mod will suggest blocks that closely match that colour.
If you are in Creative Mode and have Operator Level Permissions, you can click blocks to give them to yourself. 

In any inventory, you can also see the breakdown of a block's colour info by holding SHIFT while hovering over it. This tells you the percentage each colour takes up in each texture, and which blockstates it corresponds to.
![Tooltip for Pink Concrete Powder with Colour Info](https://github.com/JacobHerd/MCRGB/blob/main/readme_images/tooltip_1.PNG?raw=true) 

![Tooltip for Birch Log with Colour Info](https://github.com/JacobHerd/MCRGB/blob/main/readme_images/tooltip_2.PNG?raw=true) ![Tooltip for Redstone Lamp with Colour Info](https://github.com/JacobHerd/MCRGB/blob/main/readme_images/tooltip_3.PNG?raw=true)

Bonus Feature: You can also give yourself leather armour of any RGB/HEX colour using the colour picker.

![Colour Picker UI with the hex colour "#CEBBED" selected. A preview is shown for leather armour and horse armour](https://github.com/JacobHerd/MCRGB/blob/main/readme_images/cebbed_armour_ui.PNG?raw=true)
![A Player wearing leather armour of the hex colour "#CEBBED"](https://github.com/JacobHerd/MCRGB/blob/main/readme_images/cebbed_armour.png?raw=true)
## How it works:

On first launch, MCRGB will generate a file, located in `.minecraft/mcrgb_colours/file.json`
MCRGB attempts to scan every block texture in the game and calculates the dominant colours by grouping together similar pixels based on their euclidean distance in sRGB space, and calculating the mean average of each group. The results are saved in this file. If you ever need to regenerate the file (if you've changed resource packs or added new mods which add more blocks), click the "Refresh" button in the colour picker UI.

When you input a colour to the colour picker, the list of blocks is sorted by the euclidean distance to each of the dominant colours in each texture. Each texture is weighted according to how much of that colour takes up in the texture. 
