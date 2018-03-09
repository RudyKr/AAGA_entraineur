AAGA - Problème de l'entraineur

CHARPIGNON - KRUISSEL

I - Heuristique

Pour traiter le problème de l'entraineur nous avons procédé par étapes dans notre dévellopement.
Dans un premier temps nous avons essayer de calculer la complexité en temps que pourrait prendre le calcul d'un alignement sur n noeuds. Pour cela nous nous sommes appuyés sur l'exemple utilisé dans le sujet mis à disposition. De ce fait nous avons essayé de réduire la complexité de la fonction de calcul de la valeur de l'alignement plutot que de s'appliquer à produire un algorithme poussé de calcul de l'alignement.
Dans un second temps nous avons commencé par concevoir une solution gloutonne. L'heuristique étant:
  - Ajouter au début le sommet de degré le plus faible.
  - Parmi tous les autres points du graphes prendre celui qui une fois ajouté dans l'alignement nous donne la meilleure valeur d'alignement.
  - Continuer cette heuristique jusqu'a ce que l'alignement soit constitué de l'ensembles des points du graphe.

Du fait de la complexité obtenue par cette heuristique nous avons essayé d'autre techniques de calcul sur les graphes comme par exemple la technique de recherche locale. Pour cela nous avons décidé d'utiliser notre algorithme glouton pour produire un algorithme de recherche locale consistant à intervertir deux sommets distincts de l'alignement. L'algorithme consiste donc à itérer sur l'alignement produit afin d'en ressortir une valeur d'alignement plus petite en interchangeant les sommets qui consituent l'alignement. De ces différentes valeurs d'alignements on en retire la plus petite valeur d'alignement.

En observant nos résultats, nous avons pu conclure que nos deux algorithmes nous permettent d'obtenir des scores valides mais avec une différences peu significatives c'est à dire avec une différence minime dans les valeurs obtenues. De ce fait nous avons opté pour améliorer les fonctions qui nous coute le plus de temps c'est à dire les fonctions de calcul de score. L'idée étant de parallèliser au maximum les fonctions de calculs afin de réduire au mieux le calcul en fonction de la machine.

Après avoir effectué des tests sur un panel significatif de graphes, nous avons remarqué que la corrélation entre le degré du premier point choisi dans l'alignement et le score de l'alignement n'était que très faible, voire inexistante. La première partie de notre heuristique était donc à changer. Nous avons opté pour essayer tous les points comme premier point de l'alignement. Nous avons amélioré notre score de presque 20% sur toutes les instances. 

II - Optimisation

Les meilleurs résultats retournés par les algorithmes sont stockés dans des fichiers afins de pouvoir garder le meilleur résultat de manière statique et ne pouvoir qu'améliorer notre score par la suite (ou reprendre l'alignement sauvegardé à défaut).

Pour optimiser la vitesse de calcul, nous avons décidé de paralléliser certaines parties du calcul. Les fonctions de score étant les fonctions appelées le plus souvent, nous avons commencé par threader celles ci. De plus, pour éviter la concurrence dans les écritures de fichier, ce choix était plus logique.
Lorsque nous avons changé le début de notre heuristique nous avons remarqué que la puissance de calcul n'était pas exploitée complètement, nous avons donc aussi threadé les tests de chaque point en tant que premier point.

Nous avons également divisé notre temps de calcul par 2 en n'utilisant que la distance au carré lors d'un calcul de distance entre 2 points.

Nous pourrions également grandement améliorer la vitesse de calcul de notre fonction swap (qui est beaucoup trop longue pour être lancée sur de grandes instances) en recalculant seulement le delta de score qui a changé sachant que le score de l'alignement précédant le premier item qui a été échangé sera le même et ainsi éviter de recalculer depuis le début de l'alignement. Malheuresement, nous n'avons pas réussi a implémenter cette optimisation.


