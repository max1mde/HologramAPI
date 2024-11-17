</div>
<div align="center">
<a href="https://github.com/max1mde/HologramAPI/blob/master/LICENSE"><img src="https://img.shields.io/github/license/max1mde/HologramAPI.svg" alt="License"></a>  
<a href="https://github.com/max1mde/HologramAPI/wiki"><img src="https://img.shields.io/badge/Wiki%20page-AF5E86" alt="Version"></a>
<a href="https://github.com/max1mde/ExampleHologramPlugin"><img src="https://img.shields.io/badge/Example%20plugin-13B8E1" alt="Version"></a>
<a href="https://jitpack.io/#max1mde/HologramAPI"><img src="https://jitpack.io/v/max1mde/HologramAPI.svg" alt="jitpack"></a>  
<a href="https://jitpack.io/#max1mde/HologramAPI"><img src="https://sloc.xyz/github/max1mde/HologramAPI" alt="jitpack"></a>  
<img width="800px" src="https://github.com/max1mde/HologramAPI/assets/114857048/d442ef02-aa87-41ed-bfc2-e2e61d6faffd">
</div>

# Features
- Text animations
- Minimessage support
- Packet based
- Per player holograms
- ItemsAdder emoji support

# Installation

- Download packet events https://www.spigotmc.org/resources/80279/
- Download HologramAPI-[version]**.jar** file from the [latest release](https://github.com/max1mde/HologramAPI/releases)
- Upload the HologramAPI-[version]**.jar** and packet events file on your server (_yourserver/**plugins**_ folder)
- Add the plugin as a dependency to your plugin and use it

**Gradle installation**
```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  compileOnly 'com.github.max1mde:HologramAPI:1.3.0'
}
```
**Maven installation**
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.max1mde</groupId>
  <artifactId>HologramAPI</artifactId>
  <version>1.3.0</version>
  <scope>provided</scope>
</dependency>
```
Add this to your plugin
`plugin.yml`
```yml
depend:
  - HologramAPI
```

# Example/Showcase Plugin
https://github.com/max1mde/ExampleHologramPlugin


# First steps
---------------------------------------------------------------------
### Creating a hologram

> Display.Billboard.CENTER = the hologram rotates to the player like a nametag (default value)<br>
> Display.Billboard.FIXED = The holograms rotation is fixed<br>
> Display.Billboard.VERTICAL = The hologram only rotates to the left and right (is horizontally fixed)<br>
> Display.Billboard.HORIZONTAL = The hologram only rotates up and down (is vertically fixed)<br>

```java
TextHologram hologram = new TextHologram("your_hologram_id")
                .setMiniMessageText("<aqua>Hello world!")
                .setSeeThroughBlocks(false)
                .setBillboard(Display.Billboard.VERTICAL)
                .setShadow(true)
                .setScale(1.5F,1.5F,1.5F)
                .setTextOpacity((byte) 200)
                .setBackgroundColor(Color.fromARGB(0/*Opacity*/, 255/*Red*/, 236/*Green*/, 222/*Blue*/).asARGB()); // You can use the https://htmlcolorcodes.com/color-picker/ to get the RGB color you want!
```

Spawn and remove your hologram
```
HologramAPI.getHologram().spawn(hologram, <location>);
HologramAPI.getHologram().remove(hologram);
```

You can change the attributes of the hologram afterwards but you always **need** to call the TextHologram#**update()** method to apply the changes to the hologram

```java
hologram.setSize(0.5F,0.5F,0.5F); // The hologram is now 50% smaller
hologram.setSize(5,5,5); // And now 5 times bigger
hologram.setMiniMessageText("<red>Updated text!")
hologram.update();
```

You can also kill the hologram what only kills the entity not the data in that TextHologram object
This means you can just call the TextHologram#spawn() method
```java
hologram.kill();
hologram.spawn(LOCATION);
```
---------------------------------------------------------------------
### Animations

**Text animation**
This animation changes the text content every _x_ ticks after _x_ ticks
```java
TextAnimation animation = new TextAnimation()
                        .addFrame(ChatColor.RED + "First frame")
                        .addFrame("Second frame")
                        .addFrame("Third frame\nSecond line")
                        .addFrame("Last frame");
```
Default values of speed and delay are 20 ticks (1 second)
You can change these values like that:
```java
animation.setDelay(20 * 5); // The animation starts after 5 seconds
animation.setSpeed(20 * 3); // The text gets updated every 3 seconds
```

**Apply the animation on a hologram**
> If the hologram already has an active animation the new one will be played and the previous cancelled
```java
HologramAPI.getHologramManager().applyAnimation(hologram, animation);
```

**Stop an animation**
```java
HologramAPI.getHologramManager().cancelAnimation(hologram);
```
---------------------------------------------------------------------

Contributions to this repo or the example plugin are welcome!
