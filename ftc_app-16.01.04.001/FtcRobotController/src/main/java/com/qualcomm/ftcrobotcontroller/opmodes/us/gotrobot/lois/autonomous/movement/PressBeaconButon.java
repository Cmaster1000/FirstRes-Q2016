package com.qualcomm.ftcrobotcontroller.opmodes.us.gotrobot.lois.autonomous.movement;

import com.qualcomm.ftcrobotcontroller.opmodes.us.gotrobot.lois.autonomous.control.Preferences;
import com.qualcomm.ftcrobotcontroller.opmodes.us.gotrobot.lois.autonomous.control.StateManager;
import com.qualcomm.ftcrobotcontroller.opmodes.us.gotrobot.lois.autonomous.sensors.SensorManager;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class PressBeaconButon
{
    Preferences presets;
    StateManager stateManager;
    MotorManager motorManager;
    ServoManager servoManager;
    SensorManager sensorManager;

    double rightCorrection = 0;
    double leftCorrecion = 0;

    public PressBeaconButon(Preferences presets, StateManager stateManager, MotorManager motorManager, ServoManager servoManager, SensorManager sensorManager)
    {
        this.presets = presets;
        this.stateManager = stateManager;
        this.motorManager = motorManager;
        this.servoManager = servoManager;
        this.sensorManager = sensorManager;
    }

    private double calcEncoderValue(double inches)
    {
        return (1440 * (inches/(Math.PI * presets.getWHEEL_DIAMETER()))) * presets.getGEAR_RATIO();
    } //calcEncoderValue

    public void driveWithCorrection(double speed, int ThreshholdDistance)
    {
        if (stateManager.getStateStage() == 0)
        {
            if (sensorManager.getColorSensorHolder().getColorSensor().blue() > sensorManager.getColorSensorHolder().getColorSensor().red())
            {
                servoManager.getBeaconServo().setPosition(0); //Update this value
            }
            else
            {
                servoManager.getBeaconServo().setPosition(0); //Update this value
            }

            stateManager.continueCommand();
        }
        else if (stateManager.getStateStage() == 1) {
            motorManager.getRightMotor2().setMode(DcMotorController.RunMode.RESET_ENCODERS);
            motorManager.getLeftMotor2().setMode(DcMotorController.RunMode.RESET_ENCODERS);

            motorManager.getLeftMotor2().setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            motorManager.getRightMotor2().setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

            if (speed > 0) {
                motorManager.getLeftMotor1().setPower(speed);
                motorManager.getRightMotor1().setPower(-speed);
                motorManager.getLeftMotor2().setPower(speed);
                motorManager.getRightMotor2().setPower(-speed);
            } else if (speed < 0) {
                motorManager.getLeftMotor1().setPower(-speed);
                motorManager.getRightMotor1().setPower(speed);
                motorManager.getLeftMotor2().setPower(-speed);
                motorManager.getRightMotor2().setPower(speed);
            }

            stateManager.continueCommand();
        }
        else if (stateManager.getStateStage() == 2)
        {
            if(ThreshholdDistance >= sensorManager.getUltrasonicSensorHolder().getLevel() && sensorManager.getUltrasonicSensorHolder().getLevel() != 0)
            {
                motorManager.getLeftMotor1().setPower(0);
                motorManager.getRightMotor1().setPower(0);
                motorManager.getLeftMotor2().setPower(0);
                motorManager.getRightMotor2().setPower(0);

                stateManager.continueProgram();
            }
            else
            {
                    leftCorrecion = speed-(sensorManager.getGyroscopeHolder().getRotation()*presets.getGYRO_ERROR_CORRECTION());
                    rightCorrection = speed+(sensorManager.getGyroscopeHolder().getRotation()*presets.getGYRO_ERROR_CORRECTION());

                    if (leftCorrecion>1)
                    {
                        leftCorrecion = 1;
                    }
                    else if (leftCorrecion<-1)
                    {
                        leftCorrecion = -1;
                    }

                    if (rightCorrection>1)
                    {
                        rightCorrection = 1;
                    }
                    else if (rightCorrection<-1)
                    {
                        rightCorrection = -1;
                    }

                    motorManager.getLeftMotor1().setPower(leftCorrecion);
                    motorManager.getRightMotor1().setPower(rightCorrection);
                    motorManager.getLeftMotor2().setPower(leftCorrecion);
                    motorManager.getRightMotor2().setPower(rightCorrection);
            }
        }
    }
}
