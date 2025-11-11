## Sobre este repositório

Pequeno jogo feito com libGDX (Java) que simula/integra dados de uma bicicleta ergométrica (RPM) para controlar a nave. O projeto usa o modelo padrão do gdx-liftoff com módulos `core` (lógica do jogo) e `lwjgl3` (plataforma desktop).

## Objetivo das instruções para agentes IA

Permitir que um agente de programação seja imediatamente produtivo: entender arquitetura, fluxo de dados (RPM -> gameplay), pontos de integração IoT, comandos de build/run e padrões de implementação do código.

## Arquitetura (big picture)
- Módulos:
  - `core`: lógica principal do jogo (código reutilizável entre plataformas). Ex.: `core/src/main/java/br/mackenzie/screens/GameScreen.java`.
  - `lwjgl3`: launcher / plataforma desktop — tarefa útil: `lwjgl3:run` para executar localmente.
- Fluxo de dados principal: sensor de RPM (IoT) -> classe de interface (mock ou real) -> `GameScreen` lê RPM a cada frame -> aplica lógica: movimento vertical, crouch, métricas (score, consistência) -> rendering via libGDX.

## Pontos de integração importantes
- Classe de entrada de dados IoT: `br.mackenzie.utils.IoTMockupController` (substituir/estender para integrar Serial/BLE). Procure por `IoTMockupController` para localizar a implementação.
- Recursos (imagens/sons) carregados por `br.mackenzie.utils.GameAssets` (textures usadas em `GameScreen`). Não altere nomes de recursos sem atualizar `GameAssets`.

## Convenções e padrões do projeto
- Mapeamento RPM -> movimento: no `GameScreen` a lógica usa
  - targetRPM = 50 + level*10 (ajuste por nível)
  - subida/descida: shipY += (rpm - targetRPM) * VERTICAL_SENSITIVITY * delta
  - crouch é detectado por RPM muito baixo: rpm < targetRPM - 30f -> set crouch por tempo fixo (CROUCH_DURATION = 1s).
- Obstáculos: gerados pela direita e movem-se para a esquerda; ao sair da tela pontuam.
- Sensibilidade e tempos são constantes no `GameScreen` (VERTICAL_SENSITIVITY, SPAWN_INTERVAL_BASE etc.). Ajuste aqui para balanceamento rápido.

## Comandos úteis (desenvolvimento)
- Entrar no projeto e rodar desktop (dev rápido):
  - cd Astro/projeto && ./gradlew lwjgl3:run
- Build do jar executável desktop:
  - cd Astro/projeto && ./gradlew lwjgl3:jar
- Limpar e build full:
  - cd Astro/projeto && ./gradlew clean build
- Tarefas gradle por módulo (ex): `core:clean`, `lwjgl3:run`.

## Arquivos-chave a revisar primeiro
- `README.md` (root) — visão geral do projeto e requisitos acadêmicos.
- `Astro/projeto/README.md` — instruções do template libGDX e comandos Gradle.
- `core/src/main/java/br/mackenzie/screens/GameScreen.java` — mapa principal de gameplay e exemplo de como o RPM é consumido.
- `core/src/main/java/br/mackenzie/utils/IoTMockupController.java` — ponto para substituir pelo código real de leitura Serial/BLE.
- `core/src/main/java/br/mackenzie/utils/GameAssets.java` — onde Textures/Assets são referenciados.

## Exemplos rápidos (padrões encontrados)
- Detecção de crouch (exemplo do repo):
  - condição: `if (rpm < targetRPM - 30f) { isCrouching = true; crouchTimer = CROUCH_DURATION; }`
- Mapeamento linear RPM -> Y:
  - `shipY += (rpm - targetRPM) * VERTICAL_SENSITIVITY * delta;`

## Boas substituições para integração real IoT
- Substituir `IoTMockupController` por uma implementação que exponha `float getCurrentRPM()` e encapsule:
  - inicialização/conexão (Serial/BLE), parsing de pacotes, buffering e suavização (média móvel), fallback para mock quando desconectado.
- Recomenda-se aplicar suavização no próprio controller (ex.: média móvel N=5, sampling ~50ms) para manter `GameScreen` simples.

## Limitações e observações detectáveis
- Não há testes unitários significativos — procure por `src/test` (provavelmente vazio). Alterações críticas devem ser validadas manualmente com `lwjgl3:run`.
- Assets e paths esperados pelo jogo devem estar em `Astro/projeto/assets` ou conforme `GameAssets`.

## Checklist rápido para PRs onde agente muda lógica de jogo
1. Atualizar `GameAssets` se renomear/adicionar texturas.
2. Garantir fallback no `IoTMockupController` ou documentar necessidade de hardware.
3. Rodar `./gradlew lwjgl3:run` e validar visualmente as mudanças de comportamento.

---
Se alguma parte estiver incompleta (ex.: localização real de `IoTMockupController` ou `GameAssets`), diga quais arquivos quer que eu abra e eu atualizo as instruções. Quer que eu mescle esse arquivo agora no repositório? 
