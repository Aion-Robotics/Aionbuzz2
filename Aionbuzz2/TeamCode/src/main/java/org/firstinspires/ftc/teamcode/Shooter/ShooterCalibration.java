package org.firstinspires.ftc.teamcode.Shooter;

public class ShooterCalibration {

        // Estado da calibração
        public static boolean HOOD_CALIBRADA = false;

        public static boolean MECANICA_VALIDADA = false;

        // Curso nominal do servo
        public static final double CURSO_SERVO = 300.0;

        // Engrenagens
        public static double DENTES_ENGRENAGEM_SERVO = 30;
        public static double DENTES_ENGRENAGEM_HOOD = 30;

        // Sentido da transmissão
        public static double SENTIDO_TRANSMISSAO = 1.0;

        // Referência angular
        public static double ANGULO_ZERO_SERVO = 0.0;
        public static double ANGULO_ZERO_HOOD = 0.0;

        // Limites mecânicos ainda não medidos
        public static double ANGULO_MINIMO_HOOD = 0.0;
        public static double ANGULO_MAXIMO_HOOD = 0.0;

        // Posições normalizadas do servo
        public static double POSICAO_MINIMA_SERVO = 0.0;
        public static double POSICAO_MAXIMA_SERVO = 1.0;

        //parte responsável pelo movimento da trava
        public static boolean TRAVA_CALIBRADA = false;
        public static double MOVIMENTO_TRAVA_GRAUS = 90; // modifique esse valor para mudar a posição do servo
        public static double POSIÇAO_TRAVA_FECHADA = 0.0;
        public static double POSICAO_TRAVA_ABERTA = 0.0;
}