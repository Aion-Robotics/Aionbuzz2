package org.firstinspires.ftc.teamcode.ShooterV4;

import com.qualcomm.robotcore.util.Range;

public class ControleAngular {
        private double anguloAtual;
        private double anguloReferencia;
        private double erroAngular;

        private double kp;
        private double resposta;

        public ControleAngular(double kp){
            this.kp = kp;
        }
        public void calcular(double atual, double referencia){
            anguloAtual = atual;
            anguloReferencia = referencia;

            erroAngular = ((referencia - atual + 540) % 360) - 180;
            resposta = kp * Math.abs(erroAngular);
            resposta = Range.clip(resposta,0,1);
        }
        public double getErroAngular(){
            return erroAngular;
        }
        public double getResposta(){
            return resposta;
        }
    }