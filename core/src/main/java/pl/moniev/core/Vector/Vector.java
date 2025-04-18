package pl.moniev.core.Vector;

import com.badlogic.gdx.math.Vector2;

public class Vector {
    public float x, y;
    public float length;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
        this.length = length();
    }

    public Vector(float[] vector) {
        this.x = vector[0];
        this.y = vector[1];
    }

    public Vector(Vector other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public Vector substract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector substract(float scalar) {
        return new Vector(this.x - scalar, this.y - scalar);
    }

    public Vector subdivide(float scalar) {
        return new Vector(this.x / scalar, this.y / scalar);
    }

    public Vector clamp(float maxLength) {
        float length = this.length();
        if (length > maxLength) {
            return subdivide(length).multiply(maxLength);
        }
        return new Vector(this);
    }

    public Vector multiply(float scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    public float dotProduct(Vector other) {
        return this.x * other.x + this.y * other.y;
    }

    public void set(Vector other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void set(float scalar) {
        this.x = scalar;
        this.y = scalar;
    }

    public float length() {
        return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public float distance(Vector other) {
        return (float) Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public float distance(float x, float y) {
        return (float) Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
    }

    public Vector normalize() { 
        return subdivide(length);
    }

    public String toString() {
        return "x: " + this.x + "y: " + this.y;
    }

    public void print(){
        System.out.println(this.toString());
    }

}
