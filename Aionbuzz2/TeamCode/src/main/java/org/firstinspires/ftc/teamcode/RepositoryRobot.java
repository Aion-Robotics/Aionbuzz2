package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class RepositoryRobot extends OpMode {
    DcMotor[] motor = new DcMotor[4];
    CRServo[] speedServo = new CRServo[2];
    double limiter;
    final static double rpm = 5990.4;
    public void init() {
    }

    public void loop() {
    }

    public void initializerHardware() {
        // Configurando os motores
        motor[0] = hardwareMap.get(DcMotor.class, "motorDir");
        motor[1] = hardwareMap.get(DcMotor.class, "motorEsq");
        motor[2] = hardwareMap.get(DcMotor.class, "motorIntake");
        motor[3] = hardwareMap.get(DcMotor.class, "motorShooter");

        // Reverte o sentido de um dos motores
        motor[1].setDirection(DcMotor.Direction.REVERSE);

        // Configurando os Servos
        speedServo[0] = hardwareMap.get(CRServo.class, "servoDir");
        speedServo[1] = hardwareMap.get(CRServo.class, "servoEsq");

        speedServo[0].setDirection(CRServo.Direction.REVERSE);
    }

    public double distanceMeter(){
        Double[] distance = new Double[2];
        // Calcula a distância a partir dos ticks emitidos por cada motor individualmente
        for (int i = 0; i < 2; i++){
            int ticks = motor[i].getCurrentPosition();
            double cm = (ticks / 537.6) * (9.66 * Math.PI);
            distance[i] = cm;
        }
        return (distance[0] + distance[1]) / 2.0;
    }
    public void moveRobot(){
        DcMotor.ZeroPowerBehavior stateMotor;

        double stickY = -gamepad1.left_stick_y;
        double stickX = gamepad1.left_stick_x;

        // Potências individuais de cada motor
        double leftMove = Range.clip(stickY - stickX,-0.4, 0.4);
        double rightMove = Range.clip(stickY + stickX, -0.4, 0.4);

        motor[0].setPower(rightMove);
        motor[1].setPower(leftMove);

        // Se o botão RB estiver pressionado, os motores recebem potência máxima
        if (gamepad1.right_bumper) {
            double turbo = 2.5;
            for(int i = 0; i < 2; i++)
                motor[i].setPower(motor[i].getPower()*turbo);
        }

        /* Se o robô estiver não se movendo, as rodas travam,
         impedindo um movimento involuntário por interferência externa */

        if((leftMove == 0.0) && (rightMove == 0.0))
            stateMotor = DcMotor.ZeroPowerBehavior.BRAKE; // BRAKE --> Trava as rodas
        else
            stateMotor = DcMotor.ZeroPowerBehavior.FLOAT; // FLOAT: --> Destrava as rodas

        for (int i = 0; i < 2; i++)
            motor[i].setZeroPowerBehavior(stateMotor);
    }

    public void energyIntake() {
        if (gamepad1.left_bumper) { // Se e o botão LB é pressionado, o intake funciona
            motor[2].setPower(0.75);
            telemetry.addData("Intake Status", "On");
        } else {
            // Se não, ele para de funcionar
            motor[2].setPower(0.0);
            telemetry.addData("Intake Status", "Off");
        }
    }

    public void servoCR() {
        if (gamepad1.left_bumper) {
            for (int i = 0; i < 2; i++)
                speedServo[i].setPower(1.0);
        } else {
            for (CRServo servo : speedServo)
                servo.setPower(0.0);
        }
    }
    public void motorShooter() {
        if (gamepad1.a) {
            limiter = 0.75;
        } else if (gamepad1.b) {
            limiter = 0.9;
        } else if (gamepad1.y){
            limiter = 1;
        }
        else{
            motor[3].setPower(0);
        }

        double valorFinal = limiter * gamepad1.right_trigger;
        motor[3].setPower(valorFinal);
        telemetry.addData("Velocidade do Shooter:","%.1f RPM", (motor[3].getPower()*rpm));
    }
}