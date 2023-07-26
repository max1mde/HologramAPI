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
  implementation 'com.github.User:Repo:Tag'
}
```
Maven
```
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.User</groupId>
  <artifactId>Repo</artifactId>
  <version>Tag</version>
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

I did not test the api yet so if there are bugs please open an issue
