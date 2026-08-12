package com.berbersoft.traducteursignestext.models

enum class GestureType {
    NONE,
    AMOUR,
    AU_REVOIR,
    PAS_BIEN,
    OUI,
    MERCI,
    MOI,
    NON,
    DIRE,
    HELLO_ASL,
    APPRENDRE,
    NOM,
    STYLO_ASL,
    PLEASE,
    // Lettres de l'alphabet
    A, B, C, D, E, F, G, H, I, J, K, L, M, 
    N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    companion object {
        fun fromLabel(label: String): GestureType {
            return when (label.lowercase()) {
                "love", "iloveyou" -> AMOUR
                "bye" -> AU_REVOIR
                "notok" -> PAS_BIEN
                "yes" -> OUI
                "thankyou" -> MERCI
                "me" -> MOI
                "pointing_up" -> NON
                "tell" -> DIRE
                "hello" -> HELLO_ASL
                "learn" -> APPRENDRE
                "name" -> NOM
                "pen" -> STYLO_ASL
                "please" -> PLEASE
                // Mapper les gestes alphabétiques
                "a" -> A
                "b" -> B
                "c" -> C
                "d" -> D
                "e" -> E
                "f" -> F
                "g" -> G
                "h" -> H
                "i" -> I
                "j" -> J
                "k" -> K
                "l" -> L
                "m" -> M
                "n" -> N
                "o" -> O
                "p" -> P
                "q" -> Q
                "r" -> R
                "s" -> S
                "t" -> T
                "u" -> U
                "v" -> V
                "w" -> W
                "x" -> X
                "y" -> Y
                "z" -> Z
                else -> NONE
            }
        }
    }
} 