# Présentation du Projet de Fin d'Études
## Traducteur de Langue des Signes sur Android

---

## 1. Titre du Projet
**Thème :** Conception et mise en œuvre d'une solution logicielle sur Android dédiée à la traduction de la langue des signes par reconnaissance de gestes.

**Application :** LS Trad (Traducteursignestext)

**Réalisé par :**
* AHRIKENCHIKH Said
* AZOUAOU Jugurtha

**Encadré par :**
* Dr M. GANA

**Année Universitaire :** 2024-2025
**Établissement :** Université Mouloud Mammeri de Tizi-Ouzou

---

## 2. Introduction & Contexte
* **Problématique :** Les personnes sourdes et malentendantes font face à des barrières de communication importantes au quotidien.
* **Besoin :** Nécessité d'outils technologiques accessibles pour faciliter l'interaction entre entendants et malentendants.
* **Contexte Algérien :** Manque de solutions adaptées à la Langue des Signes Algérienne (LSA) et aux spécificités linguistiques locales (Dialectes, Kabyle, Arabe).

---

## 3. Objectif du Projet
Développer une application mobile Android intuitive et performante permettant une **traduction bidirectionnelle** :
1. **Signes → Texte/Parole :** Reconnaissance des gestes de la main via la caméra.
2. **Voix/Texte → Signes :** Conversion de la parole en démonstration visuelle de signes.

---

## 4. Solution Proposée : LS Trad
Une application mobile innovante intégrant l'Intelligence Artificielle pour briser les barrières de communication.

### Fonctionnalités Clés :
* **Traduction Temps Réel :** Interprétation instantanée des gestes.
* **Multilingue :** Support du Français, Anglais, Arabe et Kabyle.
* **Reconnaissance Vocale :** Utilisation de Google Speech Recognition pour capturer la parole.
* **Interface Intuitive :** Design moderne développé avec Jetpack Compose.
* **Accessibilité :** Adaptée aux besoins locaux et utilisable hors ligne (partiellement).

---

## 5. Architecture Technique

### Stack Technologique :
* **OS :** Android
* **Langages :** Kotlin, Java
* **UI :** Jetpack Compose (Material Design 3)
* **Architecture :** MVVM (Model-View-ViewModel)

### Cœur IA & Traitement :
* **Reconnaissance de Gestes :** Google MediaPipe (Hand Tracking & Gesture Recognition).
* **Reconnaissance Vocale :** Google Speech-to-Text API.
* **Machine Learning :** Modèles personnalisés (TensorFlow) entraînés sur un dataset de +3000 images.

---

## 6. Démonstration des Modules

### Module A : Signes vers Texte
* Utilisation de la caméra du smartphone.
* Détection des points clés de la main (Hand Landmarks).
* Classification du geste et affichage du texte correspondant.

### Module B : Voix vers Signes
* Capture de la voix de l'utilisateur.
* Conversion en texte.
* Recherche et affichage de la vidéo/image du signe correspondant dans la base de données.

---

## 7. Résultats & Performances
* **Précision :** 
    * 80% pour l'alphabet.
    * 75% pour les expressions courantes.
* **Temps de réponse :** Entre 0.5 et 1 seconde.
* **Impact Social :** Favorise l'inclusion sociale et l'autonomie des personnes malentendantes.

---

## 8. Perspectives d'Avenir
* Intégration d'un avatar 3D pour une restitution plus fluide des signes.
* Enrichissement du dictionnaire de la Langue des Signes Algérienne (LSA).
* Amélioration du mode hors ligne complet.
* Portabilité sur d'autres plateformes (iOS, Web).

---

## 9. Conclusion
Ce projet démontre la faisabilité d'une solution technologique locale pour l'inclusion. LS Trad est un pas en avant vers une communication sans barrières en Algérie.

**Merci de votre attention !**
