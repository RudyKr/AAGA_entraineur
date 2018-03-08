AAGA - Problème de l'entraineur

CHARPIGNON - KRUISSEL

I - Heuristique

Pour traiter le problème de l'entraineur nous avons procédé pa étapes dans notre dévellopement.
Dans un premier temps nous avons essayer de calculer la complexité en temps que pourrais prendre le calcul d'un alignement sur n noeuds. Pour cela nous nous sommes appuyé sur l'exemple utilisé dans le sujet mis à dispositions et nous avons conclu que la complexité était de O(n2). De ce fait nous avons essayé de réduire la complexité de la fonction de calcul de la valeur de l'alignement plutot que de s'appliquer à produire un algorithme poussé de calcul de l'alignement.
Dans un second temps nous avons commencé par concevoir une solution gloutonne. L'heuristique étant:
  - Ajouter au début le sommet de degré le plus faible.
  - Parmi tous les autres points du graphes prendre celui qui une fois ajouté dans l'alignement nous donne la meilleur valeur d'alignement.
  - Continuer cette heuristique jusqu'a ce que l'alignement soit constitué de l'ensembles des points du graphe.

Du fait de la complexité obtenue par cette heuristique nous avons essayé d'autre techniques de calcul sur les graphes comme par exemples la technique de recherche locale. Pour cela nous avons décidé d'utiliser notre algorithme glouton pour produire un algorithme de recherche locale consistant à intervertir deux sommets distincts de l'alignement. L'algorithme consiste donc à itérer sur l'alignement produit afin d'en ressortir une valeur d'alignement plus petite en interchangeant les sommets qui consituent l'alignement. De ces différentes valeurs d'alignements on en retire la plus petite valeur d'alignement.

Les résultats de chaque algorithmes que nous avons écris sont stockés dans des fichiers afins de pouvoir garder un résultat valide de manière statique malgré le fait que cela nous empèche de parallèlise nos algorithmes à cause de la concurrence.

En observant nos résultats, nous avons pu conclure que nos deux algorithmes nous permettents d'obtenir des scores valides mais avec une différences peu significatives c'est à dire avec une différence minime dans les valeurs obtenues. De ce fait nous avons opté pour améliorer les fonctions qui nous coute le plus de temps c'est à dire les fonctions de calcul de score. L'idée étant de parallèliser au maximum les fonctions de calculs afin de réduire au mieux le calcul en fonction de la machine.

II - Optimisation

(TTTTTTTTT OOOOOO DDDDDD OOOOOOO)
