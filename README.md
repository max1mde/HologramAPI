<h1 align="center">HologramAPI</h1>


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

