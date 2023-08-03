<h1 align="center">HologramAPI</h1>
</div>
<div align="center">
  <a href="https://github.com/MaximFiedler/HologramAPI/blob/master/LICENSE"><img src="https://img.shields.io/github/license/MaximFiedler/HologramAPI.svg" alt="License"></a>  
<a href="https://github.com/MaximFiedler/HologramAPI/releases"><img src="https://img.shields.io/github/v/tag/MaximFiedler/HologramAPI.svg" alt="Version"></a>  
<a href="https://jitpack.io/#MaximFiedler/HologramAPI"><img src="https://jitpack.io/v/MaximFiedler/HologramAPI.svg" alt="jitpack"></a>  
<img src="https://github.com/MaximFiedler/HologramAPI/assets/114857048/d1d956b4-192c-4117-8483-4d8e5d973678">

</div>

## Installation

- Download .jar from the [latest release](https://github.com/MaximFiedler/HologramAPI/releases)
- Upload it to your server (plugins folder)
- Add the plugin as a dependency to your plugin and use it

Gradle
```
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.MaximFiedler:HologramAPI:1.1.1'
}
```
Maven
```
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.MaximFiedler</groupId>
  <artifactId>HologramAPI</artifactId>
  <version>1.1.1</version>
</dependency>
```

## Examples

Create a hologram

```java
TextHologram hologram = new TextHologram("test_hologram")
                .setText(ChatColor.AQUA + "Hello world!")
                .addLine(ChatColor.RED + "Second line")
                .addLine(ChatColor.DARK_PURPLE + "Third line")
                .setBillboard(Display.Billboard.CENTER)
                .setTextShadow(true)
                .setSize(2,2,2)
                .setTextOpacity((byte) 200)
                .setBackgroundColor(Color.fromARGB(0, 255, 236, 222))
                .spawn(location);
```

Remove all holograms with a specific ID

```java
HologramAPI.getHologramManager().removeAll("test_hologram");
```

Get all holograms with the same ID as a list

```java
List<TextHologram> test_holograms = HologramAPI.getHologramManager().getHologramsByID("test_hologram");
```

I did not test the api yet so if you find any bugs please open an issue
