package org.firstinspires.ftc.teamcode.Mechanisms.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Shooter.Shooter;

@Disabled
@TeleOp(name = "TeleOp")
public class DriveTrain extends OpMode {
    Shooter shooter;
    CRServo servoTransfer;
    DcMotor[] motor = new DcMotor[3];
    public static final double POTENCIA_MAXIMA = 0.5;
    public static final double TICKS_POR_VOLTA = 537.6;
    public static final double CIRCUNFERENCIA_RODA = 9.6 * Math.PI;


    @Override
    public void init(){
        initHardware();
        telemetry.addData("Status do Hardware", "Registrado");
    }

    @Override
    public void loop(){
        // Odometria
        double totalDistance = getCurrentDistance();
        telemetry.addData("Distância", "%.1f cm", totalDistance);

        driveRobot();
        runIntakeControl();
        shooter.handleShooting(gamepad1, telemetry);
    }

    private void initHardware(){
        // Registra os motores do Drive Train
        motor[0] = hardwareMap.get(DcMotor.class, "motorDir");
        motor[1] = hardwareMap.get(DcMotor.class, "motorEsq");
        motor[2] = hardwareMap.get(DcMotor.class, "motorIntake");

        // Registra o servo do Transfer e a instância do Shooter
        servoTransfer = hardwareMap.get(CRServo.class, "servoTransfer");
        shooter = new Shooter(hardwareMap);

        // Reverte o sentido de um dos motores da base e do servo
        motor[1].setDirection(DcMotor.Direction.REVERSE);
        servoTransfer.setDirection(DcMotorSimple.Direction.REVERSE);

        // Reseta os ticks na inicialização
        motor[0].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor[1].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Induz os motores a funcionarem com suporte dos encoders
        motor[0].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor[1].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    private double getCurrentDistance(){
        // Calcula a distância percorrida por ambos os motores juntos
        Double[] distance = new Double[2];
        for (int i = 0; i < 2; i++) {
            int ticks = motor[i].getCurrentPosition();
            double cm = (ticks / TICKS_POR_VOLTA) * (CIRCUNFERENCIA_RODA);
            distance[i] = cm;
        }
        return (distance[0] + distance[1]) / 2.0;
    }
    private void driveRobot(){
        DcMotor.ZeroPowerBehavior stateMotor;
        double currentMax = POTENCIA_MAXIMA;

        // Recebe os valores dos analógicos do controle
        double stickY = -gamepad1.left_stick_y;
        double stickX = gamepad1.left_stick_x;

        // Se o botão RB é pressionado, os motores funcionam na capacidade máxima
        if (gamepad1.left_bumper)
            currentMax = 1.0;

        // Guarda as potências individuais de cada motor
        double leftMove = Range.clip(stickY - stickX, -currentMax, currentMax);
        double rightMove = Range.clip(stickY + stickX, -currentMax, currentMax);

        motor[0].setPower(rightMove);
        motor[1].setPower(leftMove);

        // Se ambos os motores estiverem parados, as rodas são travadas
        stateMotor = ((leftMove == 0.0) && (rightMove == 0.0)) ?
                DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT;

        for (int i = 0; i < 2; i++)
            motor[i].setZeroPowerBehavior(stateMotor);
    }
    private void runIntakeControl(){
        boolean intakeLigado;
        boolean transferLigado;

        if (gamepad1.right_bumper) { // Se o botão LB estiver pressionado, o intake é ligado
            motor[2].setPower(1.0);
            intakeLigado = true;
        }
        else { // Caso contrário, ele permanece desligado
            motor[2].setPower(0.0);
            intakeLigado = false;
        }

        if (gamepad1.right_trigger_pressed){
            servoTransfer.setPower(1.0);
            transferLigado = true;
        }
        else{
            servoTransfer.setPower(0.0);
            transferLigado = false;
        }
        telemetry.addData("Intake Status", intakeLigado ? "On" : "Off");
        telemetry.addData("Transfer Status", transferLigado ? "On" : "Off");
    }
}