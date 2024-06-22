package com.maximde.hologramapi.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vector3F {
    public final float x;
    public final float y;
    public final float z;

    public Vector3F() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
    }

    public Vector3F(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3F subtract(float x, float y, float z) {
        return new Vector3F(this.x - x, this.y - y, this.z - z);
    }
    public Vector3F subtract(Vector3F other) {
        return this.subtract(other.x, other.y, other.z);
    }
    public Vector3F multiply(float x, float y, float z) {
        return new Vector3F(this.x * x, this.y * y, this.z * z);
    }
    public Vector3F multiply(Vector3F other) {
        return this.multiply(other.x, other.y, other.z);
    }
    public Vector3F multiply(float value) {
        return this.multiply(value, value, value);
    }
    public Vector3F add(float x, float y, float z) {
        return new Vector3F(this.x + x, this.y + y, this.z + z);
    }
    public Vector3F add(Vector3F other) {
        return this.add(other.x, other.y, other.z);
    }
    public static Vector3F zero() {
        return new Vector3F();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Vector3F vec) {
            return this.x == vec.x && this.y == vec.y && this.z == vec.z;
        }
        return false;
    }
}
