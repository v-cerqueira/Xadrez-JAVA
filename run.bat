@echo off
echo Compilando o Jogo de Xadrez...
javac -d target\classes src\main\java\model\*.java src\main\java\model\pieces\*.java src\main\java\view\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilacao bem-sucedida!
    echo Executando o jogo...
    java -cp target\classes view.ChessGUI
) else (
    echo Erro na compilacao!
    pause
)

