# Vlado's Gross Hacks
### Features:
* Custom Item Textures for thrown tridents and an option to set custom projectiles
* Buttons in inventory to open player stats and charms menu
* A bunch of small options that could help with performance

### Custom Projectiles Guide:

To add a texture you need to register it with .properties file in any resource pack **under "optifine" namespace**. 

**Texture file must end with "_projectile.png"** 

I recommend making a separate RP for that so you don't have to setup everything again after monumenta RP updates. 

After adding your texture you have to reload resources by restarting the game or hitting F3+T.

#### EXAMPLE
Lets say you want a projectile texture for Highwatch Pike.
    
First add a **texture** to your resource pack and name it "**highwatch_pike_projectile.png**"

Then make a "**highwatch_pike_projectile.properties**" file like this
```
type=item
matchItems=trident 
texture=highwatch_pike_projectile 
nbt.plain.display.Name=Highwatch Pike_projectile
```

texture= the name of your texture file,

Name= the name of the item + "_projectile".

Of course you can add a model to properties or an .mcmeta if you need to.
