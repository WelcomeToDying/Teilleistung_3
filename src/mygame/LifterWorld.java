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
        Box palletFoot = new Box(0.2f, 0.4f, 1.6f);
        Box palletBoard = new Box(1.6f, 0.1f, 1.6f);

        Geometry boxGeometry1 = new Geometry("Box", box);
        boxGeometry1.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        Geometry palletFoot1Geometry = new Geometry("PalletFoot1", palletFoot);
        palletFoot1Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletFoot1Geometry.setLocalTranslation(1.2f, -1.2f, 0);
        Geometry palletFoot2Geometry = new Geometry("PalletFoot2", palletFoot);
        palletFoot2Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletFoot2Geometry.setLocalTranslation(-1.2f, -1.2f, 0);
        Geometry palletBoard1Geometry = new Geometry("PalletBoard1", palletBoard);
        palletBoard1Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletBoard1Geometry.setLocalTranslation(0, -0.8f, 0);
        palletNode1.attachChild(boxGeometry1);
        palletNode1.attachChild(palletFoot1Geometry);
        palletNode1.attachChild(palletFoot2Geometry);
        palletNode1.attachChild(palletBoard1Geometry);
        palletNode1.setLocalTranslation(5, 5, -3);
        palletNode1.addControl(palletPhy1);

        Geometry boxGeometry2 = new Geometry("Box", box);
        boxGeometry2.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        Geometry palletFoot3Geometry = new Geometry("PalletFoot3", palletFoot);
        palletFoot3Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletFoot3Geometry.setLocalTranslation(1.2f, -1.2f, 0);
        Geometry palletFoot4Geometry = new Geometry("PalletFoot4", palletFoot);
        palletFoot4Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletFoot4Geometry.setLocalTranslation(-1.2f, -1.2f, 0);
        Geometry palletBoard2Geometry = new Geometry("PalletBoard2", palletBoard);
        palletBoard2Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletBoard2Geometry.setLocalTranslation(0, -0.8f, 0);
        palletNode1.attachChild(boxGeometry1);
        palletNode2.attachChild(boxGeometry2);
        palletNode2.attachChild(palletFoot3Geometry);
        palletNode2.attachChild(palletFoot4Geometry);
        palletNode2.attachChild(palletBoard2Geometry);
        palletNode2.setLocalTranslation(-5, 5, -8);
        palletNode2.addControl(palletPhy2);

        Geometry boxGeometry3 = new Geometry("Box", box);
        boxGeometry3.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        Geometry palletFoot5Geometry = new Geometry("PalletFoot5", palletFoot);
        palletFoot5Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletFoot5Geometry.setLocalTranslation(1.2f, -1.2f, 0);
        Geometry palletFoot6Geometry = new Geometry("PalletFoot6", palletFoot);
        palletFoot6Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletFoot6Geometry.setLocalTranslation(-1.2f, -1.2f, 0);
        Geometry palletBoard3Geometry = new Geometry("PalletBoard3", palletBoard);
        palletBoard3Geometry.setMaterial((Material) assetManager.loadMaterial("Materials/Wood.j3m"));
        palletBoard3Geometry.setLocalTranslation(0, -0.8f, 0);
        palletNode1.attachChild(boxGeometry1);
        palletNode3.attachChild(boxGeometry3);
        palletNode3.attachChild(palletFoot5Geometry);
        palletNode3.attachChild(palletFoot6Geometry);
        palletNode3.attachChild(palletBoard3Geometry);
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