package org.firstinspires.ftc.teamcode.Shooter;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter {
    private DcMotorEx motorShooter;
    private Servo servoGate;
    private final double servoBloq = 0.0;
    private final double servoLib = 1.0;

    final static double maxRPM = 6000;
    static double limite;
    double ticksPorVolta = 28.0;
    static final double tolerancia = 0.05;
    long tempoEstavelNanos = 250_000_000L;
    long inicioEstabilidade = -1;
    boolean pronto = false;

    // Construtor para inicializar os hardwares
    public Shooter(HardwareMap hardwareMap){
        this.motorShooter = hardwareMap.get(DcMotorEx.class, "motorShooter");
        this.servoGate = hardwareMap.get(Servo.class, "servoGate");

        motorShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorShooter.setDirection(DcMotorSimple.Direction.REVERSE);
        bloquear(); // Trava inicial
    }

    public void bloquear() {
        servoGate.setPosition(servoBloq);
    }

    public void liberar() {
        servoGate.setPosition(servoLib);
    }

    public void handleShooting(Gamepad gamepad, Telemetry telemetry){
        if (gamepad.a) {
            limite = 0.15; // 15%
        } else if (gamepad.b) {
            limite = 0.25; // 25%
        }  else if (gamepad.y) {
            limite = 0.4675; // 46.75%
        } else if (gamepad.x) {
            limite = 0.585; // 58.5%
        } else {
            limite = 0.0;
        }

        double alvoRPM = maxRPM * limite;
        double alvoTicks = (alvoRPM * ticksPorVolta) / 60.0;

        motorShooter.setVelocity(alvoTicks);
        double velocidadeTicks = Math.abs(motorShooter.getVelocity());
        double velocidadeRPM = velocidadeTicks * 60 / ticksPorVolta;

        boolean dentroDaFaixa =
                alvoRPM > 0
                        && velocidadeRPM >= alvoRPM * (1.0 - tolerancia)
                        && velocidadeRPM <= alvoRPM * (1.0 + tolerancia);

        long agora = System.nanoTime();
        if (dentroDaFaixa) {
            if (inicioEstabilidade < 0) {
                inicioEstabilidade = agora;
            }
            pronto = (agora - inicioEstabilidade) >= tempoEstavelNanos;
        } else {
            inicioEstabilidade = -1;
            pronto = false;
        }

        if (telemetry != null) {
            telemetry.addData("Alvo RPM", "%.0f", alvoRPM);
            telemetry.addData("RPM real", "%.0f", velocidadeRPM);
            telemetry.addData("Shooter", pronto ? "PRONTO PARA LANÇAR" : "AGUARDANDO VELOCIDADE");
        }

        // O bloquear e liberar são acionados automaticamente com base na velocidade
        if (pronto) {
            liberar();
        } else {
            bloquear();
        }
    }
}