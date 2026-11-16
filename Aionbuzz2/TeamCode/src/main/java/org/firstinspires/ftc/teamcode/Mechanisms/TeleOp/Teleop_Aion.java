package org.firstinspires.ftc.teamcode.Mechanisms.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class Teleop_Aion extends OpMode {
    DcMotor[] motor = new DcMotor[3];
    DcMotorEx motorShooter = null;
    private Servo servoGate;
    CRServo servoTransfer = null;
    private double servoBloq = 0.0;
    private double servoLib = 1.0;
    private boolean bloqueado = true;
    final static double maxRPM = 6000;
    double limite = 0.0;
    double ticksPorVolta = 28.0; // ticks emitido pelo motor modificado
    double tolerancia = 0.05; // diferença entre a velocidade solicitada e atual
    long temoEstavelNanos = 250_000_000L;
    long inicioEstabilidade = -1;
    boolean pronto = false;
    public static final double POTENCIA_MAXIMA = 0.5;

    @Override
    public void init(){
        initHardware();
        telemetry.addData("Hardware Status", "Registered");

    }
    public void bloquear() {
        servoGate.setPosition(servoBloq);
    }

    public void liberar() {
        servoGate.setPosition(servoLib);
    }

    @Override
    public void loop() {
        // Odometria
        double totalDistance = getCurrentDistance();
        telemetry.addData("Distance", "%.1f cm", totalDistance);

        driveRobot();
        runIntakeControl();
        runShooterControl();
    }

    @Override
    public void stop() {
        if (motorShooter != null) {
            motorShooter.setPower(0);
        }
    }

    private void initHardware(){
        // Registra os motores do Drive Train
        motor[0] = hardwareMap.get(DcMotor.class, "motorDir");
        motor[1] = hardwareMap.get(DcMotor.class, "motorEsq");
        motor[2] = hardwareMap.get(DcMotor.class, "motorIntake");

        // Reverte o sentido de um dos motores da base
        motor[1].setDirection(DcMotor.Direction.REVERSE);

        servoTransfer = hardwareMap.get(CRServo.class, "servoTransfer");
        motorShooter = hardwareMap.get(DcMotorEx.class, "motorShooter");

        motorShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorShooter.setPower(0);

        servoGate = hardwareMap.get(Servo.class, "servoGate");
    }
    private double getCurrentDistance(){
        // Calcula a distância percorrida por ambos os motores juntos
        Double[] distance = new Double[2];
        for (int i = 0; i < 2; i++) {
            int ticks = motor[i].getCurrentPosition();
            double cm = (ticks / 537.6) * (9.66 * Math.PI);
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
            servoTransfer.setPower(0.0);
            intakeLigado = false;
        }

        if (gamepad1.right_trigger_pressed){
            servoTransfer.setPower(-1.0);
            transferLigado = true;
        }
        else{
            servoTransfer.setPower(0.0);
            transferLigado = false;
        }
        telemetry.addData("Intake Status", intakeLigado ? "On" : "Off");
        telemetry.addData("Transfer Status", transferLigado ? "On" : "Off");
    }
    private void runShooterControl(){
        if (gamepad1.a) {
            limite = 0.47;
        } else if (gamepad1.b) {
            limite = 0.48 ;
        } else if (gamepad1.x) {
            limite = 0.485;
        } else if (gamepad1.y) {
            limite = 0.49;}
        else if (gamepad1.right_bumper){
            limite = limite*-1;}
        else {
            limite = 0.0;
            motorShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        double alvoRPM = maxRPM * limite;
        double alvoTicks = (alvoRPM * ticksPorVolta) / 60.0;

        motorShooter.setVelocity(alvoTicks);
        double velocidadeTicks = Math.abs(motorShooter.getVelocity());
        double velocidadeRPM = velocidadeTicks * 60 / ticksPorVolta;

        boolean dentroDaFaixa =
                alvoRPM > 0
                        && velocidadeRPM >=
                        alvoRPM * (1.0 - tolerancia)
                        && velocidadeRPM <=
                        alvoRPM * (1.0 + tolerancia);

        long agora = System.nanoTime();
        if (dentroDaFaixa) {

            if (inicioEstabilidade < 0) {
                inicioEstabilidade = agora;
            }
            pronto = (agora - inicioEstabilidade)
                    >= temoEstavelNanos;
        } else {
            inicioEstabilidade = -1;
            pronto = false;
        }

        telemetry.addData("Alvo RPM", "%.0f", alvoRPM);
        telemetry.addData("RPM real", "%.0f", velocidadeRPM);
        telemetry.addData("Shooter", pronto ? "PRONTO PARA LANÇAR" : "AGUARDANDO VELOCIDADE");
        telemetry.update();

        // Limitador do shooter
        if (pronto) {
            liberar();
        } else {
            bloquear();
        }
    }
}
