package com.berbersoft.traducteursignestext.utils

import androidx.compose.animation.core.CubicBezierEasing

// Fonctions d'interpolation personnalisées
val EaseInOutQuart = CubicBezierEasing(0.76f, 0f, 0.24f, 1f)
val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

// Classe utilitaire pour gérer 4 valeurs
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)