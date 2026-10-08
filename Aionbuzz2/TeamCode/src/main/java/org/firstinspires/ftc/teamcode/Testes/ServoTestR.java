package org.firstinspires.ftc.teamcode.Testes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanics.TestBenchServoR;

@TeleOp
public class ServoTestR extends OpMode {
    TestBenchServoR bench = new TestBenchServoR();
    double servoPosition = 0.5;
    // Ângulo de cada toque rápido
    double tapstep = 5.0 / 180.0;
    // Velocida do movimento enquanto segura
    double holdstep = 0.003;
    // Tempo necessário para considerar que está segurando
    double holdDelay = 0.15; // 250 ms
    boolean lastDpadUp = false;
    boolean lastDpadDown = false;
    ElapsedTime upTimer = new ElapsedTime();
    ElapsedTime downTimer = new ElapsedTime();

    @Override
    public void init(){
        bench.init(hardwareMap);
        bench.setServoPos(servoPosition);
    }
    @Override
    public void loop() {
        // ------------ DpadUp -------------- //
        if(gamepad1.dpad_up && !lastDpadUp){
            upTimer.reset();
        }
        if(gamepad1.dpad_up){
            if(upTimer.seconds() >= holdDelay){
                servoPosition += holdstep;
            }
        }
        if(!gamepad1.dpad_up && lastDpadUp){
            if(upTimer.seconds() < holdDelay){
                servoPosition += tapstep;
            }
        }

        // ------------ DpadDown ------------ //
        if(gamepad1.dpad_down && !lastDpadDown){
            downTimer.reset();
        }
        if(gamepad1.dpad_down){
            if(downTimer.seconds() >= holdDelay){
                servoPosition -= holdstep;
            }
        }
        if(!gamepad1.dpad_down && lastDpadDown){
            if(downTimer.seconds() < holdDelay){
                servoPosition -= tapstep;
            }
        }

        // ------------ Limites -------------- //
        if(servoPosition > 0.6){
            servoPosition = 0.6;
        }
        if(servoPosition < 0.0){
            servoPosition = 0.0;
        }
        bench.setServoPos(servoPosition);

        // Guarda o estado anterior dos botões

        lastDpadUp = gamepad1.dpad_up;
        lastDpadDown = gamepad1.dpad_down;

        //------------ telemetry ------------- //

        telemetry.addData("Posição", servoPosition);
        telemetry.addData("Ângulo aproximado", servoPosition * 180);
        telemetry.update();
    }
}
