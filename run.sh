#!/bin/bash
echo "Compilando o Jogo de Xadrez..."
javac -d target/classes src/main/java/model/*.java src/main/java/model/pieces/*.java src/main/java/view/*.java

if [ $? -eq 0 ]; then
    echo "Compilação bem-sucedida!"
    echo "Executando o jogo..."
    java -cp target/classes view.ChessGUI
else
    echo "Erro na compilação!"
    read -p "Pressione Enter para continuar..."
fi

