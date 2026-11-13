package org.firstinspires.ftc.teamcode.Testes.Rascunhos;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class TankDriveTestR extends TestBenchTankDriveR{
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
        telemetry.update();
    }
}
