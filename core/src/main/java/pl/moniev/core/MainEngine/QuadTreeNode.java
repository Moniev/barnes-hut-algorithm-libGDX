package pl.moniev.core.MainEngine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import pl.moniev.core.Body.Body;
import pl.moniev.core.Vector.Vector;


public class QuadTreeNode {

    public final int bodiesLimit;
    public List<Body> bodies;
    public List<QuadTreeNode> children;
    public boolean isLeaf;
    public float mass;
    public Vector massCenter;
    public final Vector lowerPoint, upperPoint;
    public final static float G = 1.0f;
    public final static float MAX_FACTOR = 1f;

    public QuadTreeNode(int particlesLimit, Vector lowerPoint, Vector upperPoint ) {
        this.bodiesLimit = particlesLimit;
        this.lowerPoint = lowerPoint;
        this.upperPoint = upperPoint;
        this.bodies = new ArrayList<>();
        this.children = new ArrayList<>();
        this.isLeaf = true;
        this.mass = 0f;
    }

    public void resolveCollisions() {
        if (isLeaf) {
            if (!bodies.isEmpty()) {
                for(int i = 0; i < bodies.size(); i++) {
                    for (int j = i + 1; j < bodies.size(); j++) {
                        resolvePair(bodies.get(i), bodies.get(j));
                    }
                }
            }
        } else {
            for(QuadTreeNode child : children) {
                child.resolveCollisions();
            }
        }
    }

    private void resolvePair(Body b1, Body b2) {
        float p1x = b1.position.x;
        float p1y = b1.position.y;
        float p2x = b2.position.x;
        float p2y = b2.position.y;

        float r1 = b1.radius;
        float r2 = b2.radius;

        float dX = p2x - p1x;
        float dY = p2y - p1y;
        float r = r1 + r2;

        float dSquared = dX * dX + dY * dY;
        if (dSquared > r * r) {
            return;
        }

        float v1x = b1.velocity.x;
        float v1y = b1.velocity.y;
        float v2x = b2.velocity.x;
        float v2y = b2.velocity.y;

        float vX = v2x - v1x;
        float vY = v2y - v1y;

        float dDotV = dY * vX + dY * vY;

        float m1 = b1.mass;
        float m2 = b2.mass;

        float weight1 = m2 / (m1 + m2);
        float weight2 = m1 / (m1 + m2);

        if (dDotV >= 0.0 && dSquared != 0.0) {
            float dMag = (float)Math.sqrt(dSquared);
            float tmpX = dX * (r / dMag - 1f);
            float tmpY = dY * (r / dMag - 1f);
            b1.position.x -= weight1 * tmpX;
            b1.position.y -= weight1 * tmpY;
            b2.position.x += weight2 * tmpX;
            b2.position.y += weight2 * tmpY;
            return;
        }

        float vSquared = vX * vX + vY * vY;
        float rSquared = r * r;

        float t;
        if (vSquared == 0.0) {
            t = 0f; 
        } else {
            t = (dDotV + (float)Math.sqrt(Math.max(0.0, dDotV * dDotV - vSquared * (dSquared - rSquared)))) / vSquared;
        }

        b1.position.x -= v1x * t;
        b1.position.y -= v1y * t;
        b2.position.x -= v2x * t;
        b2.position.y -= v2y * t;

        p1x = b1.position.x;
        p1y = b1.position.y;
        p2x = b2.position.x;
        p2y = b2.position.y;
        dX = p2x - p1x;
        dY = p2y - p1y;
        dSquared = dX * dX + dY * dY;
        dDotV = dX * vX + dY * vY;

        double tmpX, tmpY;
        if (dSquared == 0.0) {
            tmpX = 0.0;
            tmpY = 0.0;
        } else {
            tmpX = dX * (1.5 * dDotV / dSquared);
            tmpY = dY * (1.5 * dDotV / dSquared);
        }

        v1x += tmpX * weight1;
        v1y += tmpY * weight1;
        v2x -= tmpX * weight2;
        v2y -= tmpY * weight2;

        b1.velocity.x = (float) v1x;
        b1.velocity.y = (float) v1y;
        b2.velocity.x = (float) v2x;
        b2.velocity.y = (float) v2y;

        b1.position.x += v1x * t;
        b1.position.y += v1y * t;
        b2.position.x += v2x * t;
        b2.position.y += v2y * t;
    }

    public void clear() {
        bodies.clear();
        children.clear();
        isLeaf = true;
        mass = 0f;
        massCenter = new Vector(0, 0);
    }

    public void addBody(Body body, boolean skipPropagate) {
        if (!(lowerPoint.x <= body.position.x && body.position.x <= upperPoint.x &&
              lowerPoint.y <= body.position.y && body.position.y <= upperPoint.y)) {
            return;
        }

        if (isLeaf) {
            bodies.add(body);
            if (bodies.size() > bodiesLimit) {
                splitNode();
            }
        } else {
            for (QuadTreeNode child : children) {
                if (child.lowerPoint.x <= body.position.x && body.position.x <= child.upperPoint.x &&
                    child.lowerPoint.y <= body.position.y && body.position.y <= child.upperPoint.y) {
                    child.addBody(body, skipPropagate);
                    break;
                }
            }
        }

        if (!skipPropagate) {
            propagate();
        }
    }

    public void addBody(Body body) {
        addBody(body, false);
    }

    public void splitNode() {
        isLeaf = false;
        Float midX = (lowerPoint.x + upperPoint.x) / 2;
        Float midY = (lowerPoint.y + upperPoint.y) / 2;
    
        QuadTreeNode childA = new QuadTreeNode(bodiesLimit, new Vector(lowerPoint.x, lowerPoint.y), new Vector(midX, midY));
        QuadTreeNode childB = new QuadTreeNode(bodiesLimit, new Vector(midX, lowerPoint.y), new Vector(upperPoint.x, midY));
        QuadTreeNode childC = new QuadTreeNode(bodiesLimit, new Vector(lowerPoint.x, midY), new Vector(midX, upperPoint.y));
        QuadTreeNode childD = new QuadTreeNode(bodiesLimit, new Vector(midX, midY), new Vector(upperPoint.x, upperPoint.y));
    
        children.add(childA);
        children.add(childB);
        children.add(childC);
        children.add(childD);
    
        for (Body body : bodies) {
            for (QuadTreeNode child : children) {
                if (child.lowerPoint.x <= body.position.x && body.position.x <= child.upperPoint.x &&
                    child.lowerPoint.y <= body.position.y && body.position.y <= child.upperPoint.y) {
                    child.addBody(body, true);
                    child.mass += body.mass;
                    break;
                }
            }
        }
    
        bodies.clear();
    }

    public void accelerate(QuadTreeNode root, Body body, Float theta, Float epsilon) {
        body.acceleration = new Vector(0, 0);
        float tSq = theta * theta;
        float eSq = epsilon * epsilon;
    
        ArrayDeque<QuadTreeNode> nodesToVisit = new ArrayDeque<>();
        nodesToVisit.push(root);
    
        while (!nodesToVisit.isEmpty()) {
            QuadTreeNode node = nodesToVisit.pop();
            if (node == null || node.mass == 0.0f) {
                continue;
            }
    
            Vector d = new Vector(node.massCenter.substract(body.position));
            float dSq = d.x * d.x + d.y * d.y;
    
            if (node.isLeaf && node.bodies.size() == 1 && node.bodies.get(0) == body) {
                continue;
            }
    
            float nodeSize = node.upperPoint.x - node.lowerPoint.x;
    
            if (node.isLeaf || (nodeSize * nodeSize < dSq * tSq)) {
                if (dSq < eSq) {
                    continue; 
                }
                float denom = (dSq + eSq) * (float) Math.sqrt(dSq);
                float factor = Math.min(G * node.mass / denom, MAX_FACTOR);
    
                body.acceleration = body.acceleration.add(d.multiply(factor));
            } else {
                nodesToVisit.addAll(node.children);
            }
        }
    }

    public void propagate() {
        if (isLeaf) {
            if (bodies.isEmpty()) {
                mass = 0;
                massCenter = new Vector(0, 0);
            } else {
                Float totalMass = 0f;
                float totalX = 0;
                float totalY = 0;
                for(Body body : bodies) {
                    totalMass += body.mass;
                    totalX += body.position.x * body.mass;
                    totalY += body.position.y * body.mass;
                }
                mass = totalMass;
                massCenter = new Vector(totalX / totalMass, totalY / totalMass);
            }
        } else {
            if (children.isEmpty()) {
                return;
            }
        
            for (QuadTreeNode child : children) {
                child.propagate();
            }
        
            massCenter = new Vector(0, 0);
            mass = 0;
        
            for (QuadTreeNode child : children) {
                massCenter = massCenter.add(child.massCenter.multiply(child.mass));
                mass += child.mass; 
            }
        
            if (mass > 0) {
                massCenter = massCenter.subdivide(mass); 
            }
        }
    }

    public void renderBodies(QuadTreeNode root, SpriteBatch spriteBatch) {
        if (isLeaf) {
            for (Body body : bodies) {
                if (body != null && body.sprite != null && body.sprite.getTexture() != null) {
                    body.sprite.setPosition(body.position.x - body.radius, body.position.y - body.radius);
                    body.sprite.draw(spriteBatch);
                } 
            }
        } else {
            for (QuadTreeNode child : children) {
                if (child != null) {
                    child.renderBodies(child, spriteBatch);
                } 
            }
        }
    }

    public void renderTree(QuadTreeNode root, ShapeRenderer shapeRenderer) {
        if (root == null || shapeRenderer == null) {
            return;
        }
    
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1); 
    
        renderNode(root, shapeRenderer);
    
        shapeRenderer.end();
    }

    private void renderNode(QuadTreeNode node, ShapeRenderer shapeRenderer) {
        if (node == null) {
            return;
        }
    
        if (node.isLeaf) {
            shapeRenderer.rect(
                node.lowerPoint.x,
                node.lowerPoint.y,
                node.upperPoint.x - node.lowerPoint.x,
                node.upperPoint.y - node.lowerPoint.y
            );
        } else {
            if (node.children != null) {
                for (QuadTreeNode child : node.children) {
                    renderNode(child, shapeRenderer);
                }
            }
        }
    }
}
