</div>
<div align="center">
<a href="https://discord.gg/2UTkYj26B4" target="_blank"><img src="https://img.shields.io/badge/Discord_Server-7289DA?style=flat&logo=discord&logoColor=white" alt="Join Discord Server" style="border-radius: 15px; height: 20px;"></a>
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
- Dynamic leaderboard creation
- Advanced hologram customization
- Attachment and parenting support
- Flexible rendering modes

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
  compileOnly 'com.github.max1mde:HologramAPI:1.4.7'
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
  <version>1.4.7</version>
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

# First Steps

### Initializing HologramManager
```java
private HologramManager hologramManager;

@Override
public void onEnable() {
    hologramManager = HologramAPI.getManager().orElse(null);
    if (hologramManager == null) {
        getLogger().severe("Failed to initialize HologramAPI manager.");
        return;
    }
}
```

If you are shading the library use `HologramAPI.getManager(<Your plugin instance>)` instead!

### Hologram Rendering Modes
```java
// Different rendering modes available
TextHologram hologram = new TextHologram("example", RenderMode.NEARBY);
// Modes include:
// - NEARBY: Render for players near the hologram
// - ALL: Render for all online players
// - VIEWER_LIST: Render only for manually added viewers
// - NONE: Do not render
```

> [!NOTE]  
> Display.Billboard.CENTER = the hologram rotates to the player like a nametag (default value)
> Display.Billboard.FIXED = The holograms rotation is fixed
> Display.Billboard.VERTICAL = The hologram only rotates to the left and right (is horizontally fixed)
> Display.Billboard.HORIZONTAL = The hologram only rotates up and down (is vertically fixed)

### Hologram Creation
```java
TextHologram hologram = new TextHologram("unique_id")
    .setMiniMessageText("<aqua>Hello world!")
    .setSeeThroughBlocks(false)
    .setBillboard(Display.Billboard.VERTICAL)
    .setShadow(true)
    .setScale(1.5F, 1.5F, 1.5F)
    .setTextOpacity((byte) 200)
    .setBackgroundColor(Color.fromARGB(60, 255, 236, 222).asARGB())
    .setAlignment(TextDisplay.TextAlignment.CENTER)
    .setViewRange(1.0)
    .setMaxLineWidth(200);

hologramManager.spawn(hologram, location);
```

### Leaderboard Creation

<img src="https://github.com/user-attachments/assets/68b8ded4-c307-44e9-a746-6777ff9b6205" width="400">

```java
Map<Integer, String> leaderboardData = new LinkedHashMap<>() {{
    put(1, "PlayerOne:1000");
    put(2, "PlayerTwo:950");
    put(3, "PlayerThree:900");
    // ... more entries
}};

TextHologram leaderboard = hologramManager.generateLeaderboard(
    location,
    leaderboardData,
    HologramManager.LeaderboardOptions.builder() // There are even more option in this builder like the title and footer design
        .title("Top Players")
        .showEmptyPlaces(true)
        .scale(1.2f)
        .maxDisplayEntries(10)
        .suffix("kills")
        .build()
);

/*
 Update the leaderboard later if needed
 */
hologramManager.updateLeaderboard(
    leaderboard, 
    updatedData, 
    HologramManager.LeaderboardOptions.builder().build()
);
```

### Setting a hologram as a passenger
```java
hologramManager.attach(hologram, parentEntityId);
```

### Managing Hologram Viewers
```java
hologram.addViewer(player);
hologram.removeViewer(player);
hologram.removeAllViewers();

// The players who see the hologram
List<Player> currentViewers = hologram.getViewers();
```

### Advanced Transformations
```java
hologram.setTranslation(0, 1, 0) 
    .setLeftRotation(0, 1, 0, 0) 
    .setRightRotation(0, 1, 0, 0)
    .update();  // Apply changes (make them visible to the player)
```

### Hologram Retrieval and Management
```java
Optional<TextHologram> retrievedHologram = hologramManager.getHologram("unique_id");

hologramManager.remove("unique_id");

hologramManager.remove(hologram);

hologramManager.removeAll();
```

Contributions to this repo or the example plugin are welcome!
