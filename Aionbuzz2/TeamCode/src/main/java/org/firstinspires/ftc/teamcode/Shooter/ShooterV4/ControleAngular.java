package org.firstinspires.ftc.teamcode.Shooter.ShooterV4;

import com.qualcomm.robotcore.util.Range;

public class ControleAngular {

    // CONSTANTES DO CONTROLE

    double Kp;

    double erroAngular = 0;
    double resposta = 0;

    double potenciaMaxima = 0.4;
    double potenciaMinima = 0.0;

    // CONSTRUTOR

    public ControleAngular(double Kp) {
        this.Kp = Kp;
    }

    // CALCULO DO ERRO ANGULAR

    public void calcular(
            double anguloAtual,
            double anguloReferencia) {

        erroAngular = anguloReferencia - anguloAtual;

        // Normalização do erro entre -180 e +180 graus

        while (erroAngular > 180) {
            erroAngular -= 360;
        }

        while (erroAngular < -180) {
            erroAngular += 360;
        }

        // CONTROLE PROPORCIONAL

        resposta = erroAngular * Kp;

        // LIMITAÇÃO DA POTÊNCIA

        resposta = Range.clip(
                resposta,
                -potenciaMaxima,
                potenciaMaxima
        );

        // CORREÇÃO PARA EVITAR POTÊNCIA MUITO PEQUENA

        if (Math.abs(erroAngular) > 2.0
                && Math.abs(resposta) < potenciaMinima) {

            resposta = Math.signum(resposta) * potenciaMinima;
        }

    }

    // RETORNA O ERRO ANGULAR

    public double getErroAngular() {
        return erroAngular;
    }

    // RETORNA A RESPOSTA PROPORCIONAL

    public double getResposta() {
        return resposta;
    }

    // ALTERA O GANHO PROPORCIONAL

    public void setKp(double Kp) {
        this.Kp = Kp;
    }
}