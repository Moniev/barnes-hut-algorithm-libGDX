package pl.moniev.core.MainEngine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pl.moniev.core.Body.Body;
import pl.moniev.core.Vector.Vector;

public class QuadTree {

  public final QuadTreeNode root;
  public List<Body> bodies;

  public QuadTree(QuadTreeNode root) {
    this.root = root;
    this.bodies = new ArrayList<>();
  }

  public void addBody(Body body) {
    root.addBody(body);
    bodies.add(body);
  }

  public void updateBodies(Float dt) {
    bodies.parallelStream().forEach(body -> {
      synchronized (body) {
        Vector accelerationChange = new Vector(body.acceleration).multiplyInPlace(dt);
        body.velocity.addInPlace(accelerationChange);

        Vector velocityChange = new Vector(body.velocity).multiplyInPlace(dt);
        body.position.addInPlace(velocityChange);

        body.acceleration.setZero();
        body.position.print();
      }
    });
  }

  public void updateGravity() {
    rebuild();

    bodies.parallelStream().forEach(body -> {
      root.accelerate(root, body, 1f, 10f);
    });
  }

  public void resolveCollisions() {
    root.resolveCollisions();
  }

  public void rebuild() {
    root.clear();
    for (Body body : bodies) {
      root.addBody(body);
    }

    root.propagate();
  }

  public void renderBodies(SpriteBatch spriteBatch) {
    for (Body body : bodies) {
      body.sprite.setPosition(body.position.x - body.radius, body.position.y - body.radius);
      body.sprite.draw(spriteBatch);
    }
  }

  public void renderTree(ShapeRenderer shapeRenderer) {
    root.renderTree(root, shapeRenderer);
  }
}
