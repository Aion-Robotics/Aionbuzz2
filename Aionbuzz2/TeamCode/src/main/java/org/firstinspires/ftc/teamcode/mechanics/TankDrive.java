package org.firstinspires.ftc.teamcode.mechanics;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class TankDrive extends RepositoryRobot {
    @Override
    public void init(){
        initializerHardware();
        telemetry.addData("Hardware Status", "Registered");
    }

    @Override
    public void loop(){
        // Odometry
        double distance = distanceMeter();
        telemetry.addData("Distance", "%.1f cm", distance);

        moveRobot();
        energyIntake();
        servoCR();
        motorShooter();
        }
    }