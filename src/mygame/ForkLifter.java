package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class ForkLifter extends VehicleControl {

    private Node forkNode;

    public ForkLifter(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace) {
        Material matRed = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matRed.setColor("Color", ColorRGBA.Red);

        Material matBlue = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBlue.setColor("Color", ColorRGBA.Blue);

        Material matCyan = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matCyan.setColor("Color", ColorRGBA.Cyan);

        Material matOrange = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matOrange.setColor("Color", ColorRGBA.Orange);

        Material matYellow = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matYellow.setColor("Color", ColorRGBA.Yellow);

        /*
         * CollisionShapes for collision detection
         * CompoundCollision shape for combining multiple base shapes (body, tires, forks)
         * BoxCollisionShape for box-shaped body of the car
         * Attaching BoxCollisionShapeCabin to CompoundCollisionShape at a Vector of (0,1,0)
         * -> Shifts effective center of mass of BoxCollisionShape to 0,-1,0 
         * -> Moving forkLifter is more stable
         * Adding shapes for lift and forks
         */
        CompoundCollisionShape compoundCollisionShape = new CompoundCollisionShape();
        BoxCollisionShape boxCollisionShapeCabin = new BoxCollisionShape(new Vector3f(1.2f, 1f, 3.2f));
        BoxCollisionShape boxCollisionShapeLift = new BoxCollisionShape(new Vector3f(1f, 2f, 0.1f));
        BoxCollisionShape boxCollisionShapeFork1 = new BoxCollisionShape(new Vector3f(0.2f, 0.075f, 1f));
        BoxCollisionShape boxCollisionShapeFork2 = new BoxCollisionShape(new Vector3f(0.2f, 0.075f, 1f));
        compoundCollisionShape.addChildShape(boxCollisionShapeCabin, new Vector3f(0, 1.2f, 0));
        compoundCollisionShape.addChildShape(boxCollisionShapeLift, new Vector3f(0f, 1.8f, 3.3f));
        compoundCollisionShape.addChildShape(boxCollisionShapeFork1, new Vector3f(0.6f, 0f, 4.2f));
        compoundCollisionShape.addChildShape(boxCollisionShapeFork2, new Vector3f(-0.6f, 0f, 4.2f));

        /*
         * VehicleNode to group geometry
         * VehicleControl represents physical behaviour of forkLifter
         */
        Node forkLifterNode = new Node("vehicleNode");
        setCollisionShape(compoundCollisionShape);
        setMass(200);

        /*
         * Defining the physical properties of the forkLifter
         * VehicleControl appoints physical behaviour to forkLifter, defaults for wheels
         * Compression: damping coefficient for when the suspension is compressed
         * Damping: damping coefficient for when the suspension is expanding
         * Stiffness: stiffness constant for the suspension
         * MaxSuspensionForce: caps the maximum suspension force
         */
        float compression = .4f;
        float damping = .5f;
        float stiffness = 80.0f;
        setSuspensionCompression(compression * 2.0f * FastMath.sqrt(stiffness));
        setSuspensionDamping(damping * 2.0f * FastMath.sqrt(stiffness));
        setSuspensionStiffness(stiffness);
        setMaxSuspensionForce(10000.0f);

        // Creating four wheels and attaching them to the box
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);
        float radius = 0.8f;
        float restLength = 0.1f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 2f;

        // Cylinder shape for wheels
        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);

        Node node1 = new Node("NodeWheelFrontLeft");
        Geometry wheelFrontLeft = new Geometry("WheelFrontLeft", wheelMesh);
        node1.attachChild(wheelFrontLeft);
        wheelFrontLeft.rotate(0, FastMath.HALF_PI, 0); // Rotate wheel 90° around Y axis
        wheelFrontLeft.setMaterial(matRed);
        addWheel(node1, new Vector3f(-xOff, yOff, zOff), wheelDirection, wheelAxle, restLength, radius, false);

        Node node2 = new Node("NodeWheelFrontRight");
        Geometry wheelFrontRight = new Geometry("WheelFrontRight", wheelMesh);
        node2.attachChild(wheelFrontRight);
        wheelFrontRight.rotate(0, FastMath.HALF_PI, 0);
        wheelFrontRight.setMaterial(matRed);
        addWheel(node2, new Vector3f(xOff, yOff, zOff), wheelDirection, wheelAxle, restLength, radius, false);

        Node node3 = new Node("NodeWheelBackLeft");
        Geometry wheelsBackLeft = new Geometry("WheelBackLeft", wheelMesh);
        node3.attachChild(wheelsBackLeft);
        wheelsBackLeft.rotate(0, FastMath.HALF_PI, 0);
        wheelsBackLeft.setMaterial(matBlue);
        addWheel(node3, new Vector3f(-xOff, yOff, -zOff), wheelDirection, wheelAxle, restLength, radius, true);

        Node node4 = new Node("NodeWheelBackRight");
        Geometry wheelBackRight = new Geometry("WheelBackRight", wheelMesh);
        node4.attachChild(wheelBackRight);
        wheelBackRight.rotate(0, FastMath.HALF_PI, 0);
        wheelBackRight.setMaterial(matBlue);
        addWheel(node4, new Vector3f(xOff, yOff, -zOff), wheelDirection, wheelAxle, restLength, radius, true);

        // Adding the driver cabin
        Node cabinNode = new Node("NodeCabin");
        Box cabinBox = new Box(1.2f, 1f, 3.2f);
        Geometry cabinGeometry = new Geometry("Cabin", cabinBox);
        cabinGeometry.setMaterial(matCyan);
        cabinGeometry.setLocalTranslation(new Vector3f(0f, 1.2f, 0f));
        cabinNode.attachChild(cabinGeometry);

        // Adding the lift
        Node liftNode = new Node("NodeLift");
        Box liftBox = new Box(1f, 2f, 0.1f);
        Geometry liftGeometry = new Geometry("Lift", liftBox);
        liftGeometry.setMaterial(matOrange);
        liftGeometry.setLocalTranslation(new Vector3f(0f, 1.8f, 3.3f));
        liftNode.attachChild(liftGeometry);

        // Adding the forks
        forkNode = new Node("NodeFork");
        Box forkBox = new Box(0.2f, 0.075f, 1f);
        Geometry fork1Geometry = new Geometry("Fork1", forkBox);
        fork1Geometry.setMaterial(matYellow);
        fork1Geometry.setLocalTranslation(new Vector3f(0.6f, 0f, 4.2f));
        Geometry fork2Geometry = new Geometry("Fork2", forkBox);
        fork2Geometry.setMaterial(matYellow);
        fork2Geometry.setLocalTranslation(new Vector3f(-0.6f, 0f, 4.2f));
        forkNode.attachChild(fork1Geometry);
        forkNode.attachChild(fork2Geometry);

        // Attaching nodes to their position on the scene graph
        forkLifterNode.attachChild(node1);
        forkLifterNode.attachChild(node2);
        forkLifterNode.attachChild(node3);
        forkLifterNode.attachChild(node4);
        forkLifterNode.attachChild(cabinNode);
        forkLifterNode.attachChild(liftNode);
        forkLifterNode.attachChild(forkNode);
        forkLifterNode.addControl(this);
        rootNode.attachChild(forkLifterNode);

        physicsSpace.add(this);
    }

    void lift(float liftValue) {
        forkNode.setLocalTranslation(0, liftValue, 0);
        System.out.println("Up");
    }
}
