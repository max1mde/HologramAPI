package com.maximfiedler.hologramapi;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class TextHologram {
    private TextDisplay display;
    private final String id;
    private Vector3f size = new Vector3f(1,1,1);
    private Vector3f translation = new Vector3f(0,0,0);
    private Quaternionf rightRotation = new Quaternionf(0,0,0,1);
    private Quaternionf leftRotation = new Quaternionf(0,0,0,1);
    private String text = "Hologram API";
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private Color backgroundColor = Color.fromARGB(100, 50, 50, 0);
    private byte textOpacity;
    private Display.Brightness brightness;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private float viewRange = 1;
    private boolean seeThrough = false;
    private boolean textShadow = true;
    private int lineWidth = 200;

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

    /*
     * ======================= SETTERS =======================
     */

    public TextHologram setSize(Vector3f size) {
        this.size = size;
        return this;
    }

    public TextHologram setTranslation(Vector3f translation) {
        this.translation = translation;
        return this;
    }

    public TextHologram setRightRotation(Quaternionf rightRotation) {
        this.rightRotation = rightRotation;
        return this;
    }

    public TextHologram setLeftRotation(Quaternionf leftRotation) {
        this.leftRotation = leftRotation;
        return this;
    }

    public TextHologram setText(String text) {
        this.text = text;
        return this;
    }

    public TextHologram setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public TextHologram setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TextHologram setTextOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
        return this;
    }

    public TextHologram setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
        return this;
    }

    public TextHologram setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public TextHologram setViewRange(float viewRange) {
        this.viewRange = viewRange;
        return this;
    }

    public TextHologram setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public TextHologram setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public TextHologram setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    /*
     * ======================= SETTERS =======================
     */

    /*
     * ======================= GETTERS =======================
     */

    public TextDisplay getDisplay() {
        return display;
    }

    public String getId() {
        return id;
    }

    public Vector3f getSize() {
        return size;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Quaternionf getRightRotation() {
        return rightRotation;
    }

    public Quaternionf getLeftRotation() {
        return leftRotation;
    }

    public String getText() {
        return text;
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public byte getTextOpacity() {
        return textOpacity;
    }

    public Display.Brightness getBrightness() {
        return brightness;
    }

    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    public float getViewRange() {
        return viewRange;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    public boolean isTextShadow() {
        return textShadow;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    /*
     * ======================= GETTERS =======================
     */
}
