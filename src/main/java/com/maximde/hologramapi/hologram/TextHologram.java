package com.maximde.hologramapi.hologram;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

//TODO remove line method (count lines then remove the line given in the argument)
//TODO also add a remove last and remove first line method

public class TextHologram {
    private @Getter @Setter TextDisplay display;
    private @Getter final String id;
    private @Getter @Setter @Accessors(chain = true) Vector3f size = new Vector3f(1.5F,1.5F,1.5F);
    private @Getter @Setter @Accessors(chain = true) Vector3f translation = new Vector3f(0,0,0);
    private @Getter @Setter @Accessors(chain = true) Quaternionf rightRotation = new Quaternionf(0,0,0,1);
    private @Getter @Setter @Accessors(chain = true) Quaternionf leftRotation = new Quaternionf(0,0,0,1);
    private @Getter @Setter @Accessors(chain = true) String text = "Hologram API";
    private @Getter @Setter @Accessors(chain = true) Display.Billboard billboard = Display.Billboard.CENTER;
    private @Getter @Setter @Accessors(chain = true) Color backgroundColor = Color.fromARGB(100, 222, 222, 222);
    private @Getter @Setter @Accessors(chain = true) byte textOpacity;
    private @Getter @Setter @Accessors(chain = true) Display.Brightness brightness;
    private @Getter @Setter @Accessors(chain = true) TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private @Getter @Setter @Accessors(chain = true) float viewRange = 1;
    private @Getter @Setter @Accessors(chain = true) boolean seeThrough = false;
    private @Getter @Setter @Accessors(chain = true) boolean textShadow = true;
    private @Getter @Setter @Accessors(chain = true) int lineWidth = 200;

    public TextHologram(String id) {
        if(id.contains(" ")) throw new IllegalArgumentException("The ID cannot contain spaces!");
        this.id = id + "_hologram_api";
    }

    /**
     * Applies all properties from this class to the display entity
     */
    public void update() {
        if(this.display == null) return;
        this.display.setText(this.text);
        this.display.setBillboard(this.billboard);
        this.display.setBackgroundColor(this.backgroundColor);
        this.display.setAlignment(alignment);
        this.display.setViewRange(viewRange);
        this.display.setSeeThrough(seeThrough);
        this.display.setShadowed(textShadow);
        this.display.setLineWidth(lineWidth);
        if(this.textOpacity != 0) this.display.setTextOpacity(this.textOpacity);
        if(this.brightness != null) this.display.setBrightness(brightness);
        var transformation = new Transformation(
                this.translation,
                this.leftRotation,
                this.size,
                this.rightRotation
        );
        this.display.setInterpolationDelay(-1);
        this.display.setInterpolationDuration(0);
        this.display.setTransformation(transformation);
    }

    /**
     * Spawns the display and applies all specified properties to it
     * @param location
     */
    public TextHologram spawn(Location location) {
        location.getWorld().spawn(location, TextDisplay.class, textDisplay -> {
            this.display = textDisplay;
            textDisplay.addScoreboardTag(this.id);
            update();
        });
        return this;
    }

    public TextHologram kill() {
        this.display.remove();
        this.display = null;
        return this;
    }

    public TextHologram teleport(Location location) {
        display.teleport(location);
        return this;
    }

    public TextHologram addLine(String text) {
        this.text += "\n" + text;
        return this;
    }

    public TextHologram setSize(float x, float y, float z) {
        setSize(new Vector3f(x, y, z));
        return this;
    }

    public TextHologram setTranslation(float x, float y, float z) {
        setTranslation(new Vector3f(x, y, z));
        return this;
    }

    public TextHologram setRightRotation(float x, float y, float z, float w) {
        setRightRotation(new Quaternionf(x, y, z, w));
        return this;
    }

    public TextHologram setLeftRotation(float x, float y, float z, float w) {
        setLeftRotation(new Quaternionf(x, y, z, w));
        return this;
    }

}
