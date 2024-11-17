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

# Wiki
Code examples & more
https://github.com/max1mde/HologramAPI/wiki/Getting-started

# Example/Showcase Plugin
https://github.com/max1mde/ExampleHologramPlugin

Contributions to this repo or the example plugin are welcome!
