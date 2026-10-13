package org.firstinspires.ftc.teamcode.mechanics;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class Motor extends OpMode {
    DcMotor motorEsq, motorDir = null;
    double stickY;
    double stickX;

    @Override
    public void init(){
        motorEsq = hardwareMap.get(DcMotor.class, "motorEsq");
        motorDir = hardwareMap.get(DcMotor.class, "motorDir");
        telemetry.addData("Motor Status", "Registered");
    }
    @Override
    public void loop() {
        double ticks = motorEsq.getCurrentPosition();
        // mudar o nome dessa variável para não confundir com a variável da linha 38

        double cm2 = (ticks / 1425.2) * ((7.2 * Math.PI) * 3);

        stickY = -gamepad1.left_stick_y;
        stickX = gamepad1.left_stick_x;

        double leftMove = Range.clip(stickY + stickX, -0.75, 0.75);

        motorEsq.setPower(leftMove);

        telemetry.addData("Issued ticks", ticks);
        telemetry.addData("Distance (cm)", cm2);

        double ticks2 = motorDir.getCurrentPosition();
        // mudar o nome dessa variável para não confundir com a variável da linha 23
        double cm = (ticks / 1425.2) * ((7.2 * Math.PI) * 3);


        stickY = -gamepad1.left_stick_y;
        stickX = gamepad1.left_stick_x;

        double rightMove = Range.clip(stickY - stickX, -0.75, 0.75);
        motorDir.setPower(rightMove);

        telemetry.addData("Issued ticks", ticks);
        telemetry.addData("Distance (cm)", cm);
    }
}