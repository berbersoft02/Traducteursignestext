package com.berbersoft.traducteursignestext

enum class GestureType(val label: String, val description: String) {
    // Gestes LSF de base (statiques)
    AMOUR("ILoveYou", "Je t'aime en LSF ❤️"),
    BONJOUR("Open_Palm", "Bonjour en LSF 👋"),
    OUI("Closed_Fist", "Oui en LSF 👌"),
    NON("Pointing_Up", "Non en LSF ☝️"),
    AIDE("Thumb_Up", "Aide en LSF 🆘"),
    MERCI("Closed_Fist", "Merci en LSF 🙏"),
    
    // Gestes LSF dynamiques
    AU_REVOIR("Open_Palm", "Au revoir en LSF 👋"),
    MANGER("Open_Palm", "Manger en LSF 🍽️"),
    BOIRE("Closed_Fist", "Boire en LSF 🥤"),
    TOILETTES("Pinky_Finger", "Toilettes en LSF 🚽"),
    TOILETTES_ALT("Pinky", "Toilettes en LSF 🚽"),
    URGENCE("Open_Palm", "Urgence en LSF 🚨"),
    
    // État par défaut
    NONE("None", "Aucun geste détecté"),

    // Ajouter un nouveau type de geste pour "Mauvais"
    MAUVAIS("Victory", "Mauvais en LSF 👎");

    companion object {
        fun fromLabel(label: String): GestureType {
            return values().find { it.label.equals(label, ignoreCase = true) } ?: NONE
        }
    }
} 