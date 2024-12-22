</div>
<div align="center">
<a href="https://discord.gg/2UTkYj26B4" target="_blank"><img src="https://img.shields.io/badge/Discord_Server-7289DA?style=flat&logo=discord&logoColor=white" alt="Join Discord Server" style="border-radius: 15px; height: 20px;"></a>
<a href="https://github.com/max1mde/ExampleHologramPlugin"><img src="https://img.shields.io/badge/Example%20plugin-13B8E1" alt="Version"></a>
<a href="https://jitpack.io/#max1mde/HologramLib"><img src="https://jitpack.io/v/max1mde/HologramLib.svg" alt="jitpack"></a> 
<a href="https://github.com/max1mde/HologramLib/releases"><img src="https://img.shields.io/github/downloads/max1mde/HologramLib/total.svg" alt="Downloads"></a>  
<img width="900px" src="https://github.com/user-attachments/assets/75e2fce6-0be0-4621-be38-ff1d0cb5ca0a">
<p>Leave a :star: if you like this library :octocat:</p>
</div>

<div>
<h3>Contents</h3>
• <a href="#features">Features</a><br>
• <a href="#installation">Installation</a><br>
• <a href="#exampleshowcase-plugin">Example Plugin</a>
<br>
<br>
<details>
<summary><a href="#first-steps">First Steps</a></summary>
&nbsp;&nbsp;&nbsp;• <a href="#initializing-hologrammanager">Initializing Manager</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#hologram-rendering-modes">Rendering Modes</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#hologram-creation">Creation</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#leaderboard-creation">Leaderboards</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#setting-a-hologram-as-a-passenger">Passengers</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#managing-hologram-viewers">Viewers</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#advanced-transformations">Transformations</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#hologram-retrieval-and-management">Management</a>
</details>
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
- Download HologramLib-[version]**.jar** file from the [latest release](https://github.com/max1mde/HologramLib/releases)
- Upload the HologramLib-[version]**.jar** and packet events file on your server (_yourserver/**plugins**_ folder)
- Add the plugin as a dependency to your plugin and use it

**Gradle installation**
```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  compileOnly 'com.github.max1mde:HologramLib:1.5.0.1'
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
  <artifactId>HologramLib</artifactId>
  <version>1.5.0.1</version>
  <scope>provided</scope>
</dependency>
```
Add this to your plugin
`plugin.yml`
```yml
depend:
  - HologramLib
```

# Example/Showcase Plugin
https://github.com/max1mde/ExampleHologramPlugin

# First Steps

### Initializing HologramManager
```java
private HologramManager hologramManager;

@Override
public void onEnable() {
    hologramManager = HologramLib.getManager().orElse(null);
    if (hologramManager == null) {
        getLogger().severe("Failed to initialize HologramLib manager.");
        return;
    }
}
```

> [!IMPORTANT]
> If you are shading the library use `HologramLib.getManager(<Your plugin instance>)` instead!

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
    HologramManager.LeaderboardOptions.builder() // There are even more options in this builder like the title and footer design
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
