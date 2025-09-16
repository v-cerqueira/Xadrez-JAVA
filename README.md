# Jogo de Xadrez em Java Swing
<img width="1365" height="717" alt="image" src="https://github.com/user-attachments/assets/1a56a2e2-62fb-4a91-a300-2ec2fef29af8" />
<img width="235" height="227" alt="image" src="https://github.com/user-attachments/assets/4e1169b7-1d90-40ac-a6bc-187a9898214e" />
<img width="231" height="139" alt="image" src="https://github.com/user-attachments/assets/6b35c0d5-fd80-4cfd-ab92-fdfdd959cf04" />



# Jogo de Xadrez em Java Swing
<img width="858" height="606" alt="image" src="https://github.com/user-attachments/assets/3468d525-5580-43bd-ba9b-50ef4a128bd8" />
<img width="474" height="121" alt="image" src="https://github.com/user-attachments/assets/cffec51e-5261-4a91-9221-a04ba831abe2" />
<img width="315" height="116" alt="image" src="https://github.com/user-attachments/assets/069361d9-d779-495b-b561-31684d87fd5d" />


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
 - **Inteligência Artificial Avançada**: Duas opções de IA para jogar contra o computador
 - **Seleção de IA**: Escolha entre IA Suprema (Minimax) e IA Suprema 2 (Negamax + Quiescência) na interface
 - **Desempenho ajustável**: Profundidade da IA otimizada para velocidade e jogadas inteligentes
│       ├── Rook.java       # Torre
│       ├── Knight.java     # Cavalo
│       ├── Bishop.java     # Bispo
│       ├── Queen.java      # Rainha
│       └── King.java       # Rei
└── view/
    └── ChessGUI.java       # Interface gráfica principal
```

│   ├── IASuprema.java     # IA Minimax
│   ├── IANivel8.java      # IA Negamax + Quiescência
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
6. **Jogar contra IA**: Ative a IA avançada no menu e escolha o nível desejado
java -jar target/jogo-xadrez-1.0.0.jar
```


### Inteligência Artificial (IA)
- **IA Suprema (Minimax)**: Analisa jogadas futuras, simula respostas do adversário e escolhe o melhor movimento.
- **IA Suprema 2 (Negamax + Quiescência)**: Além de simular jogadas, continua analisando capturas até a posição ficar "quieta", evitando erros em trocas de peças.
- **Função de avaliação**: Considera valor das peças, mobilidade e segurança do rei.
- **Seleção de IA**: Menu "IA Avançada" permite alternar entre as duas IAs ou desativar.
java -cp target/classes view.ChessGUI
 - **Menu IA Avançada**: Permite escolher entre IA Suprema, IA Suprema 2 ou desativar IA
run.bat

 - **ComboBox "IA Avançada"**: Seleciona o tipo de IA para jogar contra o computador
```

 - **Algoritmos de IA clássicos**: Minimax e Negamax com busca de quiescência
3. **Ver movimentos**: As casas possíveis serão destacadas em verde
4. **Fazer movimento**: Clique na casa de destino
5. **Alternar turnos**: O jogo alterna automaticamente entre brancas e pretas

 - **IA modular**: Fácil de expandir ou ajustar algoritmos
 - **Foco didático**: Ideal para estudos e apresentações universitárias
- **Torre**: Move horizontalmente e verticalmente
- **Cavalo**: Move em L (2 casas + 1 casa perpendicular)
 - [ ] Melhorar função de avaliação da IA
 - [ ] Adicionar níveis de dificuldade para IA

### Regras Especiais
- **Passant**: Captura especial de peão, feita quando um peão adversário avança duas casas e pode ser tomado como se tivesse andado apenas uma.

## Dicas para Apresentação

- Explique a diferença entre as duas IAs (Minimax e Negamax + Quiescência)
- Mostre como alternar entre elas na interface
- Destaque a arquitetura MVC e a modularidade do código
- Comente sobre possíveis melhorias futuras

## Referências

- Algoritmos Minimax e Negamax: https://en.wikipedia.org/wiki/Minimax
- Busca de quiescência: https://en.wikipedia.org/wiki/Quiescence_search
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
