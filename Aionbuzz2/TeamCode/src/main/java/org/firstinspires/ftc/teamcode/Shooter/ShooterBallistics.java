package org.firstinspires.ftc.teamcode.Shooter;

public class ShooterBallistics {
     public static double calcularVelocidade(
                double distancia,
                double alturaLancamento,
                double alturaAlvo,
                double anguloGraus) {

            double angulo = Math.toRadians(anguloGraus);

            double deltaAltura =
                    alturaAlvo - alturaLancamento;

            double denominador =
                    2 * Math.pow(Math.cos(angulo), 2) *
                            (distancia * Math.tan(angulo) - deltaAltura);

            if (denominador <= 0) {
                return Double.NaN;
            }

            return Math.sqrt(
                    ShooterConfig.GRAVIDADE *
                            Math.pow(distancia, 2) /
                            denominador
            );
        }
    }