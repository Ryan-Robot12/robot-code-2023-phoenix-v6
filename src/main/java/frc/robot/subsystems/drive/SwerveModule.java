package frc.robot.subsystems.drive;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.HardwareConstants;
import frc.robot.Constants.ModuleConstants;

public class SwerveModule {

  private final CANcoder turnEncoder;
  private final TalonFX driveMotor;
  private final TalonFX turnMotor;

  private final ProfiledPIDController turnPIDController =
    new ProfiledPIDController(
      ModuleConstants.TURN_P,
      ModuleConstants.TURN_I,
      ModuleConstants.TURN_D,
      ModuleConstants.TURN_CONSTRAINTS
    );

  private final SimpleMotorFeedforward turnFeedForward = new SimpleMotorFeedforward(
    DriveConstants.TURN_S, 
    DriveConstants.TURN_V, 
    DriveConstants.TURN_A
  );

  private final SimpleMotorFeedforward driveFeedForward = new SimpleMotorFeedforward(
    ModuleConstants.DRIVE_S,
    ModuleConstants.DRIVE_V,
    ModuleConstants.DRIVE_A
  );

  StatusSignal<Double> turnEncoderPos;
  StatusSignal<Double> driveMotorVelocity;

  private String name;

  /**
   * Constructs a swerve module
   * @param driveMotorChannel ID of the drive motor
   * @param turnMotorChannel ID of the turn motor
   * @param turnEncoderChannel ID of the CANCoder
   * @param angleZero CANCoder offset
   * @param encoderReversed is the turn encoder reversed
   * @param driveReversed is the drive motor reversed
   */
  public SwerveModule(
    int driveMotorChannel,
    int turnMotorChannel,
    int turnEncoderChannel,
    double angleZero,
    SensorDirectionValue encoderReversed,
    InvertedValue driveReversed,
    String name
    ) {
    this.name = name;
    
    turnEncoder = new CANcoder(turnEncoderChannel, HardwareConstants.CANIVORE_CAN_BUS_STRING);
    driveMotor = new TalonFX(driveMotorChannel, HardwareConstants.CANIVORE_CAN_BUS_STRING);
    turnMotor = new TalonFX(turnMotorChannel, HardwareConstants.CANIVORE_CAN_BUS_STRING);
        
    // turnEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Signed_PlusMinus180, HardwareConstants.TIMEOUT_MS);
    // turnEncoder.configMagnetOffset(-angleZero, HardwareConstants.TIMEOUT_MS);
    // turnEncoder.configSensorDirection(encoderReversed, HardwareConstants.TIMEOUT_MS);
    CANcoderConfiguration turnEncoderConfig = new CANcoderConfiguration();
    turnEncoderConfig.MagnetSensor.MagnetOffset = -angleZero;
    turnEncoderConfig.MagnetSensor.SensorDirection = encoderReversed;
    turnEncoderConfig.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Signed_PlusMinusHalf;
    turnEncoder.getConfigurator().apply(turnEncoderConfig, HardwareConstants.TIMEOUT_MS);

    // driveMotor.configFactoryDefault(HardwareConstants.TIMEOUT_MS);
    // driveMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, HardwareConstants.TIMEOUT_MS);
    // driveMotor.config_kF(0, ModuleConstants.DRIVE_F, HardwareConstants.TIMEOUT_MS);
    // driveMotor.config_kP(0, ModuleConstants.DRIVE_P, HardwareConstants.TIMEOUT_MS);
    // driveMotor.config_kI(0, ModuleConstants.DRIVE_I, HardwareConstants.TIMEOUT_MS);
    // driveMotor.config_kD(0, ModuleConstants.DRIVE_D, HardwareConstants.TIMEOUT_MS);
    // driveMotor.setNeutralMode(NeutralMode.Brake);
    // driveMotor.setInverted(driveReversed);
    // driveMotor.configNeutralDeadband(HardwareConstants.MIN_FALCON_DEADBAND * 10, HardwareConstants.TIMEOUT_MS);
    // driveMotor.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 60, 65, 0.1), HardwareConstants.TIMEOUT_MS);
    // driveMotor.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 60, 65, 0.1), HardwareConstants.TIMEOUT_MS);
    // driveMotor.setStatusFramePeriod(StatusFrame.Status_1_General, 250);
    // // driveMotor.setStatusFramePeriod(StatusFrame.Status_1_General, 10);
    // driveMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20);
    TalonFXConfiguration driveConfig = new TalonFXConfiguration();
    driveConfig.Slot0.kP = ModuleConstants.DRIVE_P;
    driveConfig.Slot0.kI = ModuleConstants.DRIVE_I;
    driveConfig.Slot0.kD = ModuleConstants.DRIVE_D;
    driveConfig.Slot0.kS = ModuleConstants.DRIVE_F;
    driveConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    driveConfig.MotorOutput.Inverted = driveReversed;
    driveConfig.MotorOutput.DutyCycleNeutralDeadband = HardwareConstants.MIN_FALCON_DEADBAND;
    // TODO: current limits
    driveMotor.getConfigurator().apply(driveConfig, HardwareConstants.TIMEOUT_MS);

    // turnMotor.configFactoryDefault(HardwareConstants.TIMEOUT_MS);
    // turnMotor.setNeutralMode(NeutralMode.Brake);
    // turnMotor.setInverted(true);
    // turnMotor.configNeutralDeadband(HardwareConstants.MIN_FALCON_DEADBAND, HardwareConstants.TIMEOUT_MS);
    // turnMotor.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 60, 65, 0.1), HardwareConstants.TIMEOUT_MS);
    // turnMotor.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 60, 65, 0.1), HardwareConstants.TIMEOUT_MS);
    // turnMotor.setStatusFramePeriod(StatusFrame.Status_1_General, 250);
    // turnMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 250);
    TalonFXConfiguration turnConfig = new TalonFXConfiguration();
    turnConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    turnConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
    turnConfig.MotorOutput.DutyCycleNeutralDeadband = HardwareConstants.MIN_FALCON_DEADBAND;
    // TODO: config current limits
    turnMotor.getConfigurator().apply(turnConfig, HardwareConstants.TIMEOUT_MS);

    // Limit the PID Controller's input range between -pi and pi and set the input to be continuous.
    turnPIDController.enableContinuousInput(-Math.PI, Math.PI);

    turnEncoderPos = turnEncoder.getAbsolutePosition();
    driveMotorVelocity = driveMotor.getVelocity();
  }

  /**
   * @param currentAngle what the controller currently reads (radians)
   * @param targetAngleSetpoint the desired angle (radians)
   * @return the target angle in controller's scope (radians)
   */
  public static double calculateContinuousInputSetpoint(double currentAngle, double targetAngleSetpoint) {
    targetAngleSetpoint = Math.IEEEremainder(targetAngleSetpoint, Math.PI * 2);

    double remainder = currentAngle % (Math.PI * 2);
    double adjustedAngleSetpoint = targetAngleSetpoint + (currentAngle - remainder);

    // We don't want to rotate over 180 degrees, so just rotate the other way (add a
    // full rotation)
    if (adjustedAngleSetpoint - currentAngle > Math.PI) {
        adjustedAngleSetpoint -= Math.PI * 2;
    } else if (adjustedAngleSetpoint - currentAngle < -Math.PI) {
        adjustedAngleSetpoint += Math.PI * 2;
    }

    return adjustedAngleSetpoint;
  }

  /**
   * Gets the heading of the module
   * @return the absolute position of the CANCoder
   */
  public double getModuleHeading() {
    turnEncoderPos.refresh();
    return turnEncoderPos.getValue();
  }

  /**
   * Returns the current state of the module.
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    driveMotorVelocity.refresh();

    double speedMetersPerSecond = ModuleConstants.DRIVE_TO_METERS_PER_SECOND * driveMotorVelocity.getValue();
    // double turnRadians = (Math.PI / 180) * turnEncoder.getAbsolutePosition();
    double turnRadians = Rotation2d.fromRotations(getModuleHeading()).getRadians();
    return new SwerveModuleState(speedMetersPerSecond, new Rotation2d(turnRadians));
  }

  public SwerveModulePosition getPosition() {
    driveMotorVelocity.refresh();

    double position = ModuleConstants.DRIVE_TO_METERS * driveMotorVelocity.getValue();;
    Rotation2d rotation = Rotation2d.fromDegrees(getCANCoderABS());
    return new SwerveModulePosition(position, rotation);
  }

  /**
   * Sets the desired state for the module and sends calculated output from controller to the motor.
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    double turnRadians = getTurnRadians();

    // Optimize the reference state to avoid spinning further than 90 degrees
    SwerveModuleState optimizedDesiredState = SwerveModuleState.optimize(desiredState, new Rotation2d(turnRadians));

    // Converts meters per second to rpm
    double desiredDriveRPM = optimizedDesiredState.speedMetersPerSecond * 60 
      * ModuleConstants.DRIVE_GEAR_RATIO / ModuleConstants.WHEEL_CIRCUMFERENCE_METERS;
      
    // Converts rpm to encoder units per 100 milliseconds
    double desiredDriveEncoderUnitsPer100MS = desiredDriveRPM / 600.0 * 1;  // TODO: check if 1 is falcon resolution

    // Sets the drive motor's speed using the built in pid controller
    // driveMotor.set(ControlMode.Velocity, desiredDriveEncoderUnitsPer100MS, 
    //   DemandType.ArbitraryFeedForward, driveFeedForward.calculate(optimizedDesiredState.speedMetersPerSecond));
    VelocityVoltage driveOutput = new VelocityVoltage(desiredDriveEncoderUnitsPer100MS);
    driveMotor.setControl(driveOutput);

    // Calculate the turning motor output from the turn PID controller.
    double turnOutput =
      turnPIDController.calculate(turnRadians, optimizedDesiredState.angle.getRadians())
        + turnFeedForward.calculate(turnPIDController.getSetpoint().velocity);
        turnMotor.set(turnOutput / 12);
  }

  public double getTurnRadians() {
    // return ((2 * Math.PI) / 360) * turnEncoder.getAbsolutePosition();
    turnEncoderPos.refresh();
    return Rotation2d.fromRotations(turnEncoderPos.getValue()).getRadians();
  }

  public double getAbsolutePosition() {
    return turnEncoder.getAbsolutePosition();
  }

  /**
   * Gets the current position of the CANCoder in relation to the magnet
   * @return current CANCoder position
   */
  public double getCANCoderABS(){
    return turnEncoder.getAbsolutePosition();
  }

  @Deprecated
  /** Zeros all the SwerveModule encoders. */
  public void resetEncoders() {
    turnEncoder.setPosition(0);
    driveMotor.setSelectedSensorPosition(0);
  }

  public void periodicFunction() {}
}