package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.joints.SliderJoint;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class CrazyForkLifter extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private VehicleControl forkLifter;
    private Camera cameraTop;
    private final float accelerationForce = 200.0f;
    private float steeringValue = 0;
    private float accelerationValue = 0;

    public static void main(String[] args) {
        CrazyForkLifter cfl = new CrazyForkLifter();
        cfl.start();
    }
    private RigidBodyControl cabin;
    private RigidBodyControl lift;
    private RigidBodyControl fork;

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        LifterWorld.createWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        initialiseKeys();
        buildPlayer();
        //System.out.print(getPhysicsSpace().getGravity(Vector3f.ZERO));
        getPhysicsSpace().setAccuracy(0.01f);

        // Setup second view
        cameraTop = cam.clone();
        cameraTop.setViewPort(0.7f, 1f, 0f, 0.3f);
        cameraTop.setLocation(new Vector3f(0.2846221f, 6.4271426f, 0.23380789f));

        ViewPort CameraTopViewPort = renderManager.createMainView("Top", cameraTop);
        CameraTopViewPort.setClearFlags(true, true, true);
        CameraTopViewPort.attachScene(rootNode);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private void initialiseKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_NUMPAD1));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_NUMPAD3));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_NUMPAD5));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Forward");
        inputManager.addListener(this, "Backward");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Reset");
    }

    private void buildPlayer() {
        Material matRed = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matRed.setColor("Color", ColorRGBA.Red);

        Material matBlue = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matBlue.setColor("Color", ColorRGBA.Blue);

        Material matCyan = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matCyan.setColor("Color", ColorRGBA.Cyan);

        Material matOrange = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matOrange.setColor("Color", ColorRGBA.Orange);

        Material matYellow = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
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
        forkLifter = new VehicleControl(compoundCollisionShape, 200);

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
        forkLifter.setSuspensionCompression(compression * 2.0f * FastMath.sqrt(stiffness));
        forkLifter.setSuspensionDamping(damping * 2.0f * FastMath.sqrt(stiffness));
        forkLifter.setSuspensionStiffness(stiffness);
        forkLifter.setMaxSuspensionForce(10000.0f);

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
        forkLifter.addWheel(node1, new Vector3f(-xOff, yOff, zOff), wheelDirection, wheelAxle, restLength, radius, false);

        Node node2 = new Node("NodeWheelFrontRight");
        Geometry wheelFrontRight = new Geometry("WheelFrontRight", wheelMesh);
        node2.attachChild(wheelFrontRight);
        wheelFrontRight.rotate(0, FastMath.HALF_PI, 0);
        wheelFrontRight.setMaterial(matRed);
        forkLifter.addWheel(node2, new Vector3f(xOff, yOff, zOff), wheelDirection, wheelAxle, restLength, radius, false);

        Node node3 = new Node("NodeWheelBackLeft");
        Geometry wheelsBackLeft = new Geometry("WheelBackLeft", wheelMesh);
        node3.attachChild(wheelsBackLeft);
        wheelsBackLeft.rotate(0, FastMath.HALF_PI, 0);
        wheelsBackLeft.setMaterial(matBlue);
        forkLifter.addWheel(node3, new Vector3f(-xOff, yOff, -zOff), wheelDirection, wheelAxle, restLength, radius, true);

        Node node4 = new Node("NodeWheelBackRight");
        Geometry wheelBackRight = new Geometry("WheelBackRight", wheelMesh);
        node4.attachChild(wheelBackRight);
        wheelBackRight.rotate(0, FastMath.HALF_PI, 0);
        wheelBackRight.setMaterial(matBlue);
        forkLifter.addWheel(node4, new Vector3f(xOff, yOff, -zOff), wheelDirection, wheelAxle, restLength, radius, true);

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
        Node forkNode = new Node("NodeFork");
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
        forkLifterNode.addControl(forkLifter);
        rootNode.attachChild(forkLifterNode);

        getPhysicsSpace().add(forkLifter);


        // Body and forklift
//        Node bodyNode = new Node("NodeBody");
//        cabin = new RigidBodyControl(400f);
//        lift = new RigidBodyControl(200f);
//        fork = new RigidBodyControl(200f);
//
//        Node cabinNode = new Node("NodeCabin");
//        Box cabinBox = new Box(1.2f, 0.5f, 3.2f);
//        Geometry cabinGeometry = new Geometry("Cabin", cabinBox);
//        cabinGeometry.setMaterial(matCyan);
//        cabinGeometry.setLocalTranslation(new Vector3f(0f, -1f, 0f));
//        cabinNode.attachChild(cabinGeometry);
//        cabinNode.addControl(cabin);
//
//        Node liftNode = new Node("NodeLift");
//        Box liftBox = new Box(1f, 2f, 0.1f);
//        Geometry liftGeometry = new Geometry("Lift", liftBox);
//        liftGeometry.setMaterial(matOrange);
//        liftGeometry.setLocalTranslation(new Vector3f(0f, 0f, 3.25f));
//        liftNode.attachChild(liftGeometry);
//        liftNode.addControl(lift);
//        
//        Node forkNode = new Node("NodeFork");
//        Box forkBox = new Box(0.2f, 0.2f, 0.5f);
//        Geometry fork1Geometry = new Geometry("Fork1", forkBox);
//        fork1Geometry.setMaterial(matYellow);
//        fork1Geometry.setLocalTranslation(new Vector3f(0.4f, 0f, 3.3f));
//        Geometry fork2Geometry = new Geometry("Fork2", forkBox);
//        fork2Geometry.setMaterial(matYellow);
//        fork2Geometry.setLocalTranslation(new Vector3f(-0.4f, 0f, 3.3f));
//        forkNode.attachChild(fork1Geometry);
//        forkNode.attachChild(fork2Geometry);
//        forkNode.addControl(fork);

//        bodyNode.attachChild(liftNode);
//        bodyNode.attachChild(cabinNode);
//        bodyNode.attachChild(forkNode);
//        rootNode.attachChild(bodyNode);

//        getPhysicsSpace().add(cabin);
//        getPhysicsSpace().add(lift);
//        getPhysicsSpace().add(fork);

        //joint
//        slider = new SliderJoint(lift, forkLifter, Vector3f.UNIT_Y.negate(), Vector3f.UNIT_Y, true);
//        slider.setUpperLinLimit(.1f);
//        slider.setLowerLinLimit(-.1f);
//
//        getPhysicsSpace().add(slider);
    }

    @Override
    public void simpleUpdate(float tpf) {
        cam.lookAt(forkLifter.getPhysicsLocation(), Vector3f.UNIT_Y);

        cameraTop.setLocation(new Vector3f(forkLifter.getPhysicsLocation().getX(), forkLifter.getPhysicsLocation().getY() + 15, forkLifter.getPhysicsLocation().getZ()));
        cameraTop.lookAt(forkLifter.getPhysicsLocation(), new Vector3f(0.2846221f, 6.4271426f, 0.23380789f));
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            forkLifter.steer(steeringValue);
        } else if (binding.equals("Right")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            forkLifter.steer(steeringValue);
        } else if (binding.equals("Forward")) {
            if (value) {
                accelerationValue += accelerationForce;
            } else {
                accelerationValue -= accelerationForce;
            }
            forkLifter.accelerate(accelerationValue);
        } else if (binding.equals("Backward")) {
            if (value) {
                accelerationValue -= accelerationForce;
            } else {
                accelerationValue += accelerationForce;
            }
            forkLifter.accelerate(accelerationValue);
        } else if (binding.equals("Reset")) {
            if (value) {
                forkLifter.setPhysicsLocation(Vector3f.ZERO);
                forkLifter.setPhysicsRotation(new Matrix3f());
                forkLifter.setLinearVelocity(Vector3f.ZERO);
                forkLifter.setAngularVelocity(Vector3f.ZERO);
                forkLifter.resetSuspension();
            } else {
            }
        }
    }
}
