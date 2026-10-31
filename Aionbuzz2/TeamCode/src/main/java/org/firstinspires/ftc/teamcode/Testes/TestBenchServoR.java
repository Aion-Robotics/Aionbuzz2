package org.firstinspires.ftc.teamcode.Testes;



import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class TestBenchServoR {
    private Servo servoPosVert;
    private Servo servoPosHor;

    public void init(HardwareMap hwMap){
        servoPosVert = hwMap.get(Servo.class, "servo_vert");
        servoPosHor =  hwMap.get(Servo.class, "servo_hor");
    }
    public void setServoPosVert(double angle){
        servoPosVert.setPosition(Range.clip(angle, 0.1, 0.9));
    }

    public void setServoPosHor(double angle) {
        servoPosHor.setPosition(Range.clip(angle, 0.1, 0.9));;

    }


}
