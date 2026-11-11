package org.firstinspires.ftc.teamcode.Testes;




import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


public class TestBenchR {

    private Servo servoPosVert;
    private Servo servoPosHor;

    public void init(HardwareMap hardwareMap){
        servoPosVert = hardwareMap.get(Servo.class, "servo_vert");
        servoPosHor =  hardwareMap.get(Servo.class, "servo_hor");
    }
    public void setServoPosVert(double angle){
        servoPosVert.setPosition(Range.clip(angle, 0.1, 0.9));
    }

    public void setServoPosHor(double angle) {
        servoPosHor.setPosition(Range.clip(angle, 0.1, 0.9));;

    }


}
