package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class LifterWorld {

    /**
     * @param rootNode
     * @param assetManager
     * @param space
     */
    public static void createWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White.mult(3.0f));
        rootNode.addLight(light);

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new ColorRGBA(1.0f, 1.0f, 0.8f, 0.2f ));
        sun.setDirection(new Vector3f(-.8f, -.8f, -.8f).normalizeLocal());
        rootNode.addLight(sun);

        Box floorBox = new Box(200, 0.5f, 200);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial((Material) assetManager.loadMaterial("Materials/Concrete.j3m"));
        floorGeometry.setLocalTranslation(0, -5, 0);
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);

        //movable boxes
        for (int i = 0; i < 4; i++) {
            Box box = new Box(1f, 1f, 1f);
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
            boxGeometry.setLocalTranslation(i, 7, -5);
            //RigidBodyControl automatically uses box collision shapes when attached to single geometry with box mesh
            boxGeometry.addControl(new RigidBodyControl(2));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
        }
    }
}