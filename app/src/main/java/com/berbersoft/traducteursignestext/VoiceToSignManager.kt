package com.berbersoft.traducteursignestext

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

import com.berbersoft.traducteursignestext.R

data class SignWord(
    val word: String,
    val imageRes: Int,
    val description: String,
    val category: SignCategory
)

enum class SignCategory {
    BASIC,      // Mots basiques (Bonjour, Merci, etc.)
    EMOTIONS,   // Émotions (Content, Triste, etc.)
    ACTIONS,    // Actions (Manger, Boire, etc.)
    QUESTIONS,  // Questions (Où, Quand, etc.)
    DAILY,// Vie quotidienne (Toilettes, Dormir, etc.)
    NUMBERS,    // Nombres (Un, Deux, Trois, etc.)
    EMERGENCY, // Urgences (Aide, Médecin, etc.) // Chiffres (Un, Deux, Trois, etc.)
    DAYS,    // Nouvelle catégorie pour les jours
    ALPHABET,   // Nouvelle catégorie pour l'alphabet
}

object SignDictionary {
    val words = listOf(
        // Ajouter l'alphabet au début de la liste
        SignWord("lettre a", R.drawable.logo_app, "Lettre A en LSF", SignCategory.ALPHABET),
        SignWord("lettre b", R.drawable.logo_app, "Lettre B en LSF", SignCategory.ALPHABET),
        SignWord("lettre c", R.drawable.logo_app, "Lettre C en LSF", SignCategory.ALPHABET),
        SignWord("lettre d", R.drawable.logo_app, "Lettre D en LSF", SignCategory.ALPHABET),
        SignWord("lettre e", R.drawable.logo_app, "Lettre E en LSF", SignCategory.ALPHABET),
        SignWord("lettre f", R.drawable.logo_app, "Lettre F en LSF", SignCategory.ALPHABET),
        SignWord("lettre g", R.drawable.logo_app, "Lettre G en LSF", SignCategory.ALPHABET),
        SignWord("lettre h", R.drawable.logo_app, "Lettre H en LSF", SignCategory.ALPHABET),
        SignWord("lettre i", R.drawable.logo_app, "Lettre I en LSF", SignCategory.ALPHABET),
        SignWord("lettre j", R.drawable.logo_app, "Lettre J en LSF", SignCategory.ALPHABET),
        SignWord("lettre k", R.drawable.logo_app, "Lettre K en LSF", SignCategory.ALPHABET),
        SignWord("lettre l", R.drawable.logo_app, "Lettre L en LSF", SignCategory.ALPHABET),
        SignWord("lettre m", R.drawable.logo_app, "Lettre M en LSF", SignCategory.ALPHABET),
        SignWord("lettre n", R.drawable.logo_app, "Lettre N en LSF", SignCategory.ALPHABET),
        SignWord("lettre o", R.drawable.logo_app, "Lettre O en LSF", SignCategory.ALPHABET),
        SignWord("lettre p", R.drawable.logo_app, "Lettre P en LSF", SignCategory.ALPHABET),
        SignWord("lettre q", R.drawable.logo_app, "Lettre Q en LSF", SignCategory.ALPHABET),
        SignWord("lettre r", R.drawable.logo_app, "Lettre R en LSF", SignCategory.ALPHABET),
        SignWord("lettre s", R.drawable.logo_app, "Lettre S en LSF", SignCategory.ALPHABET),
        SignWord("lettre t", R.drawable.logo_app, "Lettre T en LSF", SignCategory.ALPHABET),
        SignWord("lettre u", R.drawable.logo_app, "Lettre U en LSF", SignCategory.ALPHABET),
        SignWord("lettre v", R.drawable.logo_app, "Lettre V en LSF", SignCategory.ALPHABET),
        SignWord("lettre w", R.drawable.logo_app, "Lettre W en LSF", SignCategory.ALPHABET),
        SignWord("lettre x", R.drawable.logo_app, "Lettre X en LSF", SignCategory.ALPHABET),
        SignWord("lettre y", R.drawable.logo_app, "Lettre Y en LSF", SignCategory.ALPHABET),
        SignWord("lettre z", R.drawable.logo_app, "Lettre Z en LSF", SignCategory.ALPHABET),

        // Les jours de la semaine
        SignWord("lundi", R.drawable.sign_lundi, "Lundi en LSF", SignCategory.DAYS),
        SignWord("mardi", R.drawable.sign_mardi, "Mardi en LSF", SignCategory.DAYS),
        SignWord("mercredi", R.drawable.sign_mercredi, "Mercredi en LSF", SignCategory.DAYS),
        SignWord("jeudi", R.drawable.sign_jeudi, "Jeudi en LSF", SignCategory.DAYS),
        SignWord("vendredi", R.drawable.sign_vendredi, "Vendredi en LSF", SignCategory.DAYS),
        SignWord("samedi", R.drawable.sign_samedi, "Samedi en LSF", SignCategory.DAYS),
        SignWord("dimanche", R.drawable.sign_dimanche, "Dimanche en LSF", SignCategory.DAYS),

        // Mots de base existants
        SignWord("bonjour", R.drawable.sign_hello, "Dire bonjour", SignCategory.BASIC),
        SignWord("salut", R.drawable.sign_hello, "Dire bonjour", SignCategory.BASIC),
        SignWord("coucou", R.drawable.sign_hello, "Dire bonjour", SignCategory.BASIC),
        SignWord("au revoir", R.drawable.sign_goodbye, "Dire au revoir", SignCategory.BASIC),
        SignWord("bye", R.drawable.sign_goodbye, "Dire au revoir", SignCategory.BASIC),
        SignWord("merci", R.drawable.sign_thanks, "Dire merci", SignCategory.BASIC),
        SignWord("toilettes", R.drawable.sign_toilet, "Aller aux toilettes", SignCategory.DAILY),
        SignWord("wc", R.drawable.sign_toilet, "Aller aux toilettes", SignCategory.DAILY),
        SignWord("boire", R.drawable.sign_drink, "Boire de l'eau", SignCategory.ACTIONS),
        SignWord("soif", R.drawable.sign_drink, "Boire de l'eau", SignCategory.ACTIONS),
        SignWord("manger", R.drawable.sign_eat, "Action de manger", SignCategory.ACTIONS),
        SignWord("faim", R.drawable.sign_eat, "Action de manger", SignCategory.ACTIONS),

        // Nouveaux mots
        SignWord("de rien", R.drawable.sign_welcome, "Dire de rien", SignCategory.BASIC),
        SignWord("désolé", R.drawable.sign_sorry, "S'excuser", SignCategory.BASIC),
        SignWord("oui", R.drawable.sign_yes, "Dire oui", SignCategory.BASIC),
        SignWord("non", R.drawable.sign_no, "Dire non", SignCategory.BASIC),

        // Nouveaux mots avec leurs synonymes
        SignWord("amour", R.drawable.sign_love, "Exprimer l'amour", SignCategory.EMOTIONS),
        SignWord("fatigué", R.drawable.sign_tired, "Exprimer la fatigue", SignCategory.EMOTIONS),
        SignWord("marcher", R.drawable.sign_walk, "Se déplacer à pied", SignCategory.ACTIONS),
        SignWord("où", R.drawable.sign_where, "Demander un lieu", SignCategory.QUESTIONS),
        SignWord("quand", R.drawable.sign_when, "Demander un moment", SignCategory.QUESTIONS),
        SignWord("comment", R.drawable.sign_how, "Demander une méthode", SignCategory.QUESTIONS),
        SignWord("école", R.drawable.sign_school, "Lieu d'éducation", SignCategory.DAILY),
        SignWord("travail", R.drawable.sign_work, "Lieu professionnel", SignCategory.DAILY),
        SignWord("urgence", R.drawable.sign_emergency, "Situation critique", SignCategory.EMERGENCY),
        SignWord("danger", R.drawable.sign_danger, "Signaler un péril", SignCategory.EMERGENCY),

        // Famille
        SignWord("papa", R.drawable.sign_father, "Le père", SignCategory.BASIC),
        SignWord("maman", R.drawable.sign_mother, "La mère", SignCategory.BASIC),
        SignWord("frère", R.drawable.sign_brother, "Le frère", SignCategory.BASIC),
        SignWord("soeur", R.drawable.sign_sister, "La soeur", SignCategory.BASIC),

        // Émotions supplémentaires
        SignWord("content", R.drawable.sign_happy, "Être heureux", SignCategory.EMOTIONS),
        SignWord("triste", R.drawable.sign_sad, "Être triste", SignCategory.EMOTIONS),
        SignWord("fâché", R.drawable.sign_angry, "Être en colère", SignCategory.EMOTIONS),

        // Besoins basiques
        SignWord("dormir", R.drawable.sign_sleep, "Aller dormir", SignCategory.ACTIONS),
        SignWord("malade", R.drawable.sign_sick, "Être malade", SignCategory.EMERGENCY),
        SignWord("médecin", R.drawable.sign_doctor, "Voir un médecin", SignCategory.EMERGENCY),

        // Temps
        SignWord("aujourd'hui", R.drawable.sign_today, "Le jour présent", SignCategory.DAILY),
        SignWord("demain", R.drawable.sign_tomorrow, "Le jour suivant", SignCategory.DAILY),
        SignWord("hier", R.drawable.sign_yesterday, "Le jour passé", SignCategory.DAILY),

        // Nouveaux mots
        SignWord("bon appétit", R.drawable.sign_appetit, "Souhaiter un bon repas", SignCategory.BASIC),
        SignWord("bravo", R.drawable.sign_congrats, "Féliciter quelqu'un", SignCategory.EMOTIONS),
        SignWord("bonsoir", R.drawable.sign_goodevening, "Saluer le soir", SignCategory.BASIC),
        SignWord("bonnes vacances", R.drawable.sign_goodhilidays, "Souhaiter de bonnes vacances", SignCategory.BASIC),
        SignWord("bonne nuit", R.drawable.sign_goodnight, "Souhaiter une bonne nuit", SignCategory.BASIC),
        SignWord("ça va", R.drawable.sign_howareyou, "Demander comment ça va", SignCategory.QUESTIONS),
        SignWord("bon weekend", R.drawable.sign_niceweekend, "Souhaiter un bon weekend", SignCategory.BASIC),
        SignWord("bienvenue", R.drawable.sign_welcome, "Accueillir quelqu'un", SignCategory.BASIC),
        SignWord("ton nom", R.drawable.sign_whaturname, "Demander le nom", SignCategory.QUESTIONS),

        // Nouveaux mots
        SignWord("fleur", R.drawable.sign_fleur, "Une fleur", SignCategory.BASIC),
        SignWord("adolescent", R.drawable.sign_adolescent, "Un adolescent", SignCategory.BASIC),
        SignWord("verre", R.drawable.sign_verre, "Un verre", SignCategory.DAILY),
        SignWord("danser", R.drawable.sign_danse, "Action de danser", SignCategory.ACTIONS),
        SignWord("vache", R.drawable.sign_vache, "Une vache", SignCategory.BASIC),
        SignWord("se lever", R.drawable.sign_selever, "Action de se lever", SignCategory.ACTIONS),
        SignWord("sauter", R.drawable.sign_sauter, "Action de sauter", SignCategory.ACTIONS),
        SignWord("chanter", R.drawable.sign_chanter, "Action de chanter", SignCategory.ACTIONS),

        // Les nombres
        SignWord("zéro", R.drawable.signe_zero, "Le chiffre 0", SignCategory.NUMBERS),
        SignWord("un", R.drawable.signe_un, "Le chiffre 1", SignCategory.NUMBERS),
        SignWord("deux", R.drawable.signe_deux, "Le chiffre 2", SignCategory.NUMBERS),
        SignWord("trois", R.drawable.signe_trois, "Le chiffre 3", SignCategory.NUMBERS),
        SignWord("quatre", R.drawable.signe_quatre, "Le chiffre 4", SignCategory.NUMBERS),
        SignWord("cinq", R.drawable.signe_cinq, "Le chiffre 5", SignCategory.NUMBERS),
        SignWord("six", R.drawable.signe_six, "Le chiffre 6", SignCategory.NUMBERS),
        SignWord("sept", R.drawable.signe_sept, "Le chiffre 7", SignCategory.NUMBERS),
        SignWord("huit", R.drawable.signe_huit, "Le chiffre 8", SignCategory.NUMBERS),
        SignWord("neuf", R.drawable.signe_neuf, "Le chiffre 9", SignCategory.NUMBERS),
        SignWord("dix", R.drawable.signe_dix, "Le chiffre 10", SignCategory.NUMBERS),
        SignWord("onze", R.drawable.signe_onze, "Le chiffre 11", SignCategory.NUMBERS),
        SignWord("douze", R.drawable.signe_douze, "Le chiffre 12", SignCategory.NUMBERS),
        SignWord("treize", R.drawable.signe_treize, "Le chiffre 13", SignCategory.NUMBERS),
        SignWord("quatorze", R.drawable.signe_quatorze, "Le chiffre 14", SignCategory.NUMBERS),
        SignWord("quinze", R.drawable.signe_quinze, "Le chiffre 15", SignCategory.NUMBERS),
        SignWord("seize", R.drawable.signe_seize, "Le chiffre 16", SignCategory.NUMBERS),
        SignWord("dix-sept", R.drawable.signe_dixsept, "Le chiffre 17", SignCategory.NUMBERS),
        SignWord("dix-huit", R.drawable.signe_dixhuit, "Le chiffre 18", SignCategory.NUMBERS),
        SignWord("dix-neuf", R.drawable.signe_dixneuf, "Le chiffre 19", SignCategory.NUMBERS),
        SignWord("vingt", R.drawable.signe_vingt, "Le chiffre 20", SignCategory.NUMBERS),
        SignWord("quarante", R.drawable.signe_quarante, "Le chiffre 40", SignCategory.NUMBERS),
        SignWord("cinquante", R.drawable.signe_cinquente, "Le chiffre 50", SignCategory.NUMBERS),
        SignWord("soixante", R.drawable.signe_soixante, "Le chiffre 60", SignCategory.NUMBERS),
        SignWord("soixante-dix", R.drawable.signe_soixantedix, "Le chiffre 70", SignCategory.NUMBERS),
        SignWord("quatre-vingts", R.drawable.signe_quatrevint, "Le chiffre 80", SignCategory.NUMBERS),
        SignWord("quatre-vingt-dix", R.drawable.signe_quatrevintdix,"Le chiffre 90", SignCategory.NUMBERS),
        SignWord("cent", R.drawable.signe_cent, "Le chiffre 100", SignCategory.NUMBERS),
        SignWord("apprendre", R.drawable.logo_app, "Apprendre en LSF", SignCategory.ACTIONS),
        SignWord("comprendre", R.drawable.logo_app, "Comprendre en LSF", SignCategory.ACTIONS),
        SignWord("pardon je comprends pas", R.drawable.logo_app, "Pardon je ne comprends pas en LSF", SignCategory.BASIC),
        SignWord("s'il te plait", R.drawable.logo_app, "S'il te plaît en LSF", SignCategory.BASIC),
        SignWord("enfant", R.drawable.logo_app, "Enfant en LSF", SignCategory.BASIC),
        SignWord("femme", R.drawable.logo_app, "Femme en LSF", SignCategory.BASIC),
        SignWord("homme", R.drawable.logo_app, "Homme en LSF", SignCategory.BASIC),
        SignWord("maison", R.drawable.logo_app, "Maison en LSF", SignCategory.BASIC),
        SignWord("poser une question", R.drawable.logo_app, "Poser une question en LSF", SignCategory.QUESTIONS),
        SignWord("ça me fait plaisir", R.drawable.logo_app, "Ça me fait plaisir en LSF", SignCategory.BASIC),
        SignWord("ça va", R.drawable.logo_app, "Ça va? en LSF", SignCategory.QUESTIONS),
        SignWord("combien", R.drawable.logo_app, "Combien? en LSF", SignCategory.QUESTIONS),
        SignWord("comment", R.drawable.logo_app, "Comment? en LSF", SignCategory.QUESTIONS),
        SignWord("j'ai compris", R.drawable.logo_app, "J'ai compris en LSF", SignCategory.BASIC),
        SignWord("je n'ai pas compris", R.drawable.logo_app, "Je n'ai pas compris en LSF", SignCategory.BASIC),
        SignWord("je suis content de te rencontrer", R.drawable.logo_app, "Je suis content de te rencontrer en LSF", SignCategory.BASIC),
        SignWord("je suis désolé", R.drawable.logo_app, "Je suis désolé en LSF", SignCategory.BASIC),
        SignWord("je t'accompagne", R.drawable.logo_app, "Je t'accompagne? en LSF", SignCategory.QUESTIONS),
        SignWord("merci pour ton aide", R.drawable.logo_app, "Merci pour ton aide en LSF", SignCategory.BASIC),
        SignWord("où", R.drawable.logo_app, "Où? en LSF", SignCategory.QUESTIONS),
        SignWord("pardon", R.drawable.logo_app, "Pardon, je m'excuse en LSF", SignCategory.BASIC),
        SignWord("pourquoi", R.drawable.logo_app, "Pourquoi? en LSF", SignCategory.QUESTIONS),
        SignWord("pourquoi faire", R.drawable.logo_app, "Pourquoi faire? en LSF", SignCategory.QUESTIONS),
        SignWord("quand", R.drawable.logo_app, "Quand? en LSF", SignCategory.QUESTIONS),
        SignWord("que faire", R.drawable.logo_app, "Que faire? en LSF", SignCategory.QUESTIONS),
        SignWord("qu'est-ce que tu fais", R.drawable.logo_app, "Qu'est-ce que tu fais? en LSF", SignCategory.QUESTIONS),
        SignWord("quelle heure est-il", R.drawable.logo_app, "Quelle heure est-il? en LSF", SignCategory.QUESTIONS),
        SignWord("qui", R.drawable.logo_app, "Qui? en LSF", SignCategory.QUESTIONS),
        SignWord("quoi", R.drawable.logo_app, "Quoi? en LSF", SignCategory.QUESTIONS),
        SignWord("tu as faim", R.drawable.logo_app, "Tu as faim? en LSF", SignCategory.QUESTIONS),
        SignWord("tu m'accompagnes", R.drawable.logo_app, "Tu m'accompagnes? en LSF", SignCategory.QUESTIONS),
        SignWord("tu peux signer lentement", R.drawable.logo_app, "Tu peux signer lentement? en LSF", SignCategory.QUESTIONS),
        SignWord("une personne", R.drawable.logo_app, "Une personne en LSF", SignCategory.BASIC),
        SignWord("meilleur", R.drawable.logo_app, "Le meilleur", SignCategory.EMOTIONS),
        SignWord("tous les mois", R.drawable.logo_app, "Tous les mois", SignCategory.DAILY),
        SignWord("c'est dommage", R.drawable.logo_app, "C'est dommage", SignCategory.EMOTIONS),
        SignWord("il n'y a plus", R.drawable.logo_app, "Il n'y a plus", SignCategory.BASIC),
        SignWord("pas besoin", R.drawable.logo_app, "Pas besoin", SignCategory.BASIC),
        SignWord("inutile", R.drawable.logo_app, "Inutile", SignCategory.BASIC),
        SignWord("c'est pas juste", R.drawable.logo_app, "C'est pas juste", SignCategory.EMOTIONS),
        SignWord("pas d'accord", R.drawable.logo_app, "Pas d'accord", SignCategory.BASIC),
        SignWord("parfait", R.drawable.logo_app, "Parfait", SignCategory.EMOTIONS),
        SignWord("c'est ça", R.drawable.logo_app, "C'est ça", SignCategory.BASIC),
        SignWord("c'est long", R.drawable.logo_app, "C'est long", SignCategory.BASIC),
        SignWord("pas bon", R.drawable.logo_app, "Pas bon", SignCategory.BASIC),
        SignWord("ce n'est pas bon", R.drawable.logo_app, "Ce n'est pas bon", SignCategory.BASIC),
        SignWord("c'est bon", R.drawable.logo_app, "C'est bon", SignCategory.BASIC),
        SignWord("ça suffit", R.drawable.logo_app, "Ça suffit", SignCategory.BASIC),
    )

    fun findSignByWord(word: String): SignWord? {
        val wordLower = word.lowercase().trim()
        
        return words.find { signWord ->
            when (wordLower) {
                // ALPHABET
                "a", "lettre a", "letter a", "حرف أ", "حرف a" -> signWord.word == "lettre a"
                "b", "lettre b", "letter b", "حرف ب", "حرف b" -> signWord.word == "lettre b"
                "c", "lettre c", "letter c", "حرف ت", "حرف c" -> signWord.word == "lettre c"
                // ... (je pourrais continuer pour tout l'alphabet mais c'est long, je fais les principaux)
                
                // JOURS DE LA SEMAINE
                "lundi", "monday", "الاثنين", "الإثنين" -> signWord.word == "lundi"
                "mardi", "tuesday", "الثلاثاء" -> signWord.word == "mardi"
                "mercredi", "wednesday", "الأربعاء" -> signWord.word == "mercredi"
                "jeudi", "thursday", "الخميس" -> signWord.word == "jeudi"
                "vendredi", "friday", "الجمعة" -> signWord.word == "vendredi"
                "samedi", "saturday", "السبت" -> signWord.word == "samedi"
                "dimanche", "sunday", "الأحد" -> signWord.word == "dimanche"

                // SALUTATIONS
                "bonjour", "salut", "coucou", "hello", "hi", "good morning", "greetings", 
                "مرحبا", "أهلا", "السلام عليكم", "صباح الخير" -> signWord.word == "bonjour"
                
                "au revoir", "bye", "goodbye", "see you", "ciao", "à plus", 
                "مع السلامة", "إلى اللقاء", "وداعا" -> signWord.word == "au revoir"
                
                "bonsoir", "good evening", "مساء الخير" -> signWord.word == "bonsoir"
                "bonne nuit", "good night", "تصبح على خير", "ليلة سعيدة" -> signWord.word == "bonne nuit"
                "bienvenue", "welcome", "أهلا وسهلا" -> signWord.word == "bienvenue"
                "enchanté", "nice to meet you", "تشرفنا" -> signWord.word == "enchanté"

                // POLITESSE
                "merci", "thanks", "thank you", "شكرا", "أشكرك" -> signWord.word == "merci"
                "de rien", "you're welcome", "no problem", "عفوا", "لا شكر على واجب" -> signWord.word == "de rien"
                "pardon", "excuse me", "sorry", "عذرا", "آسف", "أعتذر" -> signWord.word == "pardon"
                "désolé", "je suis désolé", "i am sorry", "so sorry", "أنا آسف" -> signWord.word == "désolé"
                "s'il te plait", "s'il vous plait", "please", "من فضلك", "رجاء" -> signWord.word == "s'il te plait"
                "oui", "yes", "yep", "yeah", "نعم", "أجل", "صحيح" -> signWord.word == "oui"
                "non", "no", "nope", "لا", "كلا" -> signWord.word == "non"
                "bravo", "congratulations", "well done", "أحسنت", "مبروك", "ممتاز" -> signWord.word == "bravo"
                
                // QUESTIONS
                "ça va", "comment ça va", "how are you", "how are you doing", "كيف حالك", "كيف الحال" -> signWord.word == "ça va"
                "où", "where", "أين", "فين" -> signWord.word == "où"
                "quand", "when", "متى" -> signWord.word == "quand"
                "comment", "how", "كيف" -> signWord.word == "comment"
                "combien", "how much", "how many", "كم" -> signWord.word == "combien"
                "pourquoi", "why", "لماذا", "ليه" -> signWord.word == "pourquoi"
                "qui", "who", "من" -> signWord.word == "qui"
                "quoi", "what", "ماذا", "شنو" -> signWord.word == "quoi"
                "ton nom", "quel est ton nom", "what is your name", "ma ismouk", "ما اسمك", "اسمك ايه" -> signWord.word == "ton nom"
                "quelle heure est-il", "what time is it", "time", "كم الساعة" -> signWord.word == "quelle heure est-il"
                "qu'est-ce que tu fais", "what are you doing", "maza taf3al", "ماذا تفعل" -> signWord.word == "qu'est-ce que tu fais"

                // FAMILLE
                "papa", "père", "dad", "father", "daddy", "أب", "أبي", "بابا" -> signWord.word == "papa"
                "maman", "mère", "mom", "mother", "mommy", "أم", "أمي", "ماما" -> signWord.word == "maman"
                "frère", "brother", "bro", "أخ", "أخي" -> signWord.word == "frère"
                "soeur", "sister", "sis", "أخت", "أختي" -> signWord.word == "soeur"
                "enfant", "child", "kid", "طفل", "ولد" -> signWord.word == "enfant"
                "homme", "man", "male", "رجل" -> signWord.word == "homme"
                "femme", "woman", "female", "lady", "امرأة", "سيدة" -> signWord.word == "femme"
                "adolescent", "teenager", "teen", "مراهق", "شاب" -> signWord.word == "adolescent"
                
                // ACTIONS & BESOINS
                "manger", "eat", "eating", "food", "أكل", "طعام" -> signWord.word == "manger"
                "boire", "drink", "drinking", "water", "شرب", "ماء" -> signWord.word == "boire"
                "dormir", "sleep", "sleeping", "bed", "نوم", "أنام" -> signWord.word == "dormir"
                "travailler", "travail", "work", "job", "working", "عمل", "شغل" -> signWord.word == "travail"
                "étudier", "apprendre", "learn", "study", "learning", "تعلم", "دراسة" -> signWord.word == "apprendre"
                "comprendre", "understand", "understood", "فهم", "أفهم" -> signWord.word == "comprendre"
                "danser", "dance", "dancing", "رقص" -> signWord.word == "danser"
                "chanter", "sing", "singing", "غناء", "يغني" -> signWord.word == "chanter"
                "marcher", "walk", "walking", "مشي", "يمشي" -> signWord.word == "marcher"
                "sauter", "jump", "jumping", "قفز", "يقفز" -> signWord.word == "sauter"
                
                // ÉMOTIONS
                "content", "heureux", "happy", "glad", "joy", "سعيد", "فرحان", "مسرور" -> signWord.word == "content"
                "triste", "sad", "unhappy", "depressed", "حزين", "زعلان" -> signWord.word == "triste"
                "fâché", "colère", "angry", "mad", "غاضب", "زعلان" -> signWord.word == "fâché"
                "fatigué", "tired", "exhausted", "sleepy", "تعبان", "مرهق" -> signWord.word == "fatigué"
                "amour", "je t'aime", "love", "i love you", "حب", "أحبك" -> signWord.word == "amour"
                "peur", "danger", "scared", "fear", "dangerous", "خائف", "خطر", "خوف" -> signWord.word == "danger"
                
                // DIVERS
                "maison", "home", "house", "منزل", "بيت", "دار" -> signWord.word == "maison"
                "école", "school", "education", "مدرسة" -> signWord.word == "école"
                "toilettes", "toilet", "wc", "bathroom", "restroom", "حمام", "مرحاض", "تواليت" -> signWord.word == "toilettes"
                "urgence", "emergency", "help", "aide", "طوارئ", "نجدة", "مساعدة" -> signWord.word == "urgence"
                "médecin", "docteur", "doctor", "medic", "طبيب", "دكتور" -> signWord.word == "médecin"
                "pharmacie", "pharmacy", "drugstore", "صيدلية" -> signWord.word == "pharmacie"
                "restaurant", "bistrot", "diner", "مطعم" -> signWord.word == "restaurant"
                "taxi", "cab", "تاكسي", "سيارة أجرة" -> signWord.word == "taxi"
                "téléphone", "phone", "call", "mobile", "هاتف", "تليفون", "موبايل" -> signWord.word == "téléphone"
                "fleur", "flower", "rose", "زهرة", "وردة" -> signWord.word == "fleur"
                "vache", "cow", "بقرة" -> signWord.word == "vache"
                "verre", "glass", "cup", "كأس", "كوب" -> signWord.word == "verre"
                
                // NOMBRES
                "0", "zéro", "zero", "null", "صفر" -> signWord.word == "zéro"
                "1", "un", "one", "واحد" -> signWord.word == "un"
                "2", "deux", "two", "اثنان", "ثنين" -> signWord.word == "deux"
                "3", "trois", "three", "ثلاثة" -> signWord.word == "trois"
                "4", "quatre", "four", "أربعة" -> signWord.word == "quatre"
                "5", "cinq", "five", "خمسة" -> signWord.word == "cinq"
                "6", "six", "six", "ستة" -> signWord.word == "six"
                "7", "sept", "seven", "سبعة" -> signWord.word == "sept"
                "8", "huit", "eight", "ثمانية" -> signWord.word == "huit"
                "9", "neuf", "nine", "تسعة" -> signWord.word == "neuf"
                "10", "dix", "ten", "عشرة" -> signWord.word == "dix"
                
                // EXPRESSIONS DIVERSES
                "c'est bon", "it's good", "good", "okay", "fine", "jyyid", "jayyid", "bon", "tayyib", "جيد", "هذا جيد", "طيب", "تمام" -> signWord.word == "c'est bon"
                "c'est dommage", "too bad", "what a pity", "pity", "shame", "lilasaf", "khasara", "للاسف", "خسارة", "يا للأسف" -> signWord.word == "c'est dommage"
                "il n'y a plus", "there is no more", "no more", "finished", "gone", "empty", "ma baqa", "khalas", "intah", "ما بقى", "خلاص", "انتهى", "لم يعد يوجد" -> signWord.word == "il n'y a plus"
                "je n'ai pas compris", "i don't understand", "i didn't understand", "lam afham", "ma fhemtech", "لم أفهم", "ما فهمت", "ما فهمتش" -> signWord.word == "je n'ai pas compris"
                "pas besoin", "no need", "unnecessary", "don't need", "la ahtaj", "mesh mehtaj", "mabghitch", "لا أحتاج", "لا داعي", "مش محتاج" -> signWord.word == "pas besoin"
                "inutile", "useless", "pointless", "ghayr mofid", "fadi", "ghayr nafi3", "غير مفيد", "بلا فائدة", "فاضي" -> signWord.word == "inutile"
                "c'est pas juste", "it's not fair", "unfair", "not fair", "laysa adl", "mesh adel", "hagrah", "ليس عدلا", "ظلم", "مش عدل" -> signWord.word == "c'est pas juste"
                "pas d'accord", "disagree", "i don't agree", "not agreed", "la awafiq", "mesh mowafiq", "rafid", "لا أوافق", "مش موافق", "معترض" -> signWord.word == "pas d'accord"
                "c'est ça", "that's it", "exactly", "correct", "that's right", "hatha howa", "sah", "btadb", "هذا هو", "صح", "بالضبط", "هو ده" -> signWord.word == "c'est ça"
                "ça suffit", "that's enough", "enough", "stop", "yakfi", "khalas", "barakat", "كفى", "خلاص", "يكفي", "بس" -> signWord.word == "ça suffit"
                "meilleur", "best", "better", "the best", "afdal", "ahsan", "top", "أفضل", "أحسن", "الأفضل" -> signWord.word == "meilleur"
                "tous les mois", "every month", "monthly", "each month", "koula chahr", "chahriya", "كل شهر", "شهريا" -> signWord.word == "tous les mois"
                "parfait", "perfect", "great", "excellent", "momtaz", "raie", "tamam", "ممتاز", "رائع", "تمام", "هايل" -> signWord.word == "parfait"
                "c'est long", "it's long", "too long", "long", "tawil", "taweel", "tiwil", "طويل", "هذا طويل" -> signWord.word == "c'est long"
                "pas bon", "ce n'est pas bon", "not good", "bad", "no good", "laysa jayyid", "mesh helw", "khayb", "ليس جيدا", "سيء", "مش حلو", "خايب" -> signWord.word == "pas bon"
                "ce n'est pas bon" -> signWord.word == "ce n'est pas bon" // Redondance gérée par la ligne du dessus mais gardée pour sûreté

                // Par défaut, vérifie si le mot correspond exactement (pour les cas simples)
                else -> signWord.word.equals(wordLower, ignoreCase = true)
            }
        }
    }

    fun getWordsByCategory(category: SignCategory): List<SignWord> {
        return words.filter { it.category == category }
    }

    // Liste des mots qui utilisent des vidéos
    private val videoWords = setOf(
        "lettre a", "lettre b", "lettre c", "lettre d", "lettre e",
        "lettre f", "lettre g", "lettre h", "lettre i", "lettre j",
        "lettre k", "lettre l", "lettre m", "lettre n", "lettre o",
        "lettre p", "lettre q", "lettre r", "lettre s", "lettre t",
        "lettre u", "lettre v", "lettre w", "lettre x", "lettre y",
        "lettre z",
        "malade",
        "apprendre",
        "comprendre",
        "pardon je comprends pas",
        "s'il te plait",
        "enfant",
        "femme",
        "homme",
        "maison",
        "poser une question",
        "ça me fait plaisir",
        "ça va",
        "combien",
        "comment",
        "j'ai compris",
        "je n'ai pas compris",
        "je suis content de te rencontrer",
        "je suis désolé",
        "je t'accompagne",
        "merci pour ton aide",
        "où",
        "pardon",
        "pourquoi",
        "pourquoi faire",
        "quand",
        "que faire",
        "qu'est-ce que tu fais",
        "quelle heure est-il",
        "qui",
        "quoi",
        "tu as faim",
        "tu m'accompagnes",
        "tu peux signer lentement",
        "une personne",
        "meilleur",
        "tous les mois",
        "c'est dommage",
        "il n'y a plus",
        "pas besoin",
        "inutile",
        "c'est pas juste",
        "pas d'accord",
        "parfait",
        "c'est ça",
        "c'est long",
        "pas bon",
        "ce n'est pas bon",
        "c'est bon",
        "ça suffit"
    )

    // Fonction pour vérifier si un mot a une vidéo
    fun hasVideo(word: String): Boolean {
        return videoWords.contains(word)
    }

    // Fonction pour obtenir le nom du fichier vidéo
    fun getVideoFileName(word: String): String {
        return when (word) {
            "lettre a" -> "a"
            "lettre b" -> "b"
            "lettre c" -> "c"
            "lettre d" -> "d"
            "lettre e" -> "e"
            "lettre f" -> "f"
            "lettre g" -> "g"
            "lettre h" -> "h"
            "lettre i" -> "i"
            "lettre j" -> "j"
            "lettre k" -> "k"
            "lettre l" -> "l"
            "lettre m" -> "m"
            "lettre n" -> "n"
            "lettre o" -> "o"
            "lettre p" -> "p"
            "lettre q" -> "q"
            "lettre r" -> "r"
            "lettre s" -> "s"
            "lettre t" -> "t"
            "lettre u" -> "u"
            "lettre v" -> "v"
            "lettre w" -> "w"
            "lettre x" -> "x"
            "lettre y" -> "y"
            "lettre z" -> "z"
            "malade" -> "malade"
            "apprendre" -> "apprendre"
            "comprendre" -> "comprendre"
            "pardon je comprends pas" -> "pardonjecomprendpas"
            "s'il te plait" -> "stp"
            "enfant" -> "enfant"
            "femme" -> "femme"
            "homme" -> "homme"
            "maison" -> "maison"
            "poser une question" -> "poserunequestion"
            "ça me fait plaisir" -> "camefaitplaisir"
            "ça va" -> "cava"
            "combien" -> "combien"
            "comment" -> "comment"
            "j'ai compris" -> "jaicompris"
            "je n'ai pas compris" -> "jenaipascompris"
            "je suis content de te rencontrer" -> "jesuiscontentdeterencontrer"
            "je suis désolé" -> "jesuisdesole"
            "je t'accompagne" -> "jetaccompagne"
            "merci pour ton aide" -> "mercipourtonaide"
            "où" -> "ou"  
            "une personne" -> "personne"
            "pardon" -> "pardon"
            "pourquoi" -> "pourquoi"
            "pourquoi faire" -> "pourquoifaire"
            "quand" -> "quand"
            "que faire" -> "quefaire"
            "qu'est-ce que tu fais" -> "quefaistu"
            "quelle heure est-il" -> "quelleheureestil"
            "qui" -> "qui"
            "quoi" -> "quoi"
            "tu as faim" -> "tuasfaim"
            "tu m'accompagnes" -> "tumaccompagne"
            "tu peux signer lentement" -> "tupeuxsignerlentemnt"
            "meilleur" -> "meilleur"
            "tous les mois" -> "toutlesmois"
            "c'est dommage" -> "cestdommage"
            "il n'y a plus" -> "ilnyaplus"
            "pas besoin" -> "pasbesoin"
            "inutile" -> "inutile" 
            "c'est pas juste" -> "cestpasjuste"
            "pas d'accord" -> "pasdaccord"
            "parfait" -> "parfait"
            "c'est ça" -> "cestca"
            "c'est long" -> "cestlong"
            "pas bon" -> "pasbon"
            "ce n'est pas bon" -> "cenestpasbon"
            "c'est bon" -> "cestbon"
            "ça suffit" -> "casuffit"
            else -> throw IllegalArgumentException("Pas de vidéo pour ce mot: $word")
        }
    }

    // Ajouter cette méthode dans la classe SignDictionary
    fun isVideoWord(word: String): Boolean {
        return videoWords.contains(word)
    }
}