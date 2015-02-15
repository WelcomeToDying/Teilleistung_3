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

    public static void createWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        /*
         * Adding global and directional lighning
         */
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White.mult(3.0f));
        rootNode.addLight(light);

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new ColorRGBA(1.0f, 1.0f, 0.8f, 0.2f));
        sun.setDirection(new Vector3f(-.8f, -.8f, -.8f).normalizeLocal());
        rootNode.addLight(sun);

        /*
         * Adding global and directional lighning
         */
        Box floorBox = new Box(200, 0.5f, 200);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial((Material) assetManager.loadMaterial("Materials/Concrete.j3m"));
        floorGeometry.setLocalTranslation(0, -5, 0);
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);

        /*
         * Movable Pallets
         * Creating nodes and associated controls, boxes and pallets
         * RigidBodyControl automatically uses box collision shapes when attached to single geometry with box mesh
         */
        Node palletNode1 = new Node("NodePallet1");
        Node palletNode2 = new Node("NodePallet2");
        Node palletNode3 = new Node("NodePallet3");
        RigidBodyControl palletPhy1 = new RigidBodyControl(2f);
        RigidBodyControl palletPhy2 = new RigidBodyControl(2f);
        RigidBodyControl palletPhy3 = new RigidBodyControl(2f);

        Box box = new Box(1f, 1f, 1f);
        Box palletPiece = new Box(0.2f, 0.2f, 1f);

        Geometry boxGeometry1 = new Geometry("Box", box);
        boxGeometry1.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        Geometry pallet1Geometry = new Geometry("Pallet1", palletPiece);
        pallet1Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        pallet1Geometry.setLocalTranslation(0.8f, -1.2f, 0);
        Geometry pallet2Geometry = new Geometry("Pallet2", palletPiece);
        pallet2Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        pallet2Geometry.setLocalTranslation(-0.8f, -1.2f, 0);
        palletNode1.attachChild(boxGeometry1);
        palletNode1.attachChild(pallet1Geometry);
        palletNode1.attachChild(pallet2Geometry);
        palletNode1.setLocalTranslation(5, 5, -3);
        palletNode1.addControl(palletPhy1);

        Geometry boxGeometry2 = new Geometry("Box", box);
        boxGeometry2.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        Geometry pallet3Geometry = new Geometry("Pallet1", palletPiece);
        pallet3Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        pallet3Geometry.setLocalTranslation(0.8f, -1.2f, 0);
        Geometry pallet4Geometry = new Geometry("Pallet2", palletPiece);
        pallet4Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        pallet4Geometry.setLocalTranslation(-0.8f, -1.2f, 0);
        palletNode2.attachChild(boxGeometry2);
        palletNode2.attachChild(pallet3Geometry);
        palletNode2.attachChild(pallet4Geometry);
        palletNode2.setLocalTranslation(-5, 5, -8);
        palletNode2.addControl(palletPhy2);

        Geometry boxGeometry3 = new Geometry("Box", box);
        boxGeometry3.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        Geometry pallet5Geometry = new Geometry("Pallet1", palletPiece);
        pallet5Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        pallet5Geometry.setLocalTranslation(0.8f, -1.2f, 0);
        Geometry pallet6Geometry = new Geometry("Pallet2", palletPiece);
        pallet6Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        pallet6Geometry.setLocalTranslation(-0.8f, -1.2f, 0);
        palletNode3.attachChild(boxGeometry3);
        palletNode3.attachChild(pallet5Geometry);
        palletNode3.attachChild(pallet6Geometry);
        palletNode3.setLocalTranslation(10, 5, 5);
        palletNode3.addControl(palletPhy3);

        rootNode.attachChild(palletNode1);
        rootNode.attachChild(palletNode2);
        rootNode.attachChild(palletNode3);
        space.add(palletPhy1);
        space.add(palletPhy2);
        space.add(palletPhy3);
    }
}