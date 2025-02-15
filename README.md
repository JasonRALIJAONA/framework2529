# framework2529
sprint web dynamique

SRPINT 1
1- mettez framework2529.jar dans votre librairie
2- dans votre fichier web.xml ajoutez:
    <context-param>
        <param-name>controller</param-name>
        <param-value>package de vorte controler</param-value>
    </context-param>

3- Annotez par @Controller vos controller
4- Annotez par @Jget les methodes : (exemple @JGet(path))


AUTHORIZATION:
mettez le nom de l'authorisation que vous voulez dans web.xml 
<context-param>
        <param-name>auth_name</param-name>
        <param-value>auth</param-value>
</context-param>
