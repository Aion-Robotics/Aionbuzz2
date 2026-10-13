package org.firstinspires.ftc.teamcode.Testes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RepositoryRobot;

@TeleOp
public class movementTest extends RepositoryRobot {
    DcMotor[] motor = new DcMotor[2];

    @Override
    public void init(){
        motor[0] = hardwareMap.get(DcMotor.class, "motorDir");
        motor[1] = hardwareMap.get(DcMotor.class, "motorEsq");

        telemetry.addData("Motor Status", "Registered");
    }
    @Override
    public void loop(){
        // Odometry
        double distance = distanceMeter(motor);
        telemetry.addData("Distance", "%.1f cm", distance);

        // Robot Movement
        double stickY =  gamepad1.left_stick_y;
        double stickX =  gamepad1.left_stick_x;

        double turbo = gamepad1.right_trigger;

        double leftMove = Range.clip(stickX - stickY,-0.35, 0.35);
        double rightMove = Range.clip(stickX + stickY, -0.35, 0.35);

        motor[0].setPower(rightMove);
        motor[1].setPower(leftMove);

        if (gamepad1.right_trigger_pressed) {
            rightMove *= turbo;
            leftMove *= turbo;
        }
        if((leftMove == 0.0) && (rightMove == 0.0)){
            for (DcMotor wheel: motor){
                wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        }
    }
}
