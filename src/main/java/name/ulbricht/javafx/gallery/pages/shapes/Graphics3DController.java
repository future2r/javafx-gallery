package name.ulbricht.javafx.gallery.pages.shapes;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;

/**
 * Demo page for 3D graphics: {@link Shape3D} nodes with {@link PhongMaterial}s, lights and a
 * {@link PerspectiveCamera} inside a {@link SubScene}.
 */
public class Graphics3DController implements Disposable {

    private static final double WIDTH = 560;
    private static final double HEIGHT = 360;

    @FXML
    private StackPane shapesHolder;
    @FXML
    private Label shapesOutput;
    @FXML
    private Slider rotateX;
    @FXML
    private Slider rotateY;
    @FXML
    private Slider cameraDistance;
    @FXML
    private ComboBox<DrawMode> drawMode;
    @FXML
    private ComboBox<CullFace> cullFace;
    @FXML
    private CheckBox pointLightOn;
    @FXML
    private CheckBox ambientLightOn;
    @FXML
    private CheckBox autoRotate;

    @FXML
    private StackPane materialsHolder;
    @FXML
    private Slider specularPower;
    @FXML
    private ColorPicker lightColor;
    @FXML
    private Slider lightX;
    @FXML
    private Slider sphereRotation;

    private Timeline autoRotation;
    private double dragAnchorX;
    private double dragAnchorY;
    private double dragAngleX;
    private double dragAngleY;

    @FXML
    private void initialize() {
        initShapesScene();
        initMaterialsScene();
    }

    // --- Example 1 -----------------------------------------------------------------------------------------

    private void initShapesScene() {
        Box box = new Box(110, 110, 110);
        box.setMaterial(material("#3d8fd1"));
        box.setTranslateX(-240);

        Sphere sphere = new Sphere(65);
        sphere.setMaterial(material("#e5734b"));
        sphere.setTranslateX(-80);

        Cylinder cylinder = new Cylinder(50, 140);
        cylinder.setMaterial(material("#56b16a"));
        cylinder.setTranslateX(80);

        MeshView pyramid = new MeshView(pyramidMesh(130, 65));
        pyramid.setMaterial(material("#b06ad1"));
        pyramid.setTranslateX(240);

        List<Shape3D> shapes = List.of(box, sphere, cylinder, pyramid);
        drawMode.getItems().setAll(DrawMode.values());
        drawMode.setValue(DrawMode.FILL);
        cullFace.getItems().setAll(CullFace.values());
        cullFace.setValue(CullFace.BACK);
        for (Shape3D shape : shapes) {
            shape.drawModeProperty().bind(drawMode.valueProperty());
            shape.cullFaceProperty().bind(cullFace.valueProperty());
        }

        Rotate rx = new Rotate(0, Rotate.X_AXIS);
        rx.angleProperty().bind(rotateX.valueProperty());
        Rotate ry = new Rotate(0, Rotate.Y_AXIS);
        ry.angleProperty().bind(rotateY.valueProperty());
        Group world = new Group(box, sphere, cylinder, pyramid);
        world.getTransforms().addAll(rx, ry);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(-400);
        pointLight.setTranslateY(-400);
        pointLight.setTranslateZ(-700);
        pointLight.lightOnProperty().bind(pointLightOn.selectedProperty());
        AmbientLight ambientLight = new AmbientLight(Color.gray(0.35));
        ambientLight.lightOnProperty().bind(ambientLightOn.selectedProperty());

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(10_000);
        camera.setFieldOfView(35);
        camera.translateZProperty().bind(cameraDistance.valueProperty().negate());

        SubScene subScene = new SubScene(new Group(world, pointLight, ambientLight), WIDTH, HEIGHT, true,
                SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#1e2430"));
        subScene.setCamera(camera);
        subScene.setOnMousePressed(e -> {
            dragAnchorX = e.getSceneX();
            dragAnchorY = e.getSceneY();
            dragAngleX = rotateX.getValue();
            dragAngleY = rotateY.getValue();
        });
        subScene.setOnMouseDragged(e -> {
            rotateY.setValue(wrap(dragAngleY - (e.getSceneX() - dragAnchorX) * 0.5));
            rotateX.setValue(wrap(dragAngleX + (e.getSceneY() - dragAnchorY) * 0.5));
        });
        shapesHolder.getChildren().add(subScene);

        autoRotation = new Timeline(new KeyFrame(Duration.millis(16),
                e -> rotateY.setValue(wrap(rotateY.getValue() + 0.5))));
        autoRotation.setCycleCount(Animation.INDEFINITE);
        autoRotate.selectedProperty().addListener((obs, wasOn, on) -> {
            if (on) {
                autoRotation.play();
            } else {
                autoRotation.stop();
            }
        });

        shapesOutput.setText(Platform.isSupported(ConditionalFeature.SCENE3D)
                ? "3D is supported by this graphics pipeline."
                : "This graphics pipeline does not support 3D – the scene may not render correctly.");
    }

    /** A square pyramid; faces are counter-clockwise when seen from outside, so back face culling works. */
    private static TriangleMesh pyramidMesh(float height, float halfBase) {
        float top = -height / 2;
        float bottom = height / 2;
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(
                0, top, 0,                    // 0 apex
                -halfBase, bottom, -halfBase, // 1 front left
                halfBase, bottom, -halfBase,  // 2 front right
                halfBase, bottom, halfBase,   // 3 back right
                -halfBase, bottom, halfBase); // 4 back left
        mesh.getTexCoords().addAll(0, 0);
        mesh.getFaces().addAll(
                0, 0, 1, 0, 2, 0,  // front
                0, 0, 2, 0, 3, 0,  // right
                0, 0, 3, 0, 4, 0,  // back
                0, 0, 4, 0, 1, 0,  // left
                1, 0, 3, 0, 2, 0,  // bottom
                1, 0, 4, 0, 3, 0); // bottom
        return mesh;
    }

    private static PhongMaterial material(String color) {
        PhongMaterial material = new PhongMaterial(Color.web(color));
        material.setSpecularColor(Color.gray(0.6));
        material.setSpecularPower(24);
        return material;
    }

    private static double wrap(double angle) {
        return ((angle + 180) % 360 + 360) % 360 - 180;
    }

    // --- Example 2 -----------------------------------------------------------------------------------------

    private void initMaterialsScene() {
        PhongMaterial diffuseOnly = new PhongMaterial(Color.web("#3d8fd1"));
        diffuseOnly.setSpecularColor(Color.BLACK);

        PhongMaterial specular = new PhongMaterial(Color.web("#e5734b"));
        specular.setSpecularColor(Color.WHITE);
        specular.specularPowerProperty().bind(specularPower.valueProperty());

        PhongMaterial textured = new PhongMaterial(Color.WHITE);
        textured.setDiffuseMap(checkerboard(256, 8));
        textured.setSpecularColor(Color.gray(0.4));

        Group spheres = new Group(
                sphere(diffuseOnly, -190),
                sphere(specular, 0),
                sphere(textured, 190));

        PointLight light = new PointLight();
        light.colorProperty().bind(lightColor.valueProperty());
        lightColor.setValue(Color.WHITE);
        light.translateXProperty().bind(lightX.valueProperty());
        light.setTranslateY(-250);
        light.setTranslateZ(-600);
        AmbientLight ambient = new AmbientLight(Color.gray(0.2));

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(10_000);
        camera.setFieldOfView(35);
        camera.setTranslateZ(-900);

        SubScene subScene = new SubScene(new Group(spheres, light, ambient), WIDTH, 280, true,
                SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#1e2430"));
        subScene.setCamera(camera);
        materialsHolder.getChildren().add(subScene);
    }

    private Sphere sphere(PhongMaterial material, double x) {
        Sphere sphere = new Sphere(80, 64);
        sphere.setMaterial(material);
        sphere.setTranslateX(x);
        Rotate rotate = new Rotate(0, Rotate.Y_AXIS);
        rotate.angleProperty().bind(sphereRotation.valueProperty());
        sphere.getTransforms().add(rotate);
        return sphere;
    }

    private static WritableImage checkerboard(int size, int cells) {
        WritableImage image = new WritableImage(size, size);
        PixelWriter writer = image.getPixelWriter();
        int cell = size / cells;
        Color light = Color.web("#f2f2f2");
        Color dark = Color.web("#2f6db3");
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                writer.setColor(x, y, ((x / cell) + (y / cell)) % 2 == 0 ? light : dark);
            }
        }
        return image;
    }

    @Override
    public void dispose() {
        autoRotation.stop();
    }
}
