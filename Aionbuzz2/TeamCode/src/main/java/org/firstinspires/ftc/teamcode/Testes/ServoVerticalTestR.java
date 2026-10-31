package org.firstinspires.ftc.teamcode.Testes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class ServoVerticalTestR extends OpMode {
    TestBenchServoR bench = new TestBenchServoR();
    double servoPosition = 0.5;
    // Ângulo de cada toque rápido
    double tapstep = 5.0 / 300.0;
    // Velocidade do movimento enquanto segura
    double holdstep = 0.003;
    // Tempo necessário para considerar que está segurando
    double holdDelay = 0.15; // 150 ms
    boolean lastDpadUp = false;
    boolean lastDpadDown = false;
    ElapsedTime upTimer = new ElapsedTime();
    ElapsedTime downTimer = new ElapsedTime();

    @Override
    public void init(){
        bench.init(hardwareMap);
        bench.setServoPosVert(servoPosition);
    }
    @Override
    public void loop() {
        // ------------ DpadUp -------------- //
        /*
        if(-gamepad1.right_stick_y && !lastDpadUp){
            upTimer.reset();
        }
        if(-gamepad1.right_stick_y){
            if(upTimer.seconds() >= holdDelay){
                servoPosition += holdstep;
            }
        }
        if(-gamepad1.right_stick_y && lastDpadUp){
            if(upTimer.seconds() < holdDelay){
                servoPosition += tapstep;
            }
        }

        // ------------ DpadDown ------------ //
        if(!-gamepad1.right_stick_y && !lastDpadDown){
            downTimer.reset();
        }
        if(!-gamepad1.right_stick_y){
            if(downTimer.seconds() >= holdDelay){
                servoPosition -= holdstep;
            }
        }
        if(!-gamepad1.right_stick_y && lastDpadDown){
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
        bench.setServoPosVert(servoPosition);

        // Guarda o estado anterior dos botões //

        lastDpadUp =  - gamepad1.right_stick_y ;
        lastDpadDown = gamepad1.right_stick_y;

        //------------ telemetry ------------- //

        telemetry.addData("Posição", servoPosition);
        telemetry.addData("Ângulo aproximado", servoPosition * 180);
        telemetry.update();
         */
    }
}
