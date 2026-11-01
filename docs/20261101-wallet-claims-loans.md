# Wallet-final 2.2 — réclamations et crédits

Ce dépôt a été ouvert et modifié après l’autorisation explicite du propriétaire dans la demande du 30 octobre / 1 novembre. Branche de base : main, commit 5107810.

Les modèles Moshi lisent désormais les noms exacts des champs web : principal_amount, remaining_balance, repaid_amount, total_repayment, installment_number, due_date, payment_date. Un prêt mal formé sans montant ou solde restant provoque une erreur de décodage, plutôt qu’un faux solde nul. Le contrat du prêt courant backend inclut échéancier et remboursements. Les remboursements actualisent prêt, historique et wallet après confirmation serveur et les soumissions simultanées dans l’interface sont bloquées.

L’offre et la demande utilisent la même devise sélectionnée (USD/EUR/CDF). La fréquence suit le contrat serveur : un mois hebdomadaire sur quatre échéances, trois mois mensuel, durées supérieures trimestrielles. Les erreurs de lecture serveur restent visibles. L’historique distingue pénalité et remboursement, et affiche le reste de chaque échéance.

Les réclamations restent des opérations client : aucun écran de nomination ou d’administration d’agent n’est ajouté à ce mobile. Le détail affiche le motif complet et les consignes de correction de l’agence, l’état de litige et le net renvoyés par les routes authentifiées. Les pièces et signatures KYC ne sont pas renvoyées dans ces DTO clients. Le contrôle des documents s’effectue dans l’espace web agent habilité.

La journalisation HTTP de corps a été retirée : BASIC en debug, NONE en release. Le fichier gitignore protège les futurs caches, builds et clés contre un ajout accidentel ; les artefacts déjà présents historiquement dans Git ne sont pas supprimés dans cette correction.

Version : 2.2, code 11. La validation de compilation utilise les versions du dépôt, Gradle 9.3.1, AGP 9.1.1, plateforme Android 36.1 et JDK17. Les adaptations locales de signature et vérifications de contrat résident hors dépôt ; aucune clé de production n’est utilisée ni publiée. La signature officielle et les parcours financiers sur téléphone avec comptes de test autorisés restent à effectuer avant distribution. Aucune base de production n’est modifiée.
