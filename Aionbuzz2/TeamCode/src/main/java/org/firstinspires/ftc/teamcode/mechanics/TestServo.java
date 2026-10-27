package org.firstinspires.ftc.teamcode.mechanics;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

public class TestServo extends OpMode {
    CRServo[] speedServo = new CRServo[2];
    @Override
    public void init(){
        speedServo[0] = hardwareMap.get(CRServo.class, "servoDir");
        speedServo[1] = hardwareMap.get(CRServo.class, "servoEsq");
    }
    @Override
    public void loop() {
        if (gamepad1.left_trigger_pressed){
            speedServo[0].setPower(1);
            speedServo[1].setPower(-1);
        }
    }
}
