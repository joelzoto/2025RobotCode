package frc.robot.vision;

import org.photonvision.PhotonCamera;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.Unit;

public class VisionConfig {
    
    // Creates camera names; ensure these all match with the correct camera on the Photonvison Dashboard
    public static final int TOTAL_CAMERAS = 4;
    public static final String[] camNames = new String[] {"Left_Cam", "Right_Cam"}; //TODO: add center cam and drive cam

    //Camera Positions
    // TODO: config camera transforms
    public static final Transform3d[] robotToCamTransforms = new Transform3d[] {
        //left cam
        new Transform3d(
            new Translation3d(Units.inchesToMeters(-12.436), Units.inchesToMeters(11.677), Units.inchesToMeters(7.413)),
            new Rotation3d(0,Units.degreesToRadians(15),Units.degreesToRadians(-20))),
        //right cam
        new Transform3d(
            new Translation3d(Units.inchesToMeters(-12.436), Units.inchesToMeters(-11.677), Units.inchesToMeters(7.413)), 
            new Rotation3d(0, Units.degreesToRadians(15), Units.degreesToRadians(20)))
    }; 

    // Creates field layout for AprilTags
    public static final AprilTagFieldLayout tagLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeWelded);

    // Standard deviation of vision poses, this helps with correction or something idk thats what photon said
    // TODO: experiment with standard deviation values and set them to whatever gives the most correct pose
    public static final Matrix<N3, N1> singleTagStdDevs = VecBuilder.fill(4, 4, 8); // TODO: example values, change when testing
    public static final Matrix<N3, N1> multiTagStdDevs = VecBuilder.fill(0.5, 0.5, 1); //TODO: change values when testing

    
}