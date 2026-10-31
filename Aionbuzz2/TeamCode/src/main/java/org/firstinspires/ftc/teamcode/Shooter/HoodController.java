package org.firstinspires.ftc.teamcode.Shooter;

import com.qualcomm.robotcore.hardware.Servo;

public class HoodController {

        private final Servo servo;

        private double anguloDesejado = 0;

        public HoodController(Servo servo) {
            this.servo = servo;
        }

        public void definirAngulo(double angulo) {
            anguloDesejado = angulo;
        }

        public double getAnguloDesejado() {
            return anguloDesejado;
        }

        public double converterAnguloParaPosicao(
                double anguloHood,
                double relacaoEngrenagens,
                double posicaoInicial) {

            double anguloServo =
                    anguloHood * relacaoEngrenagens;

            return posicaoInicial +
                    (anguloServo / 300.0);
        }
    }