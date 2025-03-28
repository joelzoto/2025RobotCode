package frc.robot.vision;

import static frc.robot.vision.VisionConfig.AMBIGUITY_THRESHOLD;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFieldLayout.OriginPosition;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Runnable that gets AprilTag data from PhotonVision.
 */
public class PhotonRunnable implements Runnable {

  private final PhotonPoseEstimator photonPoseEstimator;
  private final PhotonCamera photonCamera;
  private final AtomicReference<EstimatedRobotPose> atomicEstimatedRobotPose = new AtomicReference<EstimatedRobotPose>();

  public PhotonRunnable(PhotonCamera cameraName, Transform3d robotToCamera) {
    this.photonCamera = cameraName;
    PhotonPoseEstimator photonPoseEstimator = null;
    try {
      var layout = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeWelded);
      // PV estimates will always be blue, they'll get flipped by robot thread
      layout.setOrigin(OriginPosition.kBlueAllianceWallRightSide);
      if (photonCamera != null) {
        photonPoseEstimator = new PhotonPoseEstimator(
            layout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, robotToCamera);
        photonPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
      }
    } catch (Exception e) {
      DriverStation.reportError("Try Catch Block Fail in PhotonRunnable.java", e.getStackTrace());
      photonPoseEstimator = null;
    }
    this.photonPoseEstimator = photonPoseEstimator;
  }

  @Override
  public void run() {
    // Get AprilTag data
    if (photonPoseEstimator != null && photonCamera != null) {
      var photonResults = photonCamera.getAllUnreadResults();
      for(var result : photonResults) {
          if (result.hasTargets()
          && (result.targets.size() > 1
              || result.targets.get(0).getPoseAmbiguity() < AMBIGUITY_THRESHOLD)) {
          photonPoseEstimator.update(result).ifPresent(estimatedRobotPose -> {
            var estimatedPose = estimatedRobotPose.estimatedPose;
            // Make sure the measurement is on the field
            if (estimatedPose.getX() > 0.0 && estimatedPose.getX() <= VisionConfig.FIELD_LENGTH_METERS
                && estimatedPose.getY() > 0.0 && estimatedPose.getY() <= VisionConfig.FIELD_WIDTH_METERS) {
              atomicEstimatedRobotPose.set(estimatedRobotPose);
            }
          });
        }
      }
      
    }
  }

  /**
   * Gets the latest robot pose. Calling this will only return the pose once. If
   * it returns a non-null value, it is a
   * new estimate that hasn't been returned before.
   * This pose will always be for the BLUE alliance. It must be flipped if the
   * current alliance is RED.
   * 
   * @return latest estimated pose
   */
  public EstimatedRobotPose grabLatestEstimatedPose() {
    return atomicEstimatedRobotPose.getAndSet(null);
  }

}