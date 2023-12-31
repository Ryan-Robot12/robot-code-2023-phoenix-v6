package frc.robot.subsystems.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface VisionSubsystem extends Subsystem {

  /**
   * Returns true if the limelight(s) can see one or more April Tag.
   */
  boolean canSeeAprilTags();

  /**
   * Returns the pose of the robot calculated by the limelight. If there
   * are multiple limelights that can see april tags, it uses the limelight
   * that is closest to an april tag. 
   */
  Pose2d getPoseFromAprilTags();

  /**
   * Returns the distance in meters from the limelight(s) to the closest 
   * april tag that they can see.
   */
  double getDistanceFromClosestAprilTag();

  /**
   * Returns how many april tags the limelight that is being used for pose
   * estimation can see.
   */
  int getNumberOfAprilTags();

  /**
   * Returns the timestamp in seconds of when the limelight that is being
   * used for pose estimation calculated the robot's pose. 
   */
  double getTimeStampSeconds();

  /**
   * Returns the latency in seconds of when the limelight that is being
   * used for pose estimation calculated the robot's pose. It adds the
   * pipeline latency, capture latency, and json parsing latency.
   */
  double getLatencySeconds();

  /**
   * Returns true if the limelight(s) can see one or more cubes.
   */
  boolean canSeeCube();

  /**
   * Returns the translation of the closest cube relative to the robot.
   * Positive x being forward, positive y being left.
   */
  Translation2d getClosestCubePosition();

  /**
   * Returns true if the limelight(s) can see one or more cones.
   */
  boolean canSeeCone();

  /**
   * Returns the translation of the closest cone relative to the robot.
   * Positive x being forward, positive y being left.
   */
  Translation2d getClosestConePosition();

  /**
   * Crops the limelights to the specified values.
   * @param cropValues A 2D array that with each row containing the crop
   * values for each limelight as {x1, x2, y1, y2}. Values should be from
   * -1 to 1.
   */
  void cropLimelights(double[][] cropValues);

  /**
   * Changes the limelights' pipeline. If set to object detection, only
   * the limelight with the coral will be changed.
   * @param limelightPipeline The pipeline to set the limelights to.
   */
  void setLimelightsPipeline(LimelightPipelines limelightPipeline);

  enum LimelightPipelines {
    APRIL_TAGS(0-9),
    OBJECT_DETECTION(0-9);

    private int ID;

    LimelightPipelines(int ID) {
      this.ID = ID;
    }

    int getID() {
      return this.ID;
    }
  }
}