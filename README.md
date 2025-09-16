# Jogo de Xadrez em Java Swing

Um jogo de xadrez completo implementado em Java usando a biblioteca Swing para interface gráfica, seguindo a arquitetura MVC (Model-View-Controller).

## Características

- **Interface gráfica completa** com tabuleiro 8x8
- **Todas as regras oficiais** do xadrez implementadas
- **Arquitetura MVC** bem estruturada
- **Movimentos especiais**: Roque, Promoção de peão
- **Detecção de xeque e xeque-mate**
- **Histórico de movimentos** em notação de xadrez
- **Interface intuitiva** com destaque de movimentos possíveis

## Estrutura do Projeto

```
src/main/java/
├── model/
│   ├── Board.java          # Representa o tabuleiro
│   ├── Game.java           # Controla o fluxo do jogo
│   ├── Move.java           # Representa um movimento
│   ├── Position.java       # Representa uma posição
│   └── pieces/
│       ├── Piece.java      # Classe abstrata para peças
│       ├── Pawn.java       # Peão
│       ├── Rook.java       # Torre
│       ├── Knight.java     # Cavalo
│       ├── Bishop.java     # Bispo
│       ├── Queen.java      # Rainha
│       └── King.java       # Rei
└── view/
    └── ChessGUI.java       # Interface gráfica principal
```

## Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior

## Como Executar

### Opção 1: Usando Maven
```bash
# Compilar o projeto
mvn clean compile

# Executar o jogo
mvn exec:java -Dexec.mainClass="view.ChessGUI"

# Ou criar um JAR executável
mvn clean package
java -jar target/jogo-xadrez-1.0.0.jar
```

### Opção 2: Compilação Manual
```bash
# Compilar todas as classes
javac -d target/classes src/main/java/model/*.java src/main/java/model/pieces/*.java src/main/java/view/*.java

# Executar
java -cp target/classes view.ChessGUI
```

### Opção 3: Scripts de Execução
```bash
# Windows
run.bat

# Linux/Mac
chmod +x run.sh
./run.sh
```

## Como Jogar

1. **Iniciar o jogo**: Execute o programa e uma janela será aberta
2. **Selecionar peça**: Clique em uma peça do seu time (brancas começam)
3. **Ver movimentos**: As casas possíveis serão destacadas em verde
4. **Fazer movimento**: Clique na casa de destino
5. **Alternar turnos**: O jogo alterna automaticamente entre brancas e pretas

## Funcionalidades

### Movimentos Básicos
- **Peão**: Move uma casa para frente, captura nas diagonais
- **Torre**: Move horizontalmente e verticalmente
- **Cavalo**: Move em L (2 casas + 1 casa perpendicular)
- **Bispo**: Move nas diagonais
- **Rainha**: Move em todas as direções
- **Rei**: Move uma casa em qualquer direção

### Regras Especiais
- **Passant**: Captura especial de peão, feita quando um peão adversário avança duas casas e pode ser tomado como se tivesse andado apenas uma.
- **Roque**: Movimento especial entre rei e torre
- **Promoção**: Peão vira rainha ao chegar na última fileira
- **Xeque**: Detecção automática quando o rei está em perigo
- **Xeque-mate**: Fim do jogo quando não há escape

### Interface
- **Tabuleiro visual**: Cores alternadas para facilitar visualização
- **Destaque de seleção**: Casa selecionada em amarelo
- **Movimentos possíveis**: Destacados em verde
- **Histórico**: Lista de todos os movimentos realizados
- **Status**: Indica turno atual e estado do jogo

## Controles

- **Botão "Novo Jogo"**: Reinicia a partida
- **Botão "Salvar Jogo"**: Salva o estado atual (funcionalidade futura)
- **Botão "Carregar Jogo"**: Carrega jogo salvo (funcionalidade futura)

## Tecnologias Utilizadas

- **Java 11**: Linguagem de programação
- **Java Swing**: Biblioteca para interface gráfica
- **Maven**: Gerenciamento de dependências e build
- **Arquitetura MVC**: Separação de responsabilidades

## Desenvolvimento

Este projeto foi desenvolvido seguindo boas práticas de programação:

- **Código limpo** e bem documentado
- **Separação de responsabilidades** (MVC)
- **Tratamento de erros** adequado
- **Interface responsiva** e intuitiva

## Próximas Funcionalidades

- [ ] Salvar e carregar jogos
- [ ] Adicionar temas visuais
- [ ] Implementar análise de posição

## Contribuição

Sinta-se à vontade para contribuir com melhorias, correções de bugs ou novas funcionalidades!

## Licença

Este projeto é de código aberto e está disponível sob a licença MIT.
