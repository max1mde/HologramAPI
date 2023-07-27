<h1 align="center">HologramAPI</h1>
</div>
<div align="center">
  <a href="https://github.com/MaximFiedler/HologramAPI/blob/master/LICENSE"><img src="https://img.shields.io/github/license/MaximFiedler/HologramAPI.svg" alt="License"></a>  
<a href="https://github.com/MaximFiedler/HologramAPI/releases"><img src="https://img.shields.io/github/v/tag/MaximFiedler/HologramAPI.svg" alt="Version"></a>  
<a href="https://jitpack.io/#MaximFiedler/HologramAPI"><img src="https://jitpack.io/v/MaximFiedler/HologramAPI.svg" alt="jitpack"></a>  
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
  implementation 'com.github.MaximFiedler:HologramAPI:1.0.4'
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
  <version>1.0.4</version>
</dependency>
```

## Getting started

Create a hologram

```java
TextHologram hologram = new TextHologram("holo")
  .setText("Hello world!")
  .setTextShadow(true)
  .setSize(new Vector3f(2,2,2))
  .spawn(Bukkit.getWorld("world").getSpawnLocation());
```

Remove all holograms with a specific ID

```java
HologramAPI.getHologramAPI().removeAllHologramsByID("YOUR_ID");
```

Get all holograms with the same ID as a list

```java
List<TextHologram> holograms = HologramAPI.getHologramAPI().getHologramsByID("YOUR_ID");
```

I did not test the api yet so if you find any bugs please open an issue
