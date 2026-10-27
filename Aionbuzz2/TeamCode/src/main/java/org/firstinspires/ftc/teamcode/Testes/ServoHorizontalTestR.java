package org.firstinspires.ftc.teamcode.Testes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanics.TestBenchServoR;

@TeleOp
public class ServoHorizontalTestR extends OpMode {
   TestBenchServoR bench = new TestBenchServoR();
    double posHor = 0.5;
    double posVert = 0.5;
    double speed = 0.01;

    @Override
    public void init() {
        bench.init(hardwareMap);
        bench.setServoPosVert(posVert);
        bench.setServoPosHor(posHor);
        telemetry.addData("Tudo pronto", "Status");
    }

    @Override
    public void loop() {
        double eixoY = -gamepad2.right_stick_y;
        double eixoX = gamepad2.right_stick_x;

        posVert = eixoY * speed;
        posHor = eixoX * speed;

        bench.setServoPosVert(posVert);
        bench.setServoPosHor(posHor);
    }
}
